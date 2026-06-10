package nl.itqaanconsulting.servicedesk.technician.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;

record CreateTechnicianRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Email @Size(max = 254) String email,
        @NotEmpty Set<@NotBlank @Size(max = 80) String> skills
) {
}
