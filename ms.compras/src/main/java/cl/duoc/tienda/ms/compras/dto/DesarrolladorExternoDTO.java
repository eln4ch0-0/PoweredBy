package cl.duoc.tienda.ms.compras.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DesarrolladorExternoDTO {
    private Long id;
    private String nombre;
}
