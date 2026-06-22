package cl.duoc.tienda.ms.catalogo.service;

import cl.duoc.tienda.ms.catalogo.dto.JuegoRequestDTO;
import cl.duoc.tienda.ms.catalogo.dto.JuegoResponseDTO;
import cl.duoc.tienda.ms.catalogo.exception.RecursoDuplicadoException;
import cl.duoc.tienda.ms.catalogo.exception.RecursoNoEncontradoException;
import cl.duoc.tienda.ms.catalogo.model.Desarrollador;
import cl.duoc.tienda.ms.catalogo.model.Genero;
import cl.duoc.tienda.ms.catalogo.model.Juego;
import cl.duoc.tienda.ms.catalogo.repository.JuegoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests del servicio de Juegos")
public class JuegoServiceTest {

    @Mock
    private JuegoRepository repo;

    @Mock
    private GeneroService generoService;

    @Mock
    private DesarrolladorService desarrolladorService;

    @InjectMocks
    private JuegoService service;

    private Juego juego;
    private Genero genero;
    private Desarrollador desarrollador;
    private JuegoRequestDTO request;

    @BeforeEach
    void setUp() {
        genero = new Genero(1L, "RPG", "Juegos de rol");
        desarrollador = new Desarrollador(1L, "FromSoftware", "Japón", "https://fromsoftware.jp");

        juego = new Juego();
        juego.setId(1L);
        juego.setTitulo("Elden Ring");
        juego.setDescripcion("Open world RPG");
        juego.setPrecio(new BigDecimal("30000"));
        juego.setFechaLanzamiento(LocalDate.of(2022, 2, 25));
        juego.setDisponible(true);
        juego.setGenero(genero);
        juego.setDesarrollador(desarrollador);

        request = new JuegoRequestDTO();
        request.setTitulo("Elden Ring");
        request.setDescripcion("Open world RPG");
        request.setPrecio(new BigDecimal("30000"));
        request.setFechaLanzamiento(LocalDate.of(2022, 2, 25));
        request.setGeneroId(1L);
        request.setDesarrolladorId(1L);
    }

    // TESTS DEL MÉTODO crear()

    @Test
    @DisplayName("Debe crear juego correctamente cuando los datos son válidos")
    void shouldCrearJuegoCuandoDatosValidos() {

        // GIVEN
        when(repo.existsByTitulo("Elden Ring")).thenReturn(false);
        when(generoService.buscar(1L)).thenReturn(genero);
        when(desarrolladorService.buscar(1L)).thenReturn(desarrollador);
        when(repo.save(any(Juego.class))).thenReturn(juego);

        // WHEN
        JuegoResponseDTO resultado = service.crear(request);

        // THEN
        assertNotNull(resultado);
        assertEquals("Elden Ring", resultado.getTitulo());
        verify(repo, times(1)).save(any(Juego.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el título ya existe")
    void shouldThrowExceptionCuandoTituloDuplicado() {

        // GIVEN
        when(repo.existsByTitulo("Elden Ring")).thenReturn(true);

        // WHEN y THEN
        RecursoDuplicadoException ex = assertThrows(RecursoDuplicadoException.class,
                () -> service.crear(request));
        assertTrue(ex.getMessage().contains("Elden Ring"));
        verify(repo, never()).save(any(Juego.class));
    }

    // TESTS DEL MÉTODO obtener()

    @Test
    @DisplayName("Debe obtener juego por ID cuando existe")
    void shouldObtenerJuegoPorIdCuandoExiste() {

        // GIVEN
        when(repo.findById(1L)).thenReturn(Optional.of(juego));

        // WHEN
        JuegoResponseDTO resultado = service.obtener(1L);

        // THEN
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Elden Ring", resultado.getTitulo());
    }

    @Test
    @DisplayName("Debe lanzar excepción al obtener juego inexistente")
    void shouldThrowExceptionCuandoJuegoNoExiste() {

        // GIVEN
        when(repo.findById(999L)).thenReturn(Optional.empty());

        // WHEN y THEN
        assertThrows(RecursoNoEncontradoException.class,
                () -> service.obtener(999L));
    }

    // TESTS DE LISTAR Y BÚSQUEDAS

    @Test
    @DisplayName("Debe listar todos los juegos")
    void shouldListarTodosLosJuegos() {

        // GIVEN
        when(repo.findAll()).thenReturn(List.of(juego));

        // WHEN
        List<JuegoResponseDTO> resultado = service.listar();

        // THEN
        assertEquals(1, resultado.size());
        verify(repo, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe buscar juegos por género")
    void shouldBuscarJuegosPorGenero() {

        // GIVEN
        when(repo.findByGeneroId(1L)).thenReturn(List.of(juego));

        // WHEN
        List<JuegoResponseDTO> resultado = service.buscarPorGenero(1L);

        // THEN
        assertEquals(1, resultado.size());
        assertEquals("Elden Ring", resultado.get(0).getTitulo());
    }

    @Test
    @DisplayName("Debe buscar juegos disponibles hasta un precio máximo")
    void shouldBuscarJuegosHastaPrecio() {

        // GIVEN
        BigDecimal precioMax = new BigDecimal("50000");
        when(repo.findByPrecioLessThanEqualAndDisponibleTrue(precioMax)).thenReturn(List.of(juego));

        // WHEN
        List<JuegoResponseDTO> resultado = service.buscarHastaPrecio(precioMax);

        // THEN
        assertEquals(1, resultado.size());
    }

    // TESTS DEL MÉTODO eliminar()

    @Test
    @DisplayName("Debe eliminar juego cuando existe")
    void shouldEliminarJuegoCuandoExiste() {

        // GIVEN
        when(repo.existsById(1L)).thenReturn(true);

        // WHEN
        service.eliminar(1L);

        // THEN
        verify(repo, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar juego inexistente")
    void shouldThrowExceptionAlEliminarInexistente() {

        // GIVEN
        when(repo.existsById(999L)).thenReturn(false);

        // WHEN y THEN
        assertThrows(RecursoNoEncontradoException.class,
                () -> service.eliminar(999L));
        verify(repo, never()).deleteById(any());
    }

    // TEST DEL MÉTODO cambiarDisponibilidad()

    @Test
    @DisplayName("Debe cambiar disponibilidad del juego correctamente")
    void shouldCambiarDisponibilidadCorrectamente() {

        // GIVEN - juego inicialmente disponible
        when(repo.findById(1L)).thenReturn(Optional.of(juego));
        when(repo.save(any(Juego.class))).thenReturn(juego);

        // WHEN - marca no disponible
        JuegoResponseDTO resultado = service.cambiarDisponibilidad(1L, false);

        // THEN
        assertFalse(juego.getDisponible());
        verify(repo, times(1)).save(juego);
    }
}
