package cl.duoc.tienda.ms.catalogo.controller;

import cl.duoc.tienda.ms.catalogo.dto.GeneroDTO;
import cl.duoc.tienda.ms.catalogo.service.GeneroService;
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
@RequestMapping("/api/generos")
@RequiredArgsConstructor
@Tag(name = "Géneros", description = "Operaciones sobre géneros de videojuegos")
public class GeneroController {

    private final GeneroService service;

    @GetMapping
    @Operation(summary = "Listar todos los géneros")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    public ResponseEntity<List<GeneroDTO>> listar() { return ResponseEntity.ok(service.listar()); }


    @GetMapping("/{id}")
    @Operation(summary = "Obtener género por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Género encontrado"),
            @ApiResponse(responseCode = "404", description = "Género no existe")
    })
    public ResponseEntity<GeneroDTO> obtener(@PathVariable Long id) { return ResponseEntity.ok(service.obtener(id)); }


    @PostMapping
    @Operation(summary = "Crear género")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Género creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<GeneroDTO> crear(@Valid @RequestBody GeneroDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }


    @PutMapping("/{id}")
    @Operation(summary = "Actualizar género")
    @ApiResponse(responseCode = "200", description = "Género actualizado")
    public ResponseEntity<GeneroDTO> actualizar(@PathVariable Long id, @Valid @RequestBody GeneroDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar género")
    @ApiResponse(responseCode = "204", description = "Género eliminado")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
