package com.uade.e_commerce.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.uade.e_commerce.model.Usuario;
import com.uade.e_commerce.repository.UsuarioRepository;

// Usuario NO implementa UserDetails a proposito: la entidad es persistencia,
// UserDetails es seguridad. Este service es el unico lugar que traduce entre las dos.
@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario " + email + " no encontrado"));

        // .roles("ADMIN"/"CLIENTE") agrega el prefijo ROLE_ solo: es lo que despues
        // permite usar .hasRole("ADMIN") en SecurityConfig sin tener que escribir ROLE_ADMIN.
        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPassword())
                .roles(usuario.getRol().name())
                .build();
    }

}
