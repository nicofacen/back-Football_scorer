package com.uade.e_commerce.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.uade.e_commerce.dto.ClubResponse;
import com.uade.e_commerce.dto.ProductoResponse;
import com.uade.e_commerce.exception.RecursoDuplicadoException;
import com.uade.e_commerce.exception.RecursoNoEncontradoException;
import com.uade.e_commerce.model.Club;
import com.uade.e_commerce.model.Producto;
import com.uade.e_commerce.model.Usuario;
import com.uade.e_commerce.repository.ProductoRepository;
import com.uade.e_commerce.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class FavoritoService {

    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    public FavoritoService(UsuarioRepository usuarioRepository,
                           ProductoRepository productoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
    }

    public List<ProductoResponse> listar(String email) {
        Usuario usuario = buscarUsuario(email);

        return usuario.getFavoritos().stream()
                .filter(producto -> Boolean.TRUE.equals(producto.getActivo()))
                .map(this::toResponse)
                .toList();
    }

    public ProductoResponse agregar(String email, Long productoId) {
        Usuario usuario = buscarUsuario(email);

        Producto producto = productoRepository.findByIdAndActivoTrue(productoId)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Producto " + productoId + " no encontrado"));

        if (!usuario.getFavoritos().add(producto)) {
            throw new RecursoDuplicadoException(
                    "El producto " + productoId + " ya está en favoritos");
        }

        usuarioRepository.save(usuario);

        return toResponse(producto);
    }

    public void eliminar(String email, Long productoId) {
        Usuario usuario = buscarUsuario(email);

        boolean eliminado = usuario.getFavoritos()
                .removeIf(producto -> producto.getId().equals(productoId));

        if (!eliminado) {
            throw new RecursoNoEncontradoException(
                    "El producto " + productoId + " no está en favoritos");
        }

        usuarioRepository.save(usuario);
    }

    private Usuario buscarUsuario(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Usuario " + email + " no encontrado"));
    }

    private ProductoResponse toResponse(Producto producto) {
        return new ProductoResponse(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getStock(),
                producto.getImagenUrl(),
                producto.getActivo(),
                toClubResponse(producto.getClub())
        );
    }

    private ClubResponse toClubResponse(Club club) {
        if (club == null) {
            return null;
        }

        return new ClubResponse(
                club.getId(),
                club.getNombre(),
                club.getPais(),
                club.getEscudoUrl()
        );
    }
}