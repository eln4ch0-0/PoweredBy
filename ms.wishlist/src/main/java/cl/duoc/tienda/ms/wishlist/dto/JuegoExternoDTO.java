package cl.duoc.tienda.ms.wishlist.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class JuegoExternoDTO {
    private Long id;
    private String titulo;
    private BigDecimal precio;
    private Boolean disponible;
}