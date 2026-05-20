package cl.duoc.tienda.ms.catalogo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DesarrolladorDTO {
      private Long id;

    @NotBlank(message = "El nombre del desarrollador es obligatorio")
    @Size(max = 100)
    private String nombre;

    @Size(max = 100)
    private String pais;

    @Size(max = 200)
    private String sitioWeb;
}
