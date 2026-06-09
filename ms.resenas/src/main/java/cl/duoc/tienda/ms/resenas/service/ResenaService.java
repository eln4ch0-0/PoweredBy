package cl.duoc.tienda.ms.resenas.service;

import cl.duoc.tienda.ms.resenas.client.ComprasClient;
import cl.duoc.tienda.ms.resenas.dto.*;
import cl.duoc.tienda.ms.resenas.exception.RecursoNoEncontradoException;
import cl.duoc.tienda.ms.resenas.exception.ReglaNegocioException;
import cl.duoc.tienda.ms.resenas.model.Resena;
import cl.duoc.tienda.ms.resenas.repository.ResenaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResenaService {

    private final ResenaRepository repo;
    private final ComprasClient comprasClient;

    public ResenaResponseDTO crear(ResenaRequestDTO dto) {
        log.info("Creando reseña: usuario={} juego={}", dto.getUsuarioId(), dto.getJuegoId());

        // 1) Regla: solo se puede reseñar un juego que se posee
        if (!comprasClient.usuarioPoseeJuego(dto.getUsuarioId(), dto.getJuegoId()))
            throw new ReglaNegocioException(
                    "El usuario " + dto.getUsuarioId() + " no posee el juego " + dto.getJuegoId()
                            + " — solo se pueden reseñar juegos comprados");

        // 2) Regla: un usuario solo puede tener UNA reseña por juego
        if (repo.existsByUsuarioIdAndJuegoId(dto.getUsuarioId(), dto.getJuegoId()))
            throw new ReglaNegocioException(
                    "El usuario ya tiene una reseña para este juego — use PUT para actualizarla");

        Resena r = new Resena();
        r.setUsuarioId(dto.getUsuarioId());
        r.setJuegoId(dto.getJuegoId());
        r.setCalificacion(dto.getCalificacion());
        r.setTitulo(dto.getTitulo());
        r.setContenido(dto.getContenido());

        Resena guardada = repo.save(r);
        log.info("Reseña creada id={} calificacion={}", guardada.getId(), guardada.getCalificacion());
        return toResponse(guardada);
    }

    public ResenaResponseDTO actualizar(Long id, ResenaUpdateDTO dto) {
        Resena r = buscar(id);
        r.setCalificacion(dto.getCalificacion());
        r.setTitulo(dto.getTitulo());
        r.setContenido(dto.getContenido());
        log.info("Reseña {} actualizada", id);
        return toResponse(repo.save(r));
    }

    public ResenaResponseDTO obtener(Long id) {
        return toResponse(buscar(id));
    }

    public List<ResenaResponseDTO> resenasDeJuego(Long juegoId) {
        return repo.findByJuegoId(juegoId).stream().map(this::toResponse).toList();
    }

    public List<ResenaResponseDTO> resenasDeUsuario(Long usuarioId) {
        return repo.findByUsuarioId(usuarioId).stream().map(this::toResponse).toList();
    }

    public List<ResenaResponseDTO> listar() {
        return repo.findAll().stream().map(this::toResponse).toList();
    }

    public PromedioResenaDTO promedioJuego(Long juegoId) {
        Double promedio = repo.promedioCalificacionPorJuego(juegoId);
        Long total = repo.cantidadResenasPorJuego(juegoId);
        // Si no hay reseñas, promedio viene null — devolvemos 0
        return new PromedioResenaDTO(juegoId,
                promedio != null ? Math.round(promedio * 100.0) / 100.0 : 0.0,
                total);
    }

    public void eliminar(Long id) {
        if (!repo.existsById(id))
            throw new RecursoNoEncontradoException("Reseña con id " + id + " no existe");
        repo.deleteById(id);
        log.warn("Reseña con id={} eliminada", id);
    }

    private Resena buscar(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reseña con id " + id + " no existe"));
    }

    private ResenaResponseDTO toResponse(Resena r) {
        return new ResenaResponseDTO(r.getId(), r.getUsuarioId(), r.getJuegoId(),
                r.getCalificacion(), r.getTitulo(), r.getContenido(),
                r.getFechaCreacion(), r.getFechaActualizacion());
    }
}