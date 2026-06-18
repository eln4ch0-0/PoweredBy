package cl.duoc.tienda.ms.catalogo.controller;

import cl.duoc.tienda.ms.catalogo.dto.JuegoRequestDTO;
import cl.duoc.tienda.ms.catalogo.dto.JuegoResponseDTO;
import cl.duoc.tienda.ms.catalogo.service.JuegoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/juegos")
@RequiredArgsConstructor
@Tag(name = "Juegos", description = "Operaciones sobre el catálogo de videojuegos")
public class JuegoController {

    private final JuegoService service;

    @GetMapping
    @Operation(summary = "Listar todos los juegos")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    public ResponseEntity<List<JuegoResponseDTO>> listar(
            @RequestParam(required = false) Long genero,
            @RequestParam(required = false) BigDecimal precioMax) {
        if (genero != null) return ResponseEntity.ok(service.buscarPorGenero(genero));
        if (precioMax != null) return ResponseEntity.ok(service.buscarHastaPrecio(precioMax));
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener juego por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Juego encontrado"),
            @ApiResponse(responseCode = "404", description = "Juego no existe",
                    content = @Content(schema = @Schema(implementation = Map.class)))})
    public ResponseEntity<JuegoResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @PostMapping
    @Operation(summary = "Crear juego",
            description = "Registra un nuevo juego en el catálogo.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Juego creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Género o desarrollador no existe")
    })
    public ResponseEntity<JuegoResponseDTO> crear(@Valid @RequestBody JuegoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar juego")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Juego actualizado"),
            @ApiResponse(responseCode = "404", description = "Juego no existe")
    })
    public ResponseEntity<JuegoResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody JuegoRequestDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @PutMapping("/{id}/disponibilidad")
    @Operation(summary = "Cambiar disponibilidad del juego",
            description = "Activa o desactiva un juego del catálogo.")
    @ApiResponse(responseCode = "200", description = "Disponibilidad actualizada")
    public ResponseEntity<JuegoResponseDTO> cambiarDisponibilidad(@PathVariable Long id, @RequestParam boolean disponible) {
        return ResponseEntity.ok(service.cambiarDisponibilidad(id, disponible));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar juego")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Juego eliminado"),
            @ApiResponse(responseCode = "404", description = "Juego no existe")
    })
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
