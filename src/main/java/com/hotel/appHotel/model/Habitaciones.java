package com.hotel.appHotel.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "habitaciones")
public class Habitaciones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_habitacion;

    @Column(name = "numero")
    private Integer numero;

    @Column(name = "categoria")
    private String categoria;

    @Column(name = "capacidad")
    private Integer capacidad;

    @ManyToOne
    @JoinColumn(name = "estado", referencedColumnName = "id_habitacion_estado")
    private HabitacionesEstado estado;

    @Column(name = "razon_estado")
    private String razon_estado = "";

    @ManyToOne
    @JoinColumn(name = "tipo", referencedColumnName = "id_habitacion_tipo")
    private HabitacionesTipos tipo;

    @Column(name = "precio")
    private Double precio;

    @Column(name = "fecha_creacion")
    private String fecha_creacion;

    @Column(name = "eliminado")
    private boolean eliminado = false;
    
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "habitacion")
    private Set<Ventas> habitacionesVentas = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "habitacion")
    private Set<HabitacionesContenido> habitacionesContenido = new HashSet<>();

    // Genera la fecha en el momento del guardado y no cuando se empezó a crear la entidad
    @PrePersist
    private void prePersist() {
        this.fecha_creacion = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    }
}