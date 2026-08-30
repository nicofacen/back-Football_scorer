package com.uade.e_commerce.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.uade.e_commerce.dto.ProductoRequest;
import com.uade.e_commerce.dto.ProductoResponse;
import com.uade.e_commerce.model.Categoria;
import com.uade.e_commerce.model.Producto;
import com.uade.e_commerce.repository.CategoriaRepository;
import com.uade.e_commerce.repository.ProductoRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public List<ProductoResponse> listar() {
        return productoRepository.findByActivoTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public Optional<ProductoResponse> obtenerPorId(Long id) {
        return productoRepository.findById(id)
                .filter(Producto::getActivo)
                .map(this::toResponse);
    }

    public Optional<ProductoResponse> crear(ProductoRequest request) {
        if (!esValido(request)) {
            return Optional.empty();
        }

        Producto producto = new Producto();
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setImagenUrl(request.getImagenUrl());
        producto.setActivo(true);

        return Optional.of(toResponse(productoRepository.save(producto)));
    }

    public Optional<ProductoResponse> actualizar(Long id, ProductoRequest datos) {
        return productoRepository.findById(id)
                .filter(Producto::getActivo)
                .map(producto -> {
                    producto.setNombre(datos.getNombre());
                    producto.setDescripcion(datos.getDescripcion());
                    producto.setPrecio(datos.getPrecio());
                    producto.setStock(datos.getStock());
                    producto.setImagenUrl(datos.getImagenUrl());
                    return productoRepository.save(producto);
                })
                .map(this::toResponse);
    }

    public boolean eliminar(Long id) {
        return productoRepository.findById(id)
                .filter(Producto::getActivo)
                .map(producto -> {
                    producto.setActivo(false);
                    productoRepository.save(producto);
                    return true;
                })
                .orElse(false);
    }

    public boolean esValido(ProductoRequest request) {
        return request.getNombre() != null
                && !request.getNombre().isBlank()
                && request.getPrecio() != null
                && request.getPrecio() > 0
                && request.getStock() != null
                && request.getStock() >= 0;
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

    private ProductoResponse toResponse(Producto producto) {
        return new ProductoResponse(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getStock(),
                producto.getImagenUrl(),
                producto.getActivo());
    }

}
