package cl.duoc.expressnow.transportistas.service;

import cl.duoc.expressnow.transportistas.dto.TransportistaRequest;
import cl.duoc.expressnow.transportistas.dto.TransportistaResponse;
import cl.duoc.expressnow.transportistas.dto.VehiculoRequest;
import cl.duoc.expressnow.transportistas.dto.VehiculoResponse;

import java.util.List;

public interface TransportistaService {

    List<TransportistaResponse> listar(Boolean disponible, String comuna);

    TransportistaResponse obtener(Long id);

    TransportistaResponse crear(TransportistaRequest request);

    TransportistaResponse actualizar(Long id, TransportistaRequest request);

    TransportistaResponse cambiarDisponibilidad(Long id, boolean disponible);

    void eliminar(Long id);

    List<VehiculoResponse> listarVehiculos(Long transportistaId);

    VehiculoResponse agregarVehiculo(Long transportistaId, VehiculoRequest request);

    void eliminarVehiculo(Long transportistaId, Long vehiculoId);
}
