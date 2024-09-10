package pt.tecnico.distledger.server;

import pt.tecnico.distledger.server.domain.ServerState;
import io.grpc.StatusRuntimeException;
import io.grpc.BindableService;
import io.grpc.ServerBuilder;
import java.io.IOException;
import io.grpc.Server;

public class ServerMain {
    private static final boolean DEBUG_FLAG = (System.getProperty("debug") != null);
    public static void main(String[] args) throws IOException, InterruptedException {
        final String serviceName = "DistLedger";
        final String host = "localhost";
        final Integer namingServerPort = 5001;
        final int port = Integer.parseInt(args[0]);
        final String qualifier = args[1];
        try {
            NamingServerService namingServer = new NamingServerService(host, namingServerPort);
            Server server = null;
            CrossServerService crossServerService = new CrossServerService(namingServer, serviceName);
            ServerState state = new ServerState(qualifier, crossServerService);
            if (qualifier.charAt(0) == 'C') {
                crossServerService.increaseServerNumber();
                state.increaseServerNumber();
            }
            namingServer.register(serviceName, host + ":" + port, qualifier);
            final BindableService crossServerImpl = (BindableService) new CrossServerServiceImpl(state, DEBUG_FLAG);
            final BindableService userImpl = (BindableService) new UserServiceImpl(state, DEBUG_FLAG);
            final BindableService adminImpl = (BindableService) new AdminServiceImpl(state, DEBUG_FLAG);
            server = ServerBuilder.forPort(port).addService(userImpl).addService(adminImpl).addService(crossServerImpl).build();
            server.start();
            System.out.println("Server started");
            server.awaitTermination();
            namingServer.delete(serviceName, host + ":" + port, qualifier);
        } catch (StatusRuntimeException e) {
            System.out.println(e.getMessage());
        }
    }
}

