package nl.itqaanconsulting.servicedesk.ticket.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import nl.itqaanconsulting.servicedesk.ticket.integration.TechnicianClient;
import nl.itqaanconsulting.servicedesk.ticket.integration.TechnicianReservation;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
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
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TechnicianClient technicianClient;

    @Test
    void createsAndReturnsTicket() throws Exception {
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validTicketJson()))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value("VPN access unavailable"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.requiredSkill").value("NETWORKING"))
                .andExpect(jsonPath("$.assignmentStatus").value("UNASSIGNED"));
    }

    @Test
    void changesTicketStatusAndFiltersTickets() throws Exception {
        UUID ticketId = createTicket();

        mockMvc.perform(patch("/api/tickets/{ticketId}/status", ticketId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status":"IN_PROGRESS"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        mockMvc.perform(get("/api/tickets").param("status", "IN_PROGRESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(ticketId.toString()));
    }

    @Test
    void rejectsInvalidTicket() throws Exception {
        mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "",
                                  "description": "Missing requester details",
                                  "requesterEmail": "not-an-email",
                                  "priority": null,
                                  "requiredSkill": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.fieldErrors.title").exists())
                .andExpect(jsonPath("$.fieldErrors.requesterEmail").exists())
                .andExpect(jsonPath("$.fieldErrors.priority").exists())
                .andExpect(jsonPath("$.fieldErrors.requiredSkill").exists());
    }

    @Test
    void returnsNotFoundForUnknownTicket() throws Exception {
        UUID ticketId = UUID.randomUUID();

        mockMvc.perform(get("/api/tickets/{ticketId}", ticketId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Ticket " + ticketId + " was not found"));
    }

    @Test
    void assignsAvailableTechnician() throws Exception {
        UUID ticketId = createTicket();
        UUID technicianId = UUID.randomUUID();
        when(technicianClient.reserve(eq("NETWORKING")))
                .thenReturn(Optional.of(new TechnicianReservation(
                        technicianId,
                        "Samira de Vries",
                        "samira@example.com"
                )));

        mockMvc.perform(post("/api/tickets/{ticketId}/assignment", ticketId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignmentStatus").value("ASSIGNED"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.assignedTechnicianId").value(technicianId.toString()))
                .andExpect(jsonPath("$.assignedTechnicianName").value("Samira de Vries"));
    }

    @Test
    void keepsTicketUnassignedWhenTechnicianServiceIsUnavailable() throws Exception {
        UUID ticketId = createTicket();
        when(technicianClient.reserve(eq("NETWORKING"))).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/tickets/{ticketId}/assignment", ticketId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignmentStatus").value("UNASSIGNED"))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.assignedTechnicianId").doesNotExist());
    }

    private UUID createTicket() throws Exception {
        String response = mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validTicketJson()))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonNode ticket = objectMapper.readTree(response);
        return UUID.fromString(ticket.get("id").asText());
    }

    private String validTicketJson() {
        return """
                {
                  "title": "VPN access unavailable",
                  "description": "Remote employee cannot connect to the corporate VPN.",
                  "requesterEmail": "alex@example.com",
                  "priority": "HIGH",
                  "requiredSkill": "networking"
                }
                """;
    }
}
