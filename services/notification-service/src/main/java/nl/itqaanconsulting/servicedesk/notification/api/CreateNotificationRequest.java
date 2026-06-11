package nl.itqaanconsulting.servicedesk.notification.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateNotificationRequest(
        @NotNull UUID ticketId,
        @NotBlank String type,
        @NotBlank @Email String recipient,
        @NotBlank String subject,
        @NotBlank String message
) {
}
