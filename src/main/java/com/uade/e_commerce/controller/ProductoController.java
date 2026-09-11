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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uade.e_commerce.dto.ProductoRequest;
import com.uade.e_commerce.dto.ProductoResponse;
import com.uade.e_commerce.service.ProductoService;

import jakarta.validation.Valid;

import java.util.List;


// http://localhost:8080/api/productos
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }


    @GetMapping()
    public ResponseEntity<List<ProductoResponse>> listar(@RequestParam(required = false) String nombre,
                                                           @RequestParam(required = false) Long clubId,
                                                           @RequestParam(required = false) Long categoriaId,
                                                           @RequestParam(required = false) Double precioMax) {
        return ResponseEntity.ok(productoService.buscar(nombre, clubId, categoriaId, precioMax));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.obtenerPorId(id));
    }

    @PostMapping()
    public ResponseEntity<ProductoResponse> crear(@Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> actualizar(@PathVariable Long id,
                                                       @Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.ok(productoService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{productoId}/categorias/{categoriaId}")
    public ResponseEntity<Void> asociarCategoria(@PathVariable Long productoId, @PathVariable Long categoriaId) {
        if (!productoService.asociarCategoria(productoId, categoriaId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{productoId}/categorias/{categoriaId}")
    public ResponseEntity<Void> desasociarCategoria(@PathVariable Long productoId, @PathVariable Long categoriaId) {
        if (!productoService.desasociarCategoria(productoId, categoriaId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

}
