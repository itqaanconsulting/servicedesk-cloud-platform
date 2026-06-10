package nl.itqaanconsulting.servicedesk.technician.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "technicians")
public class Technician {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Availability availability;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "technician_skills", joinColumns = @JoinColumn(name = "technician_id"))
    @Column(name = "skill", nullable = false, length = 80)
    private Set<String> skills = new LinkedHashSet<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Technician() {
    }

    public Technician(String name, String email, Set<String> skills) {
        this.name = name;
        this.email = email.toLowerCase();
        this.skills = normalizeSkills(skills);
        this.availability = Availability.AVAILABLE;
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;
    }

    public void changeAvailability(Availability availability) {
        this.availability = availability;
        this.updatedAt = Instant.now();
    }

    private LinkedHashSet<String> normalizeSkills(Set<String> skills) {
        LinkedHashSet<String> normalized = new LinkedHashSet<>();
        skills.stream()
                .map(String::trim)
                .filter(skill -> !skill.isEmpty())
                .map(String::toUpperCase)
                .forEach(normalized::add);
        return normalized;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Availability getAvailability() {
        return availability;
    }

    public Set<String> getSkills() {
        return Set.copyOf(skills);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
