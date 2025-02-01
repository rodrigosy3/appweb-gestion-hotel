package com.hotel.appHotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hotel.appHotel.model.Ventas;

@Repository
public interface VentasRepository extends JpaRepository<Ventas, Long> {
}
