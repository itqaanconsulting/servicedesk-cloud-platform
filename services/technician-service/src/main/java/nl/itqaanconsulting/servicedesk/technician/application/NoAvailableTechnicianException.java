package nl.itqaanconsulting.servicedesk.technician.application;

public class NoAvailableTechnicianException extends RuntimeException {

    public NoAvailableTechnicianException(String skill) {
        super("No available technician found for skill " + skill);
    }
}
