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
@Table(name = "ventas")
public class Ventas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_venta;

    @ManyToOne
    @JoinColumn(name = "usuario_responsable", referencedColumnName = "id_usuario")
    private Usuarios usuario_responsable;

    @ManyToOne
    @JoinColumn(name = "habitacion_precio", referencedColumnName = "id_habitacion_precio")
    private HabitacionesPrecio habitacion_precio;

    @Column(name = "fecha_entrada")
    private String fecha_entrada; // Opcional para cotizaciones

    @Column(name = "fecha_salida")
    private String fecha_salida;

    @Column(name = "estado")
    private String estado;

    @Column(name = "descuento")
    private Double descuento = 0.0;

    @Column(name = "monto_total")
    private Double monto_total = 0.0;

    @Column(name = "monto_adelanto")
    private Double monto_adelanto = 0.0;

    @Column(name = "tipo_venta")
    private String tipo_venta; // "Cotización", "Alquiler", "Venta"

    @Column(name = "fecha_creacion")
    private String fecha_creacion; // ISO 8601

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "venta")
    private Set<VentasClientesHabitacion> ventas_VentasClientesHabiatcion = new HashSet<>();

    @PrePersist
    private void prePersist() {
        this.fecha_creacion = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
