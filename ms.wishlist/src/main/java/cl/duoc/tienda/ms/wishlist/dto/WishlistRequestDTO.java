package cl.duoc.tienda.ms.wishlist.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class WishlistRequestDTO {

    @NotNull(message = "El usuarioId es obligatorio")
    @Positive
    private Long usuarioId;

    @NotNull(message = "El juegoId es obligatorio")
    @Positive
    private Long juegoId;
}