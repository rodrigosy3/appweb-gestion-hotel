package com.hotel.appHotel.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hotel.appHotel.model.Ventas;

@Component
public class CobroDiarioScheduler {

    @Autowired
    private VentasService servicioVentas;

    // Se ejecuta todos los días a las 12:00 p.m.
    @Scheduled(cron = "03 0 12 * * *") // segundo minuto hora día mes díaSemana
    public void verificarCobrosPendientes() {
        List<Ventas> ventas = servicioVentas.getVentasActivas();

        LocalDateTime ahora = LocalDateTime.now();

        for (Ventas venta : ventas) {
            try {
                LocalDateTime ultimaFecha = LocalDateTime.parse(venta.getUltimaFecha()); // Asegúrate que esté en formato ISO
                LocalDateTime fechaSalida = LocalDateTime.parse(venta.getFecha_salida());
                // Sumar un día completo a la última fecha cobrada
                // LocalDateTime corte = ultimaFecha.plusDays(1).withHour(12).withMinute(0).withSecond(0);

                if (ahora.isAfter(ultimaFecha) && "PAGADO".equals(venta.getEstado())) {
                    venta.setEstado("POR COBRAR");

                    if (venta.getTipo_servicio().equals("MEDIO") && fechaSalida.toLocalDate().isEqual(ahora.toLocalDate())) {
                        venta.setMontoDiario(venta.getMontoDiario()/2);
                        System.out.println("Venta ID " + venta.getId_venta() + " cobro actualizado a " + (venta.getMontoDiario()/2));
                    }

                    servicioVentas.updateVenta(venta);
                    System.out.println("Venta ID " + venta.getId_venta() + " actualizada a POR COBRAR");
                }
            } catch (Exception e) {
                System.err.println("Error actualizando venta ID " + venta.getId_venta() + ": " + e.getMessage());
            }
        }
    }

}
