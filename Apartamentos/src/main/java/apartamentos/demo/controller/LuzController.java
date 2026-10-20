package apartamentos.demo.controller;

import apartamentos.demo.domain.Apartamento;
import apartamentos.demo.domain.Luz;
import apartamentos.demo.domain.MontosLuz;
import apartamentos.demo.service.LuzService;

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

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequestMapping("/luz")
public class LuzController {

    @Autowired
    private LuzService luzService;

    // ===== URL y XPATH NUEVOS =====
    private static final String URL_ICE = "https://agenciaelectricidad.cn.ice.go.cr/public/consultaFacturaElectrica";

    private static final String XPATH_NIS_INPUT =
            "/html/body/div/div/main/div[2]/div/div/div/form/div[3]/div/input";

    private static final String XPATH_BOTON_CONSULTA =
            "/html/body/div/div/main/div[2]/div/div/div/form/div[4]/button";

    private static final String XPATH_MONTO =
            "/html/body/div/div/main/div[2]/div/div/div/div/div[1]/div[3]/table/tbody/tr/td[4]/div[2]";

    private static final String XPATH_FECHA_VENCIMIENTO =
            "/html/body/div/div/main/div[2]/div/div/div/div/div[1]/div[3]/table/tbody/tr/td[5]/div[2]/span";

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @GetMapping("/listado")
    public String inicio(Model model) {
        var luz = luzService.getLuz(true);
        Date now = new Date();

        for (Luz a : luz) {
            if (a.getFechaPagar() != null && a.getFechaPagar().before(now)) {
                a.setFechaPagar(addMonth(a.getFechaPagar()));
            }
            if (a.getFechaInicioPago() != null && a.getFechaInicioPago().before(now)) {
                a.setFechaInicioPago(addMonth(a.getFechaInicioPago()));
            }
            luzGuardar(a);
        }

        model.addAttribute("luz", luz);
        model.addAttribute("totalLuz", luz.size());
        return "luz/listado";
    }

