/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apartamentos.demo.service.Impl;

import apartamentos.demo.dao.EncargadoDao;
import apartamentos.demo.domain.Encargado;
import apartamentos.demo.service.EncargadoService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author gabriel badilla
 */
@Service
public class EncargadoServiceImpl implements EncargadoService{
    @Autowired
    private EncargadoDao encargadoDao;


    @Override
    @Transactional(readOnly = true)
    public List<Encargado> getEncargado(boolean activos) {
        return encargadoDao.findAll();

    }

    @Override
    @Transactional(readOnly = true)
    public Encargado getEncargado(Encargado encargado) {
        return encargadoDao.findById(encargado.getIdEncargado()).orElse(null);
    }

    @Override
    @Transactional
    public void save(Encargado encargado) {
        encargadoDao.save(encargado);
    }

    @Override
    @Transactional
    public void delete(Encargado encargado) {
        encargadoDao.delete(encargado);
    }
}
