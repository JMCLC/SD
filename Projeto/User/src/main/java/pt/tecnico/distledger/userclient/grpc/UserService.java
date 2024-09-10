package pt.tecnico.distledger.userclient.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.ulisboa.tecnico.distledger.contract.user.UserDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.user.UserServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.namingserver.NamingServer.*;
import pt.ulisboa.tecnico.distledger.contract.namingserver.NamingServerServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;
import io.grpc.StatusRuntimeException;
import java.util.*;

public class UserService{

    ManagedChannel channel;
    NamingServerServiceGrpc.NamingServerServiceBlockingStub stub;
    private Map<String,UserServiceGrpc.UserServiceBlockingStub> stubs;
    VectorClock timestamps;

    public UserService(String host, int port){
        channel = ManagedChannelBuilder.forAddress(host,port).usePlaintext().build();
        stub = NamingServerServiceGrpc.newBlockingStub(channel);
        this.stubs = new HashMap<>();
        this.channel = channel;
        this.stub = stub;
        this.timestamps = new VectorClock();
    }

    private NamingServerServiceGrpc.NamingServerServiceBlockingStub getStub(){
        return this.stub;
    }

    private ManagedChannel getChannel(){
        return this.channel;
    }

    private VectorClock getTimestamps(){
        return this.timestamps;
    }

    public String lookup(String serviceName, String qualifier) {
        LookupRequest request = LookupRequest.newBuilder()
            .setServiceName(serviceName)
            .setQualifier(qualifier)
            .build();
        LookupResponse response = stub.lookup(request);

        return response.getServers(0);
    }

    public int balance(String qualifier,String username) throws StatusRuntimeException{
        try{
            if (!stubs.containsKey(qualifier)){
                String response = lookup("DistLedger",qualifier);
                ManagedChannel channel = ManagedChannelBuilder.forTarget(response).usePlaintext().build();
                stubs.put(qualifier,UserServiceGrpc.newBlockingStub(channel));
            }
            BalanceRequest request = BalanceRequest.newBuilder().setUserId(username).setPrevTS(timestamps.proto()).build();
            BalanceResponse response = stubs.get(qualifier).balance(request);
            timestamps.merge(new VectorClock(response.getValueTS()));

            return response.getValue();
        }
        catch(StatusRuntimeException e){
            throw(e);
        }
    }

    public VectorClock createAccount(String qualifier,String username) throws StatusRuntimeException{
        try{
            if (!stubs.containsKey(qualifier)){
                String response = lookup("DistLedger",qualifier);
                ManagedChannel channel = ManagedChannelBuilder.forTarget(response).usePlaintext().build();
                stubs.put(qualifier,UserServiceGrpc.newBlockingStub(channel));
            }
            CreateAccountRequest request = CreateAccountRequest.newBuilder().setUserId(username).setPrevTS(timestamps.proto()).build();
            CreateAccountResponse response = stubs.get(qualifier).createAccount(request);
            timestamps.merge(new VectorClock(response.getTS()));
            return timestamps;
        }
        catch(StatusRuntimeException e){
            throw(e);
        }
    }

    public VectorClock transferTo(String qualifier,String accountFrom, String accountTo, int amount) throws StatusRuntimeException{
        try{
            if (!stubs.containsKey(qualifier)){
                String response = lookup("DistLedger",qualifier);
                ManagedChannel channel = ManagedChannelBuilder.forTarget(response).usePlaintext().build();
                stubs.put(qualifier,UserServiceGrpc.newBlockingStub(channel));
            }
            TransferToRequest request = TransferToRequest.newBuilder()
            .setAccountFrom(accountFrom)
            .setAccountTo(accountTo)
            .setAmount(amount)
            .setPrevTS(timestamps.proto())
            .build();
            TransferToResponse response = stubs.get(qualifier).transferTo(request);
            timestamps.merge(new VectorClock(response.getTS()));
            return timestamps;
        }
        catch(StatusRuntimeException e){
            throw(e);
        }
    }
}