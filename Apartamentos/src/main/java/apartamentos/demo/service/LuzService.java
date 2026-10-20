/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package apartamentos.demo.service;

import apartamentos.demo.domain.Luz;
import apartamentos.demo.domain.MontosLuz;
import java.util.List;

/**
 *
 * @author gabriel badilla
 */
public interface LuzService {
    
    public List<Luz> getLuz(boolean activos);
    
    public Luz getLuz(Luz luz);
    
    public void save(Luz luz);
    
    public void delete(Luz luz);
    
    public void agregarMonto(MontosLuz monto);

}
