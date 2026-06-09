package cl.duoc.tienda.ms.promociones.dto;

import cl.duoc.tienda.ms.promociones.model.TipoDescuento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AplicarPromocionResponseDTO {
    private String codigoPromocion;
    private String nombrePromocion;
    private TipoDescuento tipoDescuento;
    private BigDecimal valorDescuento;
    private BigDecimal precioOriginal;
    private BigDecimal montoDescuento;
    private BigDecimal precioFinal;
}