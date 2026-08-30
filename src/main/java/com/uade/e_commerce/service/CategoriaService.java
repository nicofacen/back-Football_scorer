package com.uade.e_commerce.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.uade.e_commerce.dto.CategoriaRequest;
import com.uade.e_commerce.dto.CategoriaResponse;
import com.uade.e_commerce.model.Categoria;
import com.uade.e_commerce.repository.CategoriaRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<CategoriaResponse> listar() {
        return categoriaRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public Optional<CategoriaResponse> obtenerPorId(Long id) {
        return categoriaRepository.findById(id)
                .map(this::toResponse);
    }

    public Optional<CategoriaResponse> crear(CategoriaRequest request) {
        if (!esValido(request) || categoriaRepository.existsByNombre(request.getNombre())) {
            return Optional.empty();
        }

        Categoria categoria = new Categoria();
        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());

        return Optional.of(toResponse(categoriaRepository.save(categoria)));
    }

    public Optional<CategoriaResponse> actualizar(Long id, CategoriaRequest datos) {
        return categoriaRepository.findById(id)
                .map(categoria -> {
                    categoria.setNombre(datos.getNombre());
                    categoria.setDescripcion(datos.getDescripcion());
                    return categoriaRepository.save(categoria);
                })
                .map(this::toResponse);
    }

    public boolean eliminar(Long id) {
        if (!categoriaRepository.existsById(id)) {
            return false;
        }
        categoriaRepository.deleteById(id);
        return true;
    }

    public boolean esValido(CategoriaRequest request) {
        return request.getNombre() != null && !request.getNombre().isBlank();
    }

    public boolean nombreDuplicado(Long id, String nombre) {
        return categoriaRepository.existsByNombreAndIdNot(nombre, id);
    }

    private CategoriaResponse toResponse(Categoria categoria) {
        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getDescripcion());
    }

}
