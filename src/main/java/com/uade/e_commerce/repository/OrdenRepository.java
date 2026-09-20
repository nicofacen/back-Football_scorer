package com.uade.e_commerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.e_commerce.model.Orden;

public interface OrdenRepository extends JpaRepository<Orden, Long> {

    // Navega Orden.usuario.email igual que CarritoRepository. OrderBy: la mas reciente primero.
    List<Orden> findByUsuarioEmailOrderByFechaDesc(String email);
}
