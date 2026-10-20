/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apartamentos.demo.controller;

import apartamentos.demo.domain.Apartamento;
import apartamentos.demo.service.AguaService;
import apartamentos.demo.service.ApartamentoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequestMapping("/apartamento")
public class ApartamentoController {
    
    @Autowired
    private AguaService aguaService;
    
    @Autowired
    private ApartamentoService apartamentoService;

    @GetMapping("/listado")
    public String inicio(Model model) {
        var apartamento = apartamentoService.getApartamento(false);
        model.addAttribute("apartamento", apartamento);
        model.addAttribute("totalApartamentos", apartamento.size());
        return "apartamento/listado";
    }
    

    
    @GetMapping("/nuevo")
    public String apartamentoNuevo(Apartamento apartamento) {
        return "apartamento/modifica";
    }
    
    @PostMapping("/guardar")
    public String apartamentoGuardar(Apartamento apartamento) {        
        apartamentoService.save(apartamento);
        return "redirect:/apartamento/listado";
    }

    @GetMapping("/eliminar/{idApartamento}")
    public String apartamentoEliminar(Apartamento apartamento) {
        apartamentoService.delete(apartamento);
        return "redirect:/apartamento/listado";
    }

    @GetMapping("/modificar/{idApartamento}")
    public String apartamentoModificar(Apartamento apartamento, Model model) {
        apartamento = apartamentoService.getApartamento(apartamento);
        model.addAttribute("apartamento", apartamento);
        return "apartamento/modifica";
    }
}
