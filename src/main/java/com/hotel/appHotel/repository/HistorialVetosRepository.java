package com.hotel.appHotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hotel.appHotel.model.HistorialVetos;

@Repository
public interface HistorialVetosRepository extends JpaRepository<HistorialVetos, Long> {

}
