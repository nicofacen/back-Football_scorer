package com.uade.e_commerce.service;

import org.springframework.stereotype.Service;

import com.uade.e_commerce.dto.LoginRequest;
import com.uade.e_commerce.dto.RegistroRequest;
import com.uade.e_commerce.dto.UsuarioResponse;
import com.uade.e_commerce.exception.CredencialesInvalidasException;
import com.uade.e_commerce.exception.RecursoDuplicadoException;
import com.uade.e_commerce.exception.RecursoNoEncontradoException;
import com.uade.e_commerce.model.Rol;
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

    public UsuarioResponse registrar(RegistroRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RecursoDuplicadoException("Ya existe un usuario con el email " + request.getEmail());
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(request.getPassword());
        usuario.setRol(Rol.CLIENTE);

        return toResponse(usuarioRepository.save(usuario));
    }

    public UsuarioResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .filter(u -> u.getPassword().equals(request.getPassword()))
                .orElseThrow(() -> new CredencialesInvalidasException("Email o password incorrectos"));

        return toResponse(usuario);
    }

    public UsuarioResponse obtenerPorId(Long id) {
        return toResponse(buscar(id));
    }

    public UsuarioResponse actualizar(Long id, RegistroRequest datos) {
        Usuario usuario = buscar(id);
        usuario.setNombre(datos.getNombre());
        usuario.setApellido(datos.getApellido());
        usuario.setEmail(datos.getEmail());
        usuario.setPassword(datos.getPassword());

        return toResponse(usuarioRepository.save(usuario));
    }

    private Usuario buscar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario " + id + " no encontrado"));
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
