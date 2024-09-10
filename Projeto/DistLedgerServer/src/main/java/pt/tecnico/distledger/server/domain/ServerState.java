package pt.tecnico.distledger.server.domain;

import pt.ulisboa.tecnico.distledger.contract.distledgerserver.CrossServerDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.LedgerState;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.Timestamp;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.user.UserDistLedger.*;
import pt.tecnico.distledger.server.CrossServerService;
import pt.tecnico.distledger.server.domain.exceptions.*;
import pt.tecnico.distledger.server.domain.operation.*;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ServerState {
    private CrossServerService crossServerService;
    private Map<String, Integer> accounts;
    private List<Operation> ledger;
    private final String qualifier;
    private VectorClock replicaTS;
    private VectorClock valueTS;
    private boolean status;
    private final boolean debug;

    public ServerState(String qualifier, CrossServerService crossServerService) {
        this.ledger = new ArrayList<>();
        this.accounts = new HashMap<>();
        this.status = true;
        this.qualifier = qualifier;
        this.crossServerService = crossServerService;
        this.replicaTS = new VectorClock();
        this.valueTS = new VectorClock();
        this.debug = debug;
        accounts.put("broker", 1000);
    }

    private void debug(String debugMessage) {
        if (debug) {
            System.err.println(debugMessage);
        }
    }

    public ActivateResponse activateServer() {
        status = true;
        return ActivateResponse.getDefaultInstance();
    }

    public DeactivateResponse deactivateServer() {
        status = false;
        return DeactivateResponse.getDefaultInstance();
    }

    public BalanceResponse getAccountBalance(String account, Timestamp prevTS) throws ServerUnavailableException, AccountNotFoundException {
        if (!status) {
            throw new ServerUnavailableException();
        } else if (!accounts.containsKey(account)) {
            throw new AccountNotFoundException(account);
        } else if (!this.valueTS.GreaterOrEqual(new VectorClock(prevTS))) {
            throw new TimestampAheadOfServerException(new VectorClock(prevTS), this.valueTS);
        }
        return BalanceResponse
        .newBuilder()
        .setValue(accounts.get(account))
        .setValueTS(this.valueTS.proto())
        .build();
    }

    public Timestamp operationTS() { 
        replicaTS.setTS(qualifier, replicaTS.getTS(qualifier) + 1);
        debug("ReplicaTS changed to: " + replicaTS.toString());
        return replicaTS.proto();
    }   
    
    public CreateAccountResponse createAccountOperation() throws ServerUnavailableException {
        if (!status)
            throw new ServerUnavailableException();
        return CreateAccountResponse.newBuilder().setTS(operationTS()).build();
    }

    public void createAccount(String account, Timestamp prevTS, Timestamp valueTS) throws AccountAlreadyExistsException {
        Operation operation = new CreateOp(account, new VectorClock(prevTS), new VectorClock(valueTS));
        debug("Operation added to ledger");
        ledger.add(operation);
        if (!this.valueTS.GreaterOrEqual(new VectorClock(prevTS)))
            throw new TimestampAheadOfServerException(new VectorClock(prevTS), new VectorClock(valueTS));
        debug("Operation is stable");
        operation.setStability(true);
        if (accounts.containsKey(account))
            throw new AccountAlreadyExistsException(account);
        this.valueTS.setTS(qualifier, this.valueTS.getTS(qualifier) + 1);
        debug("ValueTS changed to: " + this.valueTS.toString());
        accounts.put(account, 0);
    }

    public TransferToResponse transferOperation() throws ServerUnavailableException {
        if (!status)
            throw new ServerUnavailableException();
        return TransferToResponse.newBuilder().setTS(operationTS()).build();
    }

    public void transfer(String source, String destination, int amount, Timestamp prevTS, Timestamp valueTS) throws AccountNotFoundException, NotEnoughFundsException, InvalidAmountException {
        Operation operation = new TransferOp(source, destination, amount, new VectorClock(prevTS), new VectorClock(valueTS));
        debug("Operation added to ledger");
        ledger.add(operation);
        if (!this.valueTS.GreaterOrEqual(new VectorClock(prevTS)))
            throw new TimestampAheadOfServerException(new VectorClock(prevTS), new VectorClock(valueTS));
        debug("Operation is stable");
        operation.setStability(true);
        if (!accounts.containsKey(source)) {
            throw new AccountNotFoundException(source);
        } else if (accounts.get(source) < amount) {
            throw new NotEnoughFundsException(amount, accounts.get(source));
        } else if (amount <= 0) {
            throw new InvalidAmountException();
        } else if (!accounts.containsKey(destination)) {
            throw new AccountNotFoundException(destination);
        }
        this.valueTS.setTS(qualifier, this.valueTS.getTS(qualifier) + 1);
        debug("ValueTS changed to: " + this.valueTS.toString());
        accounts.put(source, accounts.get(source) - amount);
        accounts.put(destination, accounts.get(destination) + amount);
    }

    public getLedgerStateResponse getLedgerState() {
        return getLedgerStateResponse.newBuilder()
        .setLedgerState(LedgerState.newBuilder().addAllLedger(ledger.stream().map(Operation::proto).collect(Collectors.toList())).build())
        .build();
    }

    public PropagateStateResponse propagateState(LedgerState state, Timestamp replicaTS) {
        for (DistLedgerCommonDefinitions.Operation protoOperation: state.getLedgerList()) {
            debug(new VectorClock(protoOperation.getTS()) + " >= " + this.replicaTS);
            if (new VectorClock(protoOperation.getTS()).existsIn(this.ledger)) {
                continue;
            }
            if (new VectorClock(protoOperation.getTS()).GreaterOrEqual(this.replicaTS)) {
                switch (protoOperation.getType()) {
                    case OP_TRANSFER_TO:
                        TransferOp transfer = new TransferOp(protoOperation.getUserId(), protoOperation.getDestUserId(), protoOperation.getAmount(), new VectorClock(protoOperation.getPrevTS()), new VectorClock(protoOperation.getTS()));
                        debug("Operation added to ledger");
                        ledger.add(transfer);
                        if (!this.valueTS.GreaterOrEqual(transfer.getPrevTS()))
                            continue;
                        debug("Operation is stable");
                        transfer.setStability(true);
                        if (accounts.containsKey(transfer.getAccount()) && accounts.get(transfer.getAccount()) >= transfer.getAmount() && transfer.getAmount() > 0 && accounts.containsKey(transfer.getDestAccount())) {
                            Integer index = transfer.getTS().getTimestampDifference(transfer.getPrevTS());
                            this.valueTS.setTSAtIndex(index, transfer.getTS().getTSAtIndex(index));
                            debug("ValueTS changed to: " + valueTS.toString());
                            accounts.put(transfer.getAccount(), accounts.get(transfer.getAccount()) - transfer.getAmount());
                            accounts.put(transfer.getDestAccount(), accounts.get(transfer.getDestAccount()) + transfer.getAmount());
                        }
                        break;
                    case OP_CREATE_ACCOUNT:
                        CreateOp create = new CreateOp(protoOperation.getUserId(), new VectorClock(protoOperation.getPrevTS()), new VectorClock(protoOperation.getTS()));
                        debug("Operation added to ledger");
                        ledger.add(create);
                        if (!this.valueTS.GreaterOrEqual(create.getPrevTS()))
                            continue;
                        debug("Operation is stable");
                        create.setStability(true);
                        if (!accounts.containsKey(create.getAccount())) {
                            Integer createIndex = create.getTS().getTimestampDifference(create.getPrevTS());
                            this.valueTS.setTSAtIndex(createIndex, create.getTS().getTSAtIndex(createIndex));
                            debug("ValueTS changed to: " + valueTS.toString());
                            accounts.put(create.getAccount(), 0);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        for (Operation operation: this.ledger) {
            debug(valueTS + " >= " + operation.getPrevTS());
            if (valueTS.GreaterOrEqual(operation.getPrevTS()) && (!operation.getStability())) {
                debug("Operation is stable");
                operation.setStability(true);
                switch (operation.getOperationType()) {
                    case OP_CREATE_ACCOUNT:
                        if (!accounts.containsKey(operation.getAccount())) {
                            Integer index = operation.getTS().getTimestampDifference(operation.getPrevTS());
                            this.valueTS.setTSAtIndex(index, operation.getTS().getTSAtIndex(index));
                            debug("ValueTS changed to: " + valueTS.toString());
                            accounts.put(operation.getAccount(), 0);
                        }
                        break;
                    case OP_TRANSFER_TO:
                        TransferOp transfer = (TransferOp) operation;
                        if (accounts.containsKey(transfer.getAccount()) && accounts.get(transfer.getAccount()) >= transfer.getAmount() && transfer.getAmount() > 0 && accounts.containsKey(transfer.getDestAccount())) {
                            Integer index = operation.getTS().getTimestampDifference(operation.getPrevTS());
                            this.valueTS.setTSAtIndex(index, operation.getTS().getTSAtIndex(index));
                            debug("ValueTS changed to: " + valueTS.toString());
                            accounts.put(transfer.getAccount(), accounts.get(transfer.getAccount()) - transfer.getAmount());
                            accounts.put(transfer.getDestAccount(), accounts.get(transfer.getDestAccount()) + transfer.getAmount());
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        this.replicaTS.merge(new VectorClock(replicaTS));
        debug("ReplicaTS changed to: " + this.replicaTS.toString());
        return PropagateStateResponse.getDefaultInstance();
    }

    public void gossip(String qualifier) {
        LedgerState state = LedgerState.newBuilder().addAllLedger(ledger.stream().map(Operation::proto).collect(Collectors.toList())).build();
        crossServerService.propagateState(state, replicaTS.proto(), qualifier);
    }
    
    public IncreaseServerNumberResponse increaseServerNumber() {
        replicaTS.increaseClockSize();
        valueTS.increaseClockSize();
        debug("replicaTS changed to: " + this.replicaTS.toString());
        debug("ValueTS changed to: " + this.valueTS.toString());
        for (Operation operation: this.ledger) {
            operation.getPrevTS().getTimestamps().add(0);
            operation.getTS().getTimestamps().add(0);
        }
        return IncreaseServerNumberResponse.getDefaultInstance();
    }
}
