package cl.duoc.expressnow.transportistas.service;

import cl.duoc.expressnow.transportistas.dto.AsignacionRequest;
import cl.duoc.expressnow.transportistas.dto.AsignacionResponse;

import java.util.List;

public interface AsignacionService {

    AsignacionResponse asignar(AsignacionRequest request);

    List<AsignacionResponse> listar(Long transportistaId);

    AsignacionResponse obtener(Long id);

    AsignacionResponse completar(Long id);

    AsignacionResponse cancelar(Long id);
}
