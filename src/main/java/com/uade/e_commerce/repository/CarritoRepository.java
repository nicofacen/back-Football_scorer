package com.uade.e_commerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.e_commerce.model.Carrito;

public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    Optional<Carrito> findByUsuarioId(Long usuarioId);

    // Navega Carrito.usuario.email en una sola consulta (JOIN con usuarios).
    // El email es el "sub" del JWT: es lo que el controller recibe de Authentication.getName().
    Optional<Carrito> findByUsuarioEmail(String email);
}
