package pt.tecnico.distledger.server.domain.exceptions;

public class ServerUnavailableException extends RuntimeException {

    public ServerUnavailableException() {
        super("The server is not active.");
    }
}
