package cl.duoc.tienda.ms.promociones.controller;

import cl.duoc.tienda.ms.promociones.dto.*;
import cl.duoc.tienda.ms.promociones.service.PromocionService;
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
@RequestMapping("/api/promociones")
@RequiredArgsConstructor
@Tag(name = "Promociones", description = "Gestión y aplicación de promociones de descuento")
public class PromocionController {

    private final PromocionService service;

    @PostMapping
    @Operation(summary = "Crear promoción",
            description = "Crea una promoción con descuento por PORCENTAJE o MONTO_FIJO. " +
                    "Si se especifica juegoId, solo aplica a ese juego. Si no, es global.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Promoción creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o regla de negocio violada"),
            @ApiResponse(responseCode = "404", description = "Juego no existe en catálogo"),
            @ApiResponse(responseCode = "503", description = "MS-Catalogo no disponible")
    })
    public ResponseEntity<PromocionResponseDTO> crear(@Valid @RequestBody PromocionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @GetMapping
    @Operation(summary = "Listar promociones",
            description = "Devuelve todas las promociones. Use ?vigentes=true para filtrar solo las vigentes.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    public ResponseEntity<List<PromocionResponseDTO>> listar(
            @RequestParam(required = false, defaultValue = "false") boolean vigentes) {
        return ResponseEntity.ok(vigentes ? service.listarVigentes() : service.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener promoción por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Promoción encontrada"),
            @ApiResponse(responseCode = "404", description = "Promoción no existe")
    })
    public ResponseEntity<PromocionResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @GetMapping("/codigo/{codigo}")
    @Operation(summary = "Obtener promoción por código",
            description = "Busca una promoción usando su código único (ej: 'VERANO2026').")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Promoción encontrada"),
            @ApiResponse(responseCode = "404", description = "Código no existe")
    })
    public ResponseEntity<PromocionResponseDTO> porCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(service.obtenerPorCodigo(codigo));
    }

    @GetMapping("/juego/{juegoId}")
    @Operation(summary = "Promociones de un juego específico")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    public ResponseEntity<List<PromocionResponseDTO>> porJuego(@PathVariable Long juegoId) {
        return ResponseEntity.ok(service.porJuego(juegoId));
    }

    @PutMapping("/{id}/desactivar")
    @Operation(summary = "Desactivar promoción",
            description = "Marca una promoción como inactiva sin eliminarla del sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Promoción desactivada"),
            @ApiResponse(responseCode = "404", description = "Promoción no existe")
    })
    public ResponseEntity<PromocionResponseDTO> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(service.desactivar(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar promoción")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Promoción eliminada"),
            @ApiResponse(responseCode = "404", description = "Promoción no existe")
    })
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/aplicar")
    @Operation(summary = "Aplicar código de promoción a un precio",
            description = "Endpoint principal del microservicio. Valida vigencia, usos máximos, " +
                    "aplicabilidad al juego y calcula el precio con descuento. " +
                    "Puede ser consumido por MS-Compras durante el flujo de compra.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Promoción aplicada correctamente"),
            @ApiResponse(responseCode = "400", description = "Descuento mayor al precio original"),
            @ApiResponse(responseCode = "404", description = "Código de promoción no existe"),
            @ApiResponse(responseCode = "422", description = "Promoción expirada, desactivada o no aplica al juego")
    })
    public ResponseEntity<AplicarPromocionResponseDTO> aplicar(
            @Valid @RequestBody AplicarPromocionRequestDTO dto) {
        return ResponseEntity.ok(service.aplicar(dto));
    }
}