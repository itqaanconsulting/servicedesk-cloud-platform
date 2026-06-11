package nl.itqaanconsulting.servicedesk.notification.domain;

import java.time.Instant;
import java.util.UUID;

public record Notification(
        UUID id,
        UUID ticketId,
        String type,
        String recipient,
        String subject,
        String message,
        NotificationStatus status,
        String failureReason,
        Instant createdAt
) {
}
