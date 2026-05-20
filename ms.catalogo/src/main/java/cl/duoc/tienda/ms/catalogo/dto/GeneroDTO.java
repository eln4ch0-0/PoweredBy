package cl.duoc.tienda.ms.catalogo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneroDTO {
      private Long id;

    @NotBlank(message = "El nombre del género es obligatorio")
    @Size(max = 50)
    private String nombre;

    @Size(max = 255)
    private String descripcion;
}
