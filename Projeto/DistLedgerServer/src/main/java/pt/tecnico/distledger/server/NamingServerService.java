package pt.tecnico.distledger.server;

import pt.ulisboa.tecnico.distledger.contract.namingserver.NamingServerServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.namingserver.NamingServer.*;
import io.grpc.StatusRuntimeException;
import io.grpc.ManagedChannelBuilder;
import io.grpc.ManagedChannel;
import java.util.List;

public class NamingServerService {

    ManagedChannel channel;
    NamingServerServiceGrpc.NamingServerServiceBlockingStub stub;

    public NamingServerService(String host, int port) {
        this.channel = ManagedChannelBuilder.forAddress(host,port).usePlaintext().build();;
        this.stub = NamingServerServiceGrpc.newBlockingStub(channel);
    }

    public NamingServerServiceGrpc.NamingServerServiceBlockingStub getStub() {
        return stub;
    }

    public ManagedChannel getChannel() {
        return channel;
    }

    public void register(String serviceName, String address, String qualifier) {
        RegisterRequest request = RegisterRequest.newBuilder().setServiceName(serviceName).setQualifier(qualifier).setServerAddress(address).build();
        try {
            RegisterResponse response = this.getStub().register(request);
        } catch (StatusRuntimeException e) {
            throw(e);
        }
    }

    public void delete(String serviceName, String address, String qualifier) {
        DeleteRequest request = DeleteRequest.newBuilder().setServiceName(serviceName).setQualifier(qualifier).setServerAddress(address).build();
        try {
            DeleteResponse response = this.getStub().delete(request);
        } catch (StatusRuntimeException e) {
            throw(e);
        }
    }

    public List<String> lookup(String serviceName, String qualifier) {
        LookupRequest request;
        if (qualifier != "") {
            request = LookupRequest.newBuilder().setServiceName(serviceName).setQualifier(qualifier).build();
        } else {
            request = LookupRequest.newBuilder().setServiceName(serviceName).build();
        }
        try {
            LookupResponse response = this.getStub().lookup(request);
            return response.getServersList();
        } catch (StatusRuntimeException e) {
            throw(e);
        }
    }
}
