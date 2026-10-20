/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package apartamentos.demo.dao;

import apartamentos.demo.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UsuarioDao extends JpaRepository<Usuario, Long>{
   
    Usuario findByUsername(String username);

    Usuario findByUsernameAndPassword(String username, String Password);
}
