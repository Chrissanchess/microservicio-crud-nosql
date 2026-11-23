package com.utch.microservicio_crud_nosql.exception;

public class ProductoNoEncontradoException extends RuntimeException {
    public ProductoNoEncontradoException(String id) {
        super("Producto no encontrado con id: " + id);
    }
}