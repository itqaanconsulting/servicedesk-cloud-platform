package nl.itqaanconsulting.servicedesk.ticket.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(name = "requester_email", nullable = false, length = 254)
    private String requesterEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TicketPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TicketStatus status;

    @Column(name = "required_skill", nullable = false, length = 80)
    private String requiredSkill;

    @Enumerated(EnumType.STRING)
    @Column(name = "assignment_status", nullable = false, length = 20)
    private AssignmentStatus assignmentStatus;

    @Column(name = "assigned_technician_id")
    private UUID assignedTechnicianId;

    @Column(name = "assigned_technician_name", length = 120)
    private String assignedTechnicianName;

    @Column(name = "assigned_technician_email", length = 254)
    private String assignedTechnicianEmail;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Ticket() {
    }

    public Ticket(
            String title,
            String description,
            String requesterEmail,
            TicketPriority priority,
            String requiredSkill
    ) {
        this.title = title;
        this.description = description;
        this.requesterEmail = requesterEmail;
        this.priority = priority;
        this.status = TicketStatus.OPEN;
        this.requiredSkill = requiredSkill.trim().toUpperCase();
        this.assignmentStatus = AssignmentStatus.UNASSIGNED;
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;
    }

    public void changeStatus(TicketStatus status) {
        this.status = status;
        this.updatedAt = Instant.now();
    }

    public void assign(UUID technicianId, String technicianName, String technicianEmail) {
        this.assignedTechnicianId = technicianId;
        this.assignedTechnicianName = technicianName;
        this.assignedTechnicianEmail = technicianEmail;
        this.assignmentStatus = AssignmentStatus.ASSIGNED;
        this.status = TicketStatus.IN_PROGRESS;
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getRequesterEmail() {
        return requesterEmail;
    }

    public TicketPriority getPriority() {
        return priority;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public String getRequiredSkill() {
        return requiredSkill;
    }

    public AssignmentStatus getAssignmentStatus() {
        return assignmentStatus;
    }

    public UUID getAssignedTechnicianId() {
        return assignedTechnicianId;
    }

    public String getAssignedTechnicianName() {
        return assignedTechnicianName;
    }

    public String getAssignedTechnicianEmail() {
        return assignedTechnicianEmail;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
