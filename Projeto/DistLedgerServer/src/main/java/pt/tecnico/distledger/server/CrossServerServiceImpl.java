package pt.tecnico.distledger.server;

import pt.ulisboa.tecnico.distledger.contract.distledgerserver.DistLedgerCrossServerServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.CrossServerDistLedger.*;
import pt.tecnico.distledger.server.domain.exceptions.ServerUnavailableException;
import pt.tecnico.distledger.server.domain.ServerState;
import static io.grpc.Status.UNAVAILABLE;
import io.grpc.stub.StreamObserver;

public class CrossServerServiceImpl extends DistLedgerCrossServerServiceGrpc.DistLedgerCrossServerServiceImplBase {
    private ServerState state;
    private final boolean debug;

    public CrossServerServiceImpl(ServerState state, boolean debug) {
        this.state = state;
        this.debug = debug;
    }

    private void debug(String debugMessage) {
		if (debug)
			System.err.println(debugMessage);
	}

    @Override
    public void propagateState(PropagateStateRequest request, StreamObserver<PropagateStateResponse> responseObserver) {
        debug("Received propagateState.");
        try {
            debug("Sending Response.");
            PropagateStateResponse response = state.propagateState(request.getState(), request.getReplicaTS());
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            debug("Request Handled.");
        } catch (ServerUnavailableException e) {
            debug(e.getMessage());
            responseObserver.onError(UNAVAILABLE.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void increaseServerNumber(IncreaseServerNumberRequest request, StreamObserver<IncreaseServerNumberResponse> responseObserver) {
        debug("Received a request to increase the number of servers available.");
        try {
            debug("Sending Response.");
            IncreaseServerNumberResponse response = state.increaseServerNumber();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            debug("Request Handled.");
        } catch (ServerUnavailableException e) {
            debug(e.getMessage());
            responseObserver.onError(UNAVAILABLE.withDescription(e.getMessage()).asRuntimeException());
        }
    }
}
