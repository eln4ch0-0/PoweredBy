package cl.duoc.tienda.ms.promociones.service;

import cl.duoc.tienda.ms.promociones.client.CatalogoClient;
import cl.duoc.tienda.ms.promociones.dto.AplicarPromocionRequestDTO;
import cl.duoc.tienda.ms.promociones.dto.AplicarPromocionResponseDTO;
import cl.duoc.tienda.ms.promociones.dto.PromocionRequestDTO;
import cl.duoc.tienda.ms.promociones.dto.PromocionResponseDTO;
import cl.duoc.tienda.ms.promociones.exception.PromocionInvalidaException;
import cl.duoc.tienda.ms.promociones.exception.RecursoNoEncontradoException;
import cl.duoc.tienda.ms.promociones.exception.ReglaNegocioException;
import cl.duoc.tienda.ms.promociones.model.Promocion;
import cl.duoc.tienda.ms.promociones.model.TipoDescuento;
import cl.duoc.tienda.ms.promociones.repository.PromocionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests del servicio de Promociones")
public class PromocionServiceTest {

    @Mock
    private PromocionRepository repo;

    @Mock
    private CatalogoClient catalogoClient;

    @InjectMocks
    private PromocionService service;

    private Promocion promocionPorcentaje;
    private Promocion promocionMontoFijo;

    @BeforeEach
    void setUp() {
        // Promoción tipo PORCENTAJE - 20% de descuento, vigente
        promocionPorcentaje = new Promocion();
        promocionPorcentaje.setId(1L);
        promocionPorcentaje.setCodigo("VERANO2026");
        promocionPorcentaje.setNombre("Descuento de verano");
        promocionPorcentaje.setTipoDescuento(TipoDescuento.PORCENTAJE);
        promocionPorcentaje.setValorDescuento(new BigDecimal("20"));
        promocionPorcentaje.setFechaInicio(LocalDateTime.now().minusDays(5));
        promocionPorcentaje.setFechaFin(LocalDateTime.now().plusDays(5));
        promocionPorcentaje.setActiva(true);
        promocionPorcentaje.setUsosActuales(0);
        promocionPorcentaje.setUsosMaximos(100);

        // Promoción tipo MONTO_FIJO - 5000 pesos de descuento
        promocionMontoFijo = new Promocion();
        promocionMontoFijo.setId(2L);
        promocionMontoFijo.setCodigo("FIJO5000");
        promocionMontoFijo.setNombre("5000 pesos off");
        promocionMontoFijo.setTipoDescuento(TipoDescuento.MONTO_FIJO);
        promocionMontoFijo.setValorDescuento(new BigDecimal("5000"));
        promocionMontoFijo.setFechaInicio(LocalDateTime.now().minusDays(1));
        promocionMontoFijo.setFechaFin(LocalDateTime.now().plusDays(10));
        promocionMontoFijo.setActiva(true);
        promocionMontoFijo.setUsosActuales(0);
        promocionMontoFijo.setUsosMaximos(null); // ilimitado
    }


    // TESTS DEL MÉTODO CREAR()


