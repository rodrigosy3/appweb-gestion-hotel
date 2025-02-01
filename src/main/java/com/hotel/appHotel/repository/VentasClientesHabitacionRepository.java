package com.hotel.appHotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hotel.appHotel.model.VentasClientesHabitacion;

@Repository
public interface VentasClientesHabitacionRepository extends JpaRepository<VentasClientesHabitacion, Long> {
}
