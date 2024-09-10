package pt.tecnico.distledger.namingserver.domain;

public class ServerEntry {
    String address;
    String qualifier;

    public ServerEntry(String qualifier, String address) {
        this.address = address;
        this.qualifier = qualifier;
    }

    public String getAddress() {
        return address;
    }

    public String getQualifier() {
        return qualifier;
    }
}