    @Test
    @DisplayName("Debe crear promoción correctamente cuando los datos son válidos")
    void shouldCrearPromocionCuandoDatosValidos() {
        // GIVEN
        PromocionRequestDTO dto = new PromocionRequestDTO();
        dto.setCodigo("NUEVA2026");
        dto.setNombre("Promo nueva");
        dto.setTipoDescuento(TipoDescuento.PORCENTAJE);
        dto.setValorDescuento(new BigDecimal("15"));
        dto.setFechaInicio(LocalDateTime.now());
        dto.setFechaFin(LocalDateTime.now().plusDays(30));

        when(repo.existsByCodigo("NUEVA2026")).thenReturn(false);
        when(repo.save(any(Promocion.class))).thenReturn(promocionPorcentaje);

        // WHEN
        PromocionResponseDTO resultado = service.crear(dto);

        // THEN
        assertNotNull(resultado);
        assertEquals("VERANO2026", resultado.getCodigo());
        verify(repo, times(1)).save(any(Promocion.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el código ya existe")
    void shouldThrowExceptionCuandoCodigoYaExiste() {
        // GIVEN
        PromocionRequestDTO dto = new PromocionRequestDTO();
        dto.setCodigo("VERANO2026");
        dto.setNombre("Duplicada");
        dto.setTipoDescuento(TipoDescuento.PORCENTAJE);
        dto.setValorDescuento(new BigDecimal("10"));
        dto.setFechaInicio(LocalDateTime.now());
        dto.setFechaFin(LocalDateTime.now().plusDays(10));

        when(repo.existsByCodigo("VERANO2026")).thenReturn(true);

        // WHEN & THEN
        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class,
                () -> service.crear(dto));
        assertTrue(ex.getMessage().contains("VERANO2026"));
        verify(repo, never()).save(any(Promocion.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando fecha inicio es posterior a fecha fin")
    void shouldThrowExceptionCuandoFechasInvalidas() {
        // GIVEN
        PromocionRequestDTO dto = new PromocionRequestDTO();
        dto.setCodigo("INVALIDA");
        dto.setNombre("Fechas invertidas");
        dto.setTipoDescuento(TipoDescuento.PORCENTAJE);
        dto.setValorDescuento(new BigDecimal("10"));
        dto.setFechaInicio(LocalDateTime.now().plusDays(10));
        dto.setFechaFin(LocalDateTime.now());

        // WHEN & THEN
        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class,
                () -> service.crear(dto));
        assertTrue(ex.getMessage().contains("fecha de inicio"));
        verify(repo, never()).save(any(Promocion.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando descuento porcentaje supera el 100%")
    void shouldThrowExceptionCuandoPorcentajeMayorACien() {
        // GIVEN
        PromocionRequestDTO dto = new PromocionRequestDTO();
        dto.setCodigo("INVALIDA");
        dto.setNombre("Porcentaje inválido");
        dto.setTipoDescuento(TipoDescuento.PORCENTAJE);
        dto.setValorDescuento(new BigDecimal("150"));
        dto.setFechaInicio(LocalDateTime.now());
        dto.setFechaFin(LocalDateTime.now().plusDays(10));

        // WHEN & THEN
        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class,
                () -> service.crear(dto));
        assertTrue(ex.getMessage().contains("100"));
    }

    // ============================================================
    // TESTS DEL MÉTODO obtener() / obtenerPorCodigo()
    // ============================================================

    @Test
    @DisplayName("Debe obtener promoción por ID cuando existe")
    void shouldObtenerPromocionPorIdCuandoExiste() {
        // GIVEN
        when(repo.findById(1L)).thenReturn(Optional.of(promocionPorcentaje));

        // WHEN
        PromocionResponseDTO resultado = service.obtener(1L);

        // THEN
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("VERANO2026", resultado.getCodigo());
    }

    @Test
    @DisplayName("Debe lanzar excepción al obtener promoción inexistente")
    void shouldThrowExceptionCuandoIdNoExiste() {
        // GIVEN
        when(repo.findById(999L)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(RecursoNoEncontradoException.class,
                () -> service.obtener(999L));
    }

    // ============================================================
    // TESTS DEL MÉTODO aplicar() - EL MÁS IMPORTANTE
    // ============================================================

    @Test
    @DisplayName("Debe aplicar descuento por PORCENTAJE correctamente")
    void shouldAplicarDescuentoPorcentajeCorrectamente() {
        // GIVEN
        AplicarPromocionRequestDTO dto = new AplicarPromocionRequestDTO();
        dto.setCodigo("VERANO2026");
        dto.setJuegoId(1L);
        dto.setPrecioOriginal(new BigDecimal("10000"));

        when(repo.findByCodigo("VERANO2026")).thenReturn(Optional.of(promocionPorcentaje));
        when(repo.save(any(Promocion.class))).thenReturn(promocionPorcentaje);

        // WHEN
        AplicarPromocionResponseDTO resultado = service.aplicar(dto);

        // THEN
        assertEquals(new BigDecimal("2000.00"), resultado.getMontoDescuento());
        assertEquals(new BigDecimal("8000.00"), resultado.getPrecioFinal());
        assertEquals("VERANO2026", resultado.getCodigoPromocion());
    }

    @Test
    @DisplayName("Debe aplicar descuento por MONTO_FIJO correctamente")
    void shouldAplicarDescuentoMontoFijoCorrectamente() {
        // GIVEN
        AplicarPromocionRequestDTO dto = new AplicarPromocionRequestDTO();
        dto.setCodigo("FIJO5000");
        dto.setJuegoId(1L);
        dto.setPrecioOriginal(new BigDecimal("20000"));

        when(repo.findByCodigo("FIJO5000")).thenReturn(Optional.of(promocionMontoFijo));
        when(repo.save(any(Promocion.class))).thenReturn(promocionMontoFijo);

        // WHEN
        AplicarPromocionResponseDTO resultado = service.aplicar(dto);

        // THEN
        assertEquals(new BigDecimal("5000"), resultado.getMontoDescuento());
        assertEquals(new BigDecimal("15000"), resultado.getPrecioFinal());
    }

    @Test
    @DisplayName("Debe lanzar excepción al aplicar promoción expirada")
    void shouldThrowExceptionCuandoPromocionExpirada() {
        // GIVEN
        promocionPorcentaje.setFechaFin(LocalDateTime.now().minusDays(1));

        AplicarPromocionRequestDTO dto = new AplicarPromocionRequestDTO();
        dto.setCodigo("VERANO2026");
        dto.setJuegoId(1L);
        dto.setPrecioOriginal(new BigDecimal("10000"));

        when(repo.findByCodigo("VERANO2026")).thenReturn(Optional.of(promocionPorcentaje));

        // WHEN & THEN
        PromocionInvalidaException ex = assertThrows(PromocionInvalidaException.class,
                () -> service.aplicar(dto));
        assertTrue(ex.getMessage().contains("expiró"));
        verify(repo, never()).save(any(Promocion.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al aplicar promoción desactivada")
    void shouldThrowExceptionCuandoPromocionDesactivada() {
        // GIVEN
        promocionPorcentaje.setActiva(false);

        AplicarPromocionRequestDTO dto = new AplicarPromocionRequestDTO();
        dto.setCodigo("VERANO2026");
        dto.setJuegoId(1L);
        dto.setPrecioOriginal(new BigDecimal("10000"));

        when(repo.findByCodigo("VERANO2026")).thenReturn(Optional.of(promocionPorcentaje));

        // WHEN & THEN
        PromocionInvalidaException ex = assertThrows(PromocionInvalidaException.class,
                () -> service.aplicar(dto));
        assertTrue(ex.getMessage().contains("desactivada"));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando se alcanza el máximo de usos")
    void shouldThrowExceptionCuandoUsosMaximosAlcanzados() {
        // GIVEN
        promocionPorcentaje.setUsosActuales(100);
        promocionPorcentaje.setUsosMaximos(100);

        AplicarPromocionRequestDTO dto = new AplicarPromocionRequestDTO();
        dto.setCodigo("VERANO2026");
        dto.setJuegoId(1L);
        dto.setPrecioOriginal(new BigDecimal("10000"));

        when(repo.findByCodigo("VERANO2026")).thenReturn(Optional.of(promocionPorcentaje));

        // WHEN & THEN
        PromocionInvalidaException ex = assertThrows(PromocionInvalidaException.class,
                () -> service.aplicar(dto));
        assertTrue(ex.getMessage().contains("máximo de usos"));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando promoción es específica para otro juego")
    void shouldThrowExceptionCuandoJuegoNoCoincide() {
        // GIVEN
        promocionPorcentaje.setJuegoId(99L); // promo solo para juego 99

        AplicarPromocionRequestDTO dto = new AplicarPromocionRequestDTO();
        dto.setCodigo("VERANO2026");
        dto.setJuegoId(1L); // intentamos aplicar al juego 1
        dto.setPrecioOriginal(new BigDecimal("10000"));

        when(repo.findByCodigo("VERANO2026")).thenReturn(Optional.of(promocionPorcentaje));

        // WHEN & THEN
        PromocionInvalidaException ex = assertThrows(PromocionInvalidaException.class,
                () -> service.aplicar(dto));
        assertTrue(ex.getMessage().contains("solo aplica al juego"));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando descuento supera el precio original")
    void shouldThrowExceptionCuandoDescuentoSuperaPrecio() {
        // GIVEN - descuento fijo de 5000 sobre precio de solo 2000
        AplicarPromocionRequestDTO dto = new AplicarPromocionRequestDTO();
        dto.setCodigo("FIJO5000");
        dto.setJuegoId(1L);
        dto.setPrecioOriginal(new BigDecimal("2000"));

        when(repo.findByCodigo("FIJO5000")).thenReturn(Optional.of(promocionMontoFijo));

        // WHEN & THEN
        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class,
                () -> service.aplicar(dto));
        assertTrue(ex.getMessage().contains("supera el precio"));
    }

    // ============================================================
    // TEST DEL MÉTODO eliminar()
    // ============================================================

    @Test
    @DisplayName("Debe eliminar promoción cuando existe")
    void shouldEliminarPromocionCuandoExiste() {
        // GIVEN
        when(repo.existsById(1L)).thenReturn(true);

        // WHEN
        service.eliminar(1L);

        // THEN
        verify(repo, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar promoción inexistente")
    void shouldThrowExceptionAlEliminarInexistente() {
        // GIVEN
        when(repo.existsById(999L)).thenReturn(false);

        // WHEN & THEN
        assertThrows(RecursoNoEncontradoException.class,
                () -> service.eliminar(999L));
        verify(repo, never()).deleteById(any());
    }
}
