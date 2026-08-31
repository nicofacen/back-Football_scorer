package com.uade.e_commerce.controller;

import java.util.List;

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

import com.uade.e_commerce.dto.ClubRequest;
import com.uade.e_commerce.dto.ClubResponse;
import com.uade.e_commerce.service.ClubService;

// http://localhost:8080/api/clubes
@RestController
@RequestMapping("/api/clubes")
public class ClubController {

    private final ClubService clubService;

    ClubController(ClubService clubService) {
        this.clubService = clubService;
    }

    @GetMapping()
    public ResponseEntity<List<ClubResponse>> listar() {
        return ResponseEntity.ok(clubService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClubResponse> obtenerPorId(@PathVariable Long id) {
        return clubService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping()
    public ResponseEntity<ClubResponse> crear(@RequestBody ClubRequest request) {
        return clubService.crear(request)
                .map(club -> ResponseEntity.status(HttpStatus.CREATED).body(club))
                .orElse(ResponseEntity.badRequest().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClubResponse> actualizar(@PathVariable Long id,
                                                     @RequestBody ClubRequest request) {
        if (!clubService.esValido(request)) {
            return ResponseEntity.badRequest().build();
        }
        if (clubService.nombreDuplicado(id, request.getNombre())) {
            return ResponseEntity.badRequest().build();
        }
        return clubService.actualizar(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!clubService.eliminar(id)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

}
