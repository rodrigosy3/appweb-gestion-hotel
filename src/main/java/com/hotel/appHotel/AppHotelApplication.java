package com.hotel.appHotel;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.appHotel.model.Credenciales;
import com.hotel.appHotel.model.Habitaciones;
import com.hotel.appHotel.model.HabitacionesCaracteristicas;
import com.hotel.appHotel.model.HabitacionesEstado;
import com.hotel.appHotel.model.HabitacionesTipos;
import com.hotel.appHotel.model.Roles;
import com.hotel.appHotel.model.Usuarios;
import com.hotel.appHotel.repository.CredencialesRepository;
import com.hotel.appHotel.repository.HabitacionesCaracteristicasRepository;
import com.hotel.appHotel.repository.HabitacionesEstadoRepository;
import com.hotel.appHotel.repository.HabitacionesRepository;
import com.hotel.appHotel.repository.HabitacionesTiposRepository;
import com.hotel.appHotel.repository.RolesRepository;
import com.hotel.appHotel.repository.UsuariosRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableScheduling
@SpringBootApplication
public class AppHotelApplication {
	@Autowired
	RolesRepository repoRoles;

	@Autowired
	HabitacionesEstadoRepository repoHabitacionesEstado;

	@Autowired
	HabitacionesTiposRepository repoHabitacionesTipos;

	@Autowired
	UsuariosRepository repoUsuarios;

	@Autowired
	CredencialesRepository repoCredenciales;

	@Autowired
	HabitacionesRepository repoHabitaciones;

	@Autowired
	HabitacionesCaracteristicasRepository repoHabitacionesCaracteristicas;

	public static void main(String[] args) {
		int puerto = 3000;
		crearCarpetaBD();

		if (isServerRunning(puerto)) {
			System.out.println("El servidor ya está en ejecución. Abriendo el navegador...");
			abrirNavegador("http://localhost:" + puerto);
			return;
		}

		// Si el servidor no está en ejecución, lo inicia y abre el navegador
		SpringApplication.run(AppHotelApplication.class, args);
		abrirNavegador("http://localhost:" + puerto);
	}

	private static void crearCarpetaBD() {
		String ruta = System.getProperty("user.home") + "/Documents/AppHotelDatos";
		File carpeta = new File(ruta);

		if (!carpeta.exists()) {
			boolean creada = carpeta.mkdirs();
			if (creada) {
				System.out.println("📂 Carpeta creada en: " + ruta);
			} else {
				System.err.println("❌ No se pudo crear la carpeta");
			}
		}
	}

	// Verifica si el servidor ya está en ejecución
	private static boolean isServerRunning(int port) {
		try (@SuppressWarnings("unused")
		ServerSocket socket = new ServerSocket(port)) {
			return false; // Puerto libre - El servidor NO está en ejecución
		} catch (IOException e) {
			return true; // Puerto en uso - El servidor YA está en ejecución
		}
	}

	// Abre el navegador en la URL de la aplicación
	private static void abrirNavegador(String url) {
		String os = System.getProperty("os.name").toLowerCase();
		try {
			if (os.contains("win")) {
				Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
			} else if (os.contains("mac")) {
				Runtime.getRuntime().exec("open " + url);
			} else if (os.contains("nix") || os.contains("nux")) {
				Runtime.getRuntime().exec("xdg-open " + url);
			} else {
				System.out.println("No se pudo detectar el sistema operativo para abrir el navegador.");
			}
		} catch (IOException e) {
			System.out.println("Error al intentar abrir el navegador: " + e.getMessage());
		}
	}

