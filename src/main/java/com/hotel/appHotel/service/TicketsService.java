package com.hotel.appHotel.service;

import com.hotel.appHotel.model.Tickets;
import com.hotel.appHotel.model.Ventas;

public interface TicketsService  {

    public Tickets createTicket(Ventas venta);

    public Tickets getByVentaId(Long ventaId);
    
    public Tickets getByNumeroTicket(Integer numeroTicket);

    public Tickets getUltimoByVentaId(Long ventaId);

}
