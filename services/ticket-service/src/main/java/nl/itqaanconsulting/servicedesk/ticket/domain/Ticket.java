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

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Ticket() {
    }

    public Ticket(String title, String description, String requesterEmail, TicketPriority priority) {
        this.title = title;
        this.description = description;
        this.requesterEmail = requesterEmail;
        this.priority = priority;
        this.status = TicketStatus.OPEN;
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;
    }

    public void changeStatus(TicketStatus status) {
        this.status = status;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
