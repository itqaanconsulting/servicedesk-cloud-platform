package nl.itqaanconsulting.servicedesk.technician.application;

import java.util.UUID;

public class TechnicianNotFoundException extends RuntimeException {

    public TechnicianNotFoundException(UUID technicianId) {
        super("Technician " + technicianId + " was not found");
    }
}
