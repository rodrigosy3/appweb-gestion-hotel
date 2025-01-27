package com.hotel.appHotel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

// import com.hotel.appHotel.model.Roles;
import com.hotel.appHotel.repository.RolesRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class AppHotelApplication {

	public static void main(String[] args) { SpringApplication.run(AppHotelApplication.class, args); }

	@Autowired
	private RolesRepository rolesRepository;

	@Bean
	CommandLineRunner init() {
		// return args -> {
		// 	rolesRepository.findAll().forEach(rol -> {
		// 		log.info(rol.getNombre() + rol.getNivel());
		// 	});
		// };

		return args -> {
            // Crear un nuevo rol
            // Roles developerRole = new Roles();
            // developerRole.setNombre("DEVELOPER");
            // developerRole.setNivel(0);

            // // Guardar el rol en la base de datos
            // rolesRepository.save(developerRole);

            // Mostrar todos los roles para verificar la inserción
            rolesRepository.findAll().forEach(rol -> {
                log.info("Rol: {}, Nivel: {}, Fecha de creacion: {}", rol.getNombre(), rol.getNivel(), rol.getFecha_creacion());
            });
        };
	}

}
