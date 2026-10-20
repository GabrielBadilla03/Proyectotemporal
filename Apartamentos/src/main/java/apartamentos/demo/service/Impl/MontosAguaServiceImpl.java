/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apartamentos.demo.service.Impl;

import apartamentos.demo.dao.AguaDao;
import apartamentos.demo.dao.MontosAguaDao;
import apartamentos.demo.domain.Agua;
import apartamentos.demo.domain.MontosAgua;
import apartamentos.demo.service.MontosAguaService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author gabriel badilla
 */
@Service
public class MontosAguaServiceImpl implements MontosAguaService{
    @Autowired
    private MontosAguaDao montosAguaDao;

    @Autowired
    private AguaDao aguaDao;

    @Override
    @Transactional(readOnly = true)
    public List<MontosAgua> getMontosAgua(boolean activos) {
        return montosAguaDao.findAll();

    }

    @Override
    @Transactional(readOnly = true)
    public MontosAgua getMontosAgua(MontosAgua montosAgua) {
        return montosAguaDao.findById(montosAgua.getIdMonto()).orElse(null);
    }

    @Override
    @Transactional
    public void save(MontosAgua montosAgua) {
        montosAguaDao.save(montosAgua);
    }

    @Override
    @Transactional
    public void delete(MontosAgua montosAgua) {
        montosAguaDao.delete(montosAgua);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MontosAgua> findByidAgua(Long idAgua) {
        List<Agua> aguas = aguaDao.findByIdAgua(idAgua);
        Agua agua = aguas.get(0);
        return montosAguaDao.findByidAgua(agua);
    }
}
