/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apartamentos.demo.controller;

import apartamentos.demo.domain.Rol;
import apartamentos.demo.service.RolService;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author gabriel badilla
 */
@Controller
@RequestMapping("/rol")
public class RolController {
    
    @Autowired
    private RolService rolService;
    

    @GetMapping("/nuevo")
    public String rolNuevo(Rol rol) {
        return "rol/modifica";
    }

    @GetMapping("/guardar")
    public String guardarRol(@RequestParam String nombreRol, @RequestParam Long idUsuario) {
        Rol rol = new Rol();
        rol.setNombre(nombreRol);
        rol.setIdUsuario(idUsuario);
        rolService.save(rol);
        
        return "redirect:usuario/listado";
    }
    
    @GetMapping("/guardar2")
    public String guardarRol2(@RequestParam("nombreRol") String nombreRol, @RequestParam("idUsuario") String idUsuario) {

        if (nombreRol == null || idUsuario == null) {
            return "redirect:/error"; 
        }

        // Crear y guardar el rol
        Rol rol = new Rol();
        rol.setNombre(nombreRol);
        long id = Long.parseLong(idUsuario);
        rol.setIdUsuario(id);
        rolService.save(rol);

        // Redirigir al listado de usuarios después de guardar el rol
        return "redirect:/usuario/listado";
    }

    @GetMapping("/eliminar")
    public String rolEliminar(@RequestParam("nombre") String nombre, @RequestParam("idUsuario") String idUsuario) {
        // Validación de entrada
        if (nombre == null || idUsuario == null) {
            return "redirect:/error"; // Redirecciona a una página de error
        }
        Rol rol = new Rol();
        Long id = Long.parseLong(idUsuario);
        rol = rolService.getRolPorNombreYIdUsuario(nombre, id);

        rolService.delete(rol);
        return "redirect:usuario/listado";
    }
}