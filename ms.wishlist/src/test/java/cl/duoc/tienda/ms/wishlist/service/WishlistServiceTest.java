package cl.duoc.tienda.ms.wishlist.service;

import cl.duoc.tienda.ms.wishlist.client.CatalogoClient;
import cl.duoc.tienda.ms.wishlist.client.ComprasClient;
import cl.duoc.tienda.ms.wishlist.dto.ConteoWishlistDTO;
import cl.duoc.tienda.ms.wishlist.dto.JuegoExternoDTO;
import cl.duoc.tienda.ms.wishlist.dto.WishlistRequestDTO;
import cl.duoc.tienda.ms.wishlist.dto.WishlistResponseDTO;
import cl.duoc.tienda.ms.wishlist.exception.RecursoNoEncontradoException;
import cl.duoc.tienda.ms.wishlist.exception.ReglaNegocioException;
import cl.duoc.tienda.ms.wishlist.model.JuegoDeseado;
import cl.duoc.tienda.ms.wishlist.repository.JuegoDeseadoRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests del servicio de Wishlist")
public class WishlistServiceTest {

    @Mock
    private JuegoDeseadoRepository repo;

    @Mock
    private CatalogoClient catalogoClient;

    @Mock
    private ComprasClient comprasClient;

    @InjectMocks
    private WishlistService service;

    private JuegoDeseado juegoDeseado;
    private JuegoExternoDTO juegoExterno;
    private WishlistRequestDTO request;

    @BeforeEach
    void setUp() {
        juegoExterno = new JuegoExternoDTO();
        juegoExterno.setId(10L);
        juegoExterno.setTitulo("Elden Ring");
        juegoExterno.setPrecio(new BigDecimal("30000"));
        juegoExterno.setDisponible(true);

        juegoDeseado = new JuegoDeseado();
        juegoDeseado.setId(1L);
        juegoDeseado.setUsuarioId(1L);
        juegoDeseado.setJuegoId(10L);
        juegoDeseado.setTituloJuego("Elden Ring");
        juegoDeseado.setPrecioReferencia(new BigDecimal("30000"));
        juegoDeseado.setFechaAgregado(LocalDateTime.now());

        request = new WishlistRequestDTO();
        request.setUsuarioId(1L);
        request.setJuegoId(10L);
    }

    // TESTS DEL MÉTODO agregar()

