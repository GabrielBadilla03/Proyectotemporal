/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package apartamentos.demo.service;

import apartamentos.demo.domain.Rol;
import java.util.List;

/**
 *
 * @author gabriel badilla
 */
public interface RolService {

    public List<Rol> getRol();
    
    public Rol getRol(Rol rol);
    
    public Rol getRolPorNombre(String nombre);
    
    public Rol getRolPorIdRol(long id);

    public Rol getRolPorNombreYIdUsuario(String nombre, long idUsuario);    

    public void save(Rol rol);
    
    public void delete(Rol rol); 
    
    public void save2(Rol rol);
}
