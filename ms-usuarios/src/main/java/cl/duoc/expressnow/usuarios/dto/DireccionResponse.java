package cl.duoc.expressnow.usuarios.dto;

import cl.duoc.expressnow.usuarios.model.Direccion;

public record DireccionResponse(
        Long id,
        String calle,
        String numero,
        String depto,
        String comuna,
        String region,
        boolean principal) {

    public static DireccionResponse desde(Direccion d) {
        return new DireccionResponse(d.getId(), d.getCalle(), d.getNumero(), d.getDepto(),
                d.getComuna(), d.getRegion(), d.isPrincipal());
    }
}
