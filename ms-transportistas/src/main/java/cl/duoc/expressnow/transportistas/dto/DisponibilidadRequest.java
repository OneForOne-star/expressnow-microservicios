package cl.duoc.expressnow.transportistas.dto;

import jakarta.validation.constraints.NotNull;

public record DisponibilidadRequest(
        @NotNull(message = "Indique si el transportista está disponible")
        Boolean disponible) {
}
