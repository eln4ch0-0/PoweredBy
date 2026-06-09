package cl.duoc.tienda.ms.wishlist.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class JuegoEnBibliotecaExternoDTO {
    private Long id;
    private Long usuarioId;
    private Long juegoId;
}