package com.hotel.appHotel.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "usuarios")
public class Usuarios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_usuario;

    @Column(name = "nombres")
    private String nombres;

    @Column(name = "apellidos")
    private String apellidos;

    @Column(name = "edad")
    private Integer edad = 0;

    @Column(name = "dni")
    private String dni;

    @Column(name = "celular")
    private String celular = "999999999";

    @Column(name = "estado_vetado")
    private Boolean estado_vetado = false;

    @Column(name = "razon_vetado")
    private String razon_vetado = "";

    @ManyToOne
    @JoinColumn(name = "rol", referencedColumnName = "id_rol")
    private Roles rol;

    @Column(name = "fecha_creacion")
    private String fecha_creacion;

    @Column(name = "eliminado")
    private boolean eliminado = false;

    // Genera la fecha en el momento del guardado y no cuando se empezó a crear la entidad
    @PrePersist
    private void prePersist() {
        this.fecha_creacion = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    }
}
