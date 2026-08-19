# Contexto de la materia — Aplicaciones Interactivas (UADE)

Resumen de los conceptos vistos en clase y en el material de cátedra.
**Este archivo define el alcance permitido:** no usar conceptos que no estén acá.

---

## 1. Protocolos de comunicación

Un **protocolo** es un conjunto de reglas que define cómo se comunican dos
sistemas. En backend organiza la comunicación entre cliente y servidor.

**HTTP** es el protocolo central. Opera sobre TCP/IP. Define dos roles:
el cliente pide, el servidor responde.

**Regla clave:** cada solicitud tiene UNA única respuesta. Ni múltiples
respuestas por solicitud, ni solicitudes sin respuesta.

### Flujo de una solicitud

1. Cliente hace la solicitud (ej: `GET /productos`)
2. Servidor recibe, procesa y accede a la base si hace falta
3. Servidor devuelve la respuesta (datos o error)
4. Cliente interpreta y muestra

### Métodos HTTP = CRUD

| CRUD | Operación | Método |
|------|-----------|--------|
| C | Create | POST |
| R | Read | GET |
| U | Update | PUT |
| D | Delete | DELETE |

### Códigos de estado

| Código | Significado |
|--------|-------------|
| 200 | OK — respuesta exitosa |
| 201 | Created — POST exitoso |
| 204 | No Content — procesado sin contenido que devolver (DELETE) |
| 400 | Bad Request — sintaxis o formato inválido |
| 401 / 403 | Unauthorized / Forbidden |
| 404 | Not Found — el recurso no existe |
| 500 | Internal Server Error |

### REST

Estilo arquitectónico para diseñar APIs **basadas en recursos**. El backend
expone rutas accesibles por HTTP (endpoints / URI).

> "HTTP es el canal, la API es el intérprete."

**La URL dice QUÉ (sustantivo). El método HTTP dice QUÉ HACER (verbo).**

```
GET    /productos      → listar
POST   /productos      → crear
GET    /productos/{id} → traer uno
DELETE /productos/{id} → borrar
```

### Errores comunes / buenas prácticas

| Error | Buena práctica |
|-------|----------------|
| No devolver una respuesta | Devolver siempre respuesta con código adecuado |
| Usar mal los métodos HTTP | Usar correctamente los códigos de estado |
| Ignorar los códigos de estado | Estructurar respuestas estandarizadas (JSON) |
| Errores genéricos o poco claros | Validar la entrada del usuario del lado del servidor |

---

## 2. API vs WebService

**API (Application Programming Interface):** conjunto de definiciones y
protocolos que permite la interacción entre componentes de software.
Es un **contrato**. NO necesariamente es remota ni requiere internet.

Tipos: de sistema operativo, de librerías (Java Collections), de hardware
(GPU), y API web (vía HTTP).

**WebService:** tipo particular de API que funciona **exclusivamente a través
de la red**, orientado a interoperabilidad. Usa SOAP, REST o XML-RPC.

> **Todo WebService es una API, pero no toda API es un WebService.**

| Característica | API | WebService |
|---|---|---|
| Definición | Interfaz entre software | API accesible vía red |
| Transporte | Diverso (HTTP, TCP...) | Generalmente HTTP |
| Formato | JSON, XML, YAML | XML (SOAP), JSON (REST) |
| Independencia | Local o remota | Siempre requiere red |

### SOAP vs REST

| Aspecto | SOAP | REST |
|---|---|---|
| Mensajes | XML | JSON, XML u otros |
| Complejidad | Rígido y estandarizado | Flexible y simple |
| Hoy | Corporativo / bancario | Dominante en APIs modernas |
| Recursos | Más pesado | Más liviano |

### XML vs JSON

- **XML:** marcado jerárquico, verboso, validación con XSD. Sigue vivo en
  configuraciones (ej: el `pom.xml` de Maven).
- **JSON:** liviano, pares clave-valor, estándar en APIs REST modernas.

---

## 3. Monolito vs Microservicios

**Monolito:** toda la funcionalidad en un solo bloque (UI + lógica + datos).
Fácil de iniciar, difícil de escalar. Pequeños cambios afectan todo el sistema.

**Microservicios:** servicios independientes y autónomos, cada uno con una
única función de negocio, comunicados por APIs vía HTTP/REST. Despliegue
independiente.

| Dimensión | Monolito | Microservicios |
|---|---|---|
| Diseño | Código único | Módulos independientes |
| Desarrollo | Menos planificación inicial | Diseño anticipado |
| Escalado | Toda la app | Servicios individuales |
| Implementación | Una sola entidad | Múltiples contenedores |
| Modificación | Impacto general | Cambios aislados |

Monolito → apps pequeñas y MVP. Microservicios → soluciones empresariales
escalables. **Este TPO es un monolito, y está bien que lo sea.**

---

## 4. Arquitectura de tres capas

Separa responsabilidades para evitar que lógica de negocio, comunicación y
acceso a datos queden acoplados. Aporta escalabilidad, reutilización y
mantenibilidad.

