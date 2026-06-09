package cl.duoc.tienda.ms.resenas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResenaResponseDTO {
    private Long id;
    private Long usuarioId;
    private Long juegoId;
    private Integer calificacion;
    private String titulo;
    private String contenido;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}