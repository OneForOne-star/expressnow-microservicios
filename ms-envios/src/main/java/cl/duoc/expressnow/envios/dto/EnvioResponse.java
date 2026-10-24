package cl.duoc.expressnow.envios.dto;

import cl.duoc.expressnow.envios.model.Envio;
import cl.duoc.expressnow.envios.model.EstadoEnvio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record EnvioResponse(
        Long id,
        String codigoSeguimiento,
        Long clienteId,
        String direccionOrigen,
        String comunaOrigen,
        String direccionDestino,
        String comunaDestino,
        String nombreDestinatario,
        String telefonoDestinatario,
        LocalDate fechaRetiro,
        EstadoEnvio estado,
        Long transportistaId,
        BigDecimal costo,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion,
        List<PaqueteResponse> paquetes) {

    public static EnvioResponse desde(Envio e) {
        return new EnvioResponse(e.getId(), e.getCodigoSeguimiento(), e.getClienteId(), e.getDireccionOrigen(),
                e.getComunaOrigen(), e.getDireccionDestino(), e.getComunaDestino(), e.getNombreDestinatario(),
                e.getTelefonoDestinatario(), e.getFechaRetiro(), e.getEstado(), e.getTransportistaId(),
                e.getCosto(), e.getFechaCreacion(), e.getFechaActualizacion(),
                e.getPaquetes().stream().map(PaqueteResponse::desde).toList());
    }
}
