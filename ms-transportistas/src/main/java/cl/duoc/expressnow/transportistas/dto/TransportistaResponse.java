package cl.duoc.expressnow.transportistas.dto;

import cl.duoc.expressnow.transportistas.model.EstadoAsignacion;
import cl.duoc.expressnow.transportistas.model.Transportista;

import java.time.LocalDateTime;
import java.util.List;

public record TransportistaResponse(
        Long id,
        Long usuarioId,
        String rut,
        String nombre,
        String telefono,
        String comunaBase,
        boolean disponible,
        LocalDateTime fechaRegistro,
        long asignacionesActivas,
        List<VehiculoResponse> vehiculos) {

    public static TransportistaResponse desde(Transportista t) {
        long activas = t.getAsignaciones().stream()
                .filter(a -> a.getEstado() == EstadoAsignacion.ACTIVA).count();
        return new TransportistaResponse(t.getId(), t.getUsuarioId(), t.getRut(), t.getNombre(), t.getTelefono(),
                t.getComunaBase(), t.isDisponible(), t.getFechaRegistro(), activas,
                t.getVehiculos().stream().map(VehiculoResponse::desde).toList());
    }
}
