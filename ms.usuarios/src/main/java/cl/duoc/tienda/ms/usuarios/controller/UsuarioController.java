package cl.duoc.tienda.ms.usuarios.controller;

import cl.duoc.tienda.ms.usuarios.dto.RecargaSaldoDTO;
import cl.duoc.tienda.ms.usuarios.dto.UsuarioRequestDTO;
import cl.duoc.tienda.ms.usuarios.dto.UsuarioResponseDTO;
import cl.duoc.tienda.ms.usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Operaciones sobre usuarios y billetera digital")
public class UsuarioController {

    private final UsuarioService service;

    @GetMapping
    @Operation(summary = "Listar todos los usuarios",
            description = "Devuelve la lista completa de usuarios registrados en el sistema.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    public ResponseEntity<List<UsuarioResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID",
            description = "Devuelve los datos de un usuario específico identificado por su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no existe",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    public ResponseEntity<UsuarioResponseDTO> obtener(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo usuario",
            description = "Registra un nuevo usuario en el sistema. " +
                    "El username y email deben ser únicos. " +
                    "El saldo inicial de la billetera es 0.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "Username o email ya existen")
    })
    public ResponseEntity<UsuarioResponseDTO> crear(@Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario",
            description = "Modifica los datos de un usuario existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario actualizado"),
            @ApiResponse(responseCode = "404", description = "Usuario no existe"),
            @ApiResponse(responseCode = "409", description = "Username o email duplicado")
    })
    public ResponseEntity<UsuarioResponseDTO> actualizar(
            @Parameter(description = "ID del usuario a actualizar", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario",
            description = "Elimina permanentemente un usuario del sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuario eliminado"),
            @ApiResponse(responseCode = "404", description = "Usuario no existe")
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/recargar-saldo")
    @Operation(summary = "Recargar saldo de la billetera",
            description = "Suma el monto especificado al saldo actual del usuario. " +
                    "El monto mínimo de recarga es 100.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Saldo recargado"),
            @ApiResponse(responseCode = "400", description = "Monto inválido"),
            @ApiResponse(responseCode = "404", description = "Usuario no existe")
    })
    public ResponseEntity<UsuarioResponseDTO> recargar(
            @PathVariable Long id,
            @Valid @RequestBody RecargaSaldoDTO dto) {
        return ResponseEntity.ok(service.recargarSaldo(id, dto.getMonto()));
    }

    @PutMapping("/{id}/descontar-saldo")
    @Operation(summary = "Descontar saldo de la billetera",
            description = "Endpoint consumido por MS-Compras durante el proceso de compra. " +
                    "Valida que el saldo sea suficiente antes de descontar.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Saldo descontado"),
            @ApiResponse(responseCode = "400", description = "Saldo insuficiente"),
            @ApiResponse(responseCode = "404", description = "Usuario no existe")
    })
    public ResponseEntity<UsuarioResponseDTO> descontar(
            @PathVariable Long id,
            @RequestBody BigDecimal monto) {
        return ResponseEntity.ok(service.descontarSaldo(id, monto));
    }
}
