package nl.itqaanconsulting.servicedesk.ticket.api;

import jakarta.validation.constraints.NotNull;
import nl.itqaanconsulting.servicedesk.ticket.domain.TicketStatus;

record ChangeTicketStatusRequest(@NotNull TicketStatus status) {
}
