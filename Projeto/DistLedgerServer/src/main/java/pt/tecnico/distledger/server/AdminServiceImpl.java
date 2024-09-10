package pt.tecnico.distledger.server;

import pt.ulisboa.tecnico.distledger.contract.admin.AdminDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminServiceGrpc;
import pt.tecnico.distledger.server.domain.ServerState;
import io.grpc.stub.StreamObserver;

public class AdminServiceImpl extends AdminServiceGrpc.AdminServiceImplBase {
    private ServerState state;
    private final boolean debug;

    public AdminServiceImpl(ServerState state, boolean debug) {
        this.state = state;
        this.debug = debug;
    }

    private void debug(String debugMessage) {
		if (debug)
			System.err.println(debugMessage);
	}

    @Override
    public void activate(ActivateRequest request, StreamObserver<ActivateResponse> responseObserver) {
        debug("Activating Server.");
        debug("Sending Response.");
        ActivateResponse response = state.activateServer();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
        debug("Request Handled.");
    }

    @Override
    public void deactivate(DeactivateRequest request, StreamObserver<DeactivateResponse> responseObserver) {
        debug("Deactivating Server.");
        debug("Sending Response.");
        DeactivateResponse response = state.deactivateServer();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
        debug("Request Handled.");
    }

    @Override    
    public void getLedgerState(getLedgerStateRequest request, StreamObserver<getLedgerStateResponse> responseObserver) {
        debug("Received request for Ledger State.");
        debug("Sending Response.");
        getLedgerStateResponse response = state.getLedgerState();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
        debug("Request Handled.");
    }

    @Override
    public void gossip(GossipRequest request, StreamObserver<GossipResponse> responseObserver) {
        debug("Received request for gossip.");
        debug("Sending Response.");
        GossipResponse response = GossipResponse.getDefaultInstance();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
        state.gossip(request.getQualifier());
        debug("Request Handled");
    }
}