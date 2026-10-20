/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apartamentos.demo.service.Impl;

import apartamentos.demo.dao.RolDao;
import apartamentos.demo.dao.UsuarioDao;
import apartamentos.demo.domain.Rol;
import apartamentos.demo.domain.Usuario;
import apartamentos.demo.service.UsuarioService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioServiceImpl implements UsuarioService{
    
    
    @Autowired
    private UsuarioDao usuarioDao;
    
    @Autowired
    private RolDao rolDao;


    @Override
    public List<Usuario> getUsuarios() {
        return usuarioDao.findAll();    
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario getUsuario(Usuario usuario) {
        return usuarioDao.findById(usuario.getIdUsuario()).orElse(null);  
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario getUsuarioPorUsername(String username) {
        return usuarioDao.findByUsername(username);
    }

    @Override
    public Usuario getUsuarioPorUsernameYPassword(String username, String password) {
        return usuarioDao.findByUsernameAndPassword(username, password);
    }

    @Override
    public void save(Usuario usuario, boolean crearRolUser) {
        usuario=usuarioDao.save(usuario);
        if (crearRolUser) {  
            Rol rol = new Rol();
            rol.setNombre("ROLE_USER");
            rol.setIdUsuario(usuario.getIdUsuario());
            rolDao.save(rol);
        }
    }
    
    @Override
    public void delete(Usuario usuario) {
        usuarioDao.delete(usuario);
    }
    
    @Override
    public Usuario getUsuarioById(Long idUsuario) {
        // Implement the logic to retrieve a user by ID from your data source
        return usuarioDao.findById(idUsuario).orElse(null); // Example if using Spring Data JPA
    }
    
}
