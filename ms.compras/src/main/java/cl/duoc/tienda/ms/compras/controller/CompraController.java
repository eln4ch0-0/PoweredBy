package cl.duoc.tienda.ms.compras.controller;

import cl.duoc.tienda.ms.compras.dto.CompraRequestDTO;
import cl.duoc.tienda.ms.compras.dto.CompraResponseDTO;
import cl.duoc.tienda.ms.compras.service.CompraService;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import java.util.List;

@RestController
@RequestMapping("/api/compras")
@RequiredArgsConstructor
@Tag(name = "Compras", description = "Operaciones de compra de videojuegos")
public class CompraController {
   private final CompraService service;

    @PostMapping
    @Operation(summary = "Realizar una compra",
            description = "Flujo completo de compra: valida usuario y juego, " +
                    "verifica saldo, descuenta monto y registra en biblioteca. " +
                    "Consume MS-Usuarios y MS-Catalogo.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Compra realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Saldo insuficiente o juego ya poseído"),
            @ApiResponse(responseCode = "404", description = "Usuario o juego no existe"),
            @ApiResponse(responseCode = "503", description = "MS-Usuarios o MS-Catalogo no disponible")
    })
    public ResponseEntity<CompraResponseDTO> realizar(@Valid @RequestBody CompraRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.realizarCompra(dto));
    }

    @GetMapping
    @Operation(summary = "Listar todas las compras")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    public ResponseEntity<List<CompraResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener compra por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Compra encontrada"),
            @ApiResponse(responseCode = "404", description = "Compra no existe")
    })
    public ResponseEntity<CompraResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Historial de compras de un usuario")
    @ApiResponse(responseCode = "200", description = "Historial obtenido correctamente")
    public ResponseEntity<List<CompraResponseDTO>> historial(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(service.historialUsuario(usuarioId));
    }
}
