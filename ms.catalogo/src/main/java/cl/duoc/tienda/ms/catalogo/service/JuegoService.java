package cl.duoc.tienda.ms.catalogo.service;

import cl.duoc.tienda.ms.catalogo.dto.DesarrolladorDTO;
import cl.duoc.tienda.ms.catalogo.dto.GeneroDTO;
import cl.duoc.tienda.ms.catalogo.dto.JuegoRequestDTO;
import cl.duoc.tienda.ms.catalogo.dto.JuegoResponseDTO;
import cl.duoc.tienda.ms.catalogo.exception.RecursoDuplicadoException;
import cl.duoc.tienda.ms.catalogo.exception.RecursoNoEncontradoException;
import cl.duoc.tienda.ms.catalogo.model.Juego;
import cl.duoc.tienda.ms.catalogo.repository.JuegoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class JuegoService {

    private final JuegoRepository repo;
    private final GeneroService generoService;
    private final DesarrolladorService desarrolladorService;

    public List<JuegoResponseDTO> listar() {
        return repo.findAll().stream().map(this::toResponse).toList();
    }

    public JuegoResponseDTO obtener(Long id) {
        return toResponse(buscar(id));
    }

    public List<JuegoResponseDTO> buscarPorGenero(Long generoId) {
        return repo.findByGeneroId(generoId).stream().map(this::toResponse).toList();
    }

    public List<JuegoResponseDTO> buscarHastaPrecio(BigDecimal precioMax) {
        return repo.findByPrecioLessThanEqualAndDisponibleTrue(precioMax).stream()
                .map(this::toResponse).toList();
    }

    public JuegoResponseDTO crear(JuegoRequestDTO dto) {
        if (repo.existsByTitulo(dto.getTitulo()))
            throw new RecursoDuplicadoException("Ya existe un juego con título: " + dto.getTitulo());

        Juego j = new Juego();
        j.setTitulo(dto.getTitulo());
        j.setDescripcion(dto.getDescripcion());
        j.setPrecio(dto.getPrecio());
        j.setFechaLanzamiento(dto.getFechaLanzamiento());
        j.setGenero(generoService.buscar(dto.getGeneroId()));
        j.setDesarrollador(desarrolladorService.buscar(dto.getDesarrolladorId()));

        log.info("Creando juego: {}", dto.getTitulo());
        return toResponse(repo.save(j));
    }

    public JuegoResponseDTO actualizar(Long id, JuegoRequestDTO dto) {
        Juego j = buscar(id);
        if (!j.getTitulo().equals(dto.getTitulo()) && repo.existsByTitulo(dto.getTitulo()))
            throw new RecursoDuplicadoException("Ya existe un juego con título: " + dto.getTitulo());

        j.setTitulo(dto.getTitulo());
        j.setDescripcion(dto.getDescripcion());
        j.setPrecio(dto.getPrecio());
        j.setFechaLanzamiento(dto.getFechaLanzamiento());
        j.setGenero(generoService.buscar(dto.getGeneroId()));
        j.setDesarrollador(desarrolladorService.buscar(dto.getDesarrolladorId()));
        return toResponse(repo.save(j));
    }

    public void eliminar(Long id) {
        if (!repo.existsById(id))
            throw new RecursoNoEncontradoException("Juego con id " + id + " no existe");
        repo.deleteById(id);
        log.warn("Juego con id={} eliminado", id);
    }
  
    public JuegoResponseDTO cambiarDisponibilidad(Long id, boolean disponible) {
        Juego j = buscar(id);
        j.setDisponible(disponible);
        log.info("Juego {} marcado como disponible={}", id, disponible);
        return toResponse(repo.save(j));
    }
}
