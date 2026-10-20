/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package apartamentos.demo.dao;

import apartamentos.demo.domain.Rol;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RolDao extends JpaRepository<Rol, Long> {
     
    Rol findByIdRol(long idRol);

    Rol findByNombre(String nombre);
    
    Rol findByNombreAndIdUsuario(String nombre, Long idUsuario);
}
