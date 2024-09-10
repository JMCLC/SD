package pt.tecnico.distledger.namingserver.domain;

import pt.tecnico.distledger.namingserver.domain.exceptions.ServerAlreadyExistsException;
import pt.tecnico.distledger.namingserver.domain.exceptions.ServerNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class ServiceEntry {
    String name;
    List<ServerEntry> entries = new ArrayList<>();

    public ServiceEntry(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addServer(String qualifier, String address) throws ServerAlreadyExistsException {
        for (ServerEntry sv: this.entries) {
            if (sv.getAddress().equals(address)) {
                throw new ServerAlreadyExistsException();
            }
        }
        entries.add(new ServerEntry(qualifier, address));
    }

    public void deleteServer(String qualifier,String address) throws ServerNotFoundException {
        if (!entries.contains(new ServerEntry(qualifier,  address))) {
            throw new ServerNotFoundException(address, qualifier);
        }
        for(ServerEntry entry : entries){
            if (entry.getAddress().equals(address) && entry.getQualifier().equals(qualifier)) {
                entries.remove(entry);
                break;
            }
        }
    }

    public List<String> lookup(String qualifier) {
        List<String> servers = new ArrayList<>();
        for (ServerEntry entry: entries) {
            if (entry.getQualifier().equals(qualifier) || qualifier.isEmpty()) {
                servers.add(entry.getAddress());
            }
        }
        return servers;
    }
}