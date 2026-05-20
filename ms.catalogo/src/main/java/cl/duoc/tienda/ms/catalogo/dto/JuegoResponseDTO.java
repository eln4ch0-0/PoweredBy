package cl.duoc.tienda.ms.catalogo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JuegoResponseDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private BigDecimal precio;
    private LocalDate fechaLanzamiento;
    private Boolean disponible;
    private GeneroDTO genero;            
    private DesarrolladorDTO desarrollador;
}
