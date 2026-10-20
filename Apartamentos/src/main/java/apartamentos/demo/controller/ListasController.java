/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apartamentos.demo.controller;

import apartamentos.demo.service.AguaService;
import apartamentos.demo.service.ApartamentoService;
import apartamentos.demo.service.EncargadoService;
import apartamentos.demo.service.LuzService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@Slf4j
@RequestMapping("")
public class ListasController {
    
    @Autowired
    private ApartamentoService apartamentoService;
    
    @Autowired
    private LuzService luzService;
    
    @Autowired
    private AguaService aguaService;
    
    @Autowired
    private EncargadoService encargadoService;
    
    @GetMapping("/index")
    public String inicio(Model model) {
        
        var apartamento = apartamentoService.getApartamento(false);
        model.addAttribute("apartamento", apartamento);
        
        var agua = aguaService.getAgua(false);
        model.addAttribute("agua", agua);
        
        var encargado = encargadoService.getEncargado(false);
        model.addAttribute("encargado", encargado);
        
        var luz = luzService.getLuz(true);
        model.addAttribute("luz", luz);
        
        return "index";
    }
    
    @PostMapping("/guardar")
    public String luzGuardar() {        
        return "redirect:/luz/listado";
    }
}
