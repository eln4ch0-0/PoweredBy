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
}