package com.uade.e_commerce.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.uade.e_commerce.dto.ClubResponse;
import com.uade.e_commerce.dto.ProductoRequest;
import com.uade.e_commerce.dto.ProductoResponse;
import com.uade.e_commerce.exception.RecursoNoEncontradoException;
import com.uade.e_commerce.exception.SolicitudInvalidaException;
import com.uade.e_commerce.model.Categoria;
import com.uade.e_commerce.model.Club;
import com.uade.e_commerce.model.Producto;
import com.uade.e_commerce.repository.CategoriaRepository;
import com.uade.e_commerce.repository.ClubRepository;
import com.uade.e_commerce.repository.ProductoRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ClubRepository clubRepository;

    public ProductoService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository,
                            ClubRepository clubRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.clubRepository = clubRepository;
    }

    public List<ProductoResponse> listar() {
        return buscar(null, null, null, null);
    }

    public List<ProductoResponse> buscar(String nombre, Long clubId, Long categoriaId, Double precioMax) {
        return seleccionarBusqueda(nombre, clubId, categoriaId, precioMax)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Sin filtros o con uno solo, un query method derivado alcanza y da un SQL mas simple.
    // Con 2 o mas combinados hace falta el @Query de ProductoRepository.buscar(): armar un
    // query method por combinacion escalaria a 2^4 = 16 metodos.
    private List<Producto> seleccionarBusqueda(String nombre, Long clubId, Long categoriaId, Double precioMax) {
        long filtrosActivos = Stream.of(nombre, clubId, categoriaId, precioMax).filter(Objects::nonNull).count();

        if (filtrosActivos == 0) {
            return productoRepository.findByActivoTrue();
        }
        if (filtrosActivos > 1) {
            return productoRepository.buscar(nombre, clubId, categoriaId, precioMax);
        }
        if (nombre != null) {
            return productoRepository.findByActivoTrueAndNombreContainingIgnoreCase(nombre);
        }
        if (clubId != null) {
            return productoRepository.findByActivoTrueAndClubId(clubId);
        }
        if (categoriaId != null) {
            return productoRepository.findByActivoTrueAndCategoriasId(categoriaId);
        }
        return productoRepository.findByActivoTrueAndPrecioLessThanEqual(precioMax);
    }

    public ProductoResponse obtenerPorId(Long id) {
        return toResponse(buscarActivo(id));
    }

    public ProductoResponse crear(ProductoRequest request) {
        Producto producto = new Producto();
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setImagenUrl(request.getImagenUrl());
        producto.setActivo(true);
        producto.setClub(buscarClub(request.getClubId()));

        return toResponse(productoRepository.save(producto));
    }

    public ProductoResponse actualizar(Long id, ProductoRequest datos) {
        Producto producto = buscarActivo(id);
        producto.setNombre(datos.getNombre());
        producto.setDescripcion(datos.getDescripcion());
        producto.setPrecio(datos.getPrecio());
        producto.setStock(datos.getStock());
        producto.setImagenUrl(datos.getImagenUrl());
        producto.setClub(buscarClub(datos.getClubId()));

        return toResponse(productoRepository.save(producto));
    }

    public void eliminar(Long id) {
        Producto producto = buscarActivo(id);
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    public ProductoResponse actualizarStock(Long id, Integer stock) {
        Producto producto = buscarActivo(id);
        producto.setStock(stock);
        return toResponse(productoRepository.save(producto));
    }

    // Lo llama OrdenService.crearDesdeCarrito() (turno 6) dentro de su @Transactional.
    // Valida y descuenta en el mismo método, y LANZA en vez de devolver false:
    // la excepción es lo que dispara el rollback de los items que ya se descontaron.
    public void descontarStock(Long id, int cantidad) {
        if (cantidad <= 0) {
            throw new SolicitudInvalidaException("La cantidad a descontar debe ser mayor a 0");
        }

        Producto producto = buscarActivo(id);
        if (producto.getStock() < cantidad) {
            throw new SolicitudInvalidaException("Stock insuficiente para " + producto.getNombre()
                    + ": disponible " + producto.getStock() + ", pedido " + cantidad);
        }

        producto.setStock(producto.getStock() - cantidad);
        productoRepository.save(producto);
    }

    public void asociarCategoria(Long productoId, Long categoriaId) {
        Producto producto = buscarActivo(productoId);
        Categoria categoria = buscarCategoria(categoriaId);

        // Pendiente turno 2 (Mati): si add() devuelve false la categoría ya estaba → RecursoDuplicadoException (409).
        producto.getCategorias().add(categoria);
        productoRepository.save(producto);
    }

    public void desasociarCategoria(Long productoId, Long categoriaId) {
        Producto producto = buscarActivo(productoId);
        Categoria categoria = buscarCategoria(categoriaId);

        if (!producto.getCategorias().remove(categoria)) {
            throw new RecursoNoEncontradoException(
                    "La categoría " + categoriaId + " no está asociada al producto " + productoId);
        }
        productoRepository.save(producto);
    }

    private Producto buscarActivo(Long id) {
        return productoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto " + id + " no encontrado"));
    }

    private Categoria buscarCategoria(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría " + id + " no encontrada"));
    }

    // clubId null = producto sin club. Un id que no existe es un error: antes quedaba sin club sin avisar.
    private Club buscarClub(Long clubId) {
        if (clubId == null) {
            return null;
        }
        return clubRepository.findById(clubId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Club " + clubId + " no encontrado"));
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
                toResponse(producto.getClub()));
    }

    private ClubResponse toResponse(Club club) {
        if (club == null) {
            return null;
        }
        return new ClubResponse(club.getId(), club.getNombre(), club.getPais(), club.getEscudoUrl());
    }

}
