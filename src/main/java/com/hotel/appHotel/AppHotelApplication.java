package com.hotel.appHotel;

// import java.sql.Connection;
// import java.sql.DriverManager;
// import java.sql.ResultSet;
// import java.sql.Statement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @Slf4j
@SpringBootApplication
public class AppHotelApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppHotelApplication.class, args);

		// Llamar al método para obtener el tamaño de la BD
		// long size = getDatabaseSize("jdbc:sqlite:db_hotel.db");
		// System.out.println("Tamaño de la base de datos: " + size / 1024 + " KB");
	}

	// public static long getDatabaseSize(String dbUrl) {
	// 	long size = 0;
	// 	try (Connection conn = DriverManager.getConnection(dbUrl);
	// 			Statement stmt = conn.createStatement();
	// 			ResultSet rs = stmt.executeQuery(
	// 					"SELECT (page_count * page_size) AS size_in_bytes FROM pragma_page_count(), pragma_page_size();")) {

	// 		if (rs.next()) {
	// 			size = rs.getLong("size_in_bytes");
	// 		}
	// 	} catch (Exception e) {
	// 		e.printStackTrace();
	// 	}
	// 	return size;
	// }

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
