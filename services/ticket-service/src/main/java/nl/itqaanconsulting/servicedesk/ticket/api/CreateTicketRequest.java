package nl.itqaanconsulting.servicedesk.ticket.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import nl.itqaanconsulting.servicedesk.ticket.domain.TicketPriority;

record CreateTicketRequest(
        @NotBlank @Size(max = 120) String title,
        @NotBlank @Size(max = 2000) String description,
        @NotBlank @Email @Size(max = 254) String requesterEmail,
        @NotNull TicketPriority priority
) {
}
