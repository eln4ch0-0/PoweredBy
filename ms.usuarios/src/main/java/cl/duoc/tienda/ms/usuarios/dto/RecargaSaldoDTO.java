package cl.duoc.tienda.ms.usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RecargaSaldoDTO {

    @Schema(description = "Monto a recargar (mínimo 100)", example = "50000")
    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "100.0")
    private BigDecimal monto;
}