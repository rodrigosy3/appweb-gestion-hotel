package com.hotel.appHotel;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.hotel.appHotel.model.HabitacionesEstado;
import com.hotel.appHotel.model.HabitacionesTipos;
import com.hotel.appHotel.model.Roles;
import com.hotel.appHotel.model.Usuarios;
import com.hotel.appHotel.repository.HabitacionesEstadoRepository;
import com.hotel.appHotel.repository.HabitacionesTiposRepository;
import com.hotel.appHotel.repository.RolesRepository;
import com.hotel.appHotel.repository.UsuariosRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@EnableScheduling
public class AppHotelApplication {
	@Autowired
	RolesRepository repoRoles;

	@Autowired
	HabitacionesEstadoRepository repoHabitacionesEstado;

	@Autowired
	HabitacionesTiposRepository repoHabitacionesTipos;

	@Autowired
	UsuariosRepository repoUsuarios;

	public static void main(String[] args) {
		SpringApplication.run(AppHotelApplication.class, args);

		// Llamar al método para obtener el tamaño de la BD
		// long size = getDatabaseSize("jdbc:sqlite:db_hotel.db");
		// System.out.println("Tamaño de la base de datos: " + size / 1024 + " KB");
	}

	@Bean
	CommandLineRunner init() {
		return args -> {
			List<Roles> roles = repoRoles.findAll();
			List<HabitacionesEstado> habitacionesEstado = repoHabitacionesEstado.findAll();
			List<HabitacionesTipos> habitacionesTipos = repoHabitacionesTipos.findAll();
			List<Usuarios> usuarios = repoUsuarios.findAll();
			
			if (roles.isEmpty()) {
				Roles rol_1 = new Roles();
				rol_1.setNombre("cliente".toUpperCase());
				rol_1.setNivel(0);
				repoRoles.save(rol_1);

				Roles rol_2 = new Roles();
				rol_2.setNombre("owmner".toUpperCase());
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
			}

			if (usuarios.isEmpty()) {
				Usuarios usuario = new Usuarios();

				usuario.setDni("74663928");
				usuario.setNombres("Rodrigo");
				usuario.setApellidos("Sihues Yanqui");
				usuario.setRol(repoRoles.findByNombre("ADMINISTRADOR"));

				repoUsuarios.save(usuario);
			}
		};
	}
}
