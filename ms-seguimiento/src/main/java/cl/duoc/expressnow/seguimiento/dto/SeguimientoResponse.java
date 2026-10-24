package cl.duoc.expressnow.seguimiento.dto;

import cl.duoc.expressnow.seguimiento.model.EstadoSeguimiento;
import cl.duoc.expressnow.seguimiento.model.Seguimiento;

import java.time.LocalDateTime;
import java.util.List;

public record SeguimientoResponse(
        Long id,
        Long envioId,
        String codigoSeguimiento,
        EstadoSeguimiento estadoActual,
        String ubicacionActual,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion,
        List<EventoResponse> eventos) {

    public static SeguimientoResponse desde(Seguimiento s) {
        return new SeguimientoResponse(s.getId(), s.getEnvioId(), s.getCodigoSeguimiento(), s.getEstadoActual(),
                s.getUbicacionActual(), s.getFechaCreacion(), s.getFechaActualizacion(),
                s.getEventos().stream().map(EventoResponse::desde).toList());
    }
}