| Clásica | En Spring Boot | Carpeta |
|---|---|---|
| Presentación (UI) | Capa de tráfico | `controller/` |
| Negocio | Capa de servicios | `service/` |
| Datos | Acceso a datos | `repository/` |

### Capa de tráfico (controllers)

Punto de entrada. Recibe solicitudes HTTP y devuelve la respuesta.
Usa `@RestController`, `@GetMapping`, `@PostMapping`.

**`ResponseEntity`** permite devolver datos + código de estado HTTP + cabeceras.

El controller NO contiene lógica de negocio: recibe, delega al service, responde.

### Capa de servicios

Núcleo de la aplicación. Reglas de negocio y procesos. Usa `@Service`.

Herramientas recomendadas:
- **`Optional`** — `findById` lo devuelve, evita errores por nulos
- **`Streams`** — `map` transforma la entidad en DTO
- Excepciones controladas (ej: `RecursoNoEncontradoException`)

### Capa de acceso a datos (repositories)

Interactúa con la base vía **Spring Data JPA**. Los repositorios se definen
como **interfaces** (Spring genera la implementación). Usa `@Repository`.

### Anotaciones por capa

| Capa | Anotaciones |
|---|---|
| Tráfico | `@RestController`, `@RequestMapping`, `@GetMapping`, `@PostMapping` |
| Servicios | `@Service`, `@Autowired` |
| Datos | `@Entity`, `@Id`, `@GeneratedValue`, `@Repository` |

Las anotaciones son **metadatos** que indican cómo debe comportarse una clase
o método. Hacen el código más declarativo.

### Inyección de dependencias

El framework crea y provee los objetos. El controller no instancia el service;
el service no crea el repository. Reduce acoplamiento y facilita el testing.

| Forma | Ventajas | Desventajas |
|---|---|---|
| `@Autowired` en atributos | Sencillo, útil para empezar | Menos explícito, difícil de testear |
| **Por constructor** | Explícito, facilita mocks. **Estándar moderno** | Un poco más de código |

**Preferir inyección por constructor.**

### Entidades vs DTO

- **Entidad:** objeto que representa una tabla. NO debe exponerse
  directamente en la capa de tráfico.
- **DTO (Data Transfer Object):** transporta datos entre capas. Evita
  acoplamiento entre entidades y respuestas HTTP. Más seguridad y flexibilidad.

**Lombok `@Data`** genera getters, setters, `equals()`, `hashCode()`,
`toString()` y constructor para campos `final`.

### Manejo de excepciones

`@ResponseStatus` mapea excepciones a códigos HTTP. Ej: recurso no encontrado
→ responde automáticamente 404 Not Found.

### Buenas prácticas

1. Separar responsabilidades — nada de lógica de negocio en controllers
2. Usar DTOs en las respuestas
3. Centralizar el manejo de excepciones
4. Aplicar Streams y Optional
5. Bajo acoplamiento — cada capa depende solo de la inmediatamente inferior

---

## 5. Herramientas del proyecto

**JDK (Java Development Kit):** compilador (`javac`), JVM, javadoc y librerías
de clases. Java compila a **bytecode**, que la JVM traduce según el SO →
de ahí la portabilidad. Verificar con `java --version`.

**Maven:** gestor de paquetes. Declara dependencias en `pom.xml`, las descarga
de repositorios remotos, gestiona versiones y dependencias transitivas.

**Dependencia vs Framework:** una dependencia es una herramienta puntual;
un framework aporta la metodología y estructura completa. Spring Boot es framework.

**Dependencias del proyecto:**
- **Spring Web** — APIs REST, servidor Tomcat embebido, patrón MVC, enrutamiento
- **Spring Data JPA** — persistencia sin SQL manual, CRUD automático, ORM,
  desacopla el motor de base de datos
- **Lombok** — elimina boilerplate
- **H2** — base en memoria, se pierde al parar la app, solo para pruebas
- **MySQL Driver** — para la base persistente

**Archivos:**
- `pom.xml` — declaración de dependencias (es XML, es configuración, **NO es una entidad**)
- `application.properties` — configuración, incluida la conexión a la base
- `target/` — los `.class` compilados

**URL desmenuzada** (`http://localhost:8080/h2-console`):
- `http` → protocolo
- `localhost` → la propia máquina
- `8080` → puerto, identifica qué servicio dentro de la máquina
- El DNS traduce dominios a IPs

**Config H2 en `application.properties`:**
```
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

---

## 6. Alcance del TPO

Marketplace/e-commerce. **Marketplace** = cualquier usuario vende.
**E-commerce** = un solo admin vende.

Módulos mínimos:
- **Catálogo:** buscar producto, favoritos, ver detalle, agregar al carrito,
  simular compra (contra stock)
- **Usuarios:** registro, login con JWT, modificar cuenta
- **Carrito:** agregar y eliminar productos
- **Gestión de productos:** publicar, modificar precio, dar de baja

---

## 7. Conceptos pendientes (todavía no vistos en clase)

No usar hasta que se vean:
- Stateless / sin estado (mencionado al pasar, sin definir)
- Swagger / OpenAPI
- Spring Security y JWT en detalle
- `config/` como paquete
