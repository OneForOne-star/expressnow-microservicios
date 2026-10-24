package cl.duoc.expressnow.transportistas.dto;

import cl.duoc.expressnow.transportistas.model.TipoVehiculo;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record VehiculoRequest(
        @NotBlank(message = "La patente es obligatoria")
        @Pattern(regexp = "^[A-Za-z]{2,4}-?\\d{2,4}$", message = "La patente debe tener un formato como ABCD12 o AB-1234")
        String patente,

        @NotNull(message = "El tipo es obligatorio (MOTO, AUTO, FURGON o CAMION)")
        TipoVehiculo tipo,

        @Size(max = 40)
        String marca,

        @Size(max = 40)
        String modelo,

        @NotNull(message = "La capacidad en kg es obligatoria")
        @Positive(message = "La capacidad debe ser mayor a 0")
        @DecimalMax(value = "30000", message = "La capacidad máxima es 30000 kg")
        BigDecimal capacidadKg) {
}
