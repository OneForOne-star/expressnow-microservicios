package cl.duoc.expressnow.envios.service;

import cl.duoc.expressnow.envios.client.AsignacionTransportista;
import cl.duoc.expressnow.envios.client.TransportistasClient;
import cl.duoc.expressnow.envios.dto.CambioEstadoRequest;
import cl.duoc.expressnow.envios.dto.CancelarRequest;
import cl.duoc.expressnow.envios.dto.EnvioRequest;
import cl.duoc.expressnow.envios.dto.EnvioResponse;
import cl.duoc.expressnow.envios.dto.EnvioUpdateRequest;
import cl.duoc.expressnow.envios.dto.HistorialResponse;
import cl.duoc.expressnow.envios.dto.PaqueteRequest;
import cl.duoc.expressnow.envios.dto.ReprogramarRequest;
import cl.duoc.expressnow.envios.exception.RecursoNoEncontradoException;
import cl.duoc.expressnow.envios.exception.ReglaNegocioException;
import cl.duoc.expressnow.envios.model.Envio;
import cl.duoc.expressnow.envios.model.EstadoEnvio;
import cl.duoc.expressnow.envios.model.Paquete;
import cl.duoc.expressnow.envios.repository.EnvioRepository;
import cl.duoc.expressnow.envios.repository.HistorialEstadoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class EnvioServiceImpl implements EnvioService {

    /** Tarifa: cargo base + valor por kg (redondeado hacia arriba). */
    static final BigDecimal TARIFA_BASE = new BigDecimal("2990");
    static final BigDecimal VALOR_POR_KG = new BigDecimal("990");

    private static final String ALFABETO = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final EnvioRepository envioRepository;
    private final HistorialEstadoRepository historialRepository;
    private final TransportistasClient transportistasClient;

    public EnvioServiceImpl(EnvioRepository envioRepository,
                            HistorialEstadoRepository historialRepository,
                            TransportistasClient transportistasClient) {
        this.envioRepository = envioRepository;
        this.historialRepository = historialRepository;
        this.transportistasClient = transportistasClient;
    }

    @Override
    public List<EnvioResponse> listar(Long clienteId, EstadoEnvio estado) {
        List<Envio> envios;
        if (clienteId != null && estado != null) {
            envios = envioRepository.findByClienteIdAndEstado(clienteId, estado);
        } else if (clienteId != null) {
            envios = envioRepository.findByClienteIdOrderByFechaCreacionDesc(clienteId);
        } else if (estado != null) {
            envios = envioRepository.findByEstado(estado);
        } else {
            envios = envioRepository.findAll();
        }
        return envios.stream().map(EnvioResponse::desde).toList();
    }

    @Override
    public EnvioResponse obtener(Long id) {
        return EnvioResponse.desde(buscar(id));
    }

    @Override
    public EnvioResponse obtenerPorCodigo(String codigo) {
        return envioRepository.findByCodigoSeguimiento(codigo.toUpperCase())
                .map(EnvioResponse::desde)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un envío con código de seguimiento " + codigo));
    }

    /**
     * Crea el envío y pide un transportista a ms-transportistas.
     * Si la asignación falla (servicio caído o sin transportistas), el envío
     * igual se crea y queda PENDIENTE_ASIGNACION.
     */
    @Override
    @Transactional
    public EnvioResponse crear(EnvioRequest request) {
        Envio envio = new Envio();
        envio.setCodigoSeguimiento(generarCodigo());
        envio.setClienteId(request.clienteId());
        envio.setDireccionOrigen(request.direccionOrigen());
        envio.setComunaOrigen(request.comunaOrigen());
        envio.setDireccionDestino(request.direccionDestino());
        envio.setComunaDestino(request.comunaDestino());
        envio.setNombreDestinatario(request.nombreDestinatario());
        envio.setTelefonoDestinatario(request.telefonoDestinatario());
        envio.setFechaRetiro(request.fechaRetiro());
        for (PaqueteRequest p : request.paquetes()) {
            envio.agregarPaquete(aPaquete(p));
        }
        envio.setCosto(calcularCosto(request.paquetes()));
        envio.cambiarEstado(EstadoEnvio.SOLICITADO, "Solicitud de retiro creada por el cliente");
        envio = envioRepository.save(envio);

        intentarAsignar(envio);
        return EnvioResponse.desde(envio);
    }

    @Override
    @Transactional
    public EnvioResponse actualizar(Long id, EnvioUpdateRequest request) {
        Envio envio = buscar(id);
        exigirModificable(envio, "editar");
        envio.setDireccionDestino(request.direccionDestino());
        envio.setComunaDestino(request.comunaDestino());
        envio.setNombreDestinatario(request.nombreDestinatario());
        envio.setTelefonoDestinatario(request.telefonoDestinatario());
        return EnvioResponse.desde(envio);
    }

    @Override
    @Transactional
    public EnvioResponse reprogramar(Long id, ReprogramarRequest request) {
        Envio envio = buscar(id);
        exigirModificable(envio, "reprogramar");
        String comentario = "Retiro reprogramado de " + envio.getFechaRetiro() + " a " + request.fechaRetiro()
                + (request.motivo() != null ? ". Motivo: " + request.motivo() : "");
        envio.setFechaRetiro(request.fechaRetiro());
        // Se registra en el historial sin cambiar el estado
        envio.cambiarEstado(envio.getEstado(), comentario);
        return EnvioResponse.desde(envio);
    }

    @Override
    @Transactional
    public EnvioResponse cancelar(Long id, CancelarRequest request) {
        Envio envio = buscar(id);
        exigirModificable(envio, "cancelar");
        envio.cambiarEstado(EstadoEnvio.CANCELADO, "Cancelado por el cliente. Motivo: " + request.motivo());
        return EnvioResponse.desde(envio);
    }

    @Override
    @Transactional
    public EnvioResponse cambiarEstado(Long id, CambioEstadoRequest request) {
        Envio envio = buscar(id);
        if (!envio.getEstado().puedeCambiarA(request.estado())) {
            throw new ReglaNegocioException("Transición no permitida: " + envio.getEstado() + " -> "
                    + request.estado() + ". Estados válidos desde " + envio.getEstado() + ": "
                    + envio.getEstado().siguientes());
        }
        envio.cambiarEstado(request.estado(), request.comentario());
        return EnvioResponse.desde(envio);
    }

    @Override
    @Transactional
    public EnvioResponse reintentarAsignacion(Long id) {
        Envio envio = buscar(id);
        if (envio.getEstado() != EstadoEnvio.PENDIENTE_ASIGNACION) {
            throw new ReglaNegocioException("Solo se puede reintentar la asignación de envíos PENDIENTE_ASIGNACION; "
                    + "este envío está " + envio.getEstado());
        }
        intentarAsignar(envio);
        return EnvioResponse.desde(envio);
    }

    @Override
    public List<HistorialResponse> historial(Long id) {
        buscar(id);
        return historialRepository.findByEnvioIdOrderByFechaAscIdAsc(id).stream()
                .map(HistorialResponse::desde).toList();
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Envio envio = buscar(id);
        if (envio.getEstado() != EstadoEnvio.CANCELADO) {
            throw new ReglaNegocioException("Solo se pueden eliminar envíos CANCELADOS. Cancele el envío primero.");
        }
        envioRepository.delete(envio);
    }

    // ------------------------------------------------------------------ auxiliares

    private void intentarAsignar(Envio envio) {
        Optional<AsignacionTransportista> asignacion =
                transportistasClient.solicitarAsignacion(envio.getId(), envio.getComunaOrigen());
        if (asignacion.isPresent()) {
            envio.setTransportistaId(asignacion.get().transportistaId());
            envio.cambiarEstado(EstadoEnvio.ASIGNADO,
                    "Asignado al transportista " + asignacion.get().transportistaNombre());
        } else if (envio.getEstado() != EstadoEnvio.PENDIENTE_ASIGNACION) {
            envio.cambiarEstado(EstadoEnvio.PENDIENTE_ASIGNACION,
                    "No fue posible asignar transportista; se reintentará");
        }
    }

    private void exigirModificable(Envio envio, String accion) {
        if (!envio.getEstado().esModificablePorCliente()) {
            throw new ReglaNegocioException("No se puede " + accion + " un envío en estado " + envio.getEstado());
        }
    }

    BigDecimal calcularCosto(List<PaqueteRequest> paquetes) {
        BigDecimal pesoTotal = paquetes.stream().map(PaqueteRequest::pesoKg).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal kilos = pesoTotal.setScale(0, RoundingMode.CEILING);
        return TARIFA_BASE.add(VALOR_POR_KG.multiply(kilos)).setScale(2, RoundingMode.UNNECESSARY);
    }

    private Paquete aPaquete(PaqueteRequest r) {
        Paquete p = new Paquete();
        p.setDescripcion(r.descripcion());
        p.setPesoKg(r.pesoKg());
        p.setLargoCm(r.largoCm());
        p.setAnchoCm(r.anchoCm());
        p.setAltoCm(r.altoCm());
        p.setFragil(r.fragil());
        return p;
    }

    private String generarCodigo() {
        String codigo;
        do {
            StringBuilder sb = new StringBuilder("EXN-");
            for (int i = 0; i < 8; i++) {
                sb.append(ALFABETO.charAt(RANDOM.nextInt(ALFABETO.length())));
            }
            codigo = sb.toString();
        } while (envioRepository.existsByCodigoSeguimiento(codigo));
        return codigo;
    }

    private Envio buscar(Long id) {
        return envioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Envío", id));
    }
}
