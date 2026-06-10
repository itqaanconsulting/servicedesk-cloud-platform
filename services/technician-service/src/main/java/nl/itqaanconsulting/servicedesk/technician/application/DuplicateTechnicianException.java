package nl.itqaanconsulting.servicedesk.technician.application;

public class DuplicateTechnicianException extends RuntimeException {

    public DuplicateTechnicianException(String email) {
        super("A technician with email " + email + " already exists");
    }
}
