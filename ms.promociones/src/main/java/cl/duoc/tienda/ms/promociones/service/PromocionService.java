package cl.duoc.tienda.ms.promociones.service;

import cl.duoc.tienda.ms.promociones.client.CatalogoClient;
import cl.duoc.tienda.ms.promociones.dto.*;
import cl.duoc.tienda.ms.promociones.exception.PromocionInvalidaException;
import cl.duoc.tienda.ms.promociones.exception.RecursoNoEncontradoException;
import cl.duoc.tienda.ms.promociones.exception.ReglaNegocioException;
import cl.duoc.tienda.ms.promociones.model.Promocion;
import cl.duoc.tienda.ms.promociones.model.TipoDescuento;
import cl.duoc.tienda.ms.promociones.repository.PromocionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromocionService {

    private static final BigDecimal CIEN = new BigDecimal("100");

    private final PromocionRepository repo;
    private final CatalogoClient catalogoClient;

    public PromocionResponseDTO crear(PromocionRequestDTO dto) {
        log.info("Creando promoción con código {}", dto.getCodigo());

        validarReglasDeNegocio(dto);

        if (repo.existsByCodigo(dto.getCodigo()))
            throw new ReglaNegocioException("Ya existe una promoción con el código " + dto.getCodigo());

        // Si la promoción es específica para un juego, validar que ese juego exista
        if (dto.getJuegoId() != null) {
            catalogoClient.obtenerJuego(dto.getJuegoId()); // lanza 404 si no existe
        }

        Promocion p = new Promocion();
        p.setCodigo(dto.getCodigo());
        p.setNombre(dto.getNombre());
        p.setDescripcion(dto.getDescripcion());
        p.setTipoDescuento(dto.getTipoDescuento());
        p.setValorDescuento(dto.getValorDescuento());
        p.setFechaInicio(dto.getFechaInicio());
        p.setFechaFin(dto.getFechaFin());
        p.setJuegoId(dto.getJuegoId());
        p.setUsosMaximos(dto.getUsosMaximos());

        Promocion guardada = repo.save(p);
        log.info("Promoción creada id={} código={}", guardada.getId(), guardada.getCodigo());
        return toResponse(guardada);
    }

    public PromocionResponseDTO obtener(Long id) {
        return toResponse(buscar(id));
    }

    public PromocionResponseDTO obtenerPorCodigo(String codigo) {
        Promocion p = repo.findByCodigo(codigo)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la promoción " + codigo));
        return toResponse(p);
    }

    public List<PromocionResponseDTO> listar() {
        return repo.findAll().stream().map(this::toResponse).toList();
    }

    public List<PromocionResponseDTO> listarVigentes() {
        return repo.findVigentes(LocalDateTime.now()).stream().map(this::toResponse).toList();
    }

    public List<PromocionResponseDTO> porJuego(Long juegoId) {
        return repo.findByJuegoId(juegoId).stream().map(this::toResponse).toList();
    }

    public PromocionResponseDTO desactivar(Long id) {
        Promocion p = buscar(id);
        p.setActiva(false);
        log.warn("Promoción {} desactivada manualmente", id);
        return toResponse(repo.save(p));
    }

    public void eliminar(Long id) {
        if (!repo.existsById(id))
            throw new RecursoNoEncontradoException("Promoción con id " + id + " no existe");
        repo.deleteById(id);
        log.warn("Promoción con id={} eliminada", id);
    }


    @Transactional
    public AplicarPromocionResponseDTO aplicar(AplicarPromocionRequestDTO dto) {
        log.info("Aplicando promoción {} al juego {} con precio {}",
                dto.getCodigo(), dto.getJuegoId(), dto.getPrecioOriginal());

        Promocion p = repo.findByCodigo(dto.getCodigo())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe la promoción " + dto.getCodigo()));

        // --- Validaciones de aplicabilidad ---
        LocalDateTime ahora = LocalDateTime.now();

        if (Boolean.FALSE.equals(p.getActiva()))
            throw new PromocionInvalidaException("La promoción " + p.getCodigo() + " está desactivada");

        if (ahora.isBefore(p.getFechaInicio()))
            throw new PromocionInvalidaException("La promoción " + p.getCodigo()
                    + " aún no está vigente. Inicia el " + p.getFechaInicio());

        if (ahora.isAfter(p.getFechaFin()))
            throw new PromocionInvalidaException("La promoción " + p.getCodigo()
                    + " expiró el " + p.getFechaFin());

        if (p.getUsosMaximos() != null && p.getUsosActuales() >= p.getUsosMaximos())
            throw new PromocionInvalidaException("La promoción " + p.getCodigo()
                    + " alcanzó el máximo de usos (" + p.getUsosMaximos() + ")");

        // Si la promoción es específica para un juego, validar que coincida
        if (p.getJuegoId() != null && !p.getJuegoId().equals(dto.getJuegoId()))
            throw new PromocionInvalidaException("La promoción " + p.getCodigo()
                    + " solo aplica al juego " + p.getJuegoId() + ", no al juego " + dto.getJuegoId());

        // --- Cálculo del descuento ---
        BigDecimal montoDescuento;
        if (p.getTipoDescuento() == TipoDescuento.PORCENTAJE) {
            montoDescuento = dto.getPrecioOriginal()
                    .multiply(p.getValorDescuento())
                    .divide(CIEN, 2, RoundingMode.HALF_UP);
        } else {
            montoDescuento = p.getValorDescuento();
        }

        BigDecimal precioFinal = dto.getPrecioOriginal().subtract(montoDescuento);
        if (precioFinal.compareTo(BigDecimal.ZERO) < 0)
            throw new ReglaNegocioException(
                    "El descuento (" + montoDescuento + ") supera el precio original ("
                            + dto.getPrecioOriginal() + "). La promoción no puede aplicarse.");

        // --- Incrementar contador de usos ---
        p.setUsosActuales(p.getUsosActuales() + 1);
        repo.save(p);
        log.info("Promoción {} aplicada. Descuento={} | Precio final={}",
                p.getCodigo(), montoDescuento, precioFinal);

        return new AplicarPromocionResponseDTO(
                p.getCodigo(), p.getNombre(),
                p.getTipoDescuento(), p.getValorDescuento(),
                dto.getPrecioOriginal(), montoDescuento, precioFinal);
    }

    // --- Helpers ---

    private void validarReglasDeNegocio(PromocionRequestDTO dto) {
        if (dto.getFechaInicio().isAfter(dto.getFechaFin()))
            throw new ReglaNegocioException("La fecha de inicio debe ser anterior a la fecha de fin");

        if (dto.getTipoDescuento() == TipoDescuento.PORCENTAJE
                && dto.getValorDescuento().compareTo(CIEN) > 0)
            throw new ReglaNegocioException(
                    "El descuento por porcentaje no puede ser mayor a 100. Valor recibido: "
                            + dto.getValorDescuento());
    }

    private Promocion buscar(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Promoción con id " + id + " no existe"));
    }

    private PromocionResponseDTO toResponse(Promocion p) {
        return new PromocionResponseDTO(p.getId(), p.getCodigo(), p.getNombre(),
                p.getDescripcion(), p.getTipoDescuento(), p.getValorDescuento(),
                p.getFechaInicio(), p.getFechaFin(), p.getActiva(),
                p.getJuegoId(), p.getUsosMaximos(), p.getUsosActuales());
    }
}