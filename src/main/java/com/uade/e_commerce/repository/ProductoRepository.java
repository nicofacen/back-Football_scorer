package com.uade.e_commerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uade.e_commerce.model.Producto;

/**
 * JPA repository interface for managing Producto entities
 * Provides CRUD operations and query methods for interacting with the database
 * ProductoRepository
 * save, update, delete, findById, findAll, etc.
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByActivoTrue();

    // Filtra la baja lógica en el SQL (WHERE id = ? AND activo = true), no en memoria.
    Optional<Producto> findByIdAndActivoTrue(Long id);
}
