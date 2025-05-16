package com.hotel.appHotel.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hotel.appHotel.repository.CredencialesRepository;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final CredencialesRepository repo;

    public AppUserDetailsService(CredencialesRepository repo) {
        this.repo = repo;
    }

    // @Override
    // public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    //     System.out.println("Buscando usuario con DNI: " + username);
    //     // return repo.findByUsuarioDni(username) // suponiendo un método en tu repo
    //     //         .map(AppUserDetails::new)
    //     //         .orElseThrow(() -> new UsernameNotFoundException("No existe usuario " + username));
    //     return repo.buscarPorDni(username)
    //             .map(AppUserDetails::new)
    //             .orElseThrow(() -> new UsernameNotFoundException("No existe usuario " + username));
    // }
    @Override
    public UserDetails loadUserByUsername(String username) {
        // System.out.println(" >>> loadUserByUsername: intentando buscar DNI=" + username);
        return repo.buscarPorDni(username)
                .map(AppUserDetails::new)
                .orElseThrow(
                        () -> {
                            // System.out.println("    ✗ no encontró credencial");
                            return new UsernameNotFoundException("No existe usuario " + username);
                        }
                );
    }
}
