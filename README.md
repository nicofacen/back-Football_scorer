# back-Football_scorer — TPO Aplicaciones Interactivas (UADE)

Backend de un e-commerce de productos asociados a clubes de fútbol, resuelto como monolito REST en tres capas (controller → service → repository → model).
Alcance funcional: registro y login de usuarios con roles ADMIN/CLIENTE, ABM de productos, categorías y clubes, y carrito por usuario (agregar ítem, actualizar cantidad, eliminar ítem y vaciar).
Stack: Java 17, Spring Boot (Web MVC, Data JPA, Lombok, Actuator), H2 en memoria para desarrollo y MySQL para producción. El frontend React vive fuera de este repositorio.
Endpoints bajo `/api`: `/usuarios`, `/productos`, `/categorias`, `/clubes`, `/carritos`. Los errores se manejan de forma centralizada con un `@RestControllerAdvice` (`GlobalExceptionHandler`), que traduce cada excepción a un `ResponseEntity` con un cuerpo `ErrorResponse` estandarizado (`timestamp`, `status`, `error`, `mensaje`, `path`).

## Códigos de error

| Excepción | Código HTTP |
|---|---|
| `RecursoNoEncontradoException` | 404 |
| `SolicitudInvalidaException` | 400 |
| `RecursoDuplicadoException` | 409 |
| `CredencialesInvalidasException` | 401 |
| `MethodArgumentNotValidException` (fallo de `@Valid`) | 400 |
| Cualquier otra excepción no controlada | 500 |


Cómo correrlo: `./mvnw spring-boot:run` y la app queda en `http://localhost:8080` (consola H2 en `/h2-console`). Las pruebas manuales de endpoints están en `demo.http`.
Fuera de alcance por ahora: órdenes de compra, pasarela de pagos y autenticación con token (JWT); la base H2 se reinicia al detener la aplicación.
