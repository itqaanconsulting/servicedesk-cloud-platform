package nl.itqaanconsulting.servicedesk.ticket.integration;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import nl.itqaanconsulting.servicedesk.ticket.domain.Ticket;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class NotificationClient {

    private final RestClient restClient;

    public NotificationClient(@Qualifier("notificationRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    @Retry(name = "notificationService")
    @CircuitBreaker(name = "notificationService", fallbackMethod = "fallback")
    public void sendAssignment(Ticket ticket) {
        restClient.post()
                .uri("/api/notifications")
                .body(AssignmentNotificationRequest.from(ticket))
                .retrieve()
                .toBodilessEntity();
    }

    public void fallback(Ticket ticket, Throwable exception) {
        // Notification delivery must not roll back a completed technician assignment.
    }
}
