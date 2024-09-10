package pt.tecnico.distledger.server.domain.exceptions;

public class AccountNotFoundException extends IllegalArgumentException {

    public AccountNotFoundException(String account) {
        super("The account " + account + " does not exist.");
    }
}
