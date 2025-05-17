package com.hotel.appHotel.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hotel.appHotel.model.Ventas;

public interface VentasService {
    public List<Ventas> getVentas();

    public Ventas createVenta(Ventas venta);

    public Ventas getVentaById(Long id);

    public Ventas updateVenta(Ventas venta);

    public void deleteVenta(Long id);

    Page<Ventas> getAllByPage(Pageable pageable);

    Page<Ventas> getVentasNoEliminadas(Pageable pageable);
}
