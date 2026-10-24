package cl.duoc.expressnow.transportistas.dto;

import cl.duoc.expressnow.transportistas.model.Asignacion;
import cl.duoc.expressnow.transportistas.model.EstadoAsignacion;

import java.time.LocalDateTime;

public record AsignacionResponse(
        Long id,
        Long envioId,
        String comuna,
        EstadoAsignacion estado,
        LocalDateTime fechaAsignacion,
        LocalDateTime fechaCierre,
        Long transportistaId,
        String transportistaNombre) {

    public static AsignacionResponse desde(Asignacion a) {
        return new AsignacionResponse(a.getId(), a.getEnvioId(), a.getComuna(), a.getEstado(),
                a.getFechaAsignacion(), a.getFechaCierre(), a.getTransportista().getId(),
                a.getTransportista().getNombre());
    }
}
