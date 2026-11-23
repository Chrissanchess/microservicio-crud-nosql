package com.utch.microservicio_crud_nosql.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ProductoTest {

    @Test
    void constructorVacioYSettersAsignanLosCampos() {
        Producto producto = new Producto();

        assertAll(
                () -> assertNull(producto.getId()),
                () -> assertNull(producto.getNombre()),
                () -> assertNull(producto.getPrecio()),
                () -> assertNull(producto.getStock()));

        producto.setId("id-1");
        producto.setNombre("Teclado");
        producto.setPrecio(125.0);
        producto.setStock(10);

        assertAll(
                () -> assertEquals("id-1", producto.getId()),
                () -> assertEquals("Teclado", producto.getNombre()),
                () -> assertEquals(125.0, producto.getPrecio()),
                () -> assertEquals(10, producto.getStock()));
    }

    @Test
    void constructorCompletoInicializaLosCampos() {
        Producto producto = new Producto("id-1", "Teclado", 125.0, 10);

        assertAll(
                () -> assertEquals("id-1", producto.getId()),
                () -> assertEquals("Teclado", producto.getNombre()),
                () -> assertEquals(125.0, producto.getPrecio()),
                () -> assertEquals(10, producto.getStock()));
    }
}
