/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package apartamentos.demo.dao;

import apartamentos.demo.domain.Luz;
import apartamentos.demo.domain.MontosLuz;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author gabriel badilla
 */
public interface MontosLuzDao extends JpaRepository <MontosLuz,Long>{
    
    public List<MontosLuz> findByIdLuz(Luz idLuz);
}
