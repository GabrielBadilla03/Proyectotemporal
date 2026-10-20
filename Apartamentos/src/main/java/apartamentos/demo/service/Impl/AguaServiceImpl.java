/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apartamentos.demo.service.Impl;

import apartamentos.demo.dao.AguaDao;
import apartamentos.demo.dao.MontosAguaDao;
import apartamentos.demo.domain.Agua;
import apartamentos.demo.domain.MontosAgua;
import apartamentos.demo.service.AguaService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author gabriel badilla
 */
@Service
public class AguaServiceImpl implements AguaService{
    @Autowired
    private AguaDao aguaDao;

    @Autowired
    private MontosAguaDao montosAguaDao;

    @Override
    @Transactional(readOnly = true)
    public List<Agua> getAgua(boolean activos) {
        return aguaDao.findAll();

    }

    @Override
    @Transactional(readOnly = true)
    public Agua getAgua(Agua agua) {
        return aguaDao.findById(agua.getIdAgua()).orElse(null);
    }

    @Override
    @Transactional
    public void save(Agua agua) {
        aguaDao.save(agua);
    }

    @Override
    @Transactional
    public void delete(Agua agua) {
        aguaDao.delete(agua);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Agua> findByidAgua(Long idAgua) {
        return aguaDao.findByIdAgua(idAgua);
    }
    
    @Override
    @Transactional
    public void agregarMonto(MontosAgua monto) {
        montosAguaDao.save(monto);
    }
}

