package pt.tecnico.distledger.server.domain.exceptions;

public class NotEnoughFundsException extends IllegalArgumentException {
    
    public NotEnoughFundsException(Integer value, Integer balance) {
        super("This account cannot make a transaction of " + value + " when it only has " + balance + ".");
    }
}
