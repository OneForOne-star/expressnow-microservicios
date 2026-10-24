package cl.duoc.expressnow.transportistas.dto;

import cl.duoc.expressnow.transportistas.model.TipoVehiculo;
import cl.duoc.expressnow.transportistas.model.Vehiculo;

import java.math.BigDecimal;

public record VehiculoResponse(
        Long id,
        String patente,
        TipoVehiculo tipo,
        String marca,
        String modelo,
        BigDecimal capacidadKg) {

    public static VehiculoResponse desde(Vehiculo v) {
        return new VehiculoResponse(v.getId(), v.getPatente(), v.getTipo(), v.getMarca(), v.getModelo(),
                v.getCapacidadKg());
    }
}
