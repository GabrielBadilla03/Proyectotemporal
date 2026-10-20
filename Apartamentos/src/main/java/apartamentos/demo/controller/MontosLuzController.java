/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apartamentos.demo.controller;

import apartamentos.demo.domain.MontosLuz;
import apartamentos.demo.service.MontosLuzService;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
@RequestMapping("/montosLuz")
public class MontosLuzController {
    @Autowired
    private MontosLuzService montosLuzService;

    @GetMapping("/listado")
    public String inicio(Model model) {
        var montosLuz = montosLuzService.getMontosLuz(true);
        model.addAttribute("montosLuz", montosLuz);
        model.addAttribute("totalMontosLuz", montosLuz.size());
        return "montosLuz/listado";
    }
    
    @GetMapping("/nuevo")
    public String montosLuzNuevo(MontosLuz montosLuz) {
        return "montosLuz/modifica";
    }
    
    @PostMapping("/guardar")
    public String montosLuzGuardar(MontosLuz montosLuz) { 
        Date fechaActual = new Date();
        montosLuz.setFechaRegistro(fechaActual);
        montosLuzService.save(montosLuz);
        return "redirect:/montosLuz/listado";
    }

    @GetMapping("/eliminar/{idMonto}")
    public String montosLuzEliminar(MontosLuz montosLuz) {
        montosLuzService.delete(montosLuz);
        return "redirect:/montosLuz/listado";
    }

    @PostMapping("/query")
    public String consultaQuery1(@RequestParam(value = "idLuz") Long idLuz, Model model) {
        var montosLuz = montosLuzService.findByidLuz(idLuz);
        model.addAttribute("montosLuz", montosLuz);
        model.addAttribute("idLuz", idLuz);
        return "montosLuz/listado";
    }
    
}
