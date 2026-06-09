package cl.duoc.tienda.ms.wishlist.client;

import cl.duoc.tienda.ms.wishlist.dto.JuegoEnBibliotecaExternoDTO;
import cl.duoc.tienda.ms.wishlist.exception.ServicioExternoException;
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

    /** Verifica si el usuario YA POSEE el juego (consultando biblioteca de MS-Compras). */
    public boolean usuarioYaPoseeJuego(Long usuarioId, Long juegoId) {
        log.debug("Verificando si usuario {} ya posee juego {}", usuarioId, juegoId);
        try {
            JuegoEnBibliotecaExternoDTO resp = webClient.get()
                    .uri(urlBibliotecaBase + "/" + usuarioId + "/" + juegoId)
                    .retrieve()
                    .bodyToMono(JuegoEnBibliotecaExternoDTO.class)
                    .block();
            return resp != null;
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) return false;
            log.error("Error consultando MS-Compras: {}", e.getMessage());
            throw new ServicioExternoException("Error consultando MS-Compras");
        } catch (Exception e) {
            log.error("MS-Compras no disponible: {}", e.getMessage());
            throw new ServicioExternoException("MS-Compras no está disponible");
        }
    }
}