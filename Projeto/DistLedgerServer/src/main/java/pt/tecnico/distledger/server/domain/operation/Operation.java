package pt.tecnico.distledger.server.domain.operation;

import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.OperationType;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.Timestamp;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;

public class Operation {
    private String account;
    VectorClock prevTS;
    VectorClock TS;
    boolean stable;

    public Operation(String fromAccount) {
        this.account = fromAccount;
    }

    public Operation(String fromAccount, VectorClock prevTS, VectorClock TS) {
        this.account = fromAccount;
        this.prevTS = prevTS;
        this.TS = TS;
        this.stable = false;
    }

    public String getAccount() {
        return account;
    }

    public VectorClock getPrevTS() {
        return prevTS;
    }

    public VectorClock getTS() {
        return TS;
    }

    public boolean getStability() {
        return stable;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setStability(boolean stable) {
        this.stable = stable;
    }

    public OperationType getOperationType() {
        return OperationType.OP_UNSPECIFIED;
    }

    public DistLedgerCommonDefinitions.Operation proto() {
        return DistLedgerCommonDefinitions.Operation
        .newBuilder()
        .setType(getOperationType())
        .setUserId(getAccount())
        .setPrevTS(Timestamp.newBuilder().addAllTS(getPrevTS().getTimestamps()).build())
        .setTS(Timestamp.newBuilder().addAllTS(getTS().getTimestamps()).build())
        .build();
    }
}
