package nl.itqaanconsulting.servicedesk.technician.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.UUID;

public interface TechnicianRepository extends JpaRepository<Technician, UUID> {

    boolean existsByEmailIgnoreCase(String email);

    List<Technician> findAllByAvailabilityOrderByName(Availability availability);

    @Query("""
            select distinct technician
            from Technician technician
            join technician.skills skill
            where upper(skill) = upper(:skill)
              and (:availability is null or technician.availability = :availability)
            order by technician.name
            """)
    List<Technician> findBySkillAndAvailability(
            @Param("skill") String skill,
            @Param("availability") Availability availability
    );

    List<Technician> findAllByOrderByName();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select technician
            from Technician technician
            join technician.skills skill
            where upper(skill) = upper(:skill)
              and technician.availability = nl.itqaanconsulting.servicedesk.technician.domain.Availability.AVAILABLE
            order by technician.name
            """)
    List<Technician> findAvailableForReservation(@Param("skill") String skill, Pageable pageable);
}
