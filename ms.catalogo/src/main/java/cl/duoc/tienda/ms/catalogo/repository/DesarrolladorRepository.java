package cl.duoc.tienda.ms.catalogo.repository;

import cl.duoc.tienda.ms.catalogo.model.Desarrollador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DesarrolladorRepository extends JpaRepository<Desarrollador, Long> {
}
