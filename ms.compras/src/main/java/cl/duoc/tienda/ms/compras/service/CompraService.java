package cl.duoc.tienda.ms.compras.service;

import cl.duoc.tienda.ms.compras.client.CatalogoClient;
import cl.duoc.tienda.ms.compras.client.UsuarioClient;
import cl.duoc.tienda.ms.compras.dto.*;
import cl.duoc.tienda.ms.compras.exception.CompraInvalidaException;
import cl.duoc.tienda.ms.compras.exception.RecursoNoEncontradoException;
import cl.duoc.tienda.ms.compras.model.Compra;
import cl.duoc.tienda.ms.compras.model.EstadoCompra;
import cl.duoc.tienda.ms.compras.model.JuegoEnBiblioteca;
import cl.duoc.tienda.ms.compras.repository.CompraRepository;
import cl.duoc.tienda.ms.compras.repository.JuegoEnBibliotecaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompraService {

    private final CompraRepository compraRepo;
    private final JuegoEnBibliotecaRepository bibliotecaRepo;
    private final UsuarioClient usuarioClient;
    private final CatalogoClient catalogoClient;

    @Transactional
    public CompraResponseDTO realizarCompra(CompraRequestDTO dto) {
        log.info("Iniciando compra: usuario={} juego={}", dto.getUsuarioId(), dto.getJuegoId());

        JuegoExternoDTO juego = catalogoClient.obtenerJuego(dto.getJuegoId());
        if (Boolean.FALSE.equals(juego.getDisponible()))
            throw new CompraInvalidaException("El juego '" + juego.getTitulo() + "' no está disponible para compra");

        if (bibliotecaRepo.existsByUsuarioIdAndJuegoId(dto.getUsuarioId(), dto.getJuegoId()))
            throw new CompraInvalidaException("El usuario ya posee este juego en su biblioteca");

        UsuarioExternoDTO usuario = usuarioClient.obtenerUsuario(dto.getUsuarioId());
        if (Boolean.FALSE.equals(usuario.getActivo()))
            throw new CompraInvalidaException("El usuario está inactivo");
        if (usuario.getSaldoBilletera().compareTo(juego.getPrecio()) < 0)
            throw new CompraInvalidaException("Saldo insuficiente. Saldo: " + usuario.getSaldoBilletera()
                    + " | Precio: " + juego.getPrecio());

        usuarioClient.descontarSaldo(dto.getUsuarioId(), juego.getPrecio());
        log.info("Saldo descontado correctamente");

        Compra compra = new Compra();
        compra.setUsuarioId(dto.getUsuarioId());
        compra.setJuegoId(dto.getJuegoId());
        compra.setTituloJuego(juego.getTitulo());
        compra.setPrecioPagado(juego.getPrecio());
        Compra guardada = compraRepo.save(compra);

        JuegoEnBiblioteca enBiblio = new JuegoEnBiblioteca();
        enBiblio.setUsuarioId(dto.getUsuarioId());
        enBiblio.setJuegoId(dto.getJuegoId());
        enBiblio.setTituloJuego(juego.getTitulo());
        bibliotecaRepo.save(enBiblio);

        log.info("Compra completada id={} | Juego '{}' agregado a biblioteca del usuario {}",
                guardada.getId(), juego.getTitulo(), dto.getUsuarioId());

        return toResponse(guardada);
    }

    public CompraResponseDTO obtener(Long id) {
        Compra c = compraRepo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Compra con id " + id + " no existe"));
        return toResponse(c);
    }

    public List<CompraResponseDTO> historialUsuario(Long usuarioId) {
        return compraRepo.findByUsuarioId(usuarioId).stream().map(this::toResponse).toList();
    }

    public List<CompraResponseDTO> listar() {
        return compraRepo.findAll().stream().map(this::toResponse).toList();
    }

    private CompraResponseDTO toResponse(Compra c) {
        return new CompraResponseDTO(c.getId(), c.getUsuarioId(), c.getJuegoId(),
                c.getTituloJuego(), c.getPrecioPagado(), c.getFechaCompra(), c.getEstado());
    }
}
