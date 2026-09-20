package com.uade.e_commerce.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.uade.e_commerce.dto.AuthResponse;
import com.uade.e_commerce.dto.LoginRequest;
import com.uade.e_commerce.dto.RegistroRequest;
import com.uade.e_commerce.dto.UsuarioResponse;
import com.uade.e_commerce.exception.CredencialesInvalidasException;
import com.uade.e_commerce.exception.RecursoDuplicadoException;
import com.uade.e_commerce.model.Rol;
import com.uade.e_commerce.model.Usuario;
import com.uade.e_commerce.repository.UsuarioRepository;
import com.uade.e_commerce.security.JwtService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                        AuthenticationManager authenticationManager, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public UsuarioResponse registrar(RegistroRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RecursoDuplicadoException("Ya existe un usuario con el email " + request.getEmail());
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(Rol.CLIENTE)
                .build();

        Usuario guardado = usuarioRepository.save(usuario);

        return new UsuarioResponse(guardado.getId(), guardado.getNombre(), guardado.getApellido(),
                guardado.getEmail(), guardado.getRol());
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (AuthenticationException ex) {
            throw new CredencialesInvalidasException("Email o password incorrectos");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generarToken(userDetails);
        long expiraEn = System.currentTimeMillis() + expirationMs;

        return new AuthResponse(token, "Bearer", expiraEn);
    }
}