    @Test
    @DisplayName("Debe agregar juego a wishlist cuando los datos son válidos")
    void shouldAgregarJuegoAWishlistCuandoValido() {

        // GIVEN
        when(repo.existsByUsuarioIdAndJuegoId(1L, 10L)).thenReturn(false);
        when(catalogoClient.obtenerJuego(10L)).thenReturn(juegoExterno);
        when(comprasClient.usuarioYaPoseeJuego(1L, 10L)).thenReturn(false);
        when(repo.save(any(JuegoDeseado.class))).thenReturn(juegoDeseado);

        // WHEN
        WishlistResponseDTO resultado = service.agregar(request);

        // THEN
        assertNotNull(resultado);
        assertEquals("Elden Ring", resultado.getTituloJuego());
        assertEquals(new BigDecimal("30000"), resultado.getPrecioReferencia());
        verify(repo, times(1)).save(any(JuegoDeseado.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el juego ya está en la wishlist")
    void shouldThrowExceptionCuandoJuegoYaEstaEnWishlist() {

        // GIVEN
        when(repo.existsByUsuarioIdAndJuegoId(1L, 10L)).thenReturn(true);

        // WHEN y THEN
        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class,
                () -> service.agregar(request));
        assertTrue(ex.getMessage().contains("ya está en la wishlist"));
        verify(repo, never()).save(any(JuegoDeseado.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el juego no está disponible")
    void shouldThrowExceptionCuandoJuegoNoDisponible() {

        // GIVEN
        juegoExterno.setDisponible(false);
        when(repo.existsByUsuarioIdAndJuegoId(1L, 10L)).thenReturn(false);
        when(catalogoClient.obtenerJuego(10L)).thenReturn(juegoExterno);

        // WHEN y THEN
        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class,
                () -> service.agregar(request));
        assertTrue(ex.getMessage().contains("no está disponible"));
        verify(repo, never()).save(any(JuegoDeseado.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el usuario ya posee el juego")
    void shouldThrowExceptionCuandoUsuarioYaPoseeJuego() {

        // GIVEN
        when(repo.existsByUsuarioIdAndJuegoId(1L, 10L)).thenReturn(false);
        when(catalogoClient.obtenerJuego(10L)).thenReturn(juegoExterno);
        when(comprasClient.usuarioYaPoseeJuego(1L, 10L)).thenReturn(true);

        // WHEN y THEN
        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class,
                () -> service.agregar(request));
        assertTrue(ex.getMessage().contains("ya posee"));
        verify(repo, never()).save(any(JuegoDeseado.class));
    }

    // TESTS DEL MÉTODO obtener()

    @Test
    @DisplayName("Debe obtener entrada de wishlist por ID cuando existe")
    void shouldObtenerEntradaPorIdCuandoExiste() {

        // GIVEN
        when(repo.findById(1L)).thenReturn(Optional.of(juegoDeseado));

        // WHEN
        WishlistResponseDTO resultado = service.obtener(1L);

        // THEN
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Elden Ring", resultado.getTituloJuego());
    }

    @Test
    @DisplayName("Debe lanzar excepción al obtener entrada inexistente")
    void shouldThrowExceptionCuandoEntradaNoExiste() {

        // GIVEN
        when(repo.findById(999L)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(RecursoNoEncontradoException.class,
                () -> service.obtener(999L));
    }

    // TESTS DE LISTADOS Y CONSULTAS

    @Test
    @DisplayName("Debe obtener wishlist de un usuario")
    void shouldObtenerWishlistDeUsuario() {

        // GIVEN
        when(repo.findByUsuarioId(1L)).thenReturn(List.of(juegoDeseado));

        // WHEN
        List<WishlistResponseDTO> resultado = service.wishlistDe(1L);

        // THEN
        assertEquals(1, resultado.size());
        assertEquals("Elden Ring", resultado.get(0).getTituloJuego());
    }

    @Test
    @DisplayName("Debe obtener entrada por usuario y juego cuando existe")
    void shouldObtenerPorUsuarioYJuegoCuandoExiste() {

        // GIVEN
        when(repo.findByUsuarioIdAndJuegoId(1L, 10L)).thenReturn(Optional.of(juegoDeseado));

        // WHEN
        WishlistResponseDTO resultado = service.obtenerPorUsuarioYJuego(1L, 10L);

        // THEN
        assertEquals("Elden Ring", resultado.getTituloJuego());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando no se encuentra entrada por usuario y juego")
    void shouldThrowExceptionPorUsuarioYJuegoCuandoNoExiste() {

        // GIVEN
        when(repo.findByUsuarioIdAndJuegoId(1L, 99L)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(RecursoNoEncontradoException.class,
                () -> service.obtenerPorUsuarioYJuego(1L, 99L));
    }

    @Test
    @DisplayName("Debe calcular popularidad de un juego en wishlists")
    void shouldCalcularPopularidadJuego() {

        // GIVEN
        when(repo.contarPorJuego(10L)).thenReturn(25L);

        // WHEN
        ConteoWishlistDTO resultado = service.popularidadJuego(10L);

        // THEN
        assertEquals(10L, resultado.getJuegoId());
        assertEquals(25L, resultado.getCantidadUsuarios());
    }

    // TESTS DE ELIMINACIÓN

    @Test
    @DisplayName("Debe eliminar entrada por ID cuando existe")
    void shouldEliminarPorIdCuandoExiste() {

        // GIVEN
        when(repo.existsById(1L)).thenReturn(true);

        // WHEN
        service.eliminar(1L);

        // THEN
        verify(repo, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar entrada inexistente")
    void shouldThrowExceptionAlEliminarInexistente() {

        // GIVEN
        when(repo.existsById(999L)).thenReturn(false);

        // WHEN y THEN
        assertThrows(RecursoNoEncontradoException.class,
                () -> service.eliminar(999L));
        verify(repo, never()).deleteById(any());
    }

    @Test
    @DisplayName("Debe eliminar por usuario y juego cuando existe")
    void shouldEliminarPorUsuarioYJuegoCuandoExiste() {

        // GIVEN
        when(repo.findByUsuarioIdAndJuegoId(1L, 10L)).thenReturn(Optional.of(juegoDeseado));

        // WHEN
        service.eliminarPorUsuarioYJuego(1L, 10L);

        // THEN
        verify(repo, times(1)).delete(juegoDeseado);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar por usuario y juego inexistente")
    void shouldThrowExceptionAlEliminarPorUsuarioYJuegoInexistente() {

        // GIVEN
        when(repo.findByUsuarioIdAndJuegoId(1L, 99L)).thenReturn(Optional.empty());

        // WHEN y THEN
        assertThrows(RecursoNoEncontradoException.class,
                () -> service.eliminarPorUsuarioYJuego(1L, 99L));
        verify(repo, never()).delete(any(JuegoDeseado.class));
    }
}
