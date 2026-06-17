package cl.duoc.tienda.ms.usuarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UsuarioRequestDTO {

        @Schema(description = "Nombre de usuario único", example = "gamer01", minLength = 3, maxLength = 20)
        @NotBlank(message = "El username es obligatorio")
        @Size(min = 3, max = 20)
        private String username;

        @Schema(description = "Email único del usuario", example = "gamer01@correo.cl")
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene formato válido")
        private String email;

        @Schema(description = "Contraseña del usuario (mínimo 8 caracteres)", example = "clave1234")
        @NotBlank(message = "La password es obligatoria")
        @Size(min = 8)
        private String password;

        @Schema(description = "Nombre completo del usuario", example = "Juan Pérez")
        @NotBlank(message = "El nombre completo es obligatorio")
        @Size(max = 100)
        private String nombreCompleto;
}
