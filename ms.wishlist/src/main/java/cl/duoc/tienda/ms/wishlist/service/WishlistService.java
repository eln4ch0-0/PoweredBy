package cl.duoc.tienda.ms.wishlist.service;

import cl.duoc.tienda.ms.wishlist.client.CatalogoClient;
import cl.duoc.tienda.ms.wishlist.client.ComprasClient;
import cl.duoc.tienda.ms.wishlist.dto.*;
import cl.duoc.tienda.ms.wishlist.exception.RecursoNoEncontradoException;
import cl.duoc.tienda.ms.wishlist.exception.ReglaNegocioException;
import cl.duoc.tienda.ms.wishlist.model.JuegoDeseado;
import cl.duoc.tienda.ms.wishlist.repository.JuegoDeseadoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WishlistService {

    private final JuegoDeseadoRepository repo;
    private final CatalogoClient catalogoClient;
    private final ComprasClient comprasClient;

    public WishlistResponseDTO agregar(WishlistRequestDTO dto) {
        log.info("Agregando a wishlist: usuario={} juego={}", dto.getUsuarioId(), dto.getJuegoId());

        // 1) Regla: no duplicados — un juego solo puede estar una vez en la wishlist
        if (repo.existsByUsuarioIdAndJuegoId(dto.getUsuarioId(), dto.getJuegoId()))
            throw new ReglaNegocioException("El juego ya está en la wishlist del usuario");

        // 2) Validar que el juego existe en el catálogo y obtener título + precio
        JuegoExternoDTO juego = catalogoClient.obtenerJuego(dto.getJuegoId());
        if (Boolean.FALSE.equals(juego.getDisponible()))
            throw new ReglaNegocioException("El juego '" + juego.getTitulo() + "' no está disponible");

        // 3) Regla de negocio fuerte: no se puede desear un juego que ya posees
        if (comprasClient.usuarioYaPoseeJuego(dto.getUsuarioId(), dto.getJuegoId()))
            throw new ReglaNegocioException(
                    "El usuario ya posee '" + juego.getTitulo() + "', no puede agregarlo a su wishlist");

        // 4) Crear entrada con snapshot de título y precio
        JuegoDeseado jd = new JuegoDeseado();
        jd.setUsuarioId(dto.getUsuarioId());
        jd.setJuegoId(dto.getJuegoId());
        jd.setTituloJuego(juego.getTitulo());
        jd.setPrecioReferencia(juego.getPrecio());

        JuegoDeseado guardado = repo.save(jd);
        log.info("Juego '{}' agregado a wishlist del usuario {} (id={})",
                juego.getTitulo(), dto.getUsuarioId(), guardado.getId());
        return toResponse(guardado);
    }

    public WishlistResponseDTO obtener(Long id) {
        return toResponse(buscar(id));
    }

    public List<WishlistResponseDTO> wishlistDe(Long usuarioId) {
        log.debug("Listando wishlist del usuario {}", usuarioId);
        return repo.findByUsuarioId(usuarioId).stream().map(this::toResponse).toList();
    }

    public WishlistResponseDTO obtenerPorUsuarioYJuego(Long usuarioId, Long juegoId) {
        JuegoDeseado jd = repo.findByUsuarioIdAndJuegoId(usuarioId, juegoId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "El juego " + juegoId + " no está en la wishlist del usuario " + usuarioId));
        return toResponse(jd);
    }

    public ConteoWishlistDTO popularidadJuego(Long juegoId) {
        Long total = repo.contarPorJuego(juegoId);
        return new ConteoWishlistDTO(juegoId, total);
    }

    public void eliminar(Long id) {
        if (!repo.existsById(id))
            throw new RecursoNoEncontradoException("Entrada de wishlist con id " + id + " no existe");
        repo.deleteById(id);
        log.warn("Entrada de wishlist con id={} eliminada", id);
    }

    public void eliminarPorUsuarioYJuego(Long usuarioId, Long juegoId) {
        JuegoDeseado jd = repo.findByUsuarioIdAndJuegoId(usuarioId, juegoId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "El juego " + juegoId + " no está en la wishlist del usuario " + usuarioId));
        repo.delete(jd);
        log.warn("Juego {} eliminado de la wishlist del usuario {}", juegoId, usuarioId);
    }

    private JuegoDeseado buscar(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Entrada de wishlist con id " + id + " no existe"));
    }

    private WishlistResponseDTO toResponse(JuegoDeseado jd) {
        return new WishlistResponseDTO(jd.getId(), jd.getUsuarioId(), jd.getJuegoId(),
                jd.getTituloJuego(), jd.getPrecioReferencia(), jd.getFechaAgregado());
    }
}