    private Date addMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, 1);
        return cal.getTime();
    }

    @GetMapping("/nuevo")
    public String luzNuevo(Luz luz) {
        return "luz/modifica";
    }

    @PostMapping("/guardar")
    public String luzGuardar(@ModelAttribute("luz") Luz luz) {
        luzService.save(luz);
        return "redirect:/luz/listado";
    }

    @GetMapping("/eliminar/{idLuz}")
    public String luzEliminar(@PathVariable("idLuz") Long idLuz) {
        Luz luz = new Luz();
        luz.setIdLuz(idLuz);
        luzService.delete(luz);
        return "redirect:/luz/listado";
    }

    @GetMapping("/modificar/{idLuz}")
    public String luzModificar(@PathVariable("idLuz") Long idLuz, Model model) {
        Luz luz = new Luz();
        luz.setIdLuz(idLuz);
        luz = luzService.getLuz(luz);
        model.addAttribute("luz", luz);
        return "luz/modifica";
    }

    // ===================== PDF LISTADO =====================
    @GetMapping("/descargar")
    public void descargarPDF(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"datos_luz.pdf\"");

        var luzList = luzService.getLuz(true);

        List<Luz> luzOrdenada = luzList.stream()
                .sorted(Comparator.comparing(this::obtenerClaveOrdenLuz, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());

        try (PdfWriter writer = new PdfWriter(response.getOutputStream());
             Document document = new Document(new com.itextpdf.kernel.pdf.PdfDocument(writer))) {

            document.add(new Paragraph("Listado de Luz").setBold().setFontSize(18));

            float[] columnWidths = {80F, 140F, 100F, 50F, 130F};
            Table table = new Table(columnWidths);
            table.addHeaderCell(new Cell().add(new Paragraph("ID")));
            table.addHeaderCell(new Cell().add(new Paragraph("Vencimiento(Año/Mes/dia)")));
            table.addHeaderCell(new Cell().add(new Paragraph("Apartamentos")));
            table.addHeaderCell(new Cell().add(new Paragraph("Pagar")));
            table.addHeaderCell(new Cell().add(new Paragraph("Monto")));

            for (Luz luz : luzOrdenada) {
                table.addCell(new Cell().add(new Paragraph(String.valueOf(luz.getIdLuz()))));
                table.addCell(new Cell().add(new Paragraph(formatFecha(luz.getFechaVencimiento()))));
                table.addCell(new Cell().add(new Paragraph(obtenerDepartamentosOrdenadosTexto(luz))));
                table.addCell(new Cell().add(new Paragraph(Boolean.TRUE.equals(luz.getPagar()) ? "Sí" : "No")));
                table.addCell(new Cell().add(new Paragraph("")));
            }

            document.add(table);
        }
    }

    private String formatFecha(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    // ===================== SELENIUM HELPERS =====================
    private ChromeOptions buildOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--window-size=1400,900");
        if (headless) options.addArguments("--headless=new");
        return options;
    }

    private static class ConsultaIceResult {
        long monto;
        Date fechaVencimiento;
    }

    private ConsultaIceResult consultarEnIce(WebDriver driver, WebDriverWait wait, String nis) {
        // Siempre recargamos desde cero como pediste
        driver.get(URL_ICE);

        WebElement inputNis = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(XPATH_NIS_INPUT)));
        inputNis.clear();
        inputNis.sendKeys(nis);

        WebElement btnConsultar = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(XPATH_BOTON_CONSULTA)));
        btnConsultar.click();

        // Esperar resultados (monto y fecha)
        WebElement montoEl = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(XPATH_MONTO)));
        WebElement fechaEl = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(XPATH_FECHA_VENCIMIENTO)));

        ConsultaIceResult r = new ConsultaIceResult();
        r.monto = parseMonto(montoEl.getText());
        r.fechaVencimiento = parseFechaFlexible(fechaEl.getText());
        return r;
    }

    private long parseMonto(String txt) {
        if (txt == null) return 0L;

        String s = txt.trim();
        if (s.isEmpty()) return 0L;

        // 1) Si trae decimales tipo 8,585.00 -> nos quedamos con la parte entera antes del punto
        //    (y también sirve si viene con símbolo ₡ etc.)
        int dot = s.lastIndexOf('.');
        if (dot > -1) {
            s = s.substring(0, dot);
        } else {
            // Por si alguna vez viene con coma decimal: 8.585,00
            int comma = s.lastIndexOf(',');
            if (comma > -1 && (s.length() - comma - 1) == 2) {
                s = s.substring(0, comma);
            }
        }

        // 2) Quitar separadores de miles y cualquier cosa que no sea dígito
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

        // probamos formatos comunes
        String[] formatos = new String[] { "dd/MM/yyyy", "yyyy-MM-dd", "dd-MM-yyyy" };
        for (String f : formatos) {
            try {
                SimpleDateFormat df = new SimpleDateFormat(f);
                df.setLenient(false);
                return df.parse(s);
            } catch (Exception ignore) {}
        }
        return null;
    }

    // ===================== SOLO PARA VER EL PROCESO EN VIVO =====================
    @GetMapping("/interactuarPagina/{idLuz}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void interactuarPagina(@PathVariable("idLuz") Long idLuz) {
        WebDriver driver = null;
        try {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver(buildOptions(false)); // visible
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(12));

            consultarEnIce(driver, wait, idLuz.toString());

            // opcional: deja 2s para que lo veas en pantalla antes de cerrar
            try { Thread.sleep(2000); } catch (InterruptedException ignore) {}

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (driver != null) driver.quit();
        }
    }

    // ===================== PAGAR (GUARDA MONTO SI pagar=true) =====================
    @GetMapping("/pagar/{idLuz}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void pagar(@PathVariable("idLuz") Long idLuz) {
        WebDriver driver = null;

        try {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver(buildOptions(false)); // visible (si querés headless, poné true)
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(12));

            ConsultaIceResult r = consultarEnIce(driver, wait, idLuz.toString());

            Luz luz = new Luz();
            luz.setIdLuz(idLuz);
            luz = luzService.getLuz(luz);

            if (luz != null && Boolean.TRUE.equals(luz.getPagar()) && r.monto > 0) {
                Date fechaActual = new Date();
                luzService.agregarMonto(new MontosLuz(r.monto, luz, fechaActual));
            }

            // si querés también actualizar fechaVencimiento aquí:
            if (luz != null && r.fechaVencimiento != null) {
                luz.setFechaVencimiento(r.fechaVencimiento);
                luzService.save(luz);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (driver != null) driver.quit();
        }
    }

    // ===================== CONSULTAR MONTO (1 NIS) =====================
    public long consultarMonto(Long idLuz) {
        return consultarMonto(idLuz, true); // por defecto headless para que no abra ventanas
    }

    public long consultarMonto(Long idLuz, boolean headless) {
        WebDriver driver = null;
        try {
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver(buildOptions(headless));
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(12));

            ConsultaIceResult r = consultarEnIce(driver, wait, idLuz.toString());
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
    response.setHeader("Content-Disposition", "attachment; filename=\"Montos_luz.pdf\"");

    var luzList = luzService.getLuz(true);

    List<Luz> luzOrdenada = luzList.stream()
            .filter(luz -> Boolean.TRUE.equals(luz.getPagar()))
            .sorted(Comparator.comparing(this::obtenerClaveOrdenLuz, String.CASE_INSENSITIVE_ORDER))
            .collect(Collectors.toList());

    try (PdfWriter writer = new PdfWriter(response.getOutputStream());
         Document document = new Document(new com.itextpdf.kernel.pdf.PdfDocument(writer))) {

        document.add(new Paragraph("Listado de Luz").setBold().setFontSize(18));

        float[] columnWidths = {80F, 140F, 100F, 50F, 130F};
        Table table = new Table(columnWidths);
        table.addHeaderCell(new Cell().add(new Paragraph("ID")));
        table.addHeaderCell(new Cell().add(new Paragraph("Vencimiento(Año/Mes/dia)")));
        table.addHeaderCell(new Cell().add(new Paragraph("Apartamentos")));
        table.addHeaderCell(new Cell().add(new Paragraph("Pagar")));
        table.addHeaderCell(new Cell().add(new Paragraph("Monto")));

        DecimalFormat decimalFormat = new DecimalFormat("#,###");

        for (Luz luz : luzOrdenada) {
            table.addCell(new Cell().add(new Paragraph(String.valueOf(luz.getIdLuz()))));
            table.addCell(new Cell().add(new Paragraph(formatFecha(luz.getFechaVencimiento()))));
            table.addCell(new Cell().add(new Paragraph(obtenerDepartamentosOrdenadosTexto(luz))));
            table.addCell(new Cell().add(new Paragraph("Sí")));

            Long monto = consultarMonto(luz.getIdLuz(), true);
            if (monto != null && monto > 0) {
                table.addCell(new Cell().add(new Paragraph(decimalFormat.format(monto))));
            } else {
                table.addCell(new Cell().add(new Paragraph("Pago")));
            }
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
            driver = new ChromeDriver(buildOptions(true)); // headless recomendado para batch
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(12));

            List<Luz> luces = luzService.getLuz(true);

            for (Luz luz : luces) {
                if (!Boolean.TRUE.equals(luz.getPagar())) continue;

                try {
                    ConsultaIceResult r = consultarEnIce(driver, wait, luz.getIdLuz().toString());

                    if (r.fechaVencimiento != null) {
                        luz.setFechaVencimiento(r.fechaVencimiento);
                        luzService.save(luz);
                    }
                } catch (Exception ex) {
                    System.out.println("No se pudo consultar/leer fecha para NIS: " + luz.getIdLuz());
                }
            }

            return "redirect:/luz/listado";

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al actualizar fechas de vencimiento");
        } finally {
            if (driver != null) driver.quit();
        }
        
    }
    
    private String obtenerClaveOrdenDepartamento(Luz luz) {
    if (luz == null || luz.getIdApartamento() == null || luz.getIdApartamento().isEmpty()) {
        return "";
    }

            return luz.getIdApartamento().stream()
                    .filter(Objects::nonNull)
                    .map(a -> a.getDescripcionDepartamento() == null ? "" : a.getDescripcionDepartamento().trim())
                    .filter(s -> !s.isBlank())
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .findFirst()
                    .orElse("");
        }

        private String obtenerDepartamentosOrdenados(Luz luz) {
            if (luz == null || luz.getIdApartamento() == null || luz.getIdApartamento().isEmpty()) {
                return "";
            }

            return luz.getIdApartamento().stream()
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(
                            a -> a.getDescripcionDepartamento() == null ? "" : a.getDescripcionDepartamento().trim(),
                            String.CASE_INSENSITIVE_ORDER
                    ))
                    .map(apartamento -> {
                        String ubicacion = apartamento.getUbicacion() == null ? "" : apartamento.getUbicacion();
                        String descripcion = apartamento.getDescripcionDepartamento() == null ? "" : apartamento.getDescripcionDepartamento();
                        String nombreDepartamento = ubicacion + descripcion;

                        return nombreDepartamento.replaceAll("^.*?\\(", "")
                                .replace(")", "")
                                .replace(" ", "");
                    })
                    .collect(Collectors.joining("\n"));
        }
        
        private String normalizarDepartamento(Apartamento apartamento) {
            if (apartamento == null) return "";

            String ubicacion = apartamento.getUbicacion() == null ? "" : apartamento.getUbicacion();
            String descripcion = apartamento.getDescripcionDepartamento() == null ? "" : apartamento.getDescripcionDepartamento();

            String nombreDepartamento = ubicacion + descripcion;

            return nombreDepartamento
                    .replaceAll("^.*?\\(", "")
                    .replace(")", "")
                    .replace(" ", "")
                    .trim();
        }

        private List<String> obtenerDepartamentosOrdenadosLista(Luz luz) {
            if (luz == null || luz.getIdApartamento() == null) {
                return new ArrayList<>();
            }

            return luz.getIdApartamento().stream()
                    .filter(Objects::nonNull)
                    .map(this::normalizarDepartamento)
                    .filter(s -> !s.isBlank())
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .collect(Collectors.toList());
        }

        private String obtenerDepartamentosOrdenadosTexto(Luz luz) {
            return String.join("\n", obtenerDepartamentosOrdenadosLista(luz));
        }

        private String obtenerClaveOrdenLuz(Luz luz) {
            List<String> departamentos = obtenerDepartamentosOrdenadosLista(luz);
            return departamentos.isEmpty() ? "" : departamentos.get(0);
        }
}