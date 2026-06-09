package cl.duoc.tienda.ms.resenas.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ResenaUpdateDTO {

    @NotNull
    @Min(1) @Max(5)
    private Integer calificacion;

    @NotBlank
    @Size(max = 100)
    private String titulo;

    @Size(max = 2000)
    private String contenido;
}