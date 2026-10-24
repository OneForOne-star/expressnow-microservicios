package cl.duoc.expressnow.envios.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "ExpressNow · ms-envios",
        version = "1.0.0",
        description = "Solicitud de retiro, reprogramación, cancelación e historial de envíos"))
public class OpenApiConfig {
}
