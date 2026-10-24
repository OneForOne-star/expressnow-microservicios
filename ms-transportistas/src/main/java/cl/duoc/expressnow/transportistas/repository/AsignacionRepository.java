package cl.duoc.expressnow.transportistas.repository;

import cl.duoc.expressnow.transportistas.model.Asignacion;
import cl.duoc.expressnow.transportistas.model.EstadoAsignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {

    List<Asignacion> findByTransportistaId(Long transportistaId);

    boolean existsByEnvioIdAndEstado(Long envioId, EstadoAsignacion estado);
}
