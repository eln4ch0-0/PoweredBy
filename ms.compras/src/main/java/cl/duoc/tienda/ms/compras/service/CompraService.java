package cl.duoc.tienda.ms.compras.service;

import cl.duoc.tienda.ms.compras.client.CatalogoClient;
import cl.duoc.tienda.ms.compras.client.UsuarioClient;
import cl.duoc.tienda.ms.compras.dto.*;
import cl.duoc.tienda.ms.compras.exception.CompraInvalidaException;
import cl.duoc.tienda.ms.compras.exception.RecursoNoEncontradoException;
import cl.duoc.tienda.ms.compras.model.Compra;
import cl.duoc.tienda.ms.compras.model.EstadoCompra;
import cl.duoc.tienda.ms.compras.model.JuegoEnBiblioteca;
import cl.duoc.tienda.ms.compras.repository.CompraRepository;
import cl.duoc.tienda.ms.compras.repository.JuegoEnBibliotecaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompraService {
}