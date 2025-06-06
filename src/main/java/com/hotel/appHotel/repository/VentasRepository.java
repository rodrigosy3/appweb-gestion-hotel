package com.hotel.appHotel.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hotel.appHotel.model.Ventas;

@Repository
public interface VentasRepository extends JpaRepository<Ventas, Long> {

    @Query("SELECT v FROM Ventas v WHERE v.eliminado = false ORDER BY v.id_venta DESC")
    Page<Ventas> findByEliminadoFalseDesc(Pageable pageable);

    @Query("SELECT v FROM Ventas v WHERE v.eliminado = false AND SUBSTRING(v.fecha_entrada, 1, 10) <= :fecha AND SUBSTRING(v.fecha_salida, 1, 10) >= :fecha ORDER BY v.id_venta DESC")
    List<Ventas> findVentasActivasPorFecha(@Param("fecha") String fecha);

}
