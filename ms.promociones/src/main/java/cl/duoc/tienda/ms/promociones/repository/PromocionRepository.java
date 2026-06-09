package cl.duoc.tienda.ms.promociones.repository;

import cl.duoc.tienda.ms.promociones.model.Promocion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromocionRepository extends JpaRepository<Promocion, Long> {

    Optional<Promocion> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    List<Promocion> findByJuegoId(Long juegoId);

    @Query("SELECT p FROM Promocion p WHERE p.activa = true " +
            "AND :ahora BETWEEN p.fechaInicio AND p.fechaFin")
    List<Promocion> findVigentes(@Param("ahora") LocalDateTime ahora);
}