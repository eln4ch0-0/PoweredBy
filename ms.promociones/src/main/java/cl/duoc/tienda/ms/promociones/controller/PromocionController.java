package cl.duoc.tienda.ms.promociones.controller;

import cl.duoc.tienda.ms.promociones.dto.*;
import cl.duoc.tienda.ms.promociones.service.PromocionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promociones")
@RequiredArgsConstructor
public class PromocionController {

    private final PromocionService service;

    @PostMapping
    public ResponseEntity<PromocionResponseDTO> crear(@Valid @RequestBody PromocionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @GetMapping
    public ResponseEntity<List<PromocionResponseDTO>> listar(
            @RequestParam(required = false, defaultValue = "false") boolean vigentes) {
        return ResponseEntity.ok(vigentes ? service.listarVigentes() : service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PromocionResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<PromocionResponseDTO> porCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(service.obtenerPorCodigo(codigo));
    }

    @GetMapping("/juego/{juegoId}")
    public ResponseEntity<List<PromocionResponseDTO>> porJuego(@PathVariable Long juegoId) {
        return ResponseEntity.ok(service.porJuego(juegoId));
    }

    @PutMapping("/{id}/desactivar")
    public ResponseEntity<PromocionResponseDTO> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(service.desactivar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint clave: aplica una promoción a un precio. Lo consume MS-Compras (a futuro)
     * o cualquier cliente que necesite calcular el descuento.
     */
    @PostMapping("/aplicar")
    public ResponseEntity<AplicarPromocionResponseDTO> aplicar(
            @Valid @RequestBody AplicarPromocionRequestDTO dto) {
        return ResponseEntity.ok(service.aplicar(dto));
    }
}