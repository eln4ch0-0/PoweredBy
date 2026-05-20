package cl.duoc.tienda.ms.compras.dto;

import cl.duoc.tienda.ms.compras.model.EstadoCompra;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompraResponseDTO {
    private Long id;
    private Long usuarioId;
    private Long juegoId;
    private String tituloJuego;
    private BigDecimal precioPagado;
    private LocalDateTime fechaCompra;
    private EstadoCompra estado;
}
