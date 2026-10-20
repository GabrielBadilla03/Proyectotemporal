/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package apartamentos.demo.service;

import apartamentos.demo.domain.Rol;
import apartamentos.demo.domain.Usuario;
import java.util.List;

/**
 *
 * @author franv
 */
public interface UsuarioService {
    
    public List<Usuario> getUsuarios();
    
    public Usuario getUsuario(Usuario usuario);
    
    public Usuario getUsuarioPorUsername(String username);

    public Usuario getUsuarioPorUsernameYPassword(String username, String password);
       
    public void save(Usuario usuario,boolean crearRolUser);
    
    public void delete(Usuario usuario);
    
    public Usuario getUsuarioById(Long idUsuario);
}
