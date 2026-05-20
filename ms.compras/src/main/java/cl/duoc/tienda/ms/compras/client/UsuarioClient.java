package cl.duoc.tienda.ms.compras.client;

import cl.duoc.tienda.ms.compras.dto.UsuarioExternoDTO;
import cl.duoc.tienda.ms.compras.exception.RecursoNoEncontradoException;
import cl.duoc.tienda.ms.compras.exception.ServicioExternoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class UsuarioClient {
    private final WebClient webClient;

    @Value("${servicios.usuarios.url}")
    private String urlBase;

    public UsuarioExternoDTO obtenerUsuario(Long id) {
        log.debug("Llamando a MS-Usuarios para obtener usuario id={}", id);
        try {
            return webClient.get()
                    .uri(urlBase + "/" + id)
                    .retrieve()
                    .bodyToMono(UsuarioExternoDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND)
                throw new RecursoNoEncontradoException("Usuario con id " + id + " no existe");
            log.error("Error consultando MS-Usuarios: {}", e.getMessage());
            throw new ServicioExternoException("Error consultando MS-Usuarios: " + e.getMessage());
        } catch (Exception e) {
            log.error("MS-Usuarios no disponible: {}", e.getMessage());
            throw new ServicioExternoException("MS-Usuarios no está disponible");
        }
    }

    public UsuarioExternoDTO descontarSaldo(Long id, BigDecimal monto) {
        log.info("Descontando {} del saldo del usuario {}", monto, id);
        try {
            return webClient.put()
                    .uri(urlBase + "/" + id + "/descontar-saldo")
                    .bodyValue(monto)
                    .retrieve()
                    .bodyToMono(UsuarioExternoDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("Error descontando saldo: status={} body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new ServicioExternoException("No se pudo descontar saldo: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new ServicioExternoException("MS-Usuarios no está disponible");
        }
    }
}
