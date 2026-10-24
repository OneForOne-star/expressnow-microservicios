package cl.duoc.expressnow.envios.service;

import cl.duoc.expressnow.envios.dto.CambioEstadoRequest;
import cl.duoc.expressnow.envios.dto.CancelarRequest;
import cl.duoc.expressnow.envios.dto.EnvioRequest;
import cl.duoc.expressnow.envios.dto.EnvioResponse;
import cl.duoc.expressnow.envios.dto.EnvioUpdateRequest;
import cl.duoc.expressnow.envios.dto.HistorialResponse;
import cl.duoc.expressnow.envios.dto.ReprogramarRequest;
import cl.duoc.expressnow.envios.model.EstadoEnvio;

import java.util.List;

public interface EnvioService {

    List<EnvioResponse> listar(Long clienteId, EstadoEnvio estado);

    EnvioResponse obtener(Long id);

    EnvioResponse obtenerPorCodigo(String codigo);

    EnvioResponse crear(EnvioRequest request);

    EnvioResponse actualizar(Long id, EnvioUpdateRequest request);

    EnvioResponse reprogramar(Long id, ReprogramarRequest request);

    EnvioResponse cancelar(Long id, CancelarRequest request);

    EnvioResponse cambiarEstado(Long id, CambioEstadoRequest request);

    EnvioResponse reintentarAsignacion(Long id);

    List<HistorialResponse> historial(Long id);

    void eliminar(Long id);
}
