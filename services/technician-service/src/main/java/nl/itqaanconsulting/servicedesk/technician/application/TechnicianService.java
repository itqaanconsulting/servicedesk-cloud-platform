package nl.itqaanconsulting.servicedesk.technician.application;

import nl.itqaanconsulting.servicedesk.technician.domain.Availability;
import nl.itqaanconsulting.servicedesk.technician.domain.Technician;
import nl.itqaanconsulting.servicedesk.technician.domain.TechnicianRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class TechnicianService {

    private final TechnicianRepository technicianRepository;

    public TechnicianService(TechnicianRepository technicianRepository) {
        this.technicianRepository = technicianRepository;
    }

    @Transactional
    public Technician create(String name, String email, Set<String> skills) {
        if (technicianRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateTechnicianException(email);
        }
        return technicianRepository.save(new Technician(name, email, skills));
    }

    @Transactional(readOnly = true)
    public Technician get(UUID technicianId) {
        return technicianRepository.findById(technicianId)
                .orElseThrow(() -> new TechnicianNotFoundException(technicianId));
    }

    @Transactional(readOnly = true)
    public List<Technician> find(String skill, Availability availability) {
        if (skill != null && !skill.isBlank()) {
            return technicianRepository.findBySkillAndAvailability(skill.trim(), availability);
        }
        if (availability != null) {
            return technicianRepository.findAllByAvailabilityOrderByName(availability);
        }
        return technicianRepository.findAllByOrderByName();
    }

    @Transactional
    public Technician changeAvailability(UUID technicianId, Availability availability) {
        Technician technician = get(technicianId);
        technician.changeAvailability(availability);
        return technician;
    }

    @Transactional
    public Technician reserve(String skill) {
        Technician technician = technicianRepository
                .findAvailableForReservation(skill.trim(), PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElseThrow(() -> new NoAvailableTechnicianException(skill));
        technician.changeAvailability(Availability.BUSY);
        return technician;
    }
}
