package com.uade.e_commerce.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.uade.e_commerce.dto.ItemOrdenResponse;
import com.uade.e_commerce.dto.OrdenResponse;
import com.uade.e_commerce.exception.RecursoNoEncontradoException;
import com.uade.e_commerce.exception.SolicitudInvalidaException;
import com.uade.e_commerce.model.Carrito;
import com.uade.e_commerce.model.EstadoOrden;
import com.uade.e_commerce.model.ItemCarrito;
import com.uade.e_commerce.model.ItemOrden;
import com.uade.e_commerce.model.Orden;
import com.uade.e_commerce.repository.CarritoRepository;
import com.uade.e_commerce.repository.OrdenRepository;

import jakarta.transaction.Transactional;

// El usuario llega como email (el "sub" del JWT) desde el controller, igual que en CarritoService.
@Service
@Transactional
public class OrdenService {

    private final OrdenRepository ordenRepository;
    private final CarritoRepository carritoRepository;
    private final ProductoService productoService;

    public OrdenService(OrdenRepository ordenRepository, CarritoRepository carritoRepository,
                        ProductoService productoService) {
        this.ordenRepository = ordenRepository;
        this.carritoRepository = carritoRepository;
        this.productoService = productoService;
    }

    // Todo el checkout es una sola transaccion: si descontarStock() falla en el item 4,
    // los UPDATE de stock de los items 1, 2 y 3 se deshacen (rollback) y no queda ni
    // orden a medias ni stock perdido.
    public OrdenResponse crearDesdeCarrito(String email) {
        Carrito carrito = carritoRepository.findByUsuarioEmail(email)
                .orElseThrow(() -> new RecursoNoEncontradoException("El usuario " + email + " no tiene carrito"));

        if (carrito.getItems().isEmpty()) {
            throw new SolicitudInvalidaException("El carrito está vacío");
        }

        Orden orden = new Orden();
        orden.setUsuario(carrito.getUsuario());
        orden.setFecha(LocalDateTime.now());
        orden.setEstado(EstadoOrden.PENDIENTE);

        for (ItemCarrito itemCarrito : carrito.getItems()) {
            productoService.descontarStock(itemCarrito.getProducto().getId(), itemCarrito.getCantidad());

            ItemOrden itemOrden = new ItemOrden();
            itemOrden.setOrden(orden);
            itemOrden.setProducto(itemCarrito.getProducto());
            itemOrden.setCantidad(itemCarrito.getCantidad());
            // Foto del precio al momento de la compra (ver comentario en ItemOrden).
            itemOrden.setPrecioUnitario(itemCarrito.getProducto().getPrecio());
            orden.getItems().add(itemOrden);
        }

        double total = orden.getItems().stream()
                .mapToDouble(item -> item.getPrecioUnitario() * item.getCantidad())
                .sum();
        orden.setTotal(total);

        Orden guardada = ordenRepository.save(orden);

        carrito.getItems().clear();
        carritoRepository.save(carrito);

        return toResponse(guardada);
    }

    public List<OrdenResponse> listarMias(String email) {
        return ordenRepository.findByUsuarioEmailOrderByFechaDesc(email).stream()
                .map(this::toResponse)
                .toList();
    }

    // SecurityConfig solo sabe que /api/ordenes/{id} exige token; no puede saber si ESA
    // orden es del que llama. Ese chequeo va aca: no es suya -> 403 (no 404: la orden existe).
    public OrdenResponse obtenerMia(Long id, String email) {
        Orden orden = ordenRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Orden " + id + " no encontrada"));

        if (!orden.getUsuario().getEmail().equals(email)) {
            throw new AccessDeniedException("La orden " + id + " no pertenece a este usuario");
        }

        return toResponse(orden);
    }

    private OrdenResponse toResponse(Orden orden) {
        List<ItemOrdenResponse> items = orden.getItems().stream()
                .map(this::toItemResponse)
                .toList();

        return new OrdenResponse(
                orden.getId(),
                orden.getUsuario().getId(),
                orden.getFecha(),
                orden.getEstado(),
                items,
                orden.getTotal());
    }

    private ItemOrdenResponse toItemResponse(ItemOrden item) {
        double subtotal = item.getPrecioUnitario() * item.getCantidad();
        return new ItemOrdenResponse(
                item.getId(),
                item.getProducto().getId(),
                item.getProducto().getNombre(),
                item.getPrecioUnitario(),
                item.getCantidad(),
                subtotal);
    }

}
