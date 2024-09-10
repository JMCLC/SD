package pt.tecnico.distledger.adminclient.grpc;

import java.util.HashMap;
import java.util.Map;

import com.google.protobuf.Timestamp;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.namingserver.NamingServer.*;
import pt.ulisboa.tecnico.distledger.contract.namingserver.NamingServerServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;
import io.grpc.StatusRuntimeException;

public class AdminService {

    private final String host;
    private final int port;
    private final ManagedChannel channel;
    NamingServerServiceGrpc.NamingServerServiceBlockingStub stub;
    private Map<String,AdminServiceGrpc.AdminServiceBlockingStub> stubs;

    public AdminService(String host, int port) {
        this.host = host;
        this.port = port;
        this.stubs = new HashMap<>();

        channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        stub = NamingServerServiceGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown();
    }

    public String lookup(String serviceName, String qualifier) {
        LookupRequest request = LookupRequest.newBuilder()
            .setServiceName(serviceName)
            .setQualifier(qualifier)
            .build();
        LookupResponse response = stub.lookup(request);

        return response.getServers(0);
    }

    public void activate(String qualifier) throws StatusRuntimeException{
        try{
            if (!stubs.containsKey(qualifier)){
                String response = lookup("DistLedger",qualifier);
                ManagedChannel channel = ManagedChannelBuilder.forTarget(response).usePlaintext().build();
                stubs.put(qualifier,AdminServiceGrpc.newBlockingStub(channel));
            }
            ActivateRequest request = ActivateRequest.newBuilder().build();
            ActivateResponse response = stubs.get(qualifier).activate(request);
            System.out.println("OK");
        }
        catch(StatusRuntimeException e){
            throw(e);
        }
        
    }

    public void deactivate(String qualifier) throws StatusRuntimeException{
        try{
            if (!stubs.containsKey(qualifier)){
                String response = lookup("DistLedger",qualifier);
                ManagedChannel channel = ManagedChannelBuilder.forTarget(response).usePlaintext().build();
                stubs.put(qualifier,AdminServiceGrpc.newBlockingStub(channel));
            }
            DeactivateRequest request = DeactivateRequest.newBuilder().build();
            DeactivateResponse response = stubs.get(qualifier).deactivate(request);
            System.out.println("OK");
        }
        catch(StatusRuntimeException e){
            throw(e);
        }
    }

    public void getLedgerState(String qualifier) throws StatusRuntimeException {
        try{
            if (!stubs.containsKey(qualifier)){
                String response = lookup("DistLedger",qualifier);
                ManagedChannel channel = ManagedChannelBuilder.forTarget(response).usePlaintext().build();
                stubs.put(qualifier,AdminServiceGrpc.newBlockingStub(channel));
            }
            getLedgerStateRequest request = getLedgerStateRequest.newBuilder().build();
            getLedgerStateResponse response = stubs.get(qualifier).getLedgerState(request);
            System.out.println("OK");
            System.out.println("ledgerState {");
            for (DistLedgerCommonDefinitions.Operation op : response.getLedgerState().getLedgerList()) {
                System.out.println("  ledger {");
                System.out.println("    type: " + op.getType());
                System.out.println("    userId: \"" + op.getUserId() + "\"");
                if (op.getType() == DistLedgerCommonDefinitions.OperationType.OP_TRANSFER_TO) {
                    System.out.println("    destUserId: \"" + op.getDestUserId() + "\"");
                    System.out.println("    amount: " + op.getAmount());
                }
                System.out.println("    prevTS: " + op.getPrevTS().getTSList().toString());
                System.out.println("    TS: " + op.getTS().getTSList().toString());
                System.out.println("  }");
            }
            System.out.println("}");
        } catch(StatusRuntimeException e){
            throw(e);
        }
    }

    public void gossip(String qualifier, String destination) throws StatusRuntimeException {
        try{
            if(!stubs.containsKey(qualifier)){
                String response = lookup("DistLedger",qualifier);
                ManagedChannel channel = ManagedChannelBuilder.forTarget(response).usePlaintext().build();
                stubs.put(qualifier,AdminServiceGrpc.newBlockingStub(channel));
            }
            GossipRequest request = GossipRequest.newBuilder().setQualifier(destination).build();
            GossipResponse response = stubs.get(qualifier).gossip(request);
            System.out.println("OK");
        } catch(StatusRuntimeException e) {
            throw(e);
        }
    }
}

