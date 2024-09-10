package pt.tecnico.distledger.namingserver;

import pt.tecnico.distledger.namingserver.domain.exceptions.ServerAlreadyExistsException;
import pt.tecnico.distledger.namingserver.domain.exceptions.ServiceNotFoundException;
import pt.tecnico.distledger.namingserver.domain.exceptions.ServerNotFoundException;
import pt.ulisboa.tecnico.distledger.contract.namingserver.NamingServerServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.namingserver.NamingServer.*;
import pt.tecnico.distledger.namingserver.domain.NamingServerState;
import static io.grpc.Status.INVALID_ARGUMENT;
import io.grpc.stub.StreamObserver;

public class NamingServerServiceImpl extends NamingServerServiceGrpc.NamingServerServiceImplBase {
    private NamingServerState state;
    private boolean debug;

    public NamingServerServiceImpl(NamingServerState state, boolean debug) {
        this.state = state;
        this.debug = debug;
    }

    private void debug(String debugMessage) {
		if (debug)
			System.err.println(debugMessage);
	}

    @Override
    public void register(RegisterRequest request, StreamObserver<RegisterResponse> responseObserver) {
        debug("Received register request for service: " + request.getServiceName() + ", address: " + request.getServerAddress() + " and qualifier: " + request.getQualifier() + ".");
        try {
            debug("Sending Response.");
            RegisterResponse response = state.register(request.getServiceName(), request.getQualifier(), request.getServerAddress());
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            debug("Request Handled.");      
        } catch (ServerAlreadyExistsException e) {
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void delete(DeleteRequest request, StreamObserver<DeleteResponse> responseObserver) {
        debug("Received delete request for service: " + request.getServiceName() + ", address: " + request.getServerAddress() + " and qualifier: " + request.getQualifier() + ".");
        try {
            debug("Sending Response.");
            DeleteResponse response = state.delete(request.getServiceName(), request.getQualifier(), request.getServerAddress());
            responseObserver.onNext(response);
            responseObserver.onCompleted();     
            debug("Request Handled.");      
        } catch(ServiceNotFoundException | ServerNotFoundException e){
            responseObserver.onError(INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void lookup(LookupRequest request, StreamObserver<LookupResponse> responseObserver){
        debug("Received lookup request for service: " + request.getServiceName() + " and qualifier: " + request.getQualifier() + ".");
        debug("Sending Response.");
        LookupResponse response = state.lookup(request.getServiceName(), request.getQualifier());
        responseObserver.onNext(response);
        responseObserver.onCompleted();
        debug("Request Handled."); 
    }
}