	@Bean
	@Transactional
	@SuppressWarnings("unused")
	CommandLineRunner init() {
		return args -> {
			List<Roles> roles = repoRoles.findAll();
			List<HabitacionesEstado> habitacionesEstado = repoHabitacionesEstado.findAll();
			List<HabitacionesTipos> habitacionesTipos = repoHabitacionesTipos.findAll();
			Optional<Usuarios> usuarios = repoUsuarios.findById(1L);
			List<Habitaciones> habitaciones = repoHabitaciones.findAll();
			List<HabitacionesCaracteristicas> habitacionesCaracteristicas = repoHabitacionesCaracteristicas.findAll();

			if (roles.isEmpty()) {
				Roles rol_1 = new Roles();
				rol_1.setNombre("cliente".toUpperCase());
				rol_1.setNivel(0);
				repoRoles.save(rol_1);

				Roles rol_2 = new Roles();
				rol_2.setNombre("dueño".toUpperCase());
				rol_2.setNivel(1);
				repoRoles.save(rol_2);

				Roles rol_3 = new Roles();
				rol_3.setNombre("administrador".toUpperCase());
				rol_3.setNivel(2);
				repoRoles.save(rol_3);

				Roles rol_4 = new Roles();
				rol_4.setNombre("recepcionista".toUpperCase());
				rol_4.setNivel(3);
				repoRoles.save(rol_4);

				Roles rol_5 = new Roles();
				rol_5.setNombre("limpieza".toUpperCase());
				rol_5.setNivel(4);
				repoRoles.save(rol_5);
			}

			if (habitacionesEstado.isEmpty()) {
				HabitacionesEstado hEstado_1 = new HabitacionesEstado();
				hEstado_1.setEstado("Disponible".toUpperCase());
				repoHabitacionesEstado.save(hEstado_1);

				HabitacionesEstado hEstado_2 = new HabitacionesEstado();
				hEstado_2.setEstado("Ocupado".toUpperCase());
				repoHabitacionesEstado.save(hEstado_2);

				HabitacionesEstado hEstado_3 = new HabitacionesEstado();
				hEstado_3.setEstado("limpieza".toUpperCase());
				repoHabitacionesEstado.save(hEstado_3);

				HabitacionesEstado hEstado_4 = new HabitacionesEstado();
				hEstado_4.setEstado("Mantenimiento".toUpperCase());
				repoHabitacionesEstado.save(hEstado_4);

				HabitacionesEstado hEstado_5 = new HabitacionesEstado();
				hEstado_5.setEstado("reservada".toUpperCase());
				repoHabitacionesEstado.save(hEstado_5);
			}

			if (habitacionesTipos.isEmpty()) {
				HabitacionesTipos hTipo_1 = new HabitacionesTipos();
				hTipo_1.setNombre_tipo("simple con baño privado".toUpperCase());
				hTipo_1.setAbreviacion_tipo("scb".toUpperCase());
				repoHabitacionesTipos.save(hTipo_1);

				HabitacionesTipos hTipo_2 = new HabitacionesTipos();
				hTipo_2.setNombre_tipo("simple".toUpperCase());
				hTipo_2.setAbreviacion_tipo("s".toUpperCase());
				repoHabitacionesTipos.save(hTipo_2);

				HabitacionesTipos hTipo_3 = new HabitacionesTipos();
				hTipo_3.setNombre_tipo("doble con baño privado".toUpperCase());
				hTipo_3.setAbreviacion_tipo("dcb".toUpperCase());
				repoHabitacionesTipos.save(hTipo_3);

				HabitacionesTipos hTipo_4 = new HabitacionesTipos();
				hTipo_4.setNombre_tipo("doble".toUpperCase());
				hTipo_4.setAbreviacion_tipo("d".toUpperCase());
				repoHabitacionesTipos.save(hTipo_4);

				HabitacionesTipos hTipo_5 = new HabitacionesTipos();
				hTipo_5.setNombre_tipo("matrimonial con baño privado".toUpperCase());
				hTipo_5.setAbreviacion_tipo("mcb".toUpperCase());
				repoHabitacionesTipos.save(hTipo_5);

				HabitacionesTipos hTipo_6 = new HabitacionesTipos();
				hTipo_6.setNombre_tipo("matrimonial".toUpperCase());
				hTipo_6.setAbreviacion_tipo("m".toUpperCase());
				repoHabitacionesTipos.save(hTipo_6);

				if (habitaciones.isEmpty()) {
					Habitaciones habitacion_1 = new Habitaciones();
					habitacion_1.setNumero(110);
					habitacion_1.setTipo(hTipo_4);
					habitacion_1.setPrecio(50D);
					habitacion_1.setEstado(repoHabitacionesEstado.findByEstado("DISPONIBLE"));
					habitacion_1.setCategoria("ANTIGUO");
					habitacion_1.setCapacidad(5);
					repoHabitaciones.save(habitacion_1);

					Habitaciones habitacion_2 = new Habitaciones();
					habitacion_2.setNumero(210);
					habitacion_2.setTipo(hTipo_1);
					habitacion_2.setPrecio(50D);
					habitacion_2.setEstado(repoHabitacionesEstado.findByEstado("DISPONIBLE"));
					habitacion_2.setCategoria("MODERNO");
					habitacion_2.setCapacidad(5);
					repoHabitaciones.save(habitacion_2);
				}
			}

			if (usuarios.isEmpty()) {
				Usuarios usuario = new Usuarios();

				usuario.setDni("12345678");
				usuario.setNombres("admin".toUpperCase());
				usuario.setApellidos("admin admin".toUpperCase());
				usuario.setRol(repoRoles.findByNombre("ADMINISTRADOR"));

				Usuarios usuario_admin = repoUsuarios.save(usuario);

				Credenciales credencial_nueva = new Credenciales();
				credencial_nueva.setUsuario(usuario_admin);
				credencial_nueva.setContrasena("12345678");

				repoCredenciales.save(credencial_nueva);
			}

			if (habitacionesCaracteristicas.isEmpty()) {
				HabitacionesCaracteristicas hCaracteristica_1 = new HabitacionesCaracteristicas();
				hCaracteristica_1.setNombre(("Televisión 50" + '"').toUpperCase());
				hCaracteristica_1.setMarca("Samsung".toUpperCase());
				hCaracteristica_1.setPrecio(5D);

				repoHabitacionesCaracteristicas.save(hCaracteristica_1);

				HabitacionesCaracteristicas hCaracteristica_2 = new HabitacionesCaracteristicas();
				hCaracteristica_2.setNombre("Baño".toUpperCase());
				hCaracteristica_2.setMarca("Trebol".toUpperCase());
				hCaracteristica_2.setDescripcion("Contiene lavamanos, taza, espejo, ducha y cortinas.");
				hCaracteristica_2.setPrecio(10D);

				repoHabitacionesCaracteristicas.save(hCaracteristica_2);

				HabitacionesCaracteristicas hCaracteristica_3 = new HabitacionesCaracteristicas();
				hCaracteristica_3.setNombre("Cama".toUpperCase());
				hCaracteristica_3.setMarca("Cisne".toUpperCase());
				hCaracteristica_3.setDescripcion("Contiene 2 almohadas, 1 juego de sábanas y 1 edredon");
				hCaracteristica_3.setPrecio(0D);

				repoHabitacionesCaracteristicas.save(hCaracteristica_3);

				HabitacionesCaracteristicas hCaracteristica_4 = new HabitacionesCaracteristicas();
				hCaracteristica_4.setNombre("Velador".toUpperCase());
				hCaracteristica_4.setMarca("".toUpperCase());
				hCaracteristica_4.setPrecio(0D);

				repoHabitacionesCaracteristicas.save(hCaracteristica_4);
			}
		};
	}
}
