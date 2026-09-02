package com.uade.e_commerce.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.uade.e_commerce.dto.ActualizarCantidadRequest;
import com.uade.e_commerce.dto.AgregarItemRequest;
import com.uade.e_commerce.dto.CarritoResponse;
import com.uade.e_commerce.dto.ItemCarritoResponse;
import com.uade.e_commerce.exception.RecursoNoEncontradoException;
import com.uade.e_commerce.exception.SolicitudInvalidaException;
import com.uade.e_commerce.model.Carrito;
import com.uade.e_commerce.model.ItemCarrito;
import com.uade.e_commerce.model.Producto;
import com.uade.e_commerce.model.Usuario;
import com.uade.e_commerce.repository.CarritoRepository;
import com.uade.e_commerce.repository.ProductoRepository;
import com.uade.e_commerce.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    public CarritoService(CarritoRepository carritoRepository, UsuarioRepository usuarioRepository,
                           ProductoRepository productoRepository) {
        this.carritoRepository = carritoRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
    }

    public CarritoResponse obtenerCarrito(Long usuarioId) {
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("El usuario no tiene carrito"));
        return toResponse(carrito);
    }

    public CarritoResponse agregarItem(Long usuarioId, AgregarItemRequest request) {
        if (request.getCantidad() == null || request.getCantidad() <= 0) {
            throw new SolicitudInvalidaException("La cantidad debe ser mayor a 0");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado"));

        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> {
                    Carrito nuevo = new Carrito();
                    nuevo.setUsuario(usuario);
                    return nuevo;
                });

        Optional<ItemCarrito> itemExistente = carrito.getItems().stream()
                .filter(item -> item.getProducto().getId().equals(producto.getId()))
                .findFirst();

        int cantidadActual = itemExistente.map(ItemCarrito::getCantidad).orElse(0);
        int cantidadTotal = cantidadActual + request.getCantidad();

        if (cantidadTotal > producto.getStock()) {
            throw new SolicitudInvalidaException("No hay stock suficiente");
        }

        if (itemExistente.isPresent()) {
            itemExistente.get().setCantidad(cantidadTotal);
        } else {
            ItemCarrito nuevoItem = new ItemCarrito();
            nuevoItem.setCarrito(carrito);
            nuevoItem.setProducto(producto);
            nuevoItem.setCantidad(request.getCantidad());
            carrito.getItems().add(nuevoItem);
        }

        Carrito guardado = carritoRepository.save(carrito);
        return toResponse(guardado);
    }

    public CarritoResponse actualizarCantidad(Long usuarioId, Long itemId, ActualizarCantidadRequest request) {
        if (request.getCantidad() == null || request.getCantidad() <= 0) {
            throw new SolicitudInvalidaException("La cantidad debe ser mayor a 0");
        }

        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("El usuario no tiene carrito"));

        ItemCarrito item = buscarItemDelCarrito(carrito, itemId);

        if (request.getCantidad() > item.getProducto().getStock()) {
            throw new SolicitudInvalidaException("No hay stock suficiente");
        }

        item.setCantidad(request.getCantidad());
        Carrito guardado = carritoRepository.save(carrito);
        return toResponse(guardado);
    }

    public void eliminarItem(Long usuarioId, Long itemId) {
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("El usuario no tiene carrito"));

        ItemCarrito item = buscarItemDelCarrito(carrito, itemId);

        carrito.getItems().remove(item);
        carritoRepository.save(carrito);
    }

    public void vaciarCarrito(Long usuarioId) {
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("El usuario no tiene carrito"));

        carrito.getItems().clear();
        carritoRepository.save(carrito);
    }

    private ItemCarrito buscarItemDelCarrito(Carrito carrito, Long itemId) {
        return carrito.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RecursoNoEncontradoException("El item no pertenece a este carrito"));
    }

    private CarritoResponse toResponse(Carrito carrito) {
        List<ItemCarritoResponse> items = carrito.getItems().stream()
                .map(this::toItemResponse)
                .toList();

        double total = items.stream()
                .mapToDouble(ItemCarritoResponse::getSubtotal)
                .sum();

        return new CarritoResponse(carrito.getId(), carrito.getUsuario().getId(), items, total);
    }

    private ItemCarritoResponse toItemResponse(ItemCarrito item) {
        double subtotal = item.getProducto().getPrecio() * item.getCantidad();
        return new ItemCarritoResponse(
                item.getId(),
                item.getProducto().getId(),
                item.getProducto().getNombre(),
                item.getProducto().getPrecio(),
                item.getCantidad(),
                subtotal);
    }

}
