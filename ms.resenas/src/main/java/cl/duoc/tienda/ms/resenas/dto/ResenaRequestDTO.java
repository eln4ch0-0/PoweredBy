package cl.duoc.tienda.ms.resenas.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ResenaRequestDTO {

    @NotNull(message = "El usuarioId es obligatorio")
    @Positive
    private Long usuarioId;

    @NotNull(message = "El juegoId es obligatorio")
    @Positive
    private Long juegoId;

    @NotNull(message = "La calificación es obligatoria")
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    private Integer calificacion;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 100)
    private String titulo;

    @Size(max = 2000, message = "El contenido no puede exceder 2000 caracteres")
    private String contenido;
}