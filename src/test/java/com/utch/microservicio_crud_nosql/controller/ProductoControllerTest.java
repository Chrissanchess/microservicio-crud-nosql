package com.utch.microservicio_crud_nosql.controller;

import com.utch.microservicio_crud_nosql.model.Producto;
import com.utch.microservicio_crud_nosql.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoControllerTest {

    @Mock
    private ProductoService service;

    @InjectMocks
    private ProductoController controller;

    @Test
    void crearDevuelve201ConElProducto() {
        Producto producto = new Producto("id-1", "Teclado", 125.0, 10);
        when(service.crear(producto)).thenReturn(producto);

        ResponseEntity<Producto> response = controller.crear(producto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(producto, response.getBody());
    }

    @Test
    void listarDevuelve200ConLosProductos() {
        List<Producto> productos = List.of(new Producto("id-1", "Teclado", 125.0, 10));
        when(service.listarTodos()).thenReturn(productos);

        ResponseEntity<List<Producto>> response = controller.listarTodos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(productos, response.getBody());
    }

    @Test
    void obtenerPorIdDevuelve200ConElProducto() {
        Producto producto = new Producto("id-1", "Teclado", 125.0, 10);
        when(service.obtenerPorId("id-1")).thenReturn(producto);

        ResponseEntity<Producto> response = controller.obtenerPorId("id-1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(producto, response.getBody());
    }

    @Test
    void actualizarDevuelve200ConElProducto() {
        Producto producto = new Producto("id-1", "Teclado", 130.0, 9);
        when(service.actualizar("id-1", producto)).thenReturn(producto);

        ResponseEntity<Producto> response = controller.actualizar("id-1", producto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(producto, response.getBody());
    }

    @Test
    void eliminarDevuelve204() {
        ResponseEntity<Void> response = controller.eliminar("id-1");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).eliminar("id-1");
    }
}
