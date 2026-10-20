/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apartamentos.demo.controller;

import apartamentos.demo.domain.Encargado;
import apartamentos.demo.service.EncargadoService;
import apartamentos.demo.service.Impl.FirebaseStorageServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@Slf4j
@RequestMapping("/encargado")
public class EncargadoController {
    
    @Autowired
    private EncargadoService encargadoService;

    @GetMapping("/listado")
    public String inicio(Model model) {
        var encargado = encargadoService.getEncargado(false);
        model.addAttribute("encargado", encargado);
        model.addAttribute("totalEncargados", encargado.size());
        return "encargado/listado";
    }
    

    @GetMapping("/nuevo")
    public String encargadoNuevo(Encargado encargado) {
        return "encargado/modifica";
    }

    @Autowired
    private FirebaseStorageServiceImpl firebaseStorageService;
    
    @PostMapping("/guardar")
    public String encargadoGuardar(Encargado encargado,
                                   @RequestParam("imagenFile") MultipartFile imagenFile) {        
        if (!imagenFile.isEmpty()) {
            encargadoService.save(encargado);
            encargado.setFotoCedula(
                   firebaseStorageService.cargaImagen(
                            imagenFile, 
                            "encargado", 
                            encargado.getIdEncargado()));
        }
        encargadoService.save(encargado);
        return "redirect:/encargado/listado";
    }

    @GetMapping("/eliminar/{idEncargado}")
    public String encargadoEliminar(Encargado encargado) {
        encargadoService.delete(encargado);
        return "redirect:/encargado/listado";
    }

    @GetMapping("/modificar/{idEncargado}")
    public String encargadoModificar(Encargado encargado, Model model) {
        encargado = encargadoService.getEncargado(encargado);
        model.addAttribute("encargado", encargado);
        return "encargado/modifica";
    }
}

