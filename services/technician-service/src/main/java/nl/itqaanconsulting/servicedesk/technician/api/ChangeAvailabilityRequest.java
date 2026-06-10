package nl.itqaanconsulting.servicedesk.technician.api;

import jakarta.validation.constraints.NotNull;
import nl.itqaanconsulting.servicedesk.technician.domain.Availability;

record ChangeAvailabilityRequest(@NotNull Availability availability) {
}
