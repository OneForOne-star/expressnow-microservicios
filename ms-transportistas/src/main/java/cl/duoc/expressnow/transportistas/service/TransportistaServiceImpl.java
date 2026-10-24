package cl.duoc.expressnow.transportistas.service;

import cl.duoc.expressnow.transportistas.dto.TransportistaRequest;
import cl.duoc.expressnow.transportistas.dto.TransportistaResponse;
import cl.duoc.expressnow.transportistas.dto.VehiculoRequest;
import cl.duoc.expressnow.transportistas.dto.VehiculoResponse;
import cl.duoc.expressnow.transportistas.exception.RecursoNoEncontradoException;
import cl.duoc.expressnow.transportistas.exception.ReglaNegocioException;
import cl.duoc.expressnow.transportistas.model.EstadoAsignacion;
import cl.duoc.expressnow.transportistas.model.Transportista;
import cl.duoc.expressnow.transportistas.model.Vehiculo;
import cl.duoc.expressnow.transportistas.repository.TransportistaRepository;
import cl.duoc.expressnow.transportistas.repository.VehiculoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TransportistaServiceImpl implements TransportistaService {

    private final TransportistaRepository transportistaRepository;
    private final VehiculoRepository vehiculoRepository;

    public TransportistaServiceImpl(TransportistaRepository transportistaRepository,
                                    VehiculoRepository vehiculoRepository) {
        this.transportistaRepository = transportistaRepository;
        this.vehiculoRepository = vehiculoRepository;
    }

    @Override
    public List<TransportistaResponse> listar(Boolean disponible, String comuna) {
        List<Transportista> lista;
        if (disponible != null && comuna != null) {
            lista = transportistaRepository.findByDisponibleAndComunaBaseIgnoreCase(disponible, comuna);
        } else if (disponible != null) {
            lista = transportistaRepository.findByDisponible(disponible);
        } else if (comuna != null) {
            lista = transportistaRepository.findByComunaBaseIgnoreCase(comuna);
        } else {
            lista = transportistaRepository.findAll();
        }
        return lista.stream().map(TransportistaResponse::desde).toList();
    }

    @Override
    public TransportistaResponse obtener(Long id) {
        return TransportistaResponse.desde(buscar(id));
    }

    @Override
    @Transactional
    public TransportistaResponse crear(TransportistaRequest request) {
        if (transportistaRepository.existsByRut(request.rut())) {
            throw new ReglaNegocioException("Ya existe un transportista con el RUT " + request.rut());
        }
        Transportista t = new Transportista();
        copiar(request, t);
        t.setDisponible(true);
        return TransportistaResponse.desde(transportistaRepository.save(t));
    }

    @Override
    @Transactional
    public TransportistaResponse actualizar(Long id, TransportistaRequest request) {
        Transportista t = buscar(id);
        if (!t.getRut().equals(request.rut()) && transportistaRepository.existsByRut(request.rut())) {
            throw new ReglaNegocioException("Ya existe un transportista con el RUT " + request.rut());
        }
        copiar(request, t);
        return TransportistaResponse.desde(t);
    }

    @Override
    @Transactional
    public TransportistaResponse cambiarDisponibilidad(Long id, boolean disponible) {
        Transportista t = buscar(id);
        t.setDisponible(disponible);
        return TransportistaResponse.desde(t);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Transportista t = buscar(id);
        boolean tieneActivas = t.getAsignaciones().stream()
                .anyMatch(a -> a.getEstado() == EstadoAsignacion.ACTIVA);
        if (tieneActivas) {
            throw new ReglaNegocioException("No se puede eliminar: el transportista tiene asignaciones activas");
        }
        transportistaRepository.delete(t);
    }

    @Override
    public List<VehiculoResponse> listarVehiculos(Long transportistaId) {
        buscar(transportistaId);
        return vehiculoRepository.findByTransportistaId(transportistaId).stream()
                .map(VehiculoResponse::desde).toList();
    }

    @Override
    @Transactional
    public VehiculoResponse agregarVehiculo(Long transportistaId, VehiculoRequest request) {
        Transportista t = buscar(transportistaId);
        String patente = request.patente().replace("-", "").toUpperCase();
        if (vehiculoRepository.existsByPatenteIgnoreCase(patente)) {
            throw new ReglaNegocioException("Ya existe un vehículo con la patente " + patente);
        }
        Vehiculo v = new Vehiculo();
        v.setPatente(patente);
        v.setTipo(request.tipo());
        v.setMarca(request.marca());
        v.setModelo(request.modelo());
        v.setCapacidadKg(request.capacidadKg());
        t.agregarVehiculo(v);
        return VehiculoResponse.desde(vehiculoRepository.save(v));
    }

    @Override
    @Transactional
    public void eliminarVehiculo(Long transportistaId, Long vehiculoId) {
        Transportista t = buscar(transportistaId);
        Vehiculo v = vehiculoRepository.findByIdAndTransportistaId(vehiculoId, transportistaId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "El vehículo " + vehiculoId + " no existe para el transportista " + transportistaId));
        t.quitarVehiculo(v);
    }

    private void copiar(TransportistaRequest r, Transportista t) {
        t.setUsuarioId(r.usuarioId());
        t.setRut(r.rut());
        t.setNombre(r.nombre());
        t.setTelefono(r.telefono());
        t.setComunaBase(r.comunaBase());
    }

    private Transportista buscar(Long id) {
        return transportistaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Transportista", id));
    }
}
