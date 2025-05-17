package com.hotel.appHotel.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hotel.appHotel.model.Usuarios;


@Repository
public interface UsuariosRepository extends JpaRepository<Usuarios, Long> {
    
    List<Usuarios> findByRol_NombreNot(String rol);
    
    Usuarios findByDni(String dni);

    @Query("SELECT u FROM Usuarios u WHERE u.eliminado = false AND u.rol.nivel = 0 ORDER BY u.id_usuario DESC")
    Page<Usuarios> findByEliminadoFalseDesc(Pageable pageable);

}
