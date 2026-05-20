package cl.duoc.tienda.ms.catalogo.controller;

import cl.duoc.tienda.ms.catalogo.dto.DesarrolladorDTO;
import cl.duoc.tienda.ms.catalogo.service.DesarrolladorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/desarrolladores")
@RequiredArgsConstructor
public class DesarrolladorController {
  
    private final DesarrolladorService service;

    @GetMapping public ResponseEntity<List<DesarrolladorDTO>> listar() { return ResponseEntity.ok(service.listar()); }
    @GetMapping("/{id}") public ResponseEntity<DesarrolladorDTO> obtener(@PathVariable Long id) { return ResponseEntity.ok(service.obtener(id)); }
    @PostMapping public ResponseEntity<DesarrolladorDTO> crear(@Valid @RequestBody DesarrolladorDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }
    @PutMapping("/{id}") public ResponseEntity<DesarrolladorDTO> actualizar(@PathVariable Long id, @Valid @RequestBody DesarrolladorDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }
    @DeleteMapping("/{id}") public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
