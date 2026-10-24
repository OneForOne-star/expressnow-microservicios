package cl.duoc.expressnow.envios.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ReprogramarRequest(
        @NotNull(message = "La nueva fecha de retiro es obligatoria")
        @Future(message = "La nueva fecha de retiro debe ser posterior a hoy")
        LocalDate fechaRetiro,

        @Size(max = 200)
        String motivo) {
}
