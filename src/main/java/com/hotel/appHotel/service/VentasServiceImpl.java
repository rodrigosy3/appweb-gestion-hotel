package com.hotel.appHotel.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.appHotel.model.Ventas;
import com.hotel.appHotel.repository.VentasRepository;

@Service
@Transactional
public class VentasServiceImpl implements VentasService {

    @Autowired
    private VentasRepository repositorio;

    @Override
    public List<Ventas> getVentas() {
        return repositorio.findAll();
    }

    @Override
    @Transactional
    public Ventas createVenta(Ventas venta) {
        return repositorio.save(venta);
    }

    @Override
    public Ventas getVentaById(Long id) {
        return repositorio.findById(id).get();
    }

    @Override
    @Transactional
    public Ventas updateVenta(Ventas venta) {
        return repositorio.save(venta);
    }

    @Override
    @Transactional
    public void deleteVenta(Long id) {
        repositorio.deleteById(id);
    }

    @Override
    public Page<Ventas> getAllByPage(Pageable pageable) {
        return repositorio.findAll(pageable);
    }

    @Override
    public Page<Ventas> getVentasNoEliminadas(Pageable pageable) {
        return repositorio.findByEliminadoFalseDesc(pageable);
    }
}
