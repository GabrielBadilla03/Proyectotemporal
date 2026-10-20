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
public interface AguaService {
    
    public List<Agua> getAgua(boolean activos);
    
    public Agua getAgua(Agua agua);
    
    public void save(Agua agua);
    
    public void delete(Agua agua);
    
    public List<Agua> findByidAgua(Long idAgua);
    
    public void agregarMonto(MontosAgua monto);
}
