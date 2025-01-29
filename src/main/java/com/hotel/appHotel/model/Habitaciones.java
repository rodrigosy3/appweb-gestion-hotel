package com.hotel.appHotel.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    @Column(name = "numero", nullable = false, unique = true)
    private Integer numero;

    @Column(name = "categoria", nullable = false)
    private String categoria;

    @Column(name = "capacidad", nullable = false)
    private Integer capacidad;

    @ManyToOne
    @JoinColumn(name = "estado", referencedColumnName = "id_habitacion_estado")
    private HabitacionesEstado estado;

    @ManyToOne
    @JoinColumn(name = "tipo", referencedColumnName = "id_habitacion_tipo")
    private HabitacionesTipos tipo;

    @Column(name = "fecha_creacion", nullable = false)
    private String fecha_creacion;

    @PrePersist
    private void prePersist() {
        this.fecha_creacion = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}