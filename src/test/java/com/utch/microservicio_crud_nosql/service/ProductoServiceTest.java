package com.utch.microservicio_crud_nosql.service;

import com.utch.microservicio_crud_nosql.exception.DatoDuplicadoException;
import com.utch.microservicio_crud_nosql.exception.ProductoNoEncontradoException;
import com.utch.microservicio_crud_nosql.model.Producto;
import com.utch.microservicio_crud_nosql.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository repository;

    @InjectMocks
    private ProductoService service;

    @Test
    void crearProductoCorrectamente() {
        Producto producto = new Producto("id-anterior", "Teclado", 125.0, 10);
        Producto guardado = new Producto("id-1", "Teclado", 125.0, 10);
        when(repository.existsByNombreIgnoreCase("Teclado")).thenReturn(false);
        when(repository.save(producto)).thenReturn(guardado);

        Producto resultado = service.crear(producto);

        assertSame(guardado, resultado);
        assertEquals("id-1", resultado.getId());
        assertNull(producto.getId());
        verify(repository).save(producto);
    }

    @Test
    void crearLanzaErrorCuandoElNombreEstaDuplicado() {
        Producto producto = new Producto(null, "Teclado", 125.0, 10);
        when(repository.existsByNombreIgnoreCase("Teclado")).thenReturn(true);

        assertThrows(DatoDuplicadoException.class, () -> service.crear(producto));

        verify(repository, never()).save(producto);
    }

    @Test
    void obtenerProductoPorId() {
        Producto producto = new Producto("id-1", "Teclado", 125.0, 10);
        when(repository.findById("id-1")).thenReturn(Optional.of(producto));

        assertSame(producto, service.obtenerPorId("id-1"));
    }

    @Test
    void obtenerProductoPorIdLanzaErrorCuandoNoExiste() {
        when(repository.findById("id-inexistente")).thenReturn(Optional.empty());

        assertThrows(ProductoNoEncontradoException.class, () -> service.obtenerPorId("id-inexistente"));
    }

    @Test
    void listarProductosConDatos() {
        List<Producto> productos = List.of(new Producto("id-1", "Teclado", 125.0, 10));
        when(repository.findAll()).thenReturn(productos);

        assertSame(productos, service.listarTodos());
    }

    @Test
    void listarProductosCuandoLaListaEstaVacia() {
        when(repository.findAll()).thenReturn(List.of());

        assertEquals(List.of(), service.listarTodos());
    }

    @Test
    void actualizarProductoCorrectamente() {
        Producto existente = new Producto("id-1", "Teclado", 125.0, 10);
        Producto datos = new Producto(null, "Teclado mecánico", 150.0, 8);
        when(repository.findById("id-1")).thenReturn(Optional.of(existente));
        when(repository.findByNombreIgnoreCase("Teclado mecánico")).thenReturn(Optional.empty());
        when(repository.save(existente)).thenReturn(existente);

        Producto resultado = service.actualizar("id-1", datos);

        assertSame(existente, resultado);
        assertEquals("Teclado mecánico", resultado.getNombre());
        assertEquals(150.0, resultado.getPrecio());
        assertEquals(8, resultado.getStock());
        verify(repository).save(existente);
    }

    @Test
    void actualizarProductoInexistenteLanzaError() {
        when(repository.findById("id-inexistente")).thenReturn(Optional.empty());
        Producto datos = new Producto(null, "Teclado", 125.0, 10);

        assertThrows(
                ProductoNoEncontradoException.class,
                () -> service.actualizar("id-inexistente", datos));

        verify(repository, never()).findByNombreIgnoreCase(datos.getNombre());
        verify(repository, never()).save(datos);
    }

    @Test
    void actualizarProductoLanzaErrorSiOtroProductoUsaElNombre() {
        Producto existente = new Producto("id-1", "Teclado", 125.0, 10);
        Producto otro = new Producto("id-2", "Ratón", 50.0, 5);
        Producto datos = new Producto(null, "Ratón", 55.0, 4);
        when(repository.findById("id-1")).thenReturn(Optional.of(existente));
        when(repository.findByNombreIgnoreCase("Ratón")).thenReturn(Optional.of(otro));

        assertThrows(DatoDuplicadoException.class, () -> service.actualizar("id-1", datos));

        verify(repository, never()).save(existente);
    }

    @Test
    void actualizarPermiteConservarElNombreDelMismoProducto() {
        Producto existente = new Producto("id-1", "Teclado", 125.0, 10);
        Producto datos = new Producto(null, "Teclado", 130.0, 9);
        when(repository.findById("id-1")).thenReturn(Optional.of(existente));
        when(repository.findByNombreIgnoreCase("Teclado")).thenReturn(Optional.of(existente));
        when(repository.save(existente)).thenReturn(existente);

        Producto resultado = service.actualizar("id-1", datos);

        assertSame(existente, resultado);
        assertEquals(130.0, resultado.getPrecio());
        assertEquals(9, resultado.getStock());
        verify(repository).save(existente);
    }

    @Test
    void eliminarProductoExistente() {
        when(repository.existsById("id-1")).thenReturn(true);

        service.eliminar("id-1");

        verify(repository).deleteById("id-1");
    }

    @Test
    void eliminarProductoInexistenteNoLoElimina() {
        when(repository.existsById("id-inexistente")).thenReturn(false);

        assertThrows(ProductoNoEncontradoException.class, () -> service.eliminar("id-inexistente"));

        verify(repository, never()).deleteById("id-inexistente");
    }
}
