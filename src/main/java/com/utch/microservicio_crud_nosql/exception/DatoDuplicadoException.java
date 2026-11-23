package com.utch.microservicio_crud_nosql.exception;

public class DatoDuplicadoException extends RuntimeException {
    public DatoDuplicadoException(String nombre) {
        super("Ya existe un producto con el nombre: " + nombre);
    }
}