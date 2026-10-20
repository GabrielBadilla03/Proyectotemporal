/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package apartamentos.demo.service;

import apartamentos.demo.domain.Apartamento;
import java.util.List;

/**
 *
 * @author gabriel badilla
 */
public interface ApartamentoService {
    
    public List<Apartamento> getApartamento(boolean activos);
    
    public Apartamento getApartamento(Apartamento apartamento);
    
    public void save(Apartamento apartamento);
    
    public void delete(Apartamento apartamento);
}
