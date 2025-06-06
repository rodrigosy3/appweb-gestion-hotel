package com.hotel.appHotel.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hotel.appHotel.model.Tickets;
import com.hotel.appHotel.model.Ventas;
import com.hotel.appHotel.repository.TicketsRepository;

@Service
public class TicketsServiceImpl implements TicketsService {

    @Autowired
    private TicketsRepository repositorio;

    @Override
    public Tickets createTicket(Ventas venta) {
        int siguienteNumero = repositorio.getUltimoNumeroTicket().orElse(0) + 1;

        Tickets ticket = new Tickets();
        ticket.setNumeroTicket(siguienteNumero);
        ticket.setVenta(venta);
        ticket.setFechaEmision(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));

        return repositorio.save(ticket);
    }

    @Override
    public Tickets getByVentaId(Long ventaId) {
        return repositorio.findByIdVenta(ventaId)
                .orElseThrow(() -> new RuntimeException("Ticket not found for ventaId: " + ventaId));
    }

    @Override
    public Tickets getByNumeroTicket(Integer numeroTicket) {
        return repositorio.findByNumeroTicket(numeroTicket)
                .orElseThrow(() -> new RuntimeException("Ticket not found for numeroTicket: " + numeroTicket));
    }

    @Override
    public Tickets getUltimoByVentaId(Long ventaId) {
        List<Tickets> tickets = repositorio.findUltimoByVentaId(ventaId);
        
        return tickets.stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No tickets found for ventaId: " + ventaId));
    }

}