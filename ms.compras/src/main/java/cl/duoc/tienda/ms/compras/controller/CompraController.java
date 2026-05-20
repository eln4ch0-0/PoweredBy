package cl.duoc.tienda.ms.compras.controller;

import cl.duoc.tienda.ms.compras.dto.CompraRequestDTO;
import cl.duoc.tienda.ms.compras.dto.CompraResponseDTO;
import cl.duoc.tienda.ms.compras.service.CompraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compras")
@RequiredArgsConstructor
public class CompraController {
   private final CompraService service;

    @PostMapping
    public ResponseEntity<CompraResponseDTO> realizar(@Valid @RequestBody CompraRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.realizarCompra(dto));
    }

    @GetMapping
    public ResponseEntity<List<CompraResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompraResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<CompraResponseDTO>> historial(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(service.historialUsuario(usuarioId));
    }
}
