package cl.duoc.tienda.ms.catalogo.repository;

import cl.duoc.tienda.ms.catalogo.model.Juego;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface JuegoRepository extends JpaRepository<Juego, Long> {
}