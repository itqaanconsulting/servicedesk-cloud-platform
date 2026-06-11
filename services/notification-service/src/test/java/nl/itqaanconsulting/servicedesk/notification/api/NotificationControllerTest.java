package nl.itqaanconsulting.servicedesk.notification.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void recordsSuccessfulAndFailedDeliveryHistory() throws Exception {
        create("requester@example.com")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("DELIVERED"));

        create("requester@delivery-failure.test")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("FAILED"))
                .andExpect(jsonPath("$.failureReason").value("Simulated email provider rejection"));

        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    private org.springframework.test.web.servlet.ResultActions create(String recipient) throws Exception {
        return mockMvc.perform(post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "ticketId": "00000000-0000-0000-0000-000000000001",
                          "type": "TICKET_ASSIGNED",
                          "recipient": "%s",
                          "subject": "Ticket assigned",
                          "message": "Your ticket was assigned."
                        }
                        """.formatted(recipient)));
    }
}
