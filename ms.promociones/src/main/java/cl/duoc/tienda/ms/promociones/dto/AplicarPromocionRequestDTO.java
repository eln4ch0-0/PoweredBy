package cl.duoc.tienda.ms.promociones.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AplicarPromocionRequestDTO {

    @NotBlank(message = "El código de promoción es obligatorio")
    private String codigo;

    @NotNull
    @Positive
    private Long juegoId;

    @NotNull(message = "El precio original es obligatorio")
    @DecimalMin(value = "0.01")
    private BigDecimal precioOriginal;
}