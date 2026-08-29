package com.uade.e_commerce.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.uade.e_commerce.dto.LoginRequest;
import com.uade.e_commerce.dto.RegistroRequest;
import com.uade.e_commerce.dto.UsuarioResponse;
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

    public Optional<UsuarioResponse> registrar(RegistroRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            return Optional.empty();
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(request.getPassword());
        usuario.setRol(Rol.CLIENTE);

        Usuario guardado = usuarioRepository.save(usuario);
        return Optional.of(toResponse(guardado));
    }

    public Optional<UsuarioResponse> login(LoginRequest request) {
        return usuarioRepository.findByEmail(request.getEmail())
                .filter(usuario -> usuario.getPassword().equals(request.getPassword()))
                .map(this::toResponse);
    }

    public Optional<UsuarioResponse> obtenerPorId(Long id) {
        return usuarioRepository.findById(id).map(this::toResponse);
    }

    public Optional<UsuarioResponse> actualizar(Long id, RegistroRequest datos) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setNombre(datos.getNombre());
                    usuario.setApellido(datos.getApellido());
                    usuario.setEmail(datos.getEmail());
                    usuario.setPassword(datos.getPassword());
                    return usuarioRepository.save(usuario);
                })
                .map(this::toResponse);
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
