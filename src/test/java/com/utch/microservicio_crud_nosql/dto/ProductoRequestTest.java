package com.utch.microservicio_crud_nosql.dto;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ProductoRequestTest {

    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    @Test
    void toEntityMapeaCamposYNoAsignaId() {
        ProductoRequest request = new ProductoRequest("Teclado", 125.0, 10);

        var producto = request.toEntity();

        assertNull(producto.getId());
        assertEquals("Teclado", producto.getNombre());
        assertEquals(125.0, producto.getPrecio());
        assertEquals(10, producto.getStock());
    }

    @Test
    void deserializacionIgnoraIdEnviadoEnElCuerpo() throws Exception {
        ProductoRequest request = jsonMapper.readValue("""
                {
                  "id": "id-controlado-por-cliente",
                  "nombre": "Teclado",
                  "precio": 125.0,
                  "stock": 10
                }
                """, ProductoRequest.class);

        assertNull(request.toEntity().getId());
        assertEquals("Teclado", request.nombre());
    }
}
