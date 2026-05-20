package cl.duoc.tienda.ms.compras.service;

import cl.duoc.tienda.ms.compras.dto.JuegoEnBibliotecaDTO;
import cl.duoc.tienda.ms.compras.exception.RecursoNoEncontradoException;
import cl.duoc.tienda.ms.compras.model.JuegoEnBiblioteca;
import cl.duoc.tienda.ms.compras.repository.JuegoEnBibliotecaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BibliotecaService {

  private final JuegoEnBibliotecaRepository repo;

    public List<JuegoEnBibliotecaDTO> bibliotecaDe(Long usuarioId) {
        log.debug("Consultando biblioteca del usuario {}", usuarioId);
        return repo.findByUsuarioId(usuarioId).stream().map(this::toDTO).toList();
    }

    public JuegoEnBibliotecaDTO verificarPosesion(Long usuarioId, Long juegoId) {
        JuegoEnBiblioteca j = repo.findByUsuarioIdAndJuegoId(usuarioId, juegoId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "El usuario " + usuarioId + " no posee el juego " + juegoId));
        return toDTO(j);
    }
}
