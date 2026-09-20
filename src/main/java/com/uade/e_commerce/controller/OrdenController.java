package com.uade.e_commerce.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.e_commerce.dto.OrdenResponse;
import com.uade.e_commerce.service.OrdenService;

// Sin request body: la orden se arma desde el carrito del usuario del token.
@RestController
@RequestMapping("/api/ordenes")
public class OrdenController {

    private final OrdenService ordenService;

    OrdenController(OrdenService ordenService) {
        this.ordenService = ordenService;
    }

    @PostMapping
    public ResponseEntity<OrdenResponse> crear(Authentication auth) {
        OrdenResponse response = ordenService.crearDesdeCarrito(auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<OrdenResponse>> listar(Authentication auth) {
        return ResponseEntity.ok(ordenService.listarMias(auth.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenResponse> obtener(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(ordenService.obtenerMia(id, auth.getName()));
    }

}
