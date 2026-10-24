package cl.duoc.expressnow.transportistas.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "ExpressNow · ms-transportistas",
        version = "1.0.0",
        description = "Transportistas, vehículos y asignación de retiros"))
public class OpenApiConfig {
}
