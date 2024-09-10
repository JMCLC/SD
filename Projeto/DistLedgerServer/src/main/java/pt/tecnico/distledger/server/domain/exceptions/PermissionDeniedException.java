package pt.tecnico.distledger.server.domain.exceptions;

public class PermissionDeniedException extends IllegalArgumentException {
    
    public PermissionDeniedException() {
        super("This account cannot be deleted.");
    }
}
