package cl.duoc.tienda.ms.compras.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JuegoEnBibliotecaDTO {
    private Long id;
    private Long usuarioId;
    private Long juegoId;
    private String tituloJuego;
    private LocalDateTime fechaAdquisicion;
}
