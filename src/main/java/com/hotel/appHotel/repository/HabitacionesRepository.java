package com.hotel.appHotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hotel.appHotel.model.Habitaciones;

@Repository
public interface HabitacionesRepository extends JpaRepository<Habitaciones, Long> {
}
