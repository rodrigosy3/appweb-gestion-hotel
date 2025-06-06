package com.hotel.appHotel.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.appHotel.model.Cajas;
import com.hotel.appHotel.repository.CajasRepository;

@Service
@Transactional
public class CajasServiceImpl implements CajasService {

    @Autowired
    private CajasRepository repositorio;

    @Override
    public List<Cajas> getCajas() {
        return repositorio.findAll();
    }

    @Override
    @Transactional
    public Cajas createCaja(Cajas caja) {
        return repositorio.save(caja);
    }

    @Override
    public Cajas getCajaById(Long id) {
        return repositorio.findById(id).get();
    }

    @Override
    @Transactional
    public Cajas updateCaja(Cajas caja) {
        return repositorio.save(caja);
    }

    @Override
    @Transactional
    public void deleteCaja(Long id) {
        repositorio.deleteById(id);
    }

    @Override
    public List<Cajas> getCajasDelDia() {
        String hoy = LocalDate.now().toString();
        return repositorio.obtenerCajasDelDia(hoy);
    }

    @Override
    public List<Cajas> getCajasPorVenta(Long idVenta) {
        return repositorio.obtenerCajasPorVenta(idVenta);
    }
    
    @Override
    public Page<Cajas> getCajasNoEliminadas(Pageable pageable) {
        return repositorio.findByEliminadoFalseDesc(pageable);
    }
    
}
