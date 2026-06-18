package cl.duoc.tienda.ms.compras.controller;

import cl.duoc.tienda.ms.compras.dto.JuegoEnBibliotecaDTO;
import cl.duoc.tienda.ms.compras.service.BibliotecaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/biblioteca")
@RequiredArgsConstructor
@Tag(name = "Biblioteca", description = "Consulta de juegos en propiedad del usuario")

public class BibliotecaController {
  private final BibliotecaService service;

    @GetMapping("/{usuarioId}")
    @Operation(summary = "Biblioteca completa de un usuario",
            description = "Devuelve todos los juegos que posee permanentemente un usuario.")
    @ApiResponse(responseCode = "200", description = "Biblioteca obtenida correctamente")
    public ResponseEntity<List<JuegoEnBibliotecaDTO>> biblioteca(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(service.bibliotecaDe(usuarioId));
    }

    @GetMapping("/{usuarioId}/{juegoId}")
    @Operation(summary = "Verificar si un usuario posee un juego",
            description = "Endpoint consumido por MS-Resenas y MS-Wishlist para validar propiedad.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "El usuario posee el juego"),
            @ApiResponse(responseCode = "404", description = "El usuario no posee el juego")
    })
    public ResponseEntity<JuegoEnBibliotecaDTO> verificar(@PathVariable Long usuarioId, @PathVariable Long juegoId) {
        return ResponseEntity.ok(service.verificarPosesion(usuarioId, juegoId));
    }
}
