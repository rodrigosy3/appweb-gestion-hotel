package com.hotel.appHotel.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class BackupService {

    private static final String DB_FILE = "Documents/AppHotelDatos/db_hotel.db"; // Nombre de la base de datos
    private static final String BACKUP_FOLDER = "Documents/AppHotelDatos/MiHotelBackups"; // Carpeta en Documentos
    private static final int MAX_BACKUPS = 12; // Cantidad máxima de backups a conservar

    @Scheduled(fixedRate = 21600000) // Ejecuta cada 24 horas
    public void generarBackup() {
        try {
            // Obtener la carpeta de Documentos del usuario actual
            Path backupDir = Paths.get(System.getProperty("user.home"), BACKUP_FOLDER);
            Files.createDirectories(backupDir); // Crea la carpeta si no existe

            // Generar nombre del archivo de backup con fecha y hora
            String fecha = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
            Path destino = backupDir.resolve("backup_" + fecha + ".db");

            // Copiar el archivo SQLite a la carpeta de backups
            Path origen = Paths.get(System.getProperty("user.home"), DB_FILE);
            Files.copy(origen, destino, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("✅ Backup creado exitosamente: " + destino);

            limpiarBackupsAntiguos(backupDir); // Eliminar backups antiguos si hay más de 10

        } catch (IOException e) {
            System.err.println("❌ Error al crear la copia de seguridad:");
            e.printStackTrace();
        }
    }

    private void limpiarBackupsAntiguos(Path backupDir) {
        try {
            List<Path> archivos = new ArrayList<>(Files.list(backupDir)
                    .filter(Files::isRegularFile)
                    .sorted(Comparator.comparingLong(p -> p.toFile().lastModified()))
                    .toList());

            // List<Path> archivos = Files.list(backupDir)
            //         .filter(Files::isRegularFile)
            //         .sorted(Comparator.comparingLong(p -> p.toFile().lastModified())) // Ordenar por fecha
            //         .toList();

            while (archivos.size() > MAX_BACKUPS) {
                Files.delete(archivos.get(0)); // Eliminar el más antiguo
                System.out.println("🗑️ Backup eliminado: " + archivos.get(0));
                archivos.remove(0);
            }
        } catch (IOException e) {
            System.err.println("⚠️ Error al limpiar backups antiguos:");
            e.printStackTrace();
        }
    }
}
