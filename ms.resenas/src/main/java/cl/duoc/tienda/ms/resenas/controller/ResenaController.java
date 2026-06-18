package cl.duoc.tienda.ms.resenas.controller;

import cl.duoc.tienda.ms.resenas.dto.*;
import cl.duoc.tienda.ms.resenas.service.ResenaService;
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
@RequestMapping("/api/resenas")
@RequiredArgsConstructor
@Tag(name = "Reseñas", description = "Operaciones sobre reseñas y calificaciones de juegos")
public class ResenaController {

    private final ResenaService service;

    @PostMapping
    @Operation(summary = "Crear reseña",
            description = "Crea una reseña sobre un juego. Valida con MS-Compras que el usuario " +
                    "realmente posea el juego antes de permitir reseñarlo.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Reseña creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Usuario no posee el juego o ya tiene reseña"),
            @ApiResponse(responseCode = "503", description = "MS-Compras no disponible")
    })
    public ResponseEntity<ResenaResponseDTO> crear(@Valid @RequestBody ResenaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @GetMapping
    @Operation(summary = "Listar todas las reseñas")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    public ResponseEntity<List<ResenaResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener reseña por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reseña encontrada"),
            @ApiResponse(responseCode = "404", description = "Reseña no existe")
    })
    public ResponseEntity<ResenaResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar reseña",
            description = "Permite modificar la calificación, título y contenido de una reseña existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reseña actualizada"),
            @ApiResponse(responseCode = "404", description = "Reseña no existe")
    })
    public ResponseEntity<ResenaResponseDTO> actualizar(@PathVariable Long id,
                                                        @Valid @RequestBody ResenaUpdateDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar reseña")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Reseña eliminada"),
            @ApiResponse(responseCode = "404", description = "Reseña no existe")
    })
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/juego/{juegoId}")
    @Operation(summary = "Reseñas de un juego específico",
            description = "Devuelve todas las reseñas asociadas a un juego.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    public ResponseEntity<List<ResenaResponseDTO>> porJuego(@PathVariable Long juegoId) {
        return ResponseEntity.ok(service.resenasDeJuego(juegoId));
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Reseñas hechas por un usuario")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    public ResponseEntity<List<ResenaResponseDTO>> porUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(service.resenasDeUsuario(usuarioId));
    }

    @GetMapping("/juego/{juegoId}/promedio")
    @Operation(summary = "Promedio de calificación de un juego",
            description = "Calcula el promedio de calificaciones usando consulta JPQL agregada.")
    @ApiResponse(responseCode = "200", description = "Promedio calculado correctamente")
    public ResponseEntity<PromedioResenaDTO> promedio(@PathVariable Long juegoId) {
        return ResponseEntity.ok(service.promedioJuego(juegoId));
    }
}