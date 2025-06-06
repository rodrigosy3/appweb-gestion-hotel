package com.hotel.appHotel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hotel.appHotel.model.Tickets;

public interface TicketsRepository extends JpaRepository<Tickets, Long> {

    @Query("SELECT MAX(t.numeroTicket) FROM Tickets t")
    Optional<Integer> getUltimoNumeroTicket();

    @Query("SELECT t FROM Tickets t WHERE t.venta.id_venta = :idVenta")
    Optional<Tickets> findByIdVenta(@Param("idVenta") Long idVenta);

    Optional<Tickets> findByNumeroTicket(Integer numeroTicket);

    @Query("SELECT t FROM Tickets t WHERE t.venta.id_venta = :idVenta ORDER BY t.idTicket DESC")
    List<Tickets> findUltimoByVentaId(@Param("idVenta") Long idVenta);

}