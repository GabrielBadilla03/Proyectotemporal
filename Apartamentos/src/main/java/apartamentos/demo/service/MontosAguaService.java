/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package apartamentos.demo.service;

import apartamentos.demo.domain.Agua;
import apartamentos.demo.domain.MontosAgua;
import java.util.List;

/**
 *
 * @author gabriel badilla
 */
public interface MontosAguaService {
    
    public List<MontosAgua> getMontosAgua(boolean activos);
    
    public MontosAgua getMontosAgua(MontosAgua montosAgua);
    
    public void save(MontosAgua montosAgua);
    
    public void delete(MontosAgua montosAgua);
    
    public List<MontosAgua> findByidAgua(Long idAgua);
}

