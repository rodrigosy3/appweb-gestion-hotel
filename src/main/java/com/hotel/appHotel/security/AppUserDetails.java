package com.hotel.appHotel.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.hotel.appHotel.model.Credenciales;
import com.hotel.appHotel.model.Usuarios;

public class AppUserDetails implements UserDetails {

    private final Credenciales cred;
    private final Usuarios user;

    public AppUserDetails(Credenciales cred) {
        this.cred = cred;
        this.user = cred.getUsuario();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // extrae el rol del usuario y lo convierte en ROLE_xxx
        String nombreRol = user.getRol().getNombre();
        return List.of(new SimpleGrantedAuthority("ROLE_" + nombreRol));
    }

    @Override
    public String getPassword() {
        return cred.getContrasena();
    }

    @Override
    public String getUsername() {
        return user.getDni();
        /* o el campo login */ }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return !cred.isEliminado();
    }

    public String getNombres() {
        return user.getNombres();
    }

}
