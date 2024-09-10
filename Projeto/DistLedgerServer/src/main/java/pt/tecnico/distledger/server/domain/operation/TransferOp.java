package pt.tecnico.distledger.server.domain.operation;

import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.OperationType;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.Timestamp;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;

public class TransferOp extends Operation {
    private String destAccount;
    private int amount;

    public TransferOp(String fromAccount, String destAccount, int amount) {
        super(fromAccount);
        this.destAccount = destAccount;
        this.amount = amount;
    }

    public TransferOp(String fromAccount, String destAccount, int amount, VectorClock prevTS, VectorClock TS) {
        super(fromAccount, prevTS, TS);
        this.destAccount = destAccount;
        this.amount = amount;
    }

    public String getDestAccount() {
        return destAccount;
    }

    public void setDestAccount(String destAccount) {
        this.destAccount = destAccount;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public OperationType getOperationType() {
        return OperationType.OP_TRANSFER_TO;
    }

    @Override
    public DistLedgerCommonDefinitions.Operation proto() {
        return DistLedgerCommonDefinitions.Operation
        .newBuilder()
        .setType(getOperationType())
        .setUserId(getAccount())
        .setDestUserId(getDestAccount())
        .setAmount(getAmount())
        .setPrevTS(Timestamp.newBuilder().addAllTS(prevTS.getTimestamps()).build())
        .setTS(Timestamp.newBuilder().addAllTS(TS.getTimestamps()).build())
        .build();
    }
}
