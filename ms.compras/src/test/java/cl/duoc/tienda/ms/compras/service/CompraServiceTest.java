package cl.duoc.tienda.ms.compras.service;

import cl.duoc.tienda.ms.compras.client.CatalogoClient;
import cl.duoc.tienda.ms.compras.client.UsuarioClient;
import cl.duoc.tienda.ms.compras.dto.CompraRequestDTO;
import cl.duoc.tienda.ms.compras.dto.CompraResponseDTO;
import cl.duoc.tienda.ms.compras.dto.JuegoExternoDTO;
import cl.duoc.tienda.ms.compras.dto.UsuarioExternoDTO;
import cl.duoc.tienda.ms.compras.exception.CompraInvalidaException;
import cl.duoc.tienda.ms.compras.exception.RecursoNoEncontradoException;
import cl.duoc.tienda.ms.compras.model.Compra;
import cl.duoc.tienda.ms.compras.model.EstadoCompra;
import cl.duoc.tienda.ms.compras.repository.CompraRepository;
import cl.duoc.tienda.ms.compras.repository.JuegoEnBibliotecaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests del servicio de Compras")
public class CompraServiceTest {

    @Mock
    private CompraRepository compraRepo;

    @Mock
    private JuegoEnBibliotecaRepository bibliotecaRepo;

    @Mock
    private UsuarioClient usuarioClient;

    @Mock
    private CatalogoClient catalogoClient;

    @InjectMocks
    private CompraService service;

    private CompraRequestDTO request;
    private JuegoExternoDTO juegoDisponible;
    private UsuarioExternoDTO usuarioActivo;
    private Compra compraGuardada;

    @BeforeEach
    void setUp() {
        request = new CompraRequestDTO();
        request.setUsuarioId(1L);
        request.setJuegoId(10L);

        juegoDisponible = new JuegoExternoDTO();
        juegoDisponible.setId(10L);
        juegoDisponible.setTitulo("Elden Ring");
        juegoDisponible.setPrecio(new BigDecimal("30000"));
        juegoDisponible.setDisponible(true);

        usuarioActivo = new UsuarioExternoDTO();
        usuarioActivo.setId(1L);
        usuarioActivo.setUsername("ignacio");
        usuarioActivo.setEmail("ignacio@duoc.cl");
        usuarioActivo.setSaldoBilletera(new BigDecimal("50000"));
        usuarioActivo.setActivo(true);

        compraGuardada = new Compra();
        compraGuardada.setId(100L);
        compraGuardada.setUsuarioId(1L);
        compraGuardada.setJuegoId(10L);
        compraGuardada.setTituloJuego("Elden Ring");
        compraGuardada.setPrecioPagado(new BigDecimal("30000"));
        compraGuardada.setFechaCompra(LocalDateTime.now());
        compraGuardada.setEstado(EstadoCompra.COMPLETADA);
    }

    // TESTS DEL MÉTODO realizarCompra() - EL MÁS IMPORTANTE

