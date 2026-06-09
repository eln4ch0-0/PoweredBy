package cl.duoc.tienda.ms.promociones.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "promociones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Promocion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 30)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_descuento", nullable = false, length = 20)
    private TipoDescuento tipoDescuento;

    @Column(name = "valor_descuento", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorDescuento;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDateTime fechaFin;

    @Column(nullable = false)
    private Boolean activa;

    // Si juegoId es null → promoción global; si tiene valor → promoción específica para ese juego
    @Column(name = "juego_id")
    private Long juegoId;

    @Column(name = "usos_maximos")
    private Integer usosMaximos; // null = ilimitado

    @Column(name = "usos_actuales", nullable = false)
    private Integer usosActuales;

    @PrePersist
    public void prePersist() {
        if (this.activa == null) this.activa = true;
        if (this.usosActuales == null) this.usosActuales = 0;
    }
}