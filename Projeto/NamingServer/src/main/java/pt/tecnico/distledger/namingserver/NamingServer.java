package pt.tecnico.distledger.namingserver;

import pt.tecnico.distledger.namingserver.domain.NamingServerState;
import io.grpc.BindableService;
import io.grpc.ServerBuilder;
import java.io.IOException;
import io.grpc.Server;

public class NamingServer {
    private static final boolean DEBUG_FLAG = (System.getProperty("debug") != null);
    public static void main(String[] args) {
        final int port = Integer.parseInt(args[0]);
        NamingServerState state = new NamingServerState();
        final BindableService namingServiceImpl = (BindableService) new NamingServerServiceImpl(state, DEBUG_FLAG);
        Server server = null;
        try {
            server = ServerBuilder.forPort(port).addService(namingServiceImpl).build();        
            server.start();
            System.out.println("Server started");
            System.out.println();
            System.out.println("Press enter to shutdown");
            System.in.read();
            server.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (server != null) {
                server.shutdown();
            }
        }
    }
}
