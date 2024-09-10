package pt.tecnico.distledger.namingserver.domain.exceptions;

public class ServiceNotFoundException extends IllegalArgumentException {
    
    public ServiceNotFoundException(String serviceName) {
        super("The service " + serviceName + " does not exist.");
    }
}
