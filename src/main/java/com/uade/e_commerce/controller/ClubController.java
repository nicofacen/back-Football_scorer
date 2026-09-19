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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uade.e_commerce.dto.ClubRequest;
import com.uade.e_commerce.dto.ClubResponse;
import com.uade.e_commerce.service.ClubService;

import jakarta.validation.Valid;

// http://localhost:8080/api/clubes
@RestController
@RequestMapping("/api/clubes")
public class ClubController {

    private final ClubService clubService;

    ClubController(ClubService clubService) {
        this.clubService = clubService;
    }

    @GetMapping()
    public ResponseEntity<List<ClubResponse>> listar(@RequestParam(required = false) String pais) {
        return ResponseEntity.ok(clubService.listar(pais));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClubResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(clubService.obtenerPorId(id));
    }

    @PostMapping()
    public ResponseEntity<ClubResponse> crear(@Valid @RequestBody ClubRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clubService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClubResponse> actualizar(@PathVariable Long id,
                                                     @Valid @RequestBody ClubRequest request) {
        return ResponseEntity.ok(clubService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        clubService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

}
