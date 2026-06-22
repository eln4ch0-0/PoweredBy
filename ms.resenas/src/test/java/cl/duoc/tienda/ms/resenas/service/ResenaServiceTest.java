package cl.duoc.tienda.ms.resenas.service;

import cl.duoc.tienda.ms.resenas.client.ComprasClient;
import cl.duoc.tienda.ms.resenas.dto.PromedioResenaDTO;
import cl.duoc.tienda.ms.resenas.dto.ResenaRequestDTO;
import cl.duoc.tienda.ms.resenas.dto.ResenaResponseDTO;
import cl.duoc.tienda.ms.resenas.dto.ResenaUpdateDTO;
import cl.duoc.tienda.ms.resenas.exception.RecursoNoEncontradoException;
import cl.duoc.tienda.ms.resenas.exception.ReglaNegocioException;
import cl.duoc.tienda.ms.resenas.model.Resena;
import cl.duoc.tienda.ms.resenas.repository.ResenaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests del servicio de Reseñas")
public class ResenaServiceTest {

    @Mock
    private ResenaRepository repo;

    @Mock
    private ComprasClient comprasClient;

    @InjectMocks
    private ResenaService service;

    private Resena resena;
    private ResenaRequestDTO request;

    @BeforeEach
    void setUp() {
        resena = new Resena();
        resena.setId(1L);
        resena.setUsuarioId(1L);
        resena.setJuegoId(10L);
        resena.setCalificacion(5);
        resena.setTitulo("Excelente juego");
        resena.setContenido("Lo recomiendo totalmente");
        resena.setFechaCreacion(LocalDateTime.now());

        request = new ResenaRequestDTO();
        request.setUsuarioId(1L);
        request.setJuegoId(10L);
        request.setCalificacion(5);
        request.setTitulo("Excelente juego");
        request.setContenido("Lo recomiendo totalmente");
    }

    // TESTS DEL MÉTODO crear()

