package nl.itqaanconsulting.servicedesk.ticket.api;

import nl.itqaanconsulting.servicedesk.ticket.domain.Ticket;
import nl.itqaanconsulting.servicedesk.ticket.domain.AssignmentStatus;
import nl.itqaanconsulting.servicedesk.ticket.domain.TicketPriority;
import nl.itqaanconsulting.servicedesk.ticket.domain.TicketStatus;

import java.time.Instant;
import java.util.UUID;

record TicketResponse(
        UUID id,
        String title,
        String description,
        String requesterEmail,
        TicketPriority priority,
        TicketStatus status,
        String requiredSkill,
        AssignmentStatus assignmentStatus,
        UUID assignedTechnicianId,
        String assignedTechnicianName,
        String assignedTechnicianEmail,
        Instant createdAt,
        Instant updatedAt
) {

    static TicketResponse from(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getRequesterEmail(),
                ticket.getPriority(),
                ticket.getStatus(),
                ticket.getRequiredSkill(),
                ticket.getAssignmentStatus(),
                ticket.getAssignedTechnicianId(),
                ticket.getAssignedTechnicianName(),
                ticket.getAssignedTechnicianEmail(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt()
        );
    }
}
