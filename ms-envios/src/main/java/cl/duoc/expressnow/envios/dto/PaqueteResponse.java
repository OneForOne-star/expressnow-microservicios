package cl.duoc.expressnow.envios.dto;

import cl.duoc.expressnow.envios.model.Paquete;

import java.math.BigDecimal;

public record PaqueteResponse(
        Long id,
        String descripcion,
        BigDecimal pesoKg,
        Integer largoCm,
        Integer anchoCm,
        Integer altoCm,
        boolean fragil) {

    public static PaqueteResponse desde(Paquete p) {
        return new PaqueteResponse(p.getId(), p.getDescripcion(), p.getPesoKg(), p.getLargoCm(),
                p.getAnchoCm(), p.getAltoCm(), p.isFragil());
    }
}
