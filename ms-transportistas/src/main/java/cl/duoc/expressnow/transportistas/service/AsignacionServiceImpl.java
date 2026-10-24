package cl.duoc.expressnow.transportistas.service;

import cl.duoc.expressnow.transportistas.dto.AsignacionRequest;
import cl.duoc.expressnow.transportistas.dto.AsignacionResponse;
import cl.duoc.expressnow.transportistas.exception.RecursoNoEncontradoException;
import cl.duoc.expressnow.transportistas.exception.ReglaNegocioException;
import cl.duoc.expressnow.transportistas.model.Asignacion;
import cl.duoc.expressnow.transportistas.model.EstadoAsignacion;
import cl.duoc.expressnow.transportistas.model.Transportista;
import cl.duoc.expressnow.transportistas.repository.AsignacionRepository;
import cl.duoc.expressnow.transportistas.repository.TransportistaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AsignacionServiceImpl implements AsignacionService {

    private final AsignacionRepository asignacionRepository;
    private final TransportistaRepository transportistaRepository;

    public AsignacionServiceImpl(AsignacionRepository asignacionRepository,
                                 TransportistaRepository transportistaRepository) {
        this.asignacionRepository = asignacionRepository;
        this.transportistaRepository = transportistaRepository;
    }

    /**
     * Asigna el envío a un transportista disponible: primero busca en la misma comuna
     * del retiro y, si no hay, toma cualquier transportista disponible.
     */
    @Override
    @Transactional
    public AsignacionResponse asignar(AsignacionRequest request) {
        if (asignacionRepository.existsByEnvioIdAndEstado(request.envioId(), EstadoAsignacion.ACTIVA)) {
            throw new ReglaNegocioException("El envío " + request.envioId() + " ya tiene una asignación activa");
        }
        Transportista transportista = transportistaRepository
                .findFirstByDisponibleTrueAndComunaBaseIgnoreCaseOrderByIdAsc(request.comuna())
                .or(transportistaRepository::findFirstByDisponibleTrueOrderByIdAsc)
                .orElseThrow(() -> new ReglaNegocioException("No hay transportistas disponibles en este momento"));

        Asignacion asignacion = new Asignacion();
        asignacion.setEnvioId(request.envioId());
        asignacion.setComuna(request.comuna());
        asignacion.setEstado(EstadoAsignacion.ACTIVA);
        transportista.agregarAsignacion(asignacion);
        return AsignacionResponse.desde(asignacionRepository.save(asignacion));
    }

    @Override
    public List<AsignacionResponse> listar(Long transportistaId) {
        List<Asignacion> lista = (transportistaId == null)
                ? asignacionRepository.findAll()
                : asignacionRepository.findByTransportistaId(transportistaId);
        return lista.stream().map(AsignacionResponse::desde).toList();
    }

    @Override
    public AsignacionResponse obtener(Long id) {
        return AsignacionResponse.desde(buscar(id));
    }

    @Override
    @Transactional
    public AsignacionResponse completar(Long id) {
        return cerrar(id, EstadoAsignacion.COMPLETADA);
    }

    @Override
    @Transactional
    public AsignacionResponse cancelar(Long id) {
        return cerrar(id, EstadoAsignacion.CANCELADA);
    }

    private AsignacionResponse cerrar(Long id, EstadoAsignacion nuevoEstado) {
        Asignacion a = buscar(id);
        if (a.getEstado() != EstadoAsignacion.ACTIVA) {
            throw new ReglaNegocioException("La asignación " + id + " ya está " + a.getEstado());
        }
        a.setEstado(nuevoEstado);
        a.setFechaCierre(LocalDateTime.now());
        return AsignacionResponse.desde(a);
    }

    private Asignacion buscar(Long id) {
        return asignacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Asignación", id));
    }
}
