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

// La cantidad > 0 la valida @Valid en el controller. Acá quedan las reglas que
// necesitan la base: que el usuario y el producto existan y que alcance el stock.
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
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new RecursoNoEncontradoException("Usuario " + usuarioId + " no encontrado");
        }

        // El carrito se crea recién con el primer item: un usuario que no agregó nada tiene el carrito vacío.
        return carritoRepository.findByUsuarioId(usuarioId)
                .map(this::toResponse)
                .orElse(new CarritoResponse(null, usuarioId, List.of(), 0.0));
    }

    public CarritoResponse agregarItem(Long usuarioId, AgregarItemRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario " + usuarioId + " no encontrado"));

        Producto producto = productoRepository.findByIdAndActivoTrue(request.getProductoId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Producto " + request.getProductoId() + " no encontrado"));

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

        validarStock(producto, cantidadTotal);

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
        Carrito carrito = buscarCarrito(usuarioId);
        ItemCarrito item = buscarItemDelCarrito(carrito, itemId);

        validarStock(item.getProducto(), request.getCantidad());

        item.setCantidad(request.getCantidad());
        Carrito guardado = carritoRepository.save(carrito);
        return toResponse(guardado);
    }

    public void eliminarItem(Long usuarioId, Long itemId) {
        Carrito carrito = buscarCarrito(usuarioId);
        ItemCarrito item = buscarItemDelCarrito(carrito, itemId);

        carrito.getItems().remove(item);
        carritoRepository.save(carrito);
    }

    public void vaciarCarrito(Long usuarioId) {
        Carrito carrito = buscarCarrito(usuarioId);

        carrito.getItems().clear();
        carritoRepository.save(carrito);
    }

    private Carrito buscarCarrito(Long usuarioId) {
        return carritoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("El usuario " + usuarioId + " no tiene carrito"));
    }

    // Stream en memoria sobre los items de UN carrito ya cargado, no sobre toda la tabla.
    private ItemCarrito buscarItemDelCarrito(Carrito carrito, Long itemId) {
        return carrito.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "El item " + itemId + " no pertenece a este carrito"));
    }

    private void validarStock(Producto producto, int cantidadPedida) {
        if (cantidadPedida > producto.getStock()) {
            throw new SolicitudInvalidaException("Stock insuficiente para " + producto.getNombre()
                    + ": disponible " + producto.getStock() + ", pedido " + cantidadPedida);
        }
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
