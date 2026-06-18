package cl.duoc.tienda.ms.wishlist.controller;

import cl.duoc.tienda.ms.wishlist.dto.*;
import cl.duoc.tienda.ms.wishlist.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
@Tag(name = "Wishlist", description = "Operaciones sobre la lista de deseos de los usuarios")
public class WishlistController {

    private final WishlistService service;

    @PostMapping
    @Operation(summary = "Agregar juego a wishlist",
            description = "Agrega un juego a la lista de deseos. Consume MS-Catalogo para validar " +
                    "existencia del juego y MS-Compras para verificar que el usuario no lo posea ya.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Juego agregado a wishlist"),
            @ApiResponse(responseCode = "400", description = "Juego no disponible o usuario ya lo posee"),
            @ApiResponse(responseCode = "404", description = "Juego no existe en catálogo"),
            @ApiResponse(responseCode = "503", description = "MS-Catalogo o MS-Compras no disponible")
    })
    public ResponseEntity<WishlistResponseDTO> agregar(@Valid @RequestBody WishlistRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.agregar(dto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener entrada por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entrada encontrada"),
            @ApiResponse(responseCode = "404", description = "Entrada no existe")
    })
    public ResponseEntity<WishlistResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Wishlist de un usuario",
            description = "Devuelve todos los juegos en la wishlist del usuario.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    public ResponseEntity<List<WishlistResponseDTO>> porUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(service.wishlistDe(usuarioId));
    }

    @GetMapping("/usuario/{usuarioId}/juego/{juegoId}")
    @Operation(summary = "Verificar si un juego está en wishlist")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "El juego está en la wishlist"),
            @ApiResponse(responseCode = "404", description = "El juego no está en la wishlist")
    })
    public ResponseEntity<WishlistResponseDTO> verificar(@PathVariable Long usuarioId,
                                                         @PathVariable Long juegoId) {
        return ResponseEntity.ok(service.obtenerPorUsuarioYJuego(usuarioId, juegoId));
    }

    @GetMapping("/juego/{juegoId}/popularidad")
    @Operation(summary = "Popularidad de un juego",
            description = "Cuenta cuántos usuarios tienen este juego en su wishlist.")
    @ApiResponse(responseCode = "200", description = "Conteo obtenido correctamente")
    public ResponseEntity<ConteoWishlistDTO> popularidad(@PathVariable Long juegoId) {
        return ResponseEntity.ok(service.popularidadJuego(juegoId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar entrada por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Entrada eliminada"),
            @ApiResponse(responseCode = "404", description = "Entrada no existe")
    })
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/usuario/{usuarioId}/juego/{juegoId}")
    @Operation(summary = "Eliminar juego de wishlist por usuario y juego")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Juego eliminado de wishlist"),
            @ApiResponse(responseCode = "404", description = "El juego no estaba en wishlist")
    })
    public ResponseEntity<Void> eliminarPorUsuarioYJuego(@PathVariable Long usuarioId,
                                                         @PathVariable Long juegoId) {
        service.eliminarPorUsuarioYJuego(usuarioId, juegoId);
        return ResponseEntity.noContent().build();
    }
}