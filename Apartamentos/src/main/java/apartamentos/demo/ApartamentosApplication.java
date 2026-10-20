package apartamentos.demo;

import java.io.IOException;
import java.net.URISyntaxException;
import javax.swing.JOptionPane;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"apartamentos.demo"})
public class ApartamentosApplication {

    static {
        if(java.awt.Desktop.isDesktopSupported()) {
            java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
            if(desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
                try {
                    java.net.URI uri = new java.net.URI("http://localhost:8080/index");
                    desktop.browse(uri);
                    
                    // Crear el cuadro de diálogo en un hilo separado
                    Thread dialogThread = new Thread(() -> {
                        JOptionPane.showMessageDialog(null,
                                "El navegador se ha abierto. Cierra este cuadro de diálogo para detener la aplicación.",
                                "Aplicación en ejecución",
                                JOptionPane.INFORMATION_MESSAGE);

                        // Cuando el cuadro de diálogo se cierra, detener el programa
                        System.exit(0);
                    });
                    dialogThread.setDaemon(false); // Hacer que el hilo no se detenga automáticamente
                    dialogThread.start();

                } catch(URISyntaxException | IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
    public static void main(String[] args) {
        SpringApplication.run(ApartamentosApplication.class, args);
       
    }

}
