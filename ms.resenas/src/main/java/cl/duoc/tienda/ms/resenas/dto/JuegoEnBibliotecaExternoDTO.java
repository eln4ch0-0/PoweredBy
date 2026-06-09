package cl.duoc.tienda.ms.resenas.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class JuegoEnBibliotecaExternoDTO {
    private Long id;
    private Long usuarioId;
    private Long juegoId;
    private String tituloJuego;
    private LocalDateTime fechaAdquisicion;
}