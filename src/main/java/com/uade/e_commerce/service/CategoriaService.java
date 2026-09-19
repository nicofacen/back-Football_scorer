package com.uade.e_commerce.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.uade.e_commerce.dto.CategoriaRequest;
import com.uade.e_commerce.dto.CategoriaResponse;
import com.uade.e_commerce.exception.RecursoDuplicadoException;
import com.uade.e_commerce.exception.RecursoNoEncontradoException;
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

    public CategoriaResponse obtenerPorId(Long id) {
        return toResponse(buscar(id));
    }

    public CategoriaResponse crear(CategoriaRequest request) {
        if (categoriaRepository.existsByNombre(request.getNombre())) {
            throw new RecursoDuplicadoException("Ya existe una categoría con el nombre " + request.getNombre());
        }

        Categoria categoria = new Categoria();
        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());

        return toResponse(categoriaRepository.save(categoria));
    }

    public CategoriaResponse actualizar(Long id, CategoriaRequest datos) {
        Categoria categoria = buscar(id);
        if (categoriaRepository.existsByNombreAndIdNot(datos.getNombre(), id)) {
            throw new RecursoDuplicadoException("Ya existe una categoría con el nombre " + datos.getNombre());
        }

        categoria.setNombre(datos.getNombre());
        categoria.setDescripcion(datos.getDescripcion());

        return toResponse(categoriaRepository.save(categoria));
    }

    public void eliminar(Long id) {
        Categoria categoria = buscar(id);
        categoriaRepository.delete(categoria);
    }

    private Categoria buscar(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría " + id + " no encontrada"));
    }

    private CategoriaResponse toResponse(Categoria categoria) {
        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getDescripcion());
    }

}
