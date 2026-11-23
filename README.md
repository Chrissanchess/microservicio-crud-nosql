# Microservicio CRUD NoSQL

API REST para crear, consultar, actualizar y eliminar productos. Está construida con Spring Boot, Java 21 y MongoDB Atlas.

## Requisitos

- Java 21
- Una cuenta de MongoDB Atlas con un clúster accesible desde la red actual
- PowerShell (para los comandos de esta guía)

## Configurar MongoDB Atlas

Define `MONGODB_URI` en la sesión de PowerShell. Sustituye los valores de ejemplo por la URI de tu clúster:

```powershell
$env:MONGODB_URI = 'mongodb+srv://USUARIO:CONTRASENA@CLUSTER.mongodb.net/productosdb?retryWrites=true&w=majority'
```

Codifica en URL los caracteres especiales de usuario o contraseña que lo requieran. No escribas la URI ni credenciales reales en archivos del repositorio.

## Ejecutar la aplicación

Desde la raíz del proyecto:

```powershell
.\mvnw spring-boot:run
```

La API queda disponible en `http://localhost:8080`.

## API de productos

Todos los endpoints usan la ruta base `/api/productos`. Los cuerpos y respuestas JSON usan `Content-Type: application/json`, excepto `DELETE`, que responde sin cuerpo.

| Método | Ruta | Descripción | Respuesta exitosa |
|---|---|---|---|
| POST | `/api/productos` | Crear producto | `201 Created` |
| GET | `/api/productos` | Listar productos | `200 OK` |
| GET | `/api/productos/{id}` | Consultar por ID | `200 OK` |
| PUT | `/api/productos/{id}` | Actualizar producto | `200 OK` |
| DELETE | `/api/productos/{id}` | Eliminar producto | `204 No Content` |

### Crear producto

`POST /api/productos`

Petición:

```json
{
  "nombre": "Teclado",
  "precio": 125.0,
  "stock": 10
}
```

Respuesta `201 Created`:

```json
{
  "id": "665f1a2b3c4d5e6f7890abcd",
  "nombre": "Teclado",
  "precio": 125.0,
  "stock": 10
}
```

### Listar productos

`GET /api/productos`

Respuesta `200 OK`:

```json
[
  {
    "id": "665f1a2b3c4d5e6f7890abcd",
    "nombre": "Teclado",
    "precio": 125.0,
    "stock": 10
  }
]
```

### Consultar por ID

`GET /api/productos/665f1a2b3c4d5e6f7890abcd`

Respuesta `200 OK`:

```json
{
  "id": "665f1a2b3c4d5e6f7890abcd",
  "nombre": "Teclado",
  "precio": 125.0,
  "stock": 10
}
```

### Actualizar producto

`PUT /api/productos/665f1a2b3c4d5e6f7890abcd`

Petición:

```json
{
  "nombre": "Teclado mecánico",
  "precio": 150.0,
  "stock": 8
}
```

Respuesta `200 OK`:

```json
{
  "id": "665f1a2b3c4d5e6f7890abcd",
  "nombre": "Teclado mecánico",
  "precio": 150.0,
  "stock": 8
}
```

### Eliminar producto

`DELETE /api/productos/665f1a2b3c4d5e6f7890abcd`

Respuesta: `204 No Content`, sin cuerpo.

## Errores

Los errores se devuelven en JSON con `timestamp`, `status`, `error`, `message`, `path` y `fieldErrors`. El mapa `fieldErrors` contiene los errores de validación por nombre de campo y está vacío cuando no aplica.

Validación (`400 Bad Request`):

```json
{
  "timestamp": "2026-11-22T23:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "La solicitud contiene datos inválidos",
  "path": "/api/productos",
  "fieldErrors": {
    "nombre": "El nombre es obligatorio",
    "precio": "El precio debe ser mayor a 0"
  }
}
```

Producto inexistente (`404 Not Found`):

```json
{
  "timestamp": "2026-11-22T23:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Producto no encontrado con id: id-inexistente",
  "path": "/api/productos/id-inexistente",
  "fieldErrors": {}
}
```

Nombre duplicado (`409 Conflict`):

```json
{
  "timestamp": "2026-11-22T23:00:00Z",
  "status": 409,
  "error": "Conflict",
  "message": "Ya existe un producto con el nombre: Teclado",
  "path": "/api/productos",
  "fieldErrors": {}
}
```

## Pruebas y cobertura

Ejecuta las pruebas unitarias:

```powershell
.\mvnw test
```

Las pruebas de integración MockMvc usan la base `productosdb_test`, derivada de `MONGODB_URI`, y limpian su colección antes de cada caso. Para ejecutarlas, configura una URI de MongoDB Atlas con acceso a esa base y ejecuta:

```powershell
.\mvnw verify
```

Si `MONGODB_URI` no está definida, los casos de integración se omiten; las pruebas unitarias siguen ejecutándose.

Para ejecutar toda la verificación y generar el reporte JaCoCo:

```powershell
.\mvnw clean verify
```

El reporte HTML se genera en `target/site/jacoco/index.html` y el XML en `target/site/jacoco/jacoco.xml`. La compilación exige al menos 80% de cobertura de líneas y ramas.

## Análisis SonarCloud

Completa `sonar.projectKey` y `sonar.organization` en `pom.xml` (actualmente tienen valores `TODO`). Mantén el token fuera del repositorio; puedes guardarlo en una variable de entorno de la sesión:

```powershell
$env:SONAR_TOKEN = 'TOKEN_DE_SONARCLOUD'
.\mvnw clean verify sonar:sonar "-Dsonar.token=$env:SONAR_TOKEN"
```
