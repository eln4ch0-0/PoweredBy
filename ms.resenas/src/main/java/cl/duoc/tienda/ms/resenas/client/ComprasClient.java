package cl.duoc.tienda.ms.resenas.client;

import cl.duoc.tienda.ms.resenas.dto.JuegoEnBibliotecaExternoDTO;
import cl.duoc.tienda.ms.resenas.exception.ServicioExternoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
@RequiredArgsConstructor
@Slf4j
public class ComprasClient {

    private final WebClient webClient;

    @Value("${servicios.compras.biblioteca.url}")
    private String urlBibliotecaBase;

    /**
     * Verifica si un usuario posee un juego consultando a MS-Compras.
     * @return true si el usuario tiene el juego en su biblioteca, false si no
     */
    public boolean usuarioPoseeJuego(Long usuarioId, Long juegoId) {
        log.debug("Verificando si usuario {} posee juego {}", usuarioId, juegoId);
        try {
            JuegoEnBibliotecaExternoDTO resp = webClient.get()
                    .uri(urlBibliotecaBase + "/" + usuarioId + "/" + juegoId)
                    .retrieve()
                    .bodyToMono(JuegoEnBibliotecaExternoDTO.class)
                    .block();
            return resp != null;
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.debug("Usuario {} NO posee juego {}", usuarioId, juegoId);
                return false;
            }
            log.error("Error consultando MS-Compras: {}", e.getMessage());
            throw new ServicioExternoException("Error consultando MS-Compras: " + e.getMessage());
        } catch (Exception e) {
            log.error("MS-Compras no disponible: {}", e.getMessage());
            throw new ServicioExternoException("MS-Compras no está disponible");
        }
    }
}