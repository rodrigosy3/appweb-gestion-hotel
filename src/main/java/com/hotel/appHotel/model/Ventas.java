package com.hotel.appHotel.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ventas")
public class Ventas {

    @Id
    @Column(name = "id_venta")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_venta;

    @ManyToOne
    @JoinColumn(name = "usuario_responsable", referencedColumnName = "id_usuario")
    private Usuarios usuario_responsable;

    @ManyToOne
    @JoinColumn(name = "habitacion", referencedColumnName = "id_habitacion")
    private Habitaciones habitacion;

    @Column(name = "fecha_entrada")
    private String fecha_entrada;

    @Column(name = "fecha_salida")
    private String fecha_salida;

    @Column(name = "tiempo_estadia")
    private Integer tiempo_estadia = 1;

    @Column(name = "estado")
    private String estado;

    @Column(name = "estado_estadia")
    private String estado_estadia = "SIN PROBLEMAS";

    @Column(name = "descuento")
    private Double descuento = 0.0;

    @Column(name = "monto_total")
    private Double monto_total = 0.0;

    @Column(name = "monto_adelanto")
    private Double monto_adelanto = 0.0;

    @Column
    private String ultimaFecha;

    @Column(name = "por_cobrar")
    private Double porCobrar = 0.0;

    @Column(name = "monto_diario")
    private Double montoDiario = 0.0;

    @Column(name = "cobro_diario")
    private Boolean cobroDiario;
    
    @Column(name = "total_cobrado")
    private Double totalCobrado;

    @Column(name = "tipo_servicio")
    private String tipo_servicio = "COMPLETO";

    @Column(name = "tipo_venta")
    private String tipo_venta = "ALQUILER";

    @Column(name = "fecha_creacion")
    private String fecha_creacion; // ISO 8601

    @Column(name = "eliminado")
    private boolean eliminado = false;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "venta")
    private Set<VentasClientesHabitacion> ventasClientesHabitacion;

    // Genera la fecha en el momento del guardado y no cuando se empezó a crear la
    // entidad
    @PrePersist
    private void prePersist() {
        this.fecha_creacion = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    }

}
