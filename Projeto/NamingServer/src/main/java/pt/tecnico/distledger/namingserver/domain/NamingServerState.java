package pt.tecnico.distledger.namingserver.domain;

import pt.tecnico.distledger.namingserver.domain.exceptions.ServerAlreadyExistsException;
import pt.tecnico.distledger.namingserver.domain.exceptions.ServiceNotFoundException;
import pt.tecnico.distledger.namingserver.domain.exceptions.ServerNotFoundException;
import pt.ulisboa.tecnico.distledger.contract.namingserver.NamingServer.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.List;

public class NamingServerState {
    private ConcurrentHashMap<String, ServiceEntry> service = new ConcurrentHashMap<>();

    public NamingServerState() {}

    public RegisterResponse register(String serviceName, String qualifier, String address) throws ServerAlreadyExistsException {
        service.putIfAbsent(serviceName, new ServiceEntry(serviceName));
        service.get(serviceName).addServer(qualifier, address);
        return RegisterResponse.getDefaultInstance();
    }

    public DeleteResponse delete(String serviceName, String qualifier, String address) throws ServiceNotFoundException, ServerNotFoundException {
        if(!service.containsKey(serviceName)) {
            throw new ServiceNotFoundException(serviceName);
        }
        service.get(serviceName).deleteServer(qualifier, address);
        return DeleteResponse.getDefaultInstance();
    }
    
    public LookupResponse lookup(String serviceName,String qualifier) {
        List<String> servers = new ArrayList<>();
        if (service.containsKey(serviceName)) {
            servers = service.get(serviceName).lookup(qualifier);
        }
        return LookupResponse.newBuilder().addAllServers(servers).build();
    }
}
