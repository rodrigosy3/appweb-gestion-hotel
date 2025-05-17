package com.hotel.appHotel.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hotel.appHotel.model.Ventas;

@Repository
public interface VentasRepository extends JpaRepository<Ventas, Long> {

    @Query("SELECT v FROM Ventas v WHERE v.eliminado = false ORDER BY v.id_venta DESC")
    Page<Ventas> findByEliminadoFalseDesc(Pageable pageable);

}
