/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package apartamentos.demo.service;

import apartamentos.demo.domain.MontosLuz;
import java.util.List;

/**
 *
 * @author gabriel badilla
 */
public interface MontosLuzService {
    
    public List<MontosLuz> getMontosLuz(boolean activos);
    
    public MontosLuz getMontosLuz(MontosLuz montosLuz);
    
    public void save(MontosLuz montosLuz);
    
    public void delete(MontosLuz montosLuz);
    
    public List<MontosLuz> findByidLuz(Long idLuz);
    
}
