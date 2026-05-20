package cl.duoc.tienda.ms.compras.controller;

import cl.duoc.tienda.ms.compras.dto.JuegoEnBibliotecaDTO;
import cl.duoc.tienda.ms.compras.service.BibliotecaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/biblioteca")
@RequiredArgsConstructor
public class BibliotecaController {
  private final BibliotecaService service;

    @GetMapping("/{usuarioId}")
    public ResponseEntity<List<JuegoEnBibliotecaDTO>> biblioteca(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(service.bibliotecaDe(usuarioId));
    }

    @GetMapping("/{usuarioId}/{juegoId}")
    public ResponseEntity<JuegoEnBibliotecaDTO> verificar(@PathVariable Long usuarioId, @PathVariable Long juegoId) {
        return ResponseEntity.ok(service.verificarPosesion(usuarioId, juegoId));
    }
}
