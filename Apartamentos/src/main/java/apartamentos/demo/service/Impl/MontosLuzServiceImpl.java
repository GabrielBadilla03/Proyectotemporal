/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apartamentos.demo.service.Impl;

import apartamentos.demo.dao.LuzDao;
import apartamentos.demo.dao.MontosLuzDao;
import apartamentos.demo.domain.Luz;
import apartamentos.demo.domain.MontosLuz;
import apartamentos.demo.service.MontosLuzService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author gabriel badilla
 */
@Service
public class MontosLuzServiceImpl implements MontosLuzService{
    @Autowired
    private MontosLuzDao montosLuzDao;
    
    @Autowired 
    private LuzDao luzDao;


    @Override
    @Transactional(readOnly = true)
    public List<MontosLuz> getMontosLuz(boolean activos) {
        return montosLuzDao.findAll();

    }

    @Override
    @Transactional(readOnly = true)
    public MontosLuz getMontosLuz(MontosLuz montosLuz) {
        return montosLuzDao.findById(montosLuz.getIdMonto()).orElse(null);
    }

    @Override
    @Transactional
    public void save(MontosLuz montosLuz) {
        montosLuzDao.save(montosLuz);
    }

    @Override
    @Transactional
    public void delete(MontosLuz montosLuz) {
        montosLuzDao.delete(montosLuz);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MontosLuz> findByidLuz(Long idLuz) {
        List<Luz> luces = luzDao.findByIdLuz(idLuz);
        Luz luz = luces.get(0);
        return montosLuzDao.findByIdLuz(luz);
    }
}
