package cl.duoc.expressnow.seguimiento.dto;

import cl.duoc.expressnow.seguimiento.model.EstadoSeguimiento;
import cl.duoc.expressnow.seguimiento.model.EventoSeguimiento;

import java.time.LocalDateTime;

public record EventoResponse(
        Long id,
        EstadoSeguimiento estado,
        String descripcion,
        String ubicacion,
        Double latitud,
        Double longitud,
        LocalDateTime fechaEvento) {

    public static EventoResponse desde(EventoSeguimiento e) {
        return new EventoResponse(e.getId(), e.getEstado(), e.getDescripcion(), e.getUbicacion(),
                e.getLatitud(), e.getLongitud(), e.getFechaEvento());
    }
}
