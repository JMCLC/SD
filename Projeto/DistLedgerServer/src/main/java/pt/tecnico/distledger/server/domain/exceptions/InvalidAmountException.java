package pt.tecnico.distledger.server.domain.exceptions;

public class InvalidAmountException extends IllegalArgumentException {

    public InvalidAmountException() {
        super("This is an invalid amount.");
    }
}
