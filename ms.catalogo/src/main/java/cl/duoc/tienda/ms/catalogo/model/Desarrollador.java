package cl.duoc.tienda.ms.catalogo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "desarrolladores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Desarrollador {

  @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String nombre;

    @Column(length = 100)
    private String pais;

    @Column(name = "sitio_web", length = 200)
    private String sitioWeb;
}
