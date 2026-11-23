package com.utch.microservicio_crud_nosql.exception;

import com.utch.microservicio_crud_nosql.model.Producto;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void manejarValidacionIncluyeErroresPorCampo() {
        BindingResult bindingResult = new BeanPropertyBindingResult(new Producto(), "producto");
        bindingResult.addError(new FieldError("producto", "nombre", "El nombre es obligatorio"));
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);

        ErrorResponse body = handler.manejarValidacion(exception, request()).getBody();

        assertNotNull(body);
        assertEquals(400, body.status());
        assertEquals("El nombre es obligatorio", body.fieldErrors().get("nombre"));
        assertEquals("/api/productos", body.path());
        assertNotNull(body.timestamp());
    }

    @Test
    void manejarValidacionSinErroresDevuelveMapaVacio() {
        BindingResult bindingResult = new BeanPropertyBindingResult(new Producto(), "producto");
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);

        ErrorResponse body = handler.manejarValidacion(exception, request()).getBody();

        assertNotNull(body);
        assertEquals(400, body.status());
        assertEquals(0, body.fieldErrors().size());
    }

    @Test
    void manejarProductoNoEncontradoDevuelve404() {
        var response = handler.manejarProductoNoEncontrado(
                new ProductoNoEncontradoException("id-1"),
                request());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().status());
        assertEquals("Not Found", response.getBody().error());
        assertEquals("Producto no encontrado con id: id-1", response.getBody().message());
    }

    @Test
    void manejarDatoDuplicadoDevuelve409() {
        var response = handler.manejarDatoDuplicado(new DatoDuplicadoException("Teclado"), request());

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().status());
        assertEquals("Conflict", response.getBody().error());
    }

    @Test
    void manejarMensajeNoLegibleDevuelve400() {
        var response = handler.manejarMensajeNoLegible(mock(HttpMessageNotReadableException.class), request());

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().status());
        assertEquals("El cuerpo de la solicitud no es válido", response.getBody().message());
    }

    @Test
    void manejarErrorInternoDevuelve500ConMensajeGenerico() {
        var response = handler.manejarErrorInterno(new IllegalStateException("detalle interno"), request());

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().status());
        assertEquals("Ocurrió un error interno", response.getBody().message());
    }

    private MockHttpServletRequest request() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/productos");
        return request;
    }
}