    @Test
    @DisplayName("Debe crear reseña cuando el usuario posee el juego")
    void shouldCrearResenaCuandoUsuarioPoseeJuego() {

        // GIVEN
        when(comprasClient.usuarioPoseeJuego(1L, 10L)).thenReturn(true);
        when(repo.existsByUsuarioIdAndJuegoId(1L, 10L)).thenReturn(false);
        when(repo.save(any(Resena.class))).thenReturn(resena);

        // WHEN
        ResenaResponseDTO resultado = service.crear(request);

        // THEN
        assertNotNull(resultado);
        assertEquals(5, resultado.getCalificacion());
        verify(repo, times(1)).save(any(Resena.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el usuario no posee el juego")
    void shouldThrowExceptionCuandoUsuarioNoPoseeJuego() {

        // GIVEN
        when(comprasClient.usuarioPoseeJuego(1L, 10L)).thenReturn(false);

        // WHEN y THEN
        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class,
                () -> service.crear(request));
        assertTrue(ex.getMessage().contains("no posee"));
        verify(repo, never()).save(any(Resena.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el usuario ya tiene reseña para ese juego")
    void shouldThrowExceptionCuandoYaExisteResena() {
        // GIVEN
        when(comprasClient.usuarioPoseeJuego(1L, 10L)).thenReturn(true);
        when(repo.existsByUsuarioIdAndJuegoId(1L, 10L)).thenReturn(true);

        // WHEN y THEN
        ReglaNegocioException ex = assertThrows(ReglaNegocioException.class,
                () -> service.crear(request));
        assertTrue(ex.getMessage().contains("ya tiene una reseña"));
        verify(repo, never()).save(any(Resena.class));
    }

    // TESTS DEL MÉTODO actualizar()

    @Test
    @DisplayName("Debe actualizar reseña correctamente")
    void shouldActualizarResenaCorrectamente() {

        // GIVEN
        ResenaUpdateDTO update = new ResenaUpdateDTO();
        update.setCalificacion(3);
        update.setTitulo("Reseña actualizada");
        update.setContenido("Cambié de opinión");

        when(repo.findById(1L)).thenReturn(Optional.of(resena));
        when(repo.save(any(Resena.class))).thenReturn(resena);

        // WHEN
        ResenaResponseDTO resultado = service.actualizar(1L, update);

        // THEN
        assertEquals(3, resena.getCalificacion());
        assertEquals("Reseña actualizada", resena.getTitulo());
        verify(repo, times(1)).save(resena);
    }

    @Test
    @DisplayName("Debe lanzar excepción al actualizar reseña inexistente")
    void shouldThrowExceptionAlActualizarInexistente() {

        // GIVEN
        ResenaUpdateDTO update = new ResenaUpdateDTO();
        update.setCalificacion(3);
        update.setTitulo("X");
        update.setContenido("X");

        when(repo.findById(999L)).thenReturn(Optional.empty());

        // WHEN y THEN
        assertThrows(RecursoNoEncontradoException.class,
                () -> service.actualizar(999L, update));
    }

    // TESTS DEL MÉTODO obtener()

    @Test
    @DisplayName("Debe obtener reseña por ID cuando existe")
    void shouldObtenerResenaPorIdCuandoExiste() {

        // GIVEN
        when(repo.findById(1L)).thenReturn(Optional.of(resena));

        // WHEN
        ResenaResponseDTO resultado = service.obtener(1L);

        // THEN
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    @DisplayName("Debe lanzar excepción al obtener reseña inexistente")
    void shouldThrowExceptionCuandoResenaNoExiste() {

        // GIVEN
        when(repo.findById(999L)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(RecursoNoEncontradoException.class,
                () -> service.obtener(999L));
    }

    // TESTS DE LISTADOS

    @Test
    @DisplayName("Debe obtener reseñas de un juego")
    void shouldObtenerResenasDeJuego() {

        // GIVEN
        when(repo.findByJuegoId(10L)).thenReturn(List.of(resena));

        // WHEN
        List<ResenaResponseDTO> resultado = service.resenasDeJuego(10L);

        // THEN
        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Debe obtener reseñas de un usuario")
    void shouldObtenerResenasDeUsuario() {

        // GIVEN
        when(repo.findByUsuarioId(1L)).thenReturn(List.of(resena));

        // WHEN
        List<ResenaResponseDTO> resultado = service.resenasDeUsuario(1L);

        // THEN
        assertEquals(1, resultado.size());
    }

    // TESTS DEL MÉTODO promedioJuego()

    @Test
    @DisplayName("Debe calcular promedio correctamente cuando hay reseñas")
    void shouldCalcularPromedioCorrectamente() {

        // GIVEN
        when(repo.promedioCalificacionPorJuego(10L)).thenReturn(4.5);
        when(repo.cantidadResenasPorJuego(10L)).thenReturn(10L);

        // WHEN
        PromedioResenaDTO resultado = service.promedioJuego(10L);

        // THEN
        assertEquals(4.5, resultado.getPromedio());
        assertEquals(10L, resultado.getTotalResenas());
    }

    @Test
    @DisplayName("Debe devolver promedio 0 cuando no hay reseñas")
    void shouldDevolverPromedioCeroCuandoSinResenas() {

        // GIVEN
        when(repo.promedioCalificacionPorJuego(99L)).thenReturn(null);
        when(repo.cantidadResenasPorJuego(99L)).thenReturn(0L);

        // WHEN
        PromedioResenaDTO resultado = service.promedioJuego(99L);

        // THEN
        assertEquals(0.0, resultado.getPromedio());
        assertEquals(0L, resultado.getTotalResenas());
    }

    // TESTS DEL MÉTODO eliminar()

    @Test
    @DisplayName("Debe eliminar reseña cuando existe")
    void shouldEliminarResenaCuandoExiste() {

        // GIVEN
        when(repo.existsById(1L)).thenReturn(true);

        // WHEN
        service.eliminar(1L);

        // THEN
        verify(repo, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar reseña inexistente")
    void shouldThrowExceptionAlEliminarInexistente() {

        // GIVEN
        when(repo.existsById(999L)).thenReturn(false);

        // WHEN y THEN
        assertThrows(RecursoNoEncontradoException.class,
                () -> service.eliminar(999L));
        verify(repo, never()).deleteById(any());
    }
}
