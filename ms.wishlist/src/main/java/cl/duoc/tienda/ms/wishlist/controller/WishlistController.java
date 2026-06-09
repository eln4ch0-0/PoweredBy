package cl.duoc.tienda.ms.wishlist.controller;

import cl.duoc.tienda.ms.wishlist.dto.*;
import cl.duoc.tienda.ms.wishlist.service.WishlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService service;

    @PostMapping
    public ResponseEntity<WishlistResponseDTO> agregar(@Valid @RequestBody WishlistRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.agregar(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WishlistResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<WishlistResponseDTO>> porUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(service.wishlistDe(usuarioId));
    }

    @GetMapping("/usuario/{usuarioId}/juego/{juegoId}")
    public ResponseEntity<WishlistResponseDTO> verificar(@PathVariable Long usuarioId,
                                                         @PathVariable Long juegoId) {
        return ResponseEntity.ok(service.obtenerPorUsuarioYJuego(usuarioId, juegoId));
    }

    @GetMapping("/juego/{juegoId}/popularidad")
    public ResponseEntity<ConteoWishlistDTO> popularidad(@PathVariable Long juegoId) {
        return ResponseEntity.ok(service.popularidadJuego(juegoId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/usuario/{usuarioId}/juego/{juegoId}")
    public ResponseEntity<Void> eliminarPorUsuarioYJuego(@PathVariable Long usuarioId,
                                                         @PathVariable Long juegoId) {
        service.eliminarPorUsuarioYJuego(usuarioId, juegoId);
        return ResponseEntity.noContent().build();
    }
}