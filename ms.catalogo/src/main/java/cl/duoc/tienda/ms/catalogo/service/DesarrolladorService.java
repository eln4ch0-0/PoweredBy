package cl.duoc.tienda.ms.catalogo.service;

import cl.duoc.tienda.ms.catalogo.dto.DesarrolladorDTO;
import cl.duoc.tienda.ms.catalogo.exception.RecursoDuplicadoException;
import cl.duoc.tienda.ms.catalogo.exception.RecursoNoEncontradoException;
import cl.duoc.tienda.ms.catalogo.model.Desarrollador;
import cl.duoc.tienda.ms.catalogo.repository.DesarrolladorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DesarrolladorService {

  private final DesarrolladorRepository repo;

    public List<DesarrolladorDTO> listar() {
        return repo.findAll().stream().map(this::toDTO).toList();
    }

    public DesarrolladorDTO obtener(Long id) {
        return toDTO(buscar(id));
    }

    public DesarrolladorDTO crear(DesarrolladorDTO dto) {
        if (repo.existsByNombre(dto.getNombre()))
            throw new RecursoDuplicadoException("Ya existe un desarrollador con nombre: " + dto.getNombre());
        Desarrollador d = new Desarrollador(null, dto.getNombre(), dto.getPais(), dto.getSitioWeb());
        log.info("Creando desarrollador: {}", dto.getNombre());
        return toDTO(repo.save(d));
    }
}
