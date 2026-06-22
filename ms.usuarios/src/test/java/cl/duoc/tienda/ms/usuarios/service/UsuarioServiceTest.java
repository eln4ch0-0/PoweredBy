package cl.duoc.tienda.ms.usuarios.service;

import cl.duoc.tienda.ms.usuarios.dto.UsuarioRequestDTO;
import cl.duoc.tienda.ms.usuarios.dto.UsuarioResponseDTO;
import cl.duoc.tienda.ms.usuarios.exception.SaldoInvalidoException;
import cl.duoc.tienda.ms.usuarios.exception.UsuarioNoEncontradoException;
import cl.duoc.tienda.ms.usuarios.exception.UsuarioYaExisteException;
import cl.duoc.tienda.ms.usuarios.model.Usuario;
import cl.duoc.tienda.ms.usuarios.repository.UsuarioRepository;
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
@DisplayName("Tests del servicio de Usuarios")
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repo;

    @InjectMocks
    private UsuarioService service;

    private Usuario usuario;
    private UsuarioRequestDTO request;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("gamer01");
        usuario.setEmail("gamer01@correo.cl");
        usuario.setPassword("clave1234");
        usuario.setNombreCompleto("Juan Pérez");
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setSaldoBilletera(new BigDecimal("50000"));
        usuario.setActivo(true);

        request = new UsuarioRequestDTO();
        request.setUsername("nuevoUser");
        request.setEmail("nuevo@correo.cl");
        request.setPassword("clave1234");
        request.setNombreCompleto("Nuevo Usuario");
    }

    // TESTS DEL MÉTODO crear()

    @Test
    @DisplayName("Debe crear usuario correctamente cuando los datos son válidos")
    void shouldCrearUsuarioCuandoDatosValidos() {
        // GIVEN
        when(repo.existsByUsername("nuevoUser")).thenReturn(false);
        when(repo.existsByEmail("nuevo@correo.cl")).thenReturn(false);
        when(repo.save(any(Usuario.class))).thenReturn(usuario);

        // WHEN
        UsuarioResponseDTO resultado = service.crear(request);

        // THEN
        assertNotNull(resultado);
        assertEquals("gamer01", resultado.getUsername());
        verify(repo, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el username ya existe")
    void shouldThrowExceptionCuandoUsernameYaExiste() {
        // GIVEN
        when(repo.existsByUsername("nuevoUser")).thenReturn(true);

        // WHEN y THEN
        UsuarioYaExisteException ex = assertThrows(UsuarioYaExisteException.class,
                () -> service.crear(request));
        assertTrue(ex.getMessage().contains("username"));
        verify(repo, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el email ya existe")
    void shouldThrowExceptionCuandoEmailYaExiste() {
        // GIVEN
        when(repo.existsByUsername("nuevoUser")).thenReturn(false);
        when(repo.existsByEmail("nuevo@correo.cl")).thenReturn(true);

        // WHEN y THEN
        UsuarioYaExisteException ex = assertThrows(UsuarioYaExisteException.class,
                () -> service.crear(request));
        assertTrue(ex.getMessage().contains("email"));
        verify(repo, never()).save(any(Usuario.class));
    }

    // TESTS DEL MÉTODO obtenerPorId()

    @Test
    @DisplayName("Debe obtener usuario por ID cuando existe")
    void shouldObtenerUsuarioPorIdCuandoExiste() {
        // GIVEN
        when(repo.findById(1L)).thenReturn(Optional.of(usuario));

        // WHEN
        UsuarioResponseDTO resultado = service.obtenerPorId(1L);

        // THEN
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("gamer01", resultado.getUsername());
    }

    @Test
    @DisplayName("Debe lanzar excepción al obtener usuario inexistente")
    void shouldThrowExceptionCuandoUsuarioNoExiste() {
        // GIVEN
        when(repo.findById(999L)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(UsuarioNoEncontradoException.class,
                () -> service.obtenerPorId(999L));
    }

    // TESTS DEL MÉTODO listar()

    @Test
    @DisplayName("Debe listar todos los usuarios")
    void shouldListarTodosLosUsuarios() {
        // GIVEN
        when(repo.findAll()).thenReturn(List.of(usuario));

        // WHEN
        List<UsuarioResponseDTO> resultado = service.listar();

        // THEN
        assertEquals(1, resultado.size());
        assertEquals("gamer01", resultado.get(0).getUsername());
        verify(repo, times(1)).findAll();
    }

    // TESTS DEL MÉTODO eliminar()

    @Test
    @DisplayName("Debe eliminar usuario cuando existe")
    void shouldEliminarUsuarioCuandoExiste() {
        // GIVEN
        when(repo.existsById(1L)).thenReturn(true);

        // WHEN
        service.eliminar(1L);

        // THEN
        verify(repo, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar usuario inexistente")
    void shouldThrowExceptionAlEliminarInexistente() {
        // GIVEN
        when(repo.existsById(999L)).thenReturn(false);

        // WHEN y THEN
        assertThrows(UsuarioNoEncontradoException.class,
                () -> service.eliminar(999L));
        verify(repo, never()).deleteById(any());
    }

    // TESTS DEL MÉTODO recargarSaldo() - LÓGICA DE BILLETERA

    @Test
    @DisplayName("Debe recargar saldo correctamente sumando al saldo existente")
    void shouldRecargarSaldoCorrectamente() {
        // GIVEN - saldo inicial 50000, recarga 25000
        when(repo.findById(1L)).thenReturn(Optional.of(usuario));
        when(repo.save(any(Usuario.class))).thenReturn(usuario);

        // WHEN
        UsuarioResponseDTO resultado = service.recargarSaldo(1L, new BigDecimal("25000"));

        // THEN - el saldo debe ser 75000
        assertEquals(new BigDecimal("75000"), usuario.getSaldoBilletera());
        verify(repo, times(1)).save(usuario);
    }

    @Test
    @DisplayName("Debe lanzar excepción al recargar saldo a usuario inexistente")
    void shouldThrowExceptionAlRecargarUsuarioInexistente() {
        // GIVEN
        when(repo.findById(999L)).thenReturn(Optional.empty());

        // WHEN y THEN
        assertThrows(UsuarioNoEncontradoException.class,
                () -> service.recargarSaldo(999L, new BigDecimal("10000")));
        verify(repo, never()).save(any(Usuario.class));
    }

    // TESTS DEL MÉTODO descontarSaldo() - LÓGICA DE BILLETERA

    @Test
    @DisplayName("Debe descontar saldo correctamente cuando hay saldo suficiente")
    void shouldDescontarSaldoCorrectamente() {
        // GIVEN - saldo inicial 50000, descuento 30000
        when(repo.findById(1L)).thenReturn(Optional.of(usuario));
        when(repo.save(any(Usuario.class))).thenReturn(usuario);

        // WHEN
        UsuarioResponseDTO resultado = service.descontarSaldo(1L, new BigDecimal("30000"));

        // THEN - el saldo debe ser 20000
        assertEquals(new BigDecimal("20000"), usuario.getSaldoBilletera());
        verify(repo, times(1)).save(usuario);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el saldo es insuficiente para descontar")
    void shouldThrowExceptionCuandoSaldoInsuficiente() {
        // GIVEN - saldo de 50000, intenta descontar 100000
        when(repo.findById(1L)).thenReturn(Optional.of(usuario));

        // WHEN y THEN
        SaldoInvalidoException ex = assertThrows(SaldoInvalidoException.class,
                () -> service.descontarSaldo(1L, new BigDecimal("100000")));
        assertTrue(ex.getMessage().contains("Saldo insuficiente"));
        verify(repo, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al descontar saldo a usuario inexistente")
    void shouldThrowExceptionAlDescontarUsuarioInexistente() {
        // GIVEN
        when(repo.findById(999L)).thenReturn(Optional.empty());

        // WHEN y THEN
        assertThrows(UsuarioNoEncontradoException.class,
                () -> service.descontarSaldo(999L, new BigDecimal("10000")));
    }
}
