package com.utch.microservicio_crud_nosql.repository;

import com.utch.microservicio_crud_nosql.model.Producto;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface ProductoRepository extends MongoRepository<Producto, String> {
    boolean existsByNombreIgnoreCase(String nombre);
    Optional<Producto> findByNombreIgnoreCase(String nombre);
}