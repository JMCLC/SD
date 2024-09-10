package pt.tecnico.distledger.server;

import pt.tecnico.distledger.server.domain.exceptions.TimestampAheadOfServerException;
import pt.tecnico.distledger.server.domain.exceptions.AccountAlreadyExistsException;
import pt.tecnico.distledger.server.domain.exceptions.ServerUnavailableException;
import pt.tecnico.distledger.server.domain.exceptions.PermissionDeniedException;
import pt.tecnico.distledger.server.domain.exceptions.AccountNotFoundException;
import pt.tecnico.distledger.server.domain.exceptions.NotEnoughFundsException;
import pt.tecnico.distledger.server.domain.exceptions.InvalidAmountException;
import pt.ulisboa.tecnico.distledger.contract.user.UserDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.user.UserServiceGrpc;
import pt.tecnico.distledger.server.domain.ServerState;
import static io.grpc.Status.FAILED_PRECONDITION;
import static io.grpc.Status.PERMISSION_DENIED;
import static io.grpc.Status.INVALID_ARGUMENT;
import static io.grpc.Status.ALREADY_EXISTS;
import static io.grpc.Status.UNIMPLEMENTED;
import static io.grpc.Status.UNAVAILABLE;
import static io.grpc.Status.NOT_FOUND;
import io.grpc.stub.StreamObserver;

public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {
    private ServerState state;
    private final boolean debug;

    public UserServiceImpl(ServerState state, boolean debug) {
        this.state = state;
        this.debug = debug;
    }

    private void debug(String debugMessage) {
		if (debug)
			System.err.println(debugMessage);
	}

    @Override
    public void balance(BalanceRequest request, StreamObserver<BalanceResponse> responseObserver) {
        debug("Received balance request for account: " + request.getUserId() + ".");
        try {
            debug("Sending Response.");
            BalanceResponse response = state.getAccountBalance(request.getUserId(), request.getPrevTS());
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            debug("Request Handled.");
        } catch (ServerUnavailableException e) {
            debug(e.getMessage());
            responseObserver.onError(UNAVAILABLE.withDescription(e.getMessage()).asRuntimeException());
        } catch (AccountNotFoundException e) {
            debug(e.getMessage());
            responseObserver.onError(NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void createAccount(CreateAccountRequest request, StreamObserver<CreateAccountResponse> responseObserver) {
        debug("Received createAccount request with userId: " + request.getUserId());
        try {
            debug("Sending Response.");
            CreateAccountResponse response = state.createAccountOperation();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            debug("Request Handled.");
            state.createAccount(request.getUserId(), request.getPrevTS(), response.getTS());
        } catch (ServerUnavailableException e) {
            debug(e.getMessage());
            // responseObserver.onError(UNAVAILABLE.withDescription(e.getMessage()).asRuntimeException());
        } catch (TimestampAheadOfServerException e) {
            debug(e.getMessage());
            // responseObserver.onError(UNIMPLEMENTED.withDescription(e.getMessage()).asRuntimeException());
        } catch (AccountAlreadyExistsException e) {
            debug(e.getMessage());
            // responseObserver.onError(ALREADY_EXISTS.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override    
    public void transferTo(TransferToRequest request, StreamObserver<TransferToResponse> responseObserver) {
        debug("Received transferTo request from account: " + request.getAccountFrom() + " to account: " + request.getAccountTo() + " for an amount of: " + request.getAmount());
        try {
            debug("Sending Response.");
            TransferToResponse response = state.transferOperation();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            debug("Request Handled.");
            state.transfer(request.getAccountFrom(), request.getAccountTo(), request.getAmount(), request.getPrevTS(), response.getTS());
        } catch (ServerUnavailableException e) {
            debug(e.getMessage());
            // responseObserver.onError(UNAVAILABLE.withDescription(e.getMessage()).asRuntimeException());
        } catch (AccountNotFoundException e) {
            debug(e.getMessage());
            // responseObserver.onError(NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        } catch (NotEnoughFundsException e) {
            debug(e.getMessage());
            // responseObserver.onError(FAILED_PRECONDITION.withDescription(e.getMessage()).asRuntimeException());
        } catch (InvalidAmountException e) {
            debug(e.getMessage());
            // responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        }
    }
}