package com.hotel.appHotel.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class BackupService {

    private static final String BASE_DIR = System.getenv("APPDATA") + "/HostalVData";
    private static final String DB_FILE = BASE_DIR + "/db_hotel.db"; // Nombre de la base de datos
    private static final String BACKUP_FOLDER = BASE_DIR + "/Backups"; // Carpeta en Documentos
    private static final int MAX_BACKUPS = 12; // Cantidad máxima de backups a conservar
    // private static final String DB_FILE = "Documents/AppHotelDatos/db_hotel.db";
    // private static final String BACKUP_FOLDER = "Documents/AppHotelDatos/MiHotelBackups";
    // private static final int MAX_BACKUPS = 12;

    @Scheduled(fixedRate = 21600000) // Ejecuta cada 6 horas
    public void generarBackup() {
        try {
            // Obtener la carpeta de Documentos del usuario actual
            Path backupDir = Paths.get(BACKUP_FOLDER);
            Files.createDirectories(backupDir); // Crea la carpeta si no existe

            // Generar nombre del archivo de backup con fecha y hora
            String fecha = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date());
            Path destino = backupDir.resolve("backup_" + fecha + ".db");
            Path origen = Paths.get(DB_FILE);

            // // Copiar el archivo SQLite a la carpeta de backups
            // Path origen = Paths.get(System.getProperty("user.home"), DB_FILE);
            Files.copy(origen, destino, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("✅ Backup creado exitosamente: " + destino);

            limpiarBackupsAntiguos(backupDir); // Eliminar backups antiguos si hay más de 10

        } catch (IOException e) {
            System.err.println("❌ Error al crear la copia de seguridad: " + e.getMessage());
        }
    }

    private void limpiarBackupsAntiguos(Path backupDir) {
        try {
            List<Path> archivos = new ArrayList<>(Files.list(backupDir)
                    .filter(Files::isRegularFile)
                    .sorted(Comparator.comparingLong(p -> p.toFile().lastModified()))
                    .toList());

            // List<Path> archivos = Files.list(backupDir)
            // .filter(Files::isRegularFile)
            // .sorted(Comparator.comparingLong(p -> p.toFile().lastModified())) // Ordenar
            // por fecha
            // .toList();

            while (archivos.size() > MAX_BACKUPS) {
                Files.delete(archivos.get(0)); // Eliminar el más antiguo
                System.out.println("🗑️ Backup eliminado: " + archivos.get(0));
                archivos.remove(0);
            }
        } catch (IOException e) {
            System.err.println("⚠️ Error al limpiar backups antiguos: " + e.getMessage());
        }
    }
}
