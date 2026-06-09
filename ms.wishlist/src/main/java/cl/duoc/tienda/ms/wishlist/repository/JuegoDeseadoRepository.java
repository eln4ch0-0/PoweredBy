package cl.duoc.tienda.ms.wishlist.repository;

import cl.duoc.tienda.ms.wishlist.model.JuegoDeseado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JuegoDeseadoRepository extends JpaRepository<JuegoDeseado, Long> {

    List<JuegoDeseado> findByUsuarioId(Long usuarioId);

    Optional<JuegoDeseado> findByUsuarioIdAndJuegoId(Long usuarioId, Long juegoId);

    boolean existsByUsuarioIdAndJuegoId(Long usuarioId, Long juegoId);

    @Query("SELECT COUNT(j) FROM JuegoDeseado j WHERE j.juegoId = :juegoId")
    Long contarPorJuego(@Param("juegoId") Long juegoId);
}