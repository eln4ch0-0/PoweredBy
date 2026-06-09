package cl.duoc.tienda.ms.wishlist.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishlistResponseDTO {
    private Long id;
    private Long usuarioId;
    private Long juegoId;
    private String tituloJuego;
    private BigDecimal precioReferencia;
    private LocalDateTime fechaAgregado;
}