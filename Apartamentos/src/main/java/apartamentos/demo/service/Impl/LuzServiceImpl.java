/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apartamentos.demo.service.Impl;

import apartamentos.demo.dao.LuzDao;
import apartamentos.demo.dao.MontosLuzDao;
import apartamentos.demo.domain.Luz;
import apartamentos.demo.domain.MontosLuz;
import apartamentos.demo.service.LuzService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author gabriel badilla
 */
@Service
public class LuzServiceImpl implements LuzService{
    @Autowired
    private LuzDao luzDao;

    @Autowired
    private MontosLuzDao MontosLuzDao;

    @Override
    @Transactional(readOnly = true)
    public List<Luz> getLuz(boolean activos) {
        return luzDao.findAll();

    }

    @Override
    @Transactional(readOnly = true)
    public Luz getLuz(Luz luz) {
        return luzDao.findById(luz.getIdLuz()).orElse(null);
    }

    @Override
    @Transactional
    public void save(Luz luz) {
        luzDao.save(luz);
    }

    @Override
    @Transactional
    public void delete(Luz luz) {
        luzDao.delete(luz);
    }
    
    @Override
    @Transactional
    public void agregarMonto(MontosLuz monto) {
        MontosLuzDao.save(monto);
    }
}
