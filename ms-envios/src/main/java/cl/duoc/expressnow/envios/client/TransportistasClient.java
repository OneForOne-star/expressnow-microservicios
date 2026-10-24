package cl.duoc.expressnow.envios.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.Optional;

/**
 * Cliente REST hacia ms-transportistas.
 * <p>
 * La llamada está protegida con un Circuit Breaker (Resilience4j): si ms-transportistas
 * está caído, lento o no tiene transportistas libres, se ejecuta el fallback y el envío
 * queda en estado PENDIENTE_ASIGNACION en lugar de fallar.
 */
@Component
public class TransportistasClient {

    private static final Logger log = LoggerFactory.getLogger(TransportistasClient.class);

    private final RestClient restClient;

    public TransportistasClient(RestClient.Builder builder,
                                @Value("${transportistas.url}") String baseUrl,
                                @Value("${transportistas.timeout-ms:2000}") int timeoutMs) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(timeoutMs);
        requestFactory.setReadTimeout(timeoutMs);
        this.restClient = builder
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    @CircuitBreaker(name = "transportistas", fallbackMethod = "sinAsignacion")
    public Optional<AsignacionTransportista> solicitarAsignacion(Long envioId, String comuna) {
        AsignacionTransportista respuesta = restClient.post()
                .uri("/api/v1/asignaciones")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("envioId", envioId, "comuna", comuna))
                .retrieve()
                .body(AsignacionTransportista.class);
        return Optional.ofNullable(respuesta);
    }

    /** Fallback del Circuit Breaker: mismo firma + la excepción que lo disparó. */
    public Optional<AsignacionTransportista> sinAsignacion(Long envioId, String comuna, Throwable causa) {
        log.warn("No se pudo asignar transportista al envío {} ({}). Queda pendiente.", envioId, causa.toString());
        return Optional.empty();
    }
}
