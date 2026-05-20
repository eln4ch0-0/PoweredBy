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
}