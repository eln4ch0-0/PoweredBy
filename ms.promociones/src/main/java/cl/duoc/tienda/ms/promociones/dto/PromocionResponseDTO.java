package cl.duoc.tienda.ms.promociones.dto;

import cl.duoc.tienda.ms.promociones.model.TipoDescuento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromocionResponseDTO {
    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private TipoDescuento tipoDescuento;
    private BigDecimal valorDescuento;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Boolean activa;
    private Long juegoId;
    private Integer usosMaximos;
    private Integer usosActuales;
}