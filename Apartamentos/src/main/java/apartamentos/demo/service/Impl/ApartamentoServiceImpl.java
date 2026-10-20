/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apartamentos.demo.service.Impl;

import apartamentos.demo.dao.ApartamentoDao;
import apartamentos.demo.domain.Apartamento;
import apartamentos.demo.service.ApartamentoService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author gabriel badilla
 */
@Service
public class ApartamentoServiceImpl implements ApartamentoService{
    @Autowired
    private ApartamentoDao apartamentoDao;

    @Override
    @Transactional(readOnly = true)
    public List<Apartamento> getApartamento(boolean activos) {
        return apartamentoDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Apartamento getApartamento(Apartamento apartamento) {
        return apartamentoDao.findById(apartamento.getIdApartamento()).orElse(null);
    }

    @Override
    @Transactional
    public void save(Apartamento apartamento) {
        apartamentoDao.save(apartamento);
    }

    @Override
    @Transactional
    public void delete(Apartamento apartamento) {
        apartamentoDao.delete(apartamento);
    }
}
