package cl.duoc.tienda.ms.compras.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CompraRequestDTO {

    @NotNull(message = "El usuarioId es obligatorio")
    @Positive
    private Long usuarioId;

    @NotNull(message = "El juegoId es obligatorio")
    @Positive
    private Long juegoId;
}
