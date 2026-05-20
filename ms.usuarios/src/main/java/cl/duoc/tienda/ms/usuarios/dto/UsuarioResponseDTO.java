package cl.duoc.tienda.ms.usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {
    private Long id;
    private String username;
    private String email;
    private String nombreCompleto;
    private LocalDateTime fechaRegistro;
    private BigDecimal saldoBilletera;
    private Boolean activo;
}
