package cl.duoc.tienda.ms.compras.repository;

import cl.duoc.tienda.ms.compras.model.JuegoEnBiblioteca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JuegoEnBibliotecaRepository extends JpaRepository<JuegoEnBiblioteca, Long> {
    List<JuegoEnBiblioteca> findByUsuarioId(Long usuarioId);
    Optional<JuegoEnBiblioteca> findByUsuarioIdAndJuegoId(Long usuarioId, Long juegoId);
    boolean existsByUsuarioIdAndJuegoId(Long usuarioId, Long juegoId);
}
