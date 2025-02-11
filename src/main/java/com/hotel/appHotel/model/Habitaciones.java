package com.hotel.appHotel.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "habitaciones")
public class Habitaciones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_habitacion;

    @Column(name = "numero", unique = true)
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

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "habitacion")
    private Set<Ventas> habitacionesVentas = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "habitacion")
    private Set<HabitacionesContenido> habitacionesContenido = new HashSet<>();

    @PrePersist
    private void prePersist() {
        this.fecha_creacion = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}