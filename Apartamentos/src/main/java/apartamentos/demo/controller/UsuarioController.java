/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apartamentos.demo.controller;


import apartamentos.demo.domain.Rol;
import apartamentos.demo.domain.Usuario;
import apartamentos.demo.service.FirebaseStorageService;
import apartamentos.demo.service.RolService;
import apartamentos.demo.service.UsuarioService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


@Controller
@RequestMapping("/usuario")
public class UsuarioController {
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private RolService rolService;
    
    @GetMapping("/listado")
    public String listado(Model model) {
        var usuarios = usuarioService.getUsuarios();
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("totalUsuarios", usuarios.size());
        return "usuario/listado";
    }

    @GetMapping("/nuevo")
    public String usuarioNuevo(Usuario usuario) {
        return "usuario/modifica";
    }

    @Autowired
    private FirebaseStorageService firebaseStorageService;

   @PostMapping("/guardar")
    public String usuarioGuardar(Usuario usuario,
            @RequestParam("imagenFile") MultipartFile imagenFile, String rol, String noRol) {
        if (!imagenFile.isEmpty()) {
            usuarioService.save(usuario,false);
            usuario.setRutaImagen(
                    firebaseStorageService.cargaImagen(
                            imagenFile,
                            "usuario",
                            usuario.getIdUsuario()));
        }
        String contraseñaEncriptada = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(contraseñaEncriptada);
        
        if(noRol != null && !noRol.isEmpty()){
            Rol nuevoRol = new Rol();
            nuevoRol = rolService.getRolPorNombreYIdUsuario(noRol, usuario.getIdUsuario());
            rolService.delete(nuevoRol);
            
        }
        
        if(rol != null && !rol.isEmpty()){
            Rol nuevoRol = new Rol();
            nuevoRol.setNombre(rol);
            nuevoRol.setIdUsuario(usuario.getIdUsuario());
            rolService.save2(nuevoRol);
            
        }
        
        // Crear la lista para los roles asociados al usuario
        List<Rol> rolesUsuario = new ArrayList<>();

        // Obtener todos los roles y filtrar los que coincidan con el idUsuario
        var rolesGenerales = rolService.getRol();
        for (Rol rolExistente : rolesGenerales) {
            if (rolExistente.getIdUsuario() != null && 
                rolExistente.getIdUsuario().equals(usuario.getIdUsuario())) {
                rolesUsuario.add(rolExistente);
            }
        }

        usuario.setRoles(rolesUsuario);  
        
        usuarioService.save(usuario, false);
        return "redirect:/usuario/listado";
    }
    
    @PostMapping("/guardar2")
    public String usuarioGuardar2(Usuario usuario,
            @RequestParam("imagenFile") MultipartFile imagenFile) {
        if (!imagenFile.isEmpty()) {
            usuarioService.save(usuario,false);
            usuario.setRutaImagen(
                    firebaseStorageService.cargaImagen(
                            imagenFile,
                            "usuario",
                            usuario.getIdUsuario()));
        }
        String contraseñaEncriptada = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(contraseñaEncriptada);
        
        usuarioService.save(usuario,true);
        return "index";
    }
    
    @PostMapping("/guardar3")
    public String usuarioGuardar3(Usuario usuario,
            @RequestParam("imagenFile") MultipartFile imagenFile) {
        if (!imagenFile.isEmpty()) {
            usuarioService.save(usuario,false);
            usuario.setRutaImagen(
                    firebaseStorageService.cargaImagen(
                            imagenFile,
                            "usuario",
                            usuario.getIdUsuario()));
        }
        String contraseñaEncriptada = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(contraseñaEncriptada);
        
        usuarioService.save(usuario,true);
        return "redirect:/usuario/listado";
    }

    @GetMapping("/eliminar/{idUsuario}")
    public String usuarioEliminar(Usuario usuario) {
        usuarioService.delete(usuario);
        return "redirect:/usuario/listado";
    }

    @GetMapping("/modificar/{idUsuario}")
    public String usuarioModificar(Usuario usuario, Model model) {
        usuario = usuarioService.getUsuario(usuario);
        model.addAttribute("usuario", usuario);
        
        // Obtener todos los roles disponibles
        var roles = rolService.getRol();

        // Crear conjuntos para separar roles
        Set<String> rolesAsociados = new HashSet<>();
        Set<String> rolesAsociados2 = new HashSet<>();
        Set<String> rolesNoAsociados = new HashSet<>();
        rolesNoAsociados.add("");
        rolesAsociados2.add("");

        // Obtener los nombres de los roles asociados al usuario
        if (usuario.getRoles() != null) {
            for (Rol rol : usuario.getRoles()) {
                rolesAsociados.add(rol.getNombre());
                rolesAsociados2.add(rol.getNombre());
            }
        }

        // Identificar roles no asociados
        for (Rol rol : roles) {
            if (!rolesAsociados.contains(rol.getNombre())) {
                rolesNoAsociados.add(rol.getNombre());
            }
        }
        
        // Agregar atributos al modelo
        model.addAttribute("rolesAsociados", rolesAsociados);
        model.addAttribute("rolesNoAsociados", rolesNoAsociados);
        model.addAttribute("rolesAsociados2", rolesAsociados2);
        
        return "usuario/modifica";
    }
}
