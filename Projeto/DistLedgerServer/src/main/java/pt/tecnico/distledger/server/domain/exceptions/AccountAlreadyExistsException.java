package pt.tecnico.distledger.server.domain.exceptions;

public class AccountAlreadyExistsException extends IllegalArgumentException {

    public AccountAlreadyExistsException(String account) {
        super("The account " + account + " already exists.");
    }
}
