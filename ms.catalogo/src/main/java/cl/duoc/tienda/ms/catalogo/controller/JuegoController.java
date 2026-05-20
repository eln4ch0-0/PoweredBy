package cl.duoc.tienda.ms.catalogo.controller;

import cl.duoc.tienda.ms.catalogo.dto.JuegoRequestDTO;
import cl.duoc.tienda.ms.catalogo.dto.JuegoResponseDTO;
import cl.duoc.tienda.ms.catalogo.service.JuegoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/juegos")
@RequiredArgsConstructor
public class JuegoController {
  
    private final JuegoService service;

    @GetMapping
    public ResponseEntity<List<JuegoResponseDTO>> listar(
            @RequestParam(required = false) Long genero,
            @RequestParam(required = false) BigDecimal precioMax) {
        if (genero != null) return ResponseEntity.ok(service.buscarPorGenero(genero));
        if (precioMax != null) return ResponseEntity.ok(service.buscarHastaPrecio(precioMax));
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JuegoResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @PostMapping
    public ResponseEntity<JuegoResponseDTO> crear(@Valid @RequestBody JuegoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }
  
    @PutMapping("/{id}")
    public ResponseEntity<JuegoResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody JuegoRequestDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @PutMapping("/{id}/disponibilidad")
    public ResponseEntity<JuegoResponseDTO> cambiarDisponibilidad(@PathVariable Long id, @RequestParam boolean disponible) {
        return ResponseEntity.ok(service.cambiarDisponibilidad(id, disponible));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
