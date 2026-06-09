package cl.duoc.tienda.ms.resenas.controller;

import cl.duoc.tienda.ms.resenas.dto.*;
import cl.duoc.tienda.ms.resenas.service.ResenaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resenas")
@RequiredArgsConstructor
public class ResenaController {

    private final ResenaService service;

    @PostMapping
    public ResponseEntity<ResenaResponseDTO> crear(@Valid @RequestBody ResenaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @GetMapping
    public ResponseEntity<List<ResenaResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResenaResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResenaResponseDTO> actualizar(@PathVariable Long id,
                                                        @Valid @RequestBody ResenaUpdateDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/juego/{juegoId}")
    public ResponseEntity<List<ResenaResponseDTO>> porJuego(@PathVariable Long juegoId) {
        return ResponseEntity.ok(service.resenasDeJuego(juegoId));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ResenaResponseDTO>> porUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(service.resenasDeUsuario(usuarioId));
    }

    @GetMapping("/juego/{juegoId}/promedio")
    public ResponseEntity<PromedioResenaDTO> promedio(@PathVariable Long juegoId) {
        return ResponseEntity.ok(service.promedioJuego(juegoId));
    }
}