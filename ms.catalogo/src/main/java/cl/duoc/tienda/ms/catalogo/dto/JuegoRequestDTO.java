package cl.duoc.tienda.ms.catalogo.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class JuegoRequestDTO {
  
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 150)
    private String titulo;

    @Size(max = 2000)
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private BigDecimal precio;

    @PastOrPresent(message = "La fecha de lanzamiento no puede ser futura")
    private LocalDate fechaLanzamiento;

    @NotNull(message = "El género es obligatorio")
    private Long generoId;

    @NotNull(message = "El desarrollador es obligatorio")
    private Long desarrolladorId;
}
