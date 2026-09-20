package com.uade.e_commerce.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.uade.e_commerce.dto.ActualizarUsuarioRequest;
import com.uade.e_commerce.dto.UsuarioResponse;
import com.uade.e_commerce.exception.RecursoNoEncontradoException;
import com.uade.e_commerce.model.Usuario;
import com.uade.e_commerce.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioResponse obtenerPorId(Long id) {
        Usuario usuario = buscar(id);
        verificarPropietarioOAdmin(usuario);
        return toResponse(usuario);
    }

    public UsuarioResponse actualizar(Long id, ActualizarUsuarioRequest datos) {
        Usuario usuario = buscar(id);
        verificarPropietarioOAdmin(usuario);

        usuario.setNombre(datos.getNombre());
        usuario.setApellido(datos.getApellido());
        usuario.setEmail(datos.getEmail());

        return toResponse(usuarioRepository.save(usuario));
    }

    private Usuario buscar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario " + id + " no encontrado"));
    }

    // GET/PUT por id son de cualquier usuario autenticado a nivel de URL (SecurityConfig
    // no puede distinguir "el mio" de "el de otro" por patron); el chequeo de propiedad
    // del recurso puntual va aca, en el service.
    private void verificarPropietarioOAdmin(Usuario usuario) {
        Authentication autenticacion = SecurityContextHolder.getContext().getAuthentication();
        boolean esAdmin = autenticacion.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        boolean esPropietario = autenticacion.getName().equals(usuario.getEmail());

        if (!esAdmin && !esPropietario) {
            throw new AccessDeniedException("No tenes permiso para acceder a este usuario");
        }
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getRol());
    }
}
