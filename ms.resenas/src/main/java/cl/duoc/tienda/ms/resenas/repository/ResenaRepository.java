package cl.duoc.tienda.ms.resenas.repository;

import cl.duoc.tienda.ms.resenas.model.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {

    List<Resena> findByJuegoId(Long juegoId);

    List<Resena> findByUsuarioId(Long usuarioId);

    boolean existsByUsuarioIdAndJuegoId(Long usuarioId, Long juegoId);

    @Query("SELECT AVG(r.calificacion) FROM Resena r WHERE r.juegoId = :juegoId")
    Double promedioCalificacionPorJuego(@Param("juegoId") Long juegoId);

    @Query("SELECT COUNT(r) FROM Resena r WHERE r.juegoId = :juegoId")
    Long cantidadResenasPorJuego(@Param("juegoId") Long juegoId);
}