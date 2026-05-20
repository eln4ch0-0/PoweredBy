package cl.duoc.tienda.ms.compras.client;

import cl.duoc.tienda.ms.compras.dto.JuegoExternoDTO;
import cl.duoc.tienda.ms.compras.exception.RecursoNoEncontradoException;
import cl.duoc.tienda.ms.compras.exception.ServicioExternoException;
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
public class CatalogoClient {
    private final WebClient webClient;

    @Value("${servicios.catalogo.url}")
    private String urlBase;

    public JuegoExternoDTO obtenerJuego(Long id) {
        log.debug("Llamando a MS-Catalogo para obtener juego id={}", id);
        try {
            return webClient.get()
                    .uri(urlBase + "/" + id)
                    .retrieve()
                    .bodyToMono(JuegoExternoDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND)
                throw new RecursoNoEncontradoException("Juego con id " + id + " no existe");
            log.error("Error consultando MS-Catalogo: {}", e.getMessage());
            throw new ServicioExternoException("Error consultando MS-Catalogo: " + e.getMessage());
        } catch (Exception e) {
            log.error("MS-Catalogo no disponible: {}", e.getMessage());
            throw new ServicioExternoException("MS-Catalogo no está disponible");
        }
    }
}
