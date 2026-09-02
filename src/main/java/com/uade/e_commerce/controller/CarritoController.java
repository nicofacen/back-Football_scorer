package com.uade.e_commerce.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.e_commerce.dto.ActualizarCantidadRequest;
import com.uade.e_commerce.dto.AgregarItemRequest;
import com.uade.e_commerce.dto.CarritoResponse;
import com.uade.e_commerce.service.CarritoService;

@RestController
@RequestMapping("/api/carritos")
public class CarritoController {

    private final CarritoService carritoService;

    CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<CarritoResponse> obtener(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(carritoService.obtenerCarrito(usuarioId));
    }

    @PostMapping("/{usuarioId}/items")
    public ResponseEntity<CarritoResponse> agregarItem(@PathVariable Long usuarioId,
                                                         @RequestBody AgregarItemRequest request) {
        CarritoResponse response = carritoService.agregarItem(usuarioId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{usuarioId}/items/{itemId}")
    public ResponseEntity<CarritoResponse> actualizarCantidad(@PathVariable Long usuarioId,
                                                                @PathVariable Long itemId,
                                                                @RequestBody ActualizarCantidadRequest request) {
        return ResponseEntity.ok(carritoService.actualizarCantidad(usuarioId, itemId, request));
    }

    @DeleteMapping("/{usuarioId}/items/{itemId}")
    public ResponseEntity<Void> eliminarItem(@PathVariable Long usuarioId, @PathVariable Long itemId) {
        carritoService.eliminarItem(usuarioId, itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{usuarioId}")
    public ResponseEntity<Void> vaciarCarrito(@PathVariable Long usuarioId) {
        carritoService.vaciarCarrito(usuarioId);
        return ResponseEntity.noContent().build();
    }

}
