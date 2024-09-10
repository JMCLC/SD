package pt.tecnico.distledger.server;

import pt.ulisboa.tecnico.distledger.contract.distledgerserver.DistLedgerCrossServerServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.CrossServerDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.LedgerState;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions.Timestamp;
import io.grpc.StatusRuntimeException;
import io.grpc.ManagedChannelBuilder;
import io.grpc.ManagedChannel;
import java.util.ArrayList;
import java.util.List;

public class CrossServerService {

    private List<DistLedgerCrossServerServiceGrpc.DistLedgerCrossServerServiceBlockingStub> stubs;
    private NamingServerService namingServer;
    private ManagedChannel channel;
    private final String serviceName;
    
    public CrossServerService(NamingServerService namingServer, String serviceName) {
        this.namingServer = namingServer;
        this.serviceName = serviceName;
        this.stubs = new ArrayList<>();
    }

    public ManagedChannel getChannel() {
        return channel;
    }

    public NamingServerService getNamingServer() {
        return namingServer;
    }

    public void createConnection(String target) {
        this.channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        stubs.add(DistLedgerCrossServerServiceGrpc.newBlockingStub(channel));
    }

    public void propagateState(LedgerState state, Timestamp replicaTS, String qualifier) {
        for (String address: namingServer.lookup(serviceName, qualifier)) {
            createConnection(address);
        }
        PropagateStateRequest request = PropagateStateRequest.newBuilder().setState(state).setReplicaTS(replicaTS).build();
        try {
            PropagateStateResponse response = stubs.get(0).propagateState(request);
            this.stubs = new ArrayList<>();
        } catch (StatusRuntimeException e) {
            throw(e);
        }
    }

    public void increaseServerNumber() {
        for (String address: namingServer.lookup(serviceName, "")) {
            createConnection(address);
        }
        IncreaseServerNumberRequest request = IncreaseServerNumberRequest.getDefaultInstance();
        try {
            for (int i = 0; i < stubs.size(); i++) {
                IncreaseServerNumberResponse response = stubs.get(i).increaseServerNumber(request);
            }
            this.stubs = new ArrayList<>();
        } catch (StatusRuntimeException e) {
            throw(e);
        }
    }
}
