package com.hotel.appHotel.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hotel.appHotel.model.Cajas;

@Repository
public interface CajasRepository extends JpaRepository<Cajas, Long> {

    @Query("SELECT c FROM Cajas c WHERE c.eliminado = false AND date(c.fechaRegistro) = date(:fecha)")
    List<Cajas> obtenerCajasDelDia(@Param("fecha") String fecha);

    @Query("SELECT c FROM Cajas c WHERE c.eliminado = false AND c.venta.id_venta = :idVenta")
    List<Cajas> obtenerCajasPorVenta(@Param("idVenta") Long idVenta);

    @Query("SELECT c FROM Cajas c WHERE c.eliminado = false ORDER BY c.idCaja DESC")
    Page<Cajas> findByEliminadoFalseDesc(Pageable pageable);

}
