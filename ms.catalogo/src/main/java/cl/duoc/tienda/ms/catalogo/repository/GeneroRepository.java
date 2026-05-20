package cl.duoc.tienda.ms.catalogo.repository;

import cl.duoc.tienda.ms.catalogo.model.Genero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeneroRepository extends JpaRepository<Genero, Long> {
  boolean existsByNombre(String nombre);
}
