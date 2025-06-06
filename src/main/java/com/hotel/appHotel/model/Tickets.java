package com.hotel.appHotel.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tickets")
public class Tickets {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTicket;

    @Column(name = "numero_ticket", nullable = false, unique = true)
    private Integer numeroTicket;

    @ManyToOne
    @JoinColumn(name = "venta", referencedColumnName = "id_venta")
    private Ventas venta;

    @Column(name = "fecha_emision", nullable = false)
    private String fechaEmision; // formato ISO: yyyy-MM-dd'T'HH:mm:ss

    @Column(name = "eliminado")
    private Boolean eliminado = false;
    
}
