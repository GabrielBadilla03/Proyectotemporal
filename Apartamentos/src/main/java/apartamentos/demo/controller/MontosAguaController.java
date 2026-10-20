/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apartamentos.demo.controller;

import apartamentos.demo.domain.Agua;
import apartamentos.demo.domain.MontosAgua;
import apartamentos.demo.service.MontosAguaService;
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
@RequestMapping("/montosAgua")
public class MontosAguaController {

    @Autowired
    private MontosAguaService montosAguaService;

    @GetMapping("/listado")
    public String inicio(Model model) {
        var montosAgua = montosAguaService.getMontosAgua(true);
        model.addAttribute("montosAgua", montosAgua);
        model.addAttribute("totalMontosAgua", montosAgua.size());
        return "montosAgua/listado";
    }

    @GetMapping("/nuevo")
    public String montosAguaNuevo(MontosAgua montosAgua) {
        return "montosAgua/modifica";
    }

    @PostMapping("/guardar")
    public String montosAguaGuardar(MontosAgua montosAgua) {        
        montosAguaService.save(montosAgua);
        return "redirect:/montosAgua/listado";
    }

    @GetMapping("/eliminar/{idMonto}")
    public String montosAguaEliminar(MontosAgua montosAgua) {
        montosAguaService.delete(montosAgua);
        return "redirect:/montosAgua/listado";
    }
    
    @PostMapping("/query")
    public String consultaQuery1(@RequestParam(value = "idAgua") Long idAgua, Model model) {
        var montosAgua = montosAguaService.findByidAgua(idAgua);
        model.addAttribute("montosAgua", montosAgua);
        model.addAttribute("idAgua", idAgua);
        return "montosAgua/listado";
    }
}
