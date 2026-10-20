package apartamentos.demo.controller;

import apartamentos.demo.domain.Agua;
import apartamentos.demo.domain.Apartamento;
import apartamentos.demo.domain.MontosAgua;
import apartamentos.demo.service.AguaService;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import io.github.bonigarcia.wdm.WebDriverManager;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import lombok.extern.slf4j.Slf4j;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;

import org.springframework.beans.propertyeditors.CustomDateEditor;

import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@Slf4j
@RequestMapping("/agua")
public class AguaController {

    @Autowired
    private AguaService aguaService;

    // ===== URL y XPATH NUEVOS AY A =====
    private static final String URL_AYA =
            "https://www.aya.go.cr/inicio/gestiones_interes/cf/consultar_factura";

    private static final String XPATH_NIS_INPUT =
            "/html/body/app-root/app-home/div/main/app-consultar-factura/div/div/form/div/div[1]/app-campo-formulario/div/input";

    private static final String XPATH_BOTON_CONSULTA =
            "/html/body/app-root/app-home/div/main/app-consultar-factura/div/div/form/div/div[2]/button";

    // reCAPTCHA: se espera a que el usuario lo deje marcado
    private static final String XPATH_RECAPTCHA_IFRAME =
            "//iframe[contains(@src,'recaptcha') or contains(@title,'reCAPTCHA')]";

    private static final String XPATH_RECAPTCHA_ANCHOR =
            "//*[@id='recaptcha-anchor']";

    // XPath exactos que me pasaste
    private static final String XPATH_MONTO =
            "//*[@id='vlersrongriz1332593465']/ag-grid-angular/div/div[1]/div[2]/div[3]/div[1]/div[2]/div/div/div[6]/div/span";

    private static final String XPATH_FECHA_VENCIMIENTO =
            "//*[@id='vlersrongriz1332593465']/ag-grid-angular/div/div[1]/div[2]/div[3]/div[1]/div[2]/div/div/div[3]/div/span";

    // Fallbacks por si el id dinámico cambia
    private static final String XPATH_MONTO_FALLBACK =
            "//ag-grid-angular/div/div[1]/div[2]/div[3]/div[1]/div[2]/div/div/div[6]/div/span";

    private static final String XPATH_FECHA_VENCIMIENTO_FALLBACK =
            "//ag-grid-angular/div/div[1]/div[2]/div[3]/div[1]/div[2]/div/div/div[3]/div/span";

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @GetMapping("/listado")
    public String inicio(Model model) {
        var agua = aguaService.getAgua(true);
        Date now = new Date();

        for (Agua a : agua) {
            if (a.getFechaPagar() != null && a.getFechaPagar().before(now)) {
                a.setFechaPagar(addMonth(a.getFechaPagar()));
            }
            if (a.getFechaInicioPago() != null && a.getFechaInicioPago().before(now)) {
                a.setFechaInicioPago(addMonth(a.getFechaInicioPago()));
            }
            aguaGuardar(a);
        }

        model.addAttribute("agua", agua);
        model.addAttribute("totalAgua", agua.size());
        return "agua/listado";
    }

