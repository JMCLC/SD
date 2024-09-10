package pt.tecnico.distledger.namingserver.domain.exceptions;

public class ServerNotFoundException extends IllegalArgumentException {
    
    public ServerNotFoundException(String address, String qualifier) {
        super("The server " + address + " with a qualifier of " + qualifier + " does not exist.");
    }
}
