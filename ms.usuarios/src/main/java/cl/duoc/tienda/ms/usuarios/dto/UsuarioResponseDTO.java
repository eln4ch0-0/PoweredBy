package cl.duoc.tienda.ms.usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {

    @Schema(description = "ID generado del usuario", example = "1")
    private Long id;

    @Schema(description = "Nombre de usuario", example = "gamer01")
    private String username;

    @Schema(description = "Email del usuario", example = "gamer01@correo.cl")
    private String email;

    @Schema(description = "Nombre completo", example = "Juan Pérez")
    private String nombreCompleto;

    @Schema(description = "Fecha de registro del usuario")
    private LocalDateTime fechaRegistro;

    @Schema(description = "Saldo actual de la billetera digital", example = "50000.00")
    private BigDecimal saldoBilletera;

    @Schema(description = "Indica si el usuario está activo", example = "true")
    private Boolean activo;
}
