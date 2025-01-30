package com.hotel.appHotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hotel.appHotel.model.HabitacionesCaracteristicas;

@Repository
public interface HabitacionesCaracteristicasRepository extends JpaRepository<HabitacionesCaracteristicas, Long> {
}
