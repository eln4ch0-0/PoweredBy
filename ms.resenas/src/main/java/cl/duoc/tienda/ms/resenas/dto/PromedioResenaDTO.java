package cl.duoc.tienda.ms.resenas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromedioResenaDTO {
    private Long juegoId;
    private Double promedio;
    private Long totalResenas;
}