package com.uade.e_commerce.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.uade.e_commerce.model.Producto;
import com.uade.e_commerce.repository.ProductoRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<Producto> getAllProductos() {
        return productoRepository.findAll();
    }

    public Optional<Producto> getProductoById(Long id) {
        return productoRepository.findById(id);
    }

    public Producto crearProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    public Optional<Producto> actualizarProducto(Long id, Producto datos) {
        return productoRepository.findById(id)
                .map(producto -> {
                    producto.setNombre(datos.getNombre());
                    producto.setDescripcion(datos.getDescripcion());
                    producto.setPrecio(datos.getPrecio());
                    return productoRepository.save(producto);
                });
    }

    public boolean eliminarProducto(Long id) {
        if (!productoRepository.existsById(id)) {
            return false;
        }
        productoRepository.deleteById(id);
        return true;
    }

}
