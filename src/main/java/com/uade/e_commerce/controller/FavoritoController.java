package com.uade.e_commerce.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.e_commerce.dto.ProductoResponse;
import com.uade.e_commerce.service.FavoritoService;

@RestController
@RequestMapping("/api/favoritos")
public class FavoritoController {

    private final FavoritoService favoritoService;

    public FavoritoController(FavoritoService favoritoService) {
        this.favoritoService = favoritoService;
    }

    @GetMapping
    public ResponseEntity<List<ProductoResponse>> listar(Authentication auth) {
        return ResponseEntity.ok(
                favoritoService.listar(auth.getName())
        );
    }

    @PostMapping("/{productoId}")
    public ResponseEntity<ProductoResponse> agregar(
            Authentication auth,
            @PathVariable Long productoId) {

        ProductoResponse response =
                favoritoService.agregar(auth.getName(), productoId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @DeleteMapping("/{productoId}")
    public ResponseEntity<Void> eliminar(
            Authentication auth,
            @PathVariable Long productoId) {

        favoritoService.eliminar(auth.getName(), productoId);

        return ResponseEntity.noContent().build();
    }
}