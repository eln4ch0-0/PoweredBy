package cl.duoc.tienda.ms.usuarios.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RecargaSaldoDTO {

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "100.0", message = "El monto mínimo de recarga es 100")
    private BigDecimal monto;
}
