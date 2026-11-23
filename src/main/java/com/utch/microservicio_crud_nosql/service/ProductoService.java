package com.utch.microservicio_crud_nosql.service;

import com.utch.microservicio_crud_nosql.exception.DatoDuplicadoException;
import com.utch.microservicio_crud_nosql.exception.ProductoNoEncontradoException;
import com.utch.microservicio_crud_nosql.model.Producto;
import com.utch.microservicio_crud_nosql.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository repository;

    public ProductoService(ProductoRepository repository) {
        this.repository = repository;
    }

    public Producto crear(Producto producto) {
        if (repository.existsByNombreIgnoreCase(producto.getNombre())) {
            throw new DatoDuplicadoException(producto.getNombre());
        }
        producto.setId(null);
        return repository.save(producto);
    }

    public Producto obtenerPorId(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ProductoNoEncontradoException(id));
    }

    public List<Producto> listarTodos() {
        return repository.findAll();
    }

    public Producto actualizar(String id, Producto datos) {
        Producto existente = obtenerPorId(id);
        repository.findByNombreIgnoreCase(datos.getNombre())
                .filter(otro -> !otro.getId().equals(id))
                .ifPresent(otro -> {
                    throw new DatoDuplicadoException(datos.getNombre());
                });
        existente.setNombre(datos.getNombre());
        existente.setPrecio(datos.getPrecio());
        existente.setStock(datos.getStock());
        return repository.save(existente);
    }

    public void eliminar(String id) {
        if (!repository.existsById(id)) {
            throw new ProductoNoEncontradoException(id);
        }
        repository.deleteById(id);
    }
}