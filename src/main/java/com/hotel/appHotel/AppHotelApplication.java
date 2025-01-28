package com.hotel.appHotel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @Slf4j
@SpringBootApplication
public class AppHotelApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppHotelApplication.class, args);
	}

	// @Autowired
	// private RolesRepository rolesRepository;

	// @Autowired
	// private UsuariosRepository usuariosRepository;

	// @Bean
	// CommandLineRunner init() {
	// return args -> {
	// // Usuarios usuario = new Usuarios();
	// // Roles rol_1 = rolesRepository.findById(1L).orElseThrow(() -> new
	// RuntimeException("Rol no encontrado"));

	// // usuario.setNombres("RODRIGO");
	// // usuario.setApellidos("SIHUES YANQUI");
	// // usuario.setDni("74663928");
	// // usuario.setCelular("961211119");
	// // usuario.setEdad(23);
	// // usuario.setRol(rol_1);

	// // usuariosRepository.save(usuario);

	// usuariosRepository.findAll().forEach(usuario -> {
	// log.info(usuario.getNombres() + " estado: " + usuario.getEstado_vetado());
	// });
	// };
	// }

	// @Bean
	// CommandLineRunner init() {
	// // return args -> {
	// // rolesRepository.findAll().forEach(rol -> {
	// // log.info(rol.getNombre() + rol.getNivel());
	// // });
	// // };

	// return args -> {
	// // Crear un nuevo rol
	// // Roles developerRole = new Roles();
	// // developerRole.setNombre("DEVELOPER");
	// // developerRole.setNivel(0);

	// // // Guardar el rol en la base de datos
	// // rolesRepository.save(developerRole);

	// // Mostrar todos los roles para verificar la inserción
	// rolesRepository.findAll().forEach(rol -> {
	// log.info("Rol: {}, Nivel: {}, Fecha de creacion: {}", rol.getNombre(),
	// rol.getNivel(), rol.getFecha_creacion());
	// });
	// };
	// }
}
