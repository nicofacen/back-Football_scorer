package com.uade.e_commerce.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

import jakarta.validation.Valid;

// El usuario nunca viaja en la URL: sale del token. Spring inyecta el Authentication
// que dejó el JwtAuthenticationFilter en el SecurityContext; getName() es el "sub"
// del JWT (el email). Así nadie puede ver el carrito de otro cambiando un número.
@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    private final CarritoService carritoService;

    CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @GetMapping
    public ResponseEntity<CarritoResponse> obtener(Authentication auth) {
        return ResponseEntity.ok(carritoService.obtenerCarrito(auth.getName()));
    }

    @PostMapping("/items")
    public ResponseEntity<CarritoResponse> agregarItem(Authentication auth,
                                                         @Valid @RequestBody AgregarItemRequest request) {
        CarritoResponse response = carritoService.agregarItem(auth.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CarritoResponse> actualizarCantidad(Authentication auth,
                                                                @PathVariable Long itemId,
                                                                @Valid @RequestBody ActualizarCantidadRequest request) {
        return ResponseEntity.ok(carritoService.actualizarCantidad(auth.getName(), itemId, request));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> eliminarItem(Authentication auth, @PathVariable Long itemId) {
        carritoService.eliminarItem(auth.getName(), itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> vaciarCarrito(Authentication auth) {
        carritoService.vaciarCarrito(auth.getName());
        return ResponseEntity.noContent().build();
    }

}
