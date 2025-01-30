package com.hotel.appHotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hotel.appHotel.model.HabitacionesTipos;

@Repository
public interface HabitacionesTiposRepository extends JpaRepository<HabitacionesTipos, Long> {
}
