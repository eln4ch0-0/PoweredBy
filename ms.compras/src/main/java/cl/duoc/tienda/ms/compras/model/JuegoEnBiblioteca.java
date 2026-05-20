package cl.duoc.tienda.ms.compras.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "biblioteca",
        uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "juego_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JuegoEnBiblioteca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "juego_id", nullable = false)
    private Long juegoId;

    @Column(name = "titulo_juego", nullable = false, length = 150)
    private String tituloJuego;

    @Column(name = "fecha_adquisicion", nullable = false, updatable = false)
    private LocalDateTime fechaAdquisicion;

    @PrePersist
    public void prePersist() {
        if (this.fechaAdquisicion == null) this.fechaAdquisicion = LocalDateTime.now();
    }
}
