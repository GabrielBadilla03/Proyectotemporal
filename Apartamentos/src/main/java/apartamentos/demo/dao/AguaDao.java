/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package apartamentos.demo.dao;

import apartamentos.demo.domain.Agua;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AguaDao extends JpaRepository <Agua,Long>{
    
    List<Agua> findByIdAgua(Long idAgua);
    
}
