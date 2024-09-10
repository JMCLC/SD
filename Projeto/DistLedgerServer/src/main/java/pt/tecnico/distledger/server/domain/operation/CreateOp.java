package pt.tecnico.distledger.server.domain.operation;

import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.OperationType;

public class CreateOp extends Operation {

    public CreateOp(String account) {
        super(account);
    }

    public CreateOp(String fromAccount, VectorClock prevTS, VectorClock TS) {
        super(fromAccount, prevTS, TS);
    }
    
    @Override
    public OperationType getOperationType() {
        return OperationType.OP_CREATE_ACCOUNT;
    }
}
