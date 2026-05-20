package cl.duoc.tienda.ms.usuarios.service;

import cl.duoc.tienda.ms.usuarios.dto.UsuarioRequestDTO;
import cl.duoc.tienda.ms.usuarios.dto.UsuarioResponseDTO;
import cl.duoc.tienda.ms.usuarios.exception.SaldoInvalidoException;
import cl.duoc.tienda.ms.usuarios.exception.UsuarioNoEncontradoException;
import cl.duoc.tienda.ms.usuarios.exception.UsuarioYaExisteException;
import cl.duoc.tienda.ms.usuarios.model.Usuario;
import cl.duoc.tienda.ms.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository repo;

    public List<UsuarioResponseDTO> listar() {
        log.info("Listando todos los usuarios");
        return repo.findAll().stream().map(this::toResponse).toList();
    }

    public UsuarioResponseDTO obtenerPorId(Long id) {
        log.debug("Buscando usuario con id={}", id);
        Usuario u = repo.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario con id " + id + " no existe"));
        return toResponse(u);
    }

    public UsuarioResponseDTO crear(UsuarioRequestDTO dto) {
        log.info("Creando usuario con username={}", dto.getUsername());

        if (repo.existsByUsername(dto.getUsername()))
            throw new UsuarioYaExisteException("El username '" + dto.getUsername() + "' ya está en uso");
        if (repo.existsByEmail(dto.getEmail()))
            throw new UsuarioYaExisteException("El email '" + dto.getEmail() + "' ya está registrado");

        Usuario u = new Usuario();
        u.setUsername(dto.getUsername());
        u.setEmail(dto.getEmail());
        u.setPassword(dto.getPassword()); // TODO hashear en MS-Auth
        u.setNombreCompleto(dto.getNombreCompleto());

        Usuario guardado = repo.save(u);
        log.info("Usuario creado con id={}", guardado.getId());
        return toResponse(guardado);
    }

    public UsuarioResponseDTO actualizar(Long id, UsuarioRequestDTO dto) {
        Usuario u = repo.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario con id " + id + " no existe"));

        // Solo validar duplicados si cambia el valor
        if (!u.getUsername().equals(dto.getUsername()) && repo.existsByUsername(dto.getUsername()))
            throw new UsuarioYaExisteException("El username '" + dto.getUsername() + "' ya está en uso");
        if (!u.getEmail().equals(dto.getEmail()) && repo.existsByEmail(dto.getEmail()))
            throw new UsuarioYaExisteException("El email '" + dto.getEmail() + "' ya está registrado");

        u.setUsername(dto.getUsername());
        u.setEmail(dto.getEmail());
        u.setPassword(dto.getPassword());
        u.setNombreCompleto(dto.getNombreCompleto());
        return toResponse(repo.save(u));
    }

    public void eliminar(Long id) {
        if (!repo.existsById(id))
            throw new UsuarioNoEncontradoException("Usuario con id " + id + " no existe");
        repo.deleteById(id);
        log.warn("Usuario con id={} fue eliminado", id);
    }

    public UsuarioResponseDTO recargarSaldo(Long id, BigDecimal monto) {
        Usuario u = repo.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario con id " + id + " no existe"));
        u.setSaldoBilletera(u.getSaldoBilletera().add(monto));
        log.info("Recarga de {} al usuario {}. Nuevo saldo: {}", monto, id, u.getSaldoBilletera());
        return toResponse(repo.save(u));
    }

    // Método usado por MS-Compras vía HTTP (a futuro)
    public UsuarioResponseDTO descontarSaldo(Long id, BigDecimal monto) {
        Usuario u = repo.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario con id " + id + " no existe"));
        if (u.getSaldoBilletera().compareTo(monto) < 0)
            throw new SaldoInvalidoException("Saldo insuficiente. Saldo actual: " + u.getSaldoBilletera());
        u.setSaldoBilletera(u.getSaldoBilletera().subtract(monto));
        log.info("Descuento de {} al usuario {}. Nuevo saldo: {}", monto, id, u.getSaldoBilletera());
        return toResponse(repo.save(u));
    }

    private UsuarioResponseDTO toResponse(Usuario u) {
        return new UsuarioResponseDTO(
                u.getId(), u.getUsername(), u.getEmail(), u.getNombreCompleto(),
                u.getFechaRegistro(), u.getSaldoBilletera(), u.getActivo()
        );
    }
}
