package cl.duoc.expressnow.usuarios.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "ExpressNow · ms-usuarios",
        version = "1.0.0",
        description = "Registro, perfil, direcciones y login de clientes, transportistas y soporte"))
public class OpenApiConfig {
}
