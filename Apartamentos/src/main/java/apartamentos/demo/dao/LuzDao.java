/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package apartamentos.demo.dao;

import apartamentos.demo.domain.Luz;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author gabriel badilla
 */
public interface LuzDao extends JpaRepository <Luz,Long>{
    
    List<Luz> findByIdLuz(Long idLuz);
    
}
