/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package apartamentos.demo.service;

import apartamentos.demo.domain.Encargado;
import java.util.List;

/**
 *
 * @author gabriel badilla
 */
public interface EncargadoService {
    
    public List<Encargado> getEncargado(boolean activos);
    
    public Encargado getEncargado(Encargado encargado);
    
    public void save(Encargado encargado);
    
    public void delete(Encargado encargado);
}
