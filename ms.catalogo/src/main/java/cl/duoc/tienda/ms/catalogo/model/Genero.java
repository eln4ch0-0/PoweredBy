package cl.duoc.tienda.ms.catalogo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "generos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Genero {
  
  @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String nombre;

    @Column(length = 255)
    private String descripcion;
}
