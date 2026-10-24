package cl.duoc.expressnow.envios.repository;

import cl.duoc.expressnow.envios.model.Envio;
import cl.duoc.expressnow.envios.model.EstadoEnvio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnvioRepository extends JpaRepository<Envio, Long> {

    Optional<Envio> findByCodigoSeguimiento(String codigoSeguimiento);

    List<Envio> findByClienteIdOrderByFechaCreacionDesc(Long clienteId);

    List<Envio> findByEstado(EstadoEnvio estado);

    List<Envio> findByClienteIdAndEstado(Long clienteId, EstadoEnvio estado);

    boolean existsByCodigoSeguimiento(String codigoSeguimiento);
}
