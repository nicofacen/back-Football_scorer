# CLAUDE.md

## Contexto del proyecto

Este repositorio es el **TPO de la materia Aplicaciones Interactivas (UADE)**.
Es un proyecto académico grupal: un **e-commerce**.

**Stack:**
- Backend: Java 17, Spring Boot, Maven
- Base de datos: H2 (desarrollo, en memoria) / MySQL (producción)
- Frontend: React
- Arquitectura: **monolito organizado en tres capas**

**Estructura de capas (respetar siempre):**

```
Cliente HTTP
    ↓
controller/   → @RestController. Recibe HTTP y delega. CERO lógica de negocio.
    ↓
service/      → @Service. Reglas de negocio. Usa Optional y Streams.
    ↓
repository/   → @Repository. Único que habla con la base (Spring Data JPA).
    ↓
model/        → @Entity. Mapeo a tablas.
```

**Regla estructural:** cada capa depende SOLO de la inmediatamente inferior.
Un controller nunca llama directo a un repository.

---

## Reglas de trabajo (IMPORTANTES)
Estamos **cursando** esta materia. El objetivo NO es que el código esté listo,
es que nosotros entendamos lo que estamos escribiendo.
El profesor evalúa oralmente en clase y hay que poder defender cada línea

1. **Actuá como TUTOR, no como programador.** La prioridad es que entendamos
2. **NO escribas código por mí** salvo que te lo pida explícitamente.
   Primero guianos con preguntas y respuestas.
3. Si escribimos algo mal o tenemos algun concepto equivocado **correginos y explicanos POR QUÉ está mal**, no solo cuál es la respuesta correcta.
4. **Usá Plan Mode** antes de tocar archivos. Queremos leer y entender el plan antes de que edites nada
5. Ceñite a los conceptos vistos en la materia (ver `docs/CONTEXTO-MATERIA.md`).
   No metas patrones, librerías ni abstracciones que no hayamos visto.
6. Respondé en **español**.

---

## Convenciones del repo

- Paquete base: `com.uade.e_commerce`
- Nombres de endpoints en **plural y sustantivo**: `/productos`, no `/obtenerProductos`
- El verbo va en el método HTTP, nunca en la URL
- Códigos de estado: 200 (GET ok), 201 (POST ok), 204 (DELETE ok),
  400 (request inválido), 404 (no encontrado), 500 (error del server)
- Un solo criterio de nombres de paquetes para todo el grupo (`model/`, no `entity/`)

## Nota sobre commits

El profesor evalúa los commits individuales de cada integrante del grupo.
No agrupes cambios de varias personas en un solo commit.
