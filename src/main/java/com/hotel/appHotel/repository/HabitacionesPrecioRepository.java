package com.hotel.appHotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hotel.appHotel.model.HabitacionesPrecio;

@Repository
public interface HabitacionesPrecioRepository extends JpaRepository<HabitacionesPrecio, Long> {
}
