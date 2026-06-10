package nl.itqaanconsulting.servicedesk.technician.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TechnicianControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createsTechnicianWithNormalizedSkills() throws Exception {
        mockMvc.perform(post("/api/technicians")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validTechnicianJson("samira@example.com")))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.email").value("samira@example.com"))
                .andExpect(jsonPath("$.skills").isArray())
                .andExpect(jsonPath("$.skills[?(@ == 'JAVA')]").exists())
                .andExpect(jsonPath("$.skills[?(@ == 'KUBERNETES')]").exists())
                .andExpect(jsonPath("$.availability").value("AVAILABLE"));
    }

    @Test
    void searchesAvailableTechniciansBySkill() throws Exception {
        createTechnician("samira@example.com");

        mockMvc.perform(get("/api/technicians")
                        .param("skill", "java")
                        .param("availability", "AVAILABLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].email").value("samira@example.com"));
    }

    @Test
    void changesAvailability() throws Exception {
        UUID technicianId = createTechnician("samira@example.com");

        mockMvc.perform(patch("/api/technicians/{technicianId}/availability", technicianId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"availability":"BUSY"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availability").value("BUSY"));
    }

    @Test
    void rejectsDuplicateEmail() throws Exception {
        createTechnician("samira@example.com");

        mockMvc.perform(post("/api/technicians")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validTechnicianJson("samira@example.com")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message")
                        .value("A technician with email samira@example.com already exists"));
    }

    @Test
    void rejectsInvalidTechnician() throws Exception {
        mockMvc.perform(post("/api/technicians")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "",
                                  "email": "invalid",
                                  "skills": []
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.name").exists())
                .andExpect(jsonPath("$.fieldErrors.email").exists())
                .andExpect(jsonPath("$.fieldErrors.skills").exists());
    }

    private UUID createTechnician(String email) throws Exception {
        String response = mockMvc.perform(post("/api/technicians")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validTechnicianJson(email)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonNode technician = objectMapper.readTree(response);
        return UUID.fromString(technician.get("id").asText());
    }

    private String validTechnicianJson(String email) {
        return """
                {
                  "name": "Samira de Vries",
                  "email": "%s",
                  "skills": ["java", "kubernetes"]
                }
                """.formatted(email);
    }
}
