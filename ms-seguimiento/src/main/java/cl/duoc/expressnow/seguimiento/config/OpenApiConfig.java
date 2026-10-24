package cl.duoc.expressnow.seguimiento.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "ExpressNow · ms-seguimiento",
        version = "1.0.0",
        description = "Seguimiento en tiempo real y eventos de cada envío"))
public class OpenApiConfig {
}
