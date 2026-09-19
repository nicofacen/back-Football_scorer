package com.uade.e_commerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    // Query methods derivados: un filtro solo, SQL directo y legible.
    List<Producto> findByActivoTrueAndNombreContainingIgnoreCase(String nombre);

    List<Producto> findByActivoTrueAndClubId(Long clubId);

    List<Producto> findByActivoTrueAndCategoriasId(Long categoriaId);

    List<Producto> findByActivoTrueAndPrecioLessThanEqual(Double precioMax);

    // Combinar 4 filtros opcionales como query methods escalaria a 2^4 = 16 metodos.
    // Specifications resolveria esto mejor, pero no estan vistas en la materia.
    @Query("""
            SELECT p FROM Producto p
            WHERE p.activo = true
              AND (:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
              AND (:clubId IS NULL OR p.club.id = :clubId)
              AND (:categoriaId IS NULL OR :categoriaId IN (SELECT c.id FROM p.categorias c))
              AND (:precioMax IS NULL OR p.precio <= :precioMax)
            """)
    List<Producto> buscar(@Param("nombre") String nombre, @Param("clubId") Long clubId,
                           @Param("categoriaId") Long categoriaId, @Param("precioMax") Double precioMax);
}
