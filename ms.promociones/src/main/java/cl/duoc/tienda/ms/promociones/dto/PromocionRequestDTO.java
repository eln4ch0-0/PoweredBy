package cl.duoc.tienda.ms.promociones.dto;

import cl.duoc.tienda.ms.promociones.model.TipoDescuento;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PromocionRequestDTO {

    @NotBlank(message = "El código es obligatorio")
    @Size(min = 3, max = 30, message = "El código debe tener entre 3 y 30 caracteres")
    @Pattern(regexp = "^[A-Z0-9_-]+$", message = "El código solo admite mayúsculas, números, guión y guión bajo")
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    private String nombre;

    @Size(max = 500)
    private String descripcion;

    @NotNull(message = "El tipo de descuento es obligatorio")
    private TipoDescuento tipoDescuento;

    @NotNull(message = "El valor del descuento es obligatorio")
    @DecimalMin(value = "0.01", message = "El valor del descuento debe ser mayor a 0")
    private BigDecimal valorDescuento;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDateTime fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDateTime fechaFin;

    // Si es null → promoción global
    @Positive
    private Long juegoId;

    @Positive(message = "El máximo de usos debe ser mayor a 0")
    private Integer usosMaximos;
}