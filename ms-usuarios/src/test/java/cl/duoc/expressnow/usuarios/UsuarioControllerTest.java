package cl.duoc.expressnow.usuarios;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de integración de la API de usuarios contra H2 en memoria.
 */
@SpringBootTest
@AutoConfigureMockMvc
class UsuarioControllerTest {

    @Autowired
    private MockMvc mvc;

    private static final String USUARIO_JSON = """
            {
              "rut": "%s",
              "nombre": "Ana",
              "apellido": "Rojas",
              "email": "%s",
              "password": "Secreta123",
              "telefono": "+56911112222",
              "tipo": "CLIENTE"
            }
            """;

    @Test
    void crearObtenerYEliminarUsuario() throws Exception {
        String respuesta = mvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(USUARIO_JSON.formatted("11111111-1", "ana@test.cl")))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", notNullValue()))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.email").value("ana@test.cl"))
                .andExpect(jsonPath("$.passwordHash").doesNotExist())
                .andReturn().getResponse().getContentAsString();

        long id = Long.parseLong(respuesta.replaceAll(".*\"id\":(\\d+).*", "$1"));

        mvc.perform(get("/api/v1/usuarios/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ana"));

        mvc.perform(delete("/api/v1/usuarios/{id}", id))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/v1/usuarios/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void crearUsuarioInvalidoDevuelve400ConDetalle() throws Exception {
        mvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(USUARIO_JSON.formatted("rut-malo", "no-es-email")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalles.rut").exists())
                .andExpect(jsonPath("$.detalles.email").exists());
    }

    @Test
    void emailDuplicadoDevuelve409() throws Exception {
        mvc.perform(post("/api/v1/usuarios").contentType(MediaType.APPLICATION_JSON)
                        .content(USUARIO_JSON.formatted("22222222-2", "dup@test.cl")))
                .andExpect(status().isCreated());

        mvc.perform(post("/api/v1/usuarios").contentType(MediaType.APPLICATION_JSON)
                        .content(USUARIO_JSON.formatted("33333333-3", "dup@test.cl")))
                .andExpect(status().isConflict());
    }

    @Test
    void loginCorrectoYIncorrecto() throws Exception {
        mvc.perform(post("/api/v1/usuarios").contentType(MediaType.APPLICATION_JSON)
                        .content(USUARIO_JSON.formatted("44444444-4", "login@test.cl")))
                .andExpect(status().isCreated());

        mvc.perform(post("/api/v1/usuarios/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"login@test.cl\",\"password\":\"Secreta123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("CLIENTE"));

        mvc.perform(post("/api/v1/usuarios/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"login@test.cl\",\"password\":\"incorrecta\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void agregarDireccionAUsuario() throws Exception {
        String respuesta = mvc.perform(post("/api/v1/usuarios").contentType(MediaType.APPLICATION_JSON)
                        .content(USUARIO_JSON.formatted("55555555-5", "dir@test.cl")))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long id = Long.parseLong(respuesta.replaceAll(".*\"id\":(\\d+).*", "$1"));

        mvc.perform(post("/api/v1/usuarios/{id}/direcciones", id).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"calle":"Av. Providencia","numero":"1234","comuna":"Providencia",
                                 "region":"Metropolitana","principal":true}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.principal").value(true));

        mvc.perform(get("/api/v1/usuarios/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.direcciones.length()").value(1));
    }
}
