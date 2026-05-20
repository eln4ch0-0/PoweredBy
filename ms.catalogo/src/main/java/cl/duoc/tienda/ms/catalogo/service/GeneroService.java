package cl.duoc.tienda.ms.catalogo.service;

import cl.duoc.tienda.ms.catalogo.dto.GeneroDTO;
import cl.duoc.tienda.ms.catalogo.exception.RecursoDuplicadoException;
import cl.duoc.tienda.ms.catalogo.exception.RecursoNoEncontradoException;
import cl.duoc.tienda.ms.catalogo.model.Genero;
import cl.duoc.tienda.ms.catalogo.repository.GeneroRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeneroService {

    private final GeneroRepository repo;

    public List<GeneroDTO> listar() {
        return repo.findAll().stream().map(this::toDTO).toList();
    }

    public GeneroDTO obtener(Long id) {
        return toDTO(buscar(id));
    }

    public GeneroDTO crear(GeneroDTO dto) {
        if (repo.existsByNombre(dto.getNombre()))
            throw new RecursoDuplicadoException("Ya existe un género con nombre: " + dto.getNombre());
        Genero g = new Genero(null, dto.getNombre(), dto.getDescripcion());
        log.info("Creando género: {}", dto.getNombre());
        return toDTO(repo.save(g));
    }
}
