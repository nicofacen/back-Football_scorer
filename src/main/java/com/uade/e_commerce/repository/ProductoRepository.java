package com.uade.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.e_commerce.model.Producto;
/**
 * JPA repository interface for managing Producto entities
 * Provides CRUD operations and query methods for interacting with the database
 * ProductoRepository
 * save, update, delete, findById, findAll, etc.
 */
public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
