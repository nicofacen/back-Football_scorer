package com.uade.e_commerce.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.uade.e_commerce.dto.ClubResponse;
import com.uade.e_commerce.dto.ProductoRequest;
import com.uade.e_commerce.dto.ProductoResponse;
import com.uade.e_commerce.exception.RecursoNoEncontradoException;
import com.uade.e_commerce.model.Categoria;
import com.uade.e_commerce.model.Club;
import com.uade.e_commerce.model.Producto;
import com.uade.e_commerce.repository.CategoriaRepository;
import com.uade.e_commerce.repository.ClubRepository;
import com.uade.e_commerce.repository.ProductoRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ClubRepository clubRepository;

    public ProductoService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository,
                            ClubRepository clubRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.clubRepository = clubRepository;
    }

    public List<ProductoResponse> listar() {
        return buscar(null, null, null, null);
    }

    public List<ProductoResponse> buscar(String nombre, Long clubId, Long categoriaId, Double precioMax) {
        return productoRepository.findByActivoTrue()
                .stream()
                .filter(p -> nombre == null || p.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .filter(p -> clubId == null || (p.getClub() != null && p.getClub().getId().equals(clubId)))
                .filter(p -> categoriaId == null
                        || p.getCategorias().stream().anyMatch(c -> c.getId().equals(categoriaId)))
                .filter(p -> precioMax == null || p.getPrecio() <= precioMax)
                .map(this::toResponse)
                .toList();
    }

    public ProductoResponse obtenerPorId(Long id) {
        return toResponse(buscarActivo(id));
    }

    public ProductoResponse crear(ProductoRequest request) {
        Producto producto = new Producto();
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setImagenUrl(request.getImagenUrl());
        producto.setActivo(true);
        producto.setClub(buscarClub(request.getClubId()));

        return toResponse(productoRepository.save(producto));
    }

    public ProductoResponse actualizar(Long id, ProductoRequest datos) {
        Producto producto = buscarActivo(id);
        producto.setNombre(datos.getNombre());
        producto.setDescripcion(datos.getDescripcion());
        producto.setPrecio(datos.getPrecio());
        producto.setStock(datos.getStock());
        producto.setImagenUrl(datos.getImagenUrl());
        producto.setClub(buscarClub(datos.getClubId()));

        return toResponse(productoRepository.save(producto));
    }

    public void eliminar(Long id) {
        Producto producto = buscarActivo(id);
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    public boolean asociarCategoria(Long productoId, Long categoriaId) {
        Optional<Producto> producto = productoRepository.findById(productoId);
        Optional<Categoria> categoria = categoriaRepository.findById(categoriaId);

        if (producto.isEmpty() || categoria.isEmpty()) {
            return false;
        }

        producto.get().getCategorias().add(categoria.get());
        productoRepository.save(producto.get());
        return true;
    }

    public boolean desasociarCategoria(Long productoId, Long categoriaId) {
        Optional<Producto> producto = productoRepository.findById(productoId);
        Optional<Categoria> categoria = categoriaRepository.findById(categoriaId);

        if (producto.isEmpty() || categoria.isEmpty()) {
            return false;
        }

        producto.get().getCategorias().remove(categoria.get());
        productoRepository.save(producto.get());
        return true;
    }

    private Producto buscarActivo(Long id) {
        return productoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto " + id + " no encontrado"));
    }

    // clubId null = producto sin club. Un id que no existe es un error: antes quedaba sin club sin avisar.
    private Club buscarClub(Long clubId) {
        if (clubId == null) {
            return null;
        }
        return clubRepository.findById(clubId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Club " + clubId + " no encontrado"));
    }

    private ProductoResponse toResponse(Producto producto) {
        return new ProductoResponse(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getStock(),
                producto.getImagenUrl(),
                producto.getActivo(),
                toResponse(producto.getClub()));
    }

    private ClubResponse toResponse(Club club) {
        if (club == null) {
            return null;
        }
        return new ClubResponse(club.getId(), club.getNombre(), club.getPais(), club.getEscudoUrl());
    }

}
