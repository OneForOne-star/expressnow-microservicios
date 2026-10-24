package cl.duoc.expressnow.transportistas;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de integración de transportistas, vehículos y asignaciones contra H2.
 */
@SpringBootTest
@AutoConfigureMockMvc
class TransportistaControllerTest {

    @Autowired
    private MockMvc mvc;

    private long crearTransportista(String rut, String comuna) throws Exception {
        String body = """
                {"rut":"%s","nombre":"Pedro Soto","telefono":"+56922223333","comunaBase":"%s"}
                """.formatted(rut, comuna);
        String resp = mvc.perform(post("/api/v1/transportistas").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return Long.parseLong(resp.replaceAll("^\\{\"id\":(\\d+).*", "$1"));
    }

    @Test
    void crudTransportistaConVehiculo() throws Exception {
        long id = crearTransportista("12345678-5", "Maipu");

        mvc.perform(post("/api/v1/transportistas/{id}/vehiculos", id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"patente\":\"ABCD12\",\"tipo\":\"FURGON\",\"marca\":\"Peugeot\",\"modelo\":\"Partner\",\"capacidadKg\":800}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.patente").value("ABCD12"));

        mvc.perform(get("/api/v1/transportistas/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehiculos.length()").value(1));

        mvc.perform(patch("/api/v1/transportistas/{id}/disponibilidad", id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"disponible\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.disponible").value(false));

        mvc.perform(delete("/api/v1/transportistas/{id}", id))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/v1/transportistas/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void asignarEnvioYCompletar() throws Exception {
        long transportistaId = crearTransportista("9876543-2", "Providencia");

        String resp = mvc.perform(post("/api/v1/asignaciones").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"envioId\":500,\"comuna\":\"Providencia\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transportistaId").value(transportistaId))
                .andExpect(jsonPath("$.estado").value("ACTIVA"))
                .andReturn().getResponse().getContentAsString();
        long asignacionId = Long.parseLong(resp.replaceAll("^\\{\"id\":(\\d+).*", "$1"));

        // No se puede asignar dos veces el mismo envío
        mvc.perform(post("/api/v1/asignaciones").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"envioId\":500,\"comuna\":\"Providencia\"}"))
                .andExpect(status().isConflict());

        // Con una asignación activa el transportista no se puede eliminar
        mvc.perform(delete("/api/v1/transportistas/{id}", transportistaId))
                .andExpect(status().isConflict());

        mvc.perform(patch("/api/v1/asignaciones/{id}/completar", asignacionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("COMPLETADA"));
    }

    @Test
    void validacionesDevuelven400() throws Exception {
        mvc.perform(post("/api/v1/transportistas").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rut\":\"\",\"nombre\":\"\",\"comunaBase\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalles.rut").exists());
    }
}
