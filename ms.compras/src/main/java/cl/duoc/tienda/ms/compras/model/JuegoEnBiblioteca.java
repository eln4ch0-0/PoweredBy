package cl.duoc.tienda.ms.compras.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "biblioteca",
        uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "juego_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JuegoEnBiblioteca {
}