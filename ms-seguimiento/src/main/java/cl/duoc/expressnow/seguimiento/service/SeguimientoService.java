package cl.duoc.expressnow.seguimiento.service;

import cl.duoc.expressnow.seguimiento.dto.EventoRequest;
import cl.duoc.expressnow.seguimiento.dto.EventoResponse;
import cl.duoc.expressnow.seguimiento.dto.EventoUpdateRequest;
import cl.duoc.expressnow.seguimiento.dto.SeguimientoRequest;
import cl.duoc.expressnow.seguimiento.dto.SeguimientoResponse;
import cl.duoc.expressnow.seguimiento.model.EstadoSeguimiento;

import java.util.List;

public interface SeguimientoService {

    List<SeguimientoResponse> listar(EstadoSeguimiento estado);

    SeguimientoResponse obtener(Long id);

    SeguimientoResponse obtenerPorCodigo(String codigo);

    SeguimientoResponse obtenerPorEnvio(Long envioId);

    SeguimientoResponse crear(SeguimientoRequest request);

    SeguimientoResponse registrarEvento(Long id, EventoRequest request);

    List<EventoResponse> listarEventos(Long id);

    EventoResponse corregirEvento(Long id, Long eventoId, EventoUpdateRequest request);

    void eliminarEvento(Long id, Long eventoId);

    void eliminar(Long id);
}