    @Test
    @DisplayName("Debe realizar compra exitosamente cuando todos los datos son válidos")
    void shouldRealizarCompraCuandoDatosValidos() {
        // GIVEN
        when(catalogoClient.obtenerJuego(10L)).thenReturn(juegoDisponible);
        when(bibliotecaRepo.existsByUsuarioIdAndJuegoId(1L, 10L)).thenReturn(false);
        when(usuarioClient.obtenerUsuario(1L)).thenReturn(usuarioActivo);
        when(compraRepo.save(any(Compra.class))).thenReturn(compraGuardada);

        // WHEN
        CompraResponseDTO resultado = service.realizarCompra(request);

        // THEN
        assertNotNull(resultado);
        assertEquals("Elden Ring", resultado.getTituloJuego());
        assertEquals(new BigDecimal("30000"), resultado.getPrecioPagado());
        verify(usuarioClient, times(1)).descontarSaldo(1L, new BigDecimal("30000"));
        verify(compraRepo, times(1)).save(any(Compra.class));
        verify(bibliotecaRepo, times(1)).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el juego no está disponible")
    void shouldThrowExceptionCuandoJuegoNoDisponible() {
        // GIVEN
        juegoDisponible.setDisponible(false);
        when(catalogoClient.obtenerJuego(10L)).thenReturn(juegoDisponible);

        // WHEN & THEN
        CompraInvalidaException ex = assertThrows(CompraInvalidaException.class,
                () -> service.realizarCompra(request));
        assertTrue(ex.getMessage().contains("no está disponible"));
        verify(usuarioClient, never()).descontarSaldo(any(), any());
        verify(compraRepo, never()).save(any(Compra.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el usuario ya posee el juego")
    void shouldThrowExceptionCuandoUsuarioYaPoseeJuego() {
        // GIVEN
        when(catalogoClient.obtenerJuego(10L)).thenReturn(juegoDisponible);
        when(bibliotecaRepo.existsByUsuarioIdAndJuegoId(1L, 10L)).thenReturn(true);

        // WHEN & THEN
        CompraInvalidaException ex = assertThrows(CompraInvalidaException.class,
                () -> service.realizarCompra(request));
        assertTrue(ex.getMessage().contains("ya posee"));
        verify(usuarioClient, never()).descontarSaldo(any(), any());
        verify(compraRepo, never()).save(any(Compra.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el usuario está inactivo")
    void shouldThrowExceptionCuandoUsuarioInactivo() {
        // GIVEN
        usuarioActivo.setActivo(false);
        when(catalogoClient.obtenerJuego(10L)).thenReturn(juegoDisponible);
        when(bibliotecaRepo.existsByUsuarioIdAndJuegoId(1L, 10L)).thenReturn(false);
        when(usuarioClient.obtenerUsuario(1L)).thenReturn(usuarioActivo);

        // WHEN & THEN
        CompraInvalidaException ex = assertThrows(CompraInvalidaException.class,
                () -> service.realizarCompra(request));
        assertTrue(ex.getMessage().contains("inactivo"));
        verify(usuarioClient, never()).descontarSaldo(any(), any());
        verify(compraRepo, never()).save(any(Compra.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el saldo es insuficiente")
    void shouldThrowExceptionCuandoSaldoInsuficiente() {
        // GIVEN - juego cuesta 30000, usuario solo tiene 10000
        usuarioActivo.setSaldoBilletera(new BigDecimal("10000"));
        when(catalogoClient.obtenerJuego(10L)).thenReturn(juegoDisponible);
        when(bibliotecaRepo.existsByUsuarioIdAndJuegoId(1L, 10L)).thenReturn(false);
        when(usuarioClient.obtenerUsuario(1L)).thenReturn(usuarioActivo);

        // WHEN & THEN
        CompraInvalidaException ex = assertThrows(CompraInvalidaException.class,
                () -> service.realizarCompra(request));
        assertTrue(ex.getMessage().contains("Saldo insuficiente"));
        verify(usuarioClient, never()).descontarSaldo(any(), any());
        verify(compraRepo, never()).save(any(Compra.class));
    }

    // TESTS DEL MÉTODO obtener()

    @Test
    @DisplayName("Debe obtener compra por ID cuando existe")
    void shouldObtenerCompraPorIdCuandoExiste() {
        // GIVEN
        when(compraRepo.findById(100L)).thenReturn(Optional.of(compraGuardada));

        // WHEN
        CompraResponseDTO resultado = service.obtener(100L);

        // THEN
        assertNotNull(resultado);
        assertEquals(100L, resultado.getId());
        assertEquals("Elden Ring", resultado.getTituloJuego());
    }

    @Test
    @DisplayName("Debe lanzar excepción al obtener compra inexistente")
    void shouldThrowExceptionCuandoCompraNoExiste() {
        // GIVEN
        when(compraRepo.findById(999L)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(RecursoNoEncontradoException.class,
                () -> service.obtener(999L));
    }

    // TESTS DEL MÉTODO historialUsuario()

    @Test
    @DisplayName("Debe obtener historial de compras de un usuario")
    void shouldObtenerHistorialUsuario() {
        // GIVEN
        when(compraRepo.findByUsuarioId(1L)).thenReturn(List.of(compraGuardada));

        // WHEN
        List<CompraResponseDTO> resultado = service.historialUsuario(1L);

        // THEN
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Elden Ring", resultado.get(0).getTituloJuego());
        verify(compraRepo, times(1)).findByUsuarioId(1L);
    }

    @Test
    @DisplayName("Debe devolver lista vacía cuando el usuario no tiene compras")
    void shouldDevolverListaVaciaCuandoSinCompras() {
        // GIVEN
        when(compraRepo.findByUsuarioId(2L)).thenReturn(List.of());

        // WHEN
        List<CompraResponseDTO> resultado = service.historialUsuario(2L);

        // THEN
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // TESTS DEL MÉTODO listar()

    @Test
    @DisplayName("Debe listar todas las compras del sistema")
    void shouldListarTodasLasCompras() {
        // GIVEN
        when(compraRepo.findAll()).thenReturn(List.of(compraGuardada));

        // WHEN
        List<CompraResponseDTO> resultado = service.listar();

        // THEN
        assertEquals(1, resultado.size());
        verify(compraRepo, times(1)).findAll();
    }
}
