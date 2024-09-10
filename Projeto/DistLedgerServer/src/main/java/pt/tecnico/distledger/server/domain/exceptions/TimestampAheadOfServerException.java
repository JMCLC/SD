package pt.tecnico.distledger.server.domain.exceptions;

import pt.tecnico.distledger.server.domain.operation.VectorClock;

public class TimestampAheadOfServerException extends RuntimeException {
    
    public TimestampAheadOfServerException(VectorClock prevTS, VectorClock valueTS) {
        super("The timestamp received: " + prevTS.toString() + " is ahead of the attributed timestamp: " + valueTS.toString() + ".");
    }
}
