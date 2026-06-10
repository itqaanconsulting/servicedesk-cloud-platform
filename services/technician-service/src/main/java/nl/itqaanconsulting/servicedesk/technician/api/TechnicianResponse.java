package nl.itqaanconsulting.servicedesk.technician.api;

import nl.itqaanconsulting.servicedesk.technician.domain.Availability;
import nl.itqaanconsulting.servicedesk.technician.domain.Technician;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

record TechnicianResponse(
        UUID id,
        String name,
        String email,
        Set<String> skills,
        Availability availability,
        Instant createdAt,
        Instant updatedAt
) {

    static TechnicianResponse from(Technician technician) {
        return new TechnicianResponse(
                technician.getId(),
                technician.getName(),
                technician.getEmail(),
                technician.getSkills(),
                technician.getAvailability(),
                technician.getCreatedAt(),
                technician.getUpdatedAt()
        );
    }
}
