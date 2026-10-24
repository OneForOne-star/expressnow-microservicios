package cl.duoc.expressnow.seguimiento.service;

import cl.duoc.expressnow.seguimiento.dto.EventoRequest;
import cl.duoc.expressnow.seguimiento.dto.EventoResponse;
import cl.duoc.expressnow.seguimiento.dto.EventoUpdateRequest;
import cl.duoc.expressnow.seguimiento.dto.SeguimientoRequest;
import cl.duoc.expressnow.seguimiento.dto.SeguimientoResponse;
import cl.duoc.expressnow.seguimiento.exception.RecursoNoEncontradoException;
import cl.duoc.expressnow.seguimiento.exception.ReglaNegocioException;
import cl.duoc.expressnow.seguimiento.model.EstadoSeguimiento;
import cl.duoc.expressnow.seguimiento.model.EventoSeguimiento;
import cl.duoc.expressnow.seguimiento.model.Seguimiento;
import cl.duoc.expressnow.seguimiento.repository.EventoSeguimientoRepository;
import cl.duoc.expressnow.seguimiento.repository.SeguimientoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SeguimientoServiceImpl implements SeguimientoService {

    private final SeguimientoRepository seguimientoRepository;
    private final EventoSeguimientoRepository eventoRepository;

    public SeguimientoServiceImpl(SeguimientoRepository seguimientoRepository,
                                  EventoSeguimientoRepository eventoRepository) {
        this.seguimientoRepository = seguimientoRepository;
        this.eventoRepository = eventoRepository;
    }

    @Override
    public List<SeguimientoResponse> listar(EstadoSeguimiento estado) {
        List<Seguimiento> lista = (estado == null)
                ? seguimientoRepository.findAll()
                : seguimientoRepository.findByEstadoActual(estado);
        return lista.stream().map(SeguimientoResponse::desde).toList();
    }

    @Override
    public SeguimientoResponse obtener(Long id) {
        return SeguimientoResponse.desde(buscar(id));
    }

    @Override
    public SeguimientoResponse obtenerPorCodigo(String codigo) {
        return seguimientoRepository.findByCodigoSeguimientoIgnoreCase(codigo)
                .map(SeguimientoResponse::desde)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe seguimiento para el código " + codigo));
    }

    @Override
    public SeguimientoResponse obtenerPorEnvio(Long envioId) {
        return seguimientoRepository.findByEnvioId(envioId)
                .map(SeguimientoResponse::desde)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe seguimiento para el envío " + envioId));
    }

    @Override
    @Transactional
    public SeguimientoResponse crear(SeguimientoRequest request) {
        if (seguimientoRepository.existsByEnvioId(request.envioId())) {
            throw new ReglaNegocioException("El envío " + request.envioId() + " ya tiene seguimiento");
        }
        if (seguimientoRepository.existsByCodigoSeguimientoIgnoreCase(request.codigoSeguimiento())) {
            throw new ReglaNegocioException("El código " + request.codigoSeguimiento() + " ya está registrado");
        }
        Seguimiento s = new Seguimiento();
        s.setEnvioId(request.envioId());
        s.setCodigoSeguimiento(request.codigoSeguimiento());

        EventoSeguimiento inicial = new EventoSeguimiento();
        inicial.setEstado(EstadoSeguimiento.REGISTRADO);
        inicial.setDescripcion("Envío registrado, esperando retiro");
        inicial.setUbicacion(request.ubicacion());
        s.registrarEvento(inicial);

        return SeguimientoResponse.desde(seguimientoRepository.save(s));
    }

    @Override
    @Transactional
    public SeguimientoResponse registrarEvento(Long id, EventoRequest request) {
        Seguimiento s = buscar(id);
        if (s.getEstadoActual().esFinal()) {
            throw new ReglaNegocioException("El envío ya está " + s.getEstadoActual()
                    + "; no se pueden registrar más eventos");
        }
        if (request.estado() == EstadoSeguimiento.REGISTRADO) {
            throw new ReglaNegocioException("REGISTRADO solo se usa al abrir el seguimiento");
        }
        EventoSeguimiento evento = new EventoSeguimiento();
        evento.setEstado(request.estado());
        evento.setDescripcion(request.descripcion());
        evento.setUbicacion(request.ubicacion());
        evento.setLatitud(request.latitud());
        evento.setLongitud(request.longitud());
        s.registrarEvento(evento);
        // En AWS, aquí se publica el evento EstadoEnvioActualizado en SNS (ver diagrama de arquitectura)
        seguimientoRepository.flush();
        return SeguimientoResponse.desde(s);
    }

    @Override
    public List<EventoResponse> listarEventos(Long id) {
        buscar(id);
        return eventoRepository.findBySeguimientoIdOrderByFechaEventoAscIdAsc(id).stream()
                .map(EventoResponse::desde).toList();
    }

    @Override
    @Transactional
    public EventoResponse corregirEvento(Long id, Long eventoId, EventoUpdateRequest request) {
        EventoSeguimiento evento = buscarEvento(id, eventoId);
        evento.setDescripcion(request.descripcion());
        evento.setUbicacion(request.ubicacion());
        return EventoResponse.desde(evento);
    }

    @Override
    @Transactional
    public void eliminarEvento(Long id, Long eventoId) {
        Seguimiento s = buscar(id);
        EventoSeguimiento evento = buscarEvento(id, eventoId);
        if (s.getEventos().size() == 1) {
            throw new ReglaNegocioException("No se puede eliminar el único evento del seguimiento");
        }
        s.quitarEvento(evento);
        // El estado actual vuelve a ser el del último evento que queda
        EventoSeguimiento ultimo = s.getEventos().get(s.getEventos().size() - 1);
        s.setEstadoActual(ultimo.getEstado());
        if (ultimo.getUbicacion() != null) {
            s.setUbicacionActual(ultimo.getUbicacion());
        }
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        seguimientoRepository.delete(buscar(id));
    }

    private Seguimiento buscar(Long id) {
        return seguimientoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Seguimiento", id));
    }

    private EventoSeguimiento buscarEvento(Long id, Long eventoId) {
        return eventoRepository.findByIdAndSeguimientoId(eventoId, id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "El evento " + eventoId + " no existe en el seguimiento " + id));
    }
}
