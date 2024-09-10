package pt.tecnico.distledger.namingserver.domain.exceptions;

public class ServerAlreadyExistsException extends IllegalArgumentException {
    
    public ServerAlreadyExistsException() {
        super("The server already exists");
    }
}
