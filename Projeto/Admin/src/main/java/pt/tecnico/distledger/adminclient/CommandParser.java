package pt.tecnico.distledger.adminclient;

import pt.tecnico.distledger.adminclient.grpc.AdminService;
import io.grpc.StatusRuntimeException;
import java.util.Scanner;
import java.util.List;

public class CommandParser {

    private static final String SPACE = " ";
    private static final String ACTIVATE = "activate";
    private static final String DEACTIVATE = "deactivate";
    private static final String GET_LEDGER_STATE = "getLedgerState";
    private static final String GOSSIP = "gossip";
    private static final String HELP = "help";
    private static final String EXIT = "exit";


	private static final boolean DEBUG_FLAG = (System.getProperty("debug") != null);

	private static void debug(String debugMessage) {
		if (DEBUG_FLAG)
			System.err.println(debugMessage);
    }
    
    private final AdminService adminService;
    public CommandParser(AdminService adminService) {
        this.adminService = adminService;
    }
    void parseInput() {

        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.print("> ");
            String line = scanner.nextLine().trim();
            String cmd = line.split(SPACE)[0];
            debug("Command entered: " + line); 

            try{ 
                switch (cmd) {
                    case ACTIVATE:
                        this.activate(line);
                        break;

                    case DEACTIVATE:
                        this.deactivate(line);
                        break;

                    case GET_LEDGER_STATE:
                        this.dump(line);
                        break;

                    case GOSSIP:
                        this.gossip(line);
                        break;

                    case HELP:
                        this.printUsage();
                        break;

                    case EXIT:
                        exit = true;
                        break;

                    default:
                        break;
                }
            }
            catch (Exception e){
            System.err.println(e.getMessage());

            }
        }
    }


    private void activate(String line){
        String[] split = line.split(SPACE);

        if (split.length != 2){
            this.printUsage();
            return;
        }
        String server = split[1];
        try{ 
        debug("Activating server: " + server);
        adminService.activate(server);
        }
        catch(Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void deactivate(String line){
        String[] split = line.split(SPACE);

        if (split.length != 2){
            this.printUsage();
            return;
        }
        String server = split[1];
        try{ 
        debug("Deactivating server: " + server); 
        adminService.deactivate(server);
        }
        catch(Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void dump(String line){
        String[] split = line.split(SPACE);

        if (split.length != 2){
            this.printUsage();
            return;
        }
        String server = split[1];
        try{ 
        debug("Getting ledger state for server: " + server);
        adminService.getLedgerState(server);
        }
        catch(Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void gossip(String line){
        String[] split = line.split(SPACE);

        if (split.length != 3){
            this.printUsage();
            return;
        }
        String server = split[1];
        String destination = split[2];
        try {
            adminService.gossip(server, destination);
        } catch(Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void printUsage() {
        System.out.println("Usage:\n" +
                "- activate <server>\n" +
                "- deactivate <server>\n" +
                "- getLedgerState <server>\n" +
                "- gossip <server> <destination>\n" +
                "- exit\n");
    }
}
