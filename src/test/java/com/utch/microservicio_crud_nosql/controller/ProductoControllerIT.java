package com.utch.microservicio_crud_nosql.controller;

import com.utch.microservicio_crud_nosql.model.Producto;
import com.utch.microservicio_crud_nosql.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "spring.mongodb.database=productosdb_test")
@EnabledIfEnvironmentVariable(named = "MONGODB_URI", matches = ".+")
class ProductoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductoRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void limpiarColeccion() {
        repository.deleteAll();
    }

    @Test
    void crearProductoValidoDevuelve201YElId() throws Exception {
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new Producto(null, "Teclado", 125.0, 10))))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.nombre").value("Teclado"));
    }

    @Test
    void crearProductoInvalidoDevuelve400ConErroresPorCampo() throws Exception {
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new Producto(null, "", -1.0, -1))))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.path").value("/api/productos"))
                .andExpect(jsonPath("$.fieldErrors.nombre").exists())
                .andExpect(jsonPath("$.fieldErrors.precio").exists())
                .andExpect(jsonPath("$.fieldErrors.stock").exists());
    }

    @Test
    void crearProductoConNombreRepetidoDevuelve409() throws Exception {
        repository.save(new Producto(null, "Teclado", 125.0, 10));

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new Producto(null, "Teclado", 130.0, 8))))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.path").value("/api/productos"));
    }

    @Test
    void obtenerProductoExistenteDevuelve200() throws Exception {
        Producto producto = repository.save(new Producto(null, "Teclado", 125.0, 10));

        mockMvc.perform(get("/api/productos/{id}", producto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(producto.getId()))
                .andExpect(jsonPath("$.nombre").value("Teclado"));
    }

    @Test
    void obtenerProductoInexistenteDevuelve404() throws Exception {
        mockMvc.perform(get("/api/productos/{id}", "id-inexistente"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.path").value("/api/productos/id-inexistente"));
    }

    @Test
    void listarProductosDevuelve200ConLosRegistros() throws Exception {
        repository.save(new Producto(null, "Teclado", 125.0, 10));
        repository.save(new Producto(null, "Ratón", 50.0, 5));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void actualizarProductoExistenteDevuelve200() throws Exception {
        Producto existente = repository.save(new Producto(null, "Teclado", 125.0, 10));

        mockMvc.perform(put("/api/productos/{id}", existente.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new Producto(null, "Teclado mecánico", 150.0, 8))))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(existente.getId()))
                .andExpect(jsonPath("$.nombre").value("Teclado mecánico"))
                .andExpect(jsonPath("$.precio").value(150.0));
    }

    @Test
    void actualizarProductoInexistenteDevuelve404() throws Exception {
        mockMvc.perform(put("/api/productos/{id}", "id-inexistente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new Producto(null, "Teclado", 125.0, 10))))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void actualizarProductoConNombreDeOtroDevuelve409() throws Exception {
        Producto existente = repository.save(new Producto(null, "Teclado", 125.0, 10));
        repository.save(new Producto(null, "Ratón", 50.0, 5));

        mockMvc.perform(put("/api/productos/{id}", existente.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new Producto(null, "Ratón", 55.0, 4))))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void eliminarProductoExistenteDevuelve204() throws Exception {
        Producto producto = repository.save(new Producto(null, "Teclado", 125.0, 10));

        mockMvc.perform(delete("/api/productos/{id}", producto.getId()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        assertFalse(repository.existsById(producto.getId()));
    }

    @Test
    void eliminarProductoInexistenteDevuelve404() throws Exception {
        mockMvc.perform(delete("/api/productos/{id}", "id-inexistente"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void jsonMalformadoDevuelve400() throws Exception {
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.path").value("/api/productos"));
    }

    private String json(Producto producto) throws Exception {
        return objectMapper.writeValueAsString(producto);
    }
}
