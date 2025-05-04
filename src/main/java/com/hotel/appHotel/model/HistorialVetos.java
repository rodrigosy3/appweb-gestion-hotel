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
@Table(name = "historial_vetos")
public class HistorialVetos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_historial_veto;

    @Column(name = "razon")
    private String razon;

    @ManyToOne
    @JoinColumn(name = "usuario_vetado", referencedColumnName = "id_usuario")
    private Usuarios usuario_vetado;

    @ManyToOne
    @JoinColumn(name = "usuario_responsable", referencedColumnName = "id_usuario")
    private Usuarios usuario_responsable;

    @Column(name = "fecha_creacion")
    private String fecha_creacion;

    @Column(name = "eliminado")
    private boolean eliminado = false;

    // Genera la fecha en el momento del guardado y no cuando se empezó a crear la
    // entidad
    @PrePersist
    private void prePersist() {
        this.fecha_creacion = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    }
}