    private Date addMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, 1);
        return cal.getTime();
    }

    @GetMapping("/nuevo")
    public String aguaNuevo(Agua agua) {
        return "agua/modifica";
    }

    @PostMapping("/guardar")
    public String aguaGuardar(@ModelAttribute("agua") Agua agua) {
        aguaService.save(agua);
        return "redirect:/agua/listado";
    }

    @GetMapping("/eliminar/{idAgua}")
    public String aguaEliminar(@PathVariable("idAgua") Long idAgua) {
        Agua agua = new Agua();
        agua.setIdAgua(idAgua);
        aguaService.delete(agua);
        return "redirect:/agua/listado";
    }

    @GetMapping("/modificar/{idAgua}")
    public String aguaModificar(@PathVariable("idAgua") Long idAgua, Model model) {
        Agua agua = new Agua();
        agua.setIdAgua(idAgua);
        agua = aguaService.getAgua(agua);
        model.addAttribute("agua", agua);
        return "agua/modifica";
    }

    // ===================== PDF LISTADO =====================
    @GetMapping("/descargar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void descargarPDF(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"datos_Agua.pdf\"");

        List<Agua> aguaList = new java.util.ArrayList<>(aguaService.getAgua(true));

        // Ordenar por apartamento
        aguaList.sort(java.util.Comparator.comparing(
                this::obtenerTextoApartamentos,
                String.CASE_INSENSITIVE_ORDER
        ));

        try (PdfWriter writer = new PdfWriter(response.getOutputStream());
             Document document = new Document(new com.itextpdf.kernel.pdf.PdfDocument(writer))) {

            document.add(new Paragraph("Listado de Agua").setBold().setFontSize(18));

            float[] columnWidths = {80F, 140F, 100F, 50F, 130F};
            Table table = new Table(columnWidths);
            table.addHeaderCell(new Cell().add(new Paragraph("ID")));
            table.addHeaderCell(new Cell().add(new Paragraph("Vencimiento(Año/Mes/dia)")));
            table.addHeaderCell(new Cell().add(new Paragraph("Apartamentos")));
            table.addHeaderCell(new Cell().add(new Paragraph("Pagar")));
            table.addHeaderCell(new Cell().add(new Paragraph("Monto")));

            for (Agua agua : aguaList) {
                table.addCell(new Cell().add(new Paragraph(String.valueOf(agua.getIdAgua()))));
                table.addCell(new Cell().add(new Paragraph(formatFecha(agua.getFechaVencimiento()))));
                table.addCell(new Cell().add(new Paragraph(obtenerTextoApartamentos(agua))));
                table.addCell(new Cell().add(new Paragraph(
                        agua.getPagar() != null && agua.getPagar() ? "Sí" : "No"
                )));
                table.addCell(new Cell().add(new Paragraph("")));
            }

            document.add(table);
        }
    }

    private String obtenerTextoApartamentos(Agua agua) {
        StringBuilder departamentos = new StringBuilder();

        if (agua.getIdApartamento() != null) {
            for (Apartamento apartamento : agua.getIdApartamento()) {
                String nombreDepartamento = apartamento.getUbicacion() + apartamento.getDescripcionDepartamento();

                String departamentoModificado = nombreDepartamento.replaceAll("^.*?\\(", "");
                departamentoModificado = departamentoModificado.replace(")", "");
                departamentoModificado = departamentoModificado.replace(" ", "");

                if (departamentos.length() > 0) {
                    departamentos.append("\n");
                }
                departamentos.append(departamentoModificado);
            }
        }

        return departamentos.toString();
    }

    private String formatFecha(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    // ===================== SELENIUM HELPERS =====================
    private ChromeOptions buildOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();

        String bravePath1 = "C:\\Program Files\\BraveSoftware\\Brave-Browser\\Application\\brave.exe";
        String bravePath2 = System.getenv("LOCALAPPDATA")
                + "\\BraveSoftware\\Brave-Browser\\Application\\brave.exe";

        java.io.File brave1 = new java.io.File(bravePath1);
        java.io.File brave2 = new java.io.File(bravePath2);

        if (brave1.exists()) {
            options.setBinary(brave1);
        } else if (brave2.exists()) {
            options.setBinary(brave2);
        } else {
            throw new RuntimeException("No se encontró brave.exe en rutas comunes");
        }

        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--window-size=1400,900");

        if (headless) {
            options.addArguments("--headless=new");
        }

        return options;
    }

    private static class ConsultaAyaResult {
        long monto;
        Date fechaVencimiento;
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    private void esperarRecaptchaMarcado(WebDriver driver) {
        WebDriverWait waitCaptcha = new WebDriverWait(driver, Duration.ofSeconds(120));

        waitCaptcha.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.xpath(XPATH_RECAPTCHA_IFRAME)));

        WebElement anchor = waitCaptcha.until(
                ExpectedConditions.presenceOfElementLocated(By.xpath(XPATH_RECAPTCHA_ANCHOR))
        );

        waitCaptcha.until(d -> {
            try {
                String ariaChecked = anchor.getAttribute("aria-checked");
                String cls = anchor.getAttribute("class");
                return "true".equalsIgnoreCase(ariaChecked)
                        || (cls != null && cls.contains("recaptcha-checkbox-checked"));
            } catch (Exception e) {
                return false;
            }
        });

        driver.switchTo().defaultContent();
    }

    private WebElement waitVisibleAnyXpath(WebDriver driver, String... xpaths) {
        Exception last = null;

        for (String xpath : xpaths) {
            try {
                return new WebDriverWait(driver, Duration.ofSeconds(12))
                        .until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
            } catch (Exception e) {
                last = e;
            }
        }

        throw new RuntimeException("No se encontraron los elementos esperados", last);
    }

    private ConsultaAyaResult consultarEnAya(WebDriver driver, WebDriverWait wait, String nis) {
        driver.get(URL_AYA);

        WebElement inputNis = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath(XPATH_NIS_INPUT))
        );
        inputNis.clear();
        inputNis.sendKeys(nis);

        // Espera a que el captcha quede resuelto manualmente
        esperarRecaptchaMarcado(driver);

        // Pediste esperar 3 segundos antes de consultar
        sleep(3000);

        WebElement btnConsultar = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath(XPATH_BOTON_CONSULTA))
        );
        btnConsultar.click();

        WebElement montoEl = waitVisibleAnyXpath(driver, XPATH_MONTO, XPATH_MONTO_FALLBACK);
        WebElement fechaEl = waitVisibleAnyXpath(driver, XPATH_FECHA_VENCIMIENTO, XPATH_FECHA_VENCIMIENTO_FALLBACK);

        ConsultaAyaResult r = new ConsultaAyaResult();
        r.monto = parseMonto(montoEl.getText());
        r.fechaVencimiento = parseFechaFlexible(fechaEl.getText());
        return r;
    }

    private long parseMonto(String txt) {
        if (txt == null) return 0L;

        String s = txt.trim();
        if (s.isEmpty()) return 0L;

        int dot = s.lastIndexOf('.');
        if (dot > -1) {
            s = s.substring(0, dot);
        } else {
            int comma = s.lastIndexOf(',');
            if (comma > -1 && (s.length() - comma - 1) == 2) {
                s = s.substring(0, comma);
            }
        }

        s = s.replaceAll("[^0-9]", "");

        if (s.isEmpty()) return 0L;

        try {
            return Long.parseLong(s);
        } catch (Exception e) {
            return 0L;
        }
    }

    private Date parseFechaFlexible(String txt) {
        if (txt == null) return null;
        String s = txt.trim();
        if (s.isEmpty()) return null;

        String[] formatos = new String[] {
                "dd/MM/yyyy",
                "yyyy-MM-dd",
                "dd-MM-yyyy"
        };

        for (String f : formatos) {
            try {
                SimpleDateFormat df = new SimpleDateFormat(f);
                df.setLenient(false);
                return df.parse(s);
            } catch (Exception ignore) { }
        }

        return null;
    }

    // ===================== SOLO PARA VER EL PROCESO EN VIVO =====================
    @GetMapping("/interactuarPagina/{idAgua}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void interactuarPagina(@PathVariable("idAgua") Long idAgua) {
        WebDriver driver = null;
        try {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver(buildOptions(false)); // visible
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

            consultarEnAya(driver, wait, idAgua.toString());

            sleep(2000);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (driver != null) driver.quit();
        }
    }

    // ===================== PAGAR (GUARDA MONTO SI pagar=true) =====================
    @GetMapping("/pagar/{idAgua}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void pagar(@PathVariable("idAgua") Long idAgua) {
        WebDriver driver = null;

        try {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver(buildOptions(false)); // visible por reCAPTCHA
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

            ConsultaAyaResult r = consultarEnAya(driver, wait, idAgua.toString());

            Agua agua = new Agua();
            agua.setIdAgua(idAgua);
            agua = aguaService.getAgua(agua);

            if (agua != null && Boolean.TRUE.equals(agua.getPagar()) && r.monto > 0) {
                Date fechaActual = new Date();
                aguaService.agregarMonto(new MontosAgua(r.monto, agua, fechaActual));
            }

            if (agua != null && r.fechaVencimiento != null) {
                agua.setFechaVencimiento(r.fechaVencimiento);
                aguaService.save(agua);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (driver != null) driver.quit();
        }
    }

    // ===================== CONSULTAR MONTO (1 NIS) =====================
    public long consultarMonto(Long idAgua) {
        return consultarMonto(idAgua, false); // visible por reCAPTCHA
    }

    public long consultarMonto(Long idAgua, boolean headless) {
        WebDriver driver = null;
        try {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver(buildOptions(headless));
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

            ConsultaAyaResult r = consultarEnAya(driver, wait, idAgua.toString());
            return r.monto;

        } catch (Exception e) {
            return 0L;
        } finally {
            if (driver != null) driver.quit();
        }
    }

    // ===================== PDF MONTOS =====================
    @GetMapping("/descargarMontos")
    public void descargarPDFMontos(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"Montos_agua.pdf\"");

        var aguaList = aguaService.getAgua(true);

        try (PdfWriter writer = new PdfWriter(response.getOutputStream());
             Document document = new Document(new com.itextpdf.kernel.pdf.PdfDocument(writer))) {

            document.add(new Paragraph("Listado de Agua").setBold().setFontSize(18));

            float[] columnWidths = {80F, 140F, 100F, 50F, 130F};
            Table table = new Table(columnWidths);
            table.addHeaderCell(new Cell().add(new Paragraph("ID")));
            table.addHeaderCell(new Cell().add(new Paragraph("Vencimiento(Año/Mes/dia)")));
            table.addHeaderCell(new Cell().add(new Paragraph("Apartamentos")));
            table.addHeaderCell(new Cell().add(new Paragraph("Pagar")));
            table.addHeaderCell(new Cell().add(new Paragraph("Monto")));

            DecimalFormat decimalFormat = new DecimalFormat("#,###");

            WebDriver driver = null;
            try {
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver(buildOptions(false)); // visible por reCAPTCHA
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

                for (Agua agua : aguaList) {
                    if (!Boolean.TRUE.equals(agua.getPagar())) continue;

                    table.addCell(new Cell().add(new Paragraph(String.valueOf(agua.getIdAgua()))));
                    table.addCell(new Cell().add(new Paragraph(formatFecha(agua.getFechaVencimiento()))));

                    StringBuilder departamentos = new StringBuilder();
                    for (Apartamento apartamento : agua.getIdApartamento()) {
                        String nombreDepartamento = apartamento.getUbicacion() + apartamento.getDescripcionDepartamento();
                        String departamentoModificado = nombreDepartamento.replaceAll("^.*?\\(", "")
                                .replace(")", "")
                                .replace(" ", "");
                        departamentos.append(departamentoModificado).append("\n");
                    }

                    table.addCell(new Cell().add(new Paragraph(departamentos.toString())));
                    table.addCell(new Cell().add(new Paragraph("Sí")));

                    try {
                        ConsultaAyaResult r = consultarEnAya(driver, wait, agua.getIdAgua().toString());

                        if (r.fechaVencimiento != null) {
                            agua.setFechaVencimiento(r.fechaVencimiento);
                            aguaService.save(agua);
                        }

                        if (r.monto > 0) {
                            table.addCell(new Cell().add(new Paragraph(decimalFormat.format(r.monto))));
                        } else {
                            table.addCell(new Cell().add(new Paragraph("Pago")));
                        }
                    } catch (Exception ex) {
                        table.addCell(new Cell().add(new Paragraph("No consultado")));
                    }
                }

            } finally {
                if (driver != null) driver.quit();
            }

            document.add(table);
        }
    }

    // ===================== ACTUALIZAR FECHA VENCIMIENTO (BATCH) =====================
    @GetMapping("/ActualizarFechaVencimiento")
    public String ActualizarFechaVencimiento() {
        WebDriver driver = null;

        try {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver(buildOptions(false)); // visible por reCAPTCHA
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

            List<Agua> aguas = aguaService.getAgua(true);

            for (Agua agua : aguas) {
                if (!Boolean.TRUE.equals(agua.getPagar())) continue;

                try {
                    ConsultaAyaResult r = consultarEnAya(driver, wait, agua.getIdAgua().toString());

                    if (r.fechaVencimiento != null) {
                        agua.setFechaVencimiento(r.fechaVencimiento);
                        aguaService.save(agua);
                    }
                } catch (TimeoutException ex) {
                    System.out.println("No se pudo consultar/leer fecha para NIS: " + agua.getIdAgua());
                } catch (Exception ex) {
                    System.out.println("Error consultando NIS: " + agua.getIdAgua());
                }
            }

            return "redirect:/agua/listado";

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al actualizar fechas de vencimiento");
        } finally {
            if (driver != null) driver.quit();
        }
    }
}