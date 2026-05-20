package cl.duoc.tienda.ms.catalogo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "juegos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Juego {

  @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 150)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "fecha_lanzamiento")
    private LocalDate fechaLanzamiento;

    @Column(nullable = false)
    private Boolean disponible;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "genero_id", nullable = false)
    private Genero genero;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "desarrollador_id", nullable = false)
    private Desarrollador desarrollador;

    @PrePersist
    public void prePersist() {
        if (this.disponible == null) this.disponible = true;
    }
}
