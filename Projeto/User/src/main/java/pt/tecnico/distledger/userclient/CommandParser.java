package pt.tecnico.distledger.userclient;

import pt.tecnico.distledger.userclient.grpc.UserService;
import pt.tecnico.distledger.userclient.grpc.VectorClock;

import java.util.Scanner;

import io.grpc.StatusRuntimeException;

import java.util.HashMap;

public class CommandParser {

    private static final String SPACE = " ";
    private static final String CREATE_ACCOUNT = "createAccount";
    private static final String TRANSFER_TO = "transferTo";
    private static final String BALANCE = "balance";
    private static final String HELP = "help";
    private static final String EXIT = "exit";

    private final UserService userService;

    private static boolean DEBUG_FLAG = (System.getProperty("debug") != null);

    /** Helper method to print debug messages. */
    private static void debug(String debugMessage) {
        if (DEBUG_FLAG)
            System.err.println(debugMessage);
    }

    public CommandParser(UserService userService) {
        this.userService = userService;
    }

    void parseInput() {

        Scanner scanner = new Scanner(System.in);
        debug("Created scanner");
        boolean exit = false;
        while (!exit) {
            System.out.print("> ");
            String line = scanner.nextLine().trim();
            debug(String.format("line: %s", line));
            String cmd = line.split(SPACE)[0];
            debug(String.format("cmd: %s", cmd));

            try{
                switch (cmd) {
                    case CREATE_ACCOUNT:
                        this.createAccount(line);
                        break;

                    case TRANSFER_TO:
                        this.transferTo(line);
                        break;

                    case BALANCE:
                        this.balance(line);
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

    private void createAccount(String line){
        debug("createAccount");
        String[] split = line.split(SPACE);

        if (split.length != 3){
            this.printUsage();
            return;
        }

        String server = split[1];
        String username = split[2];

        try{
            debug(String.format("Creating account with name: %s", username));
            VectorClock TS = userService.createAccount(server,username);
            debug(TS.toString());
            System.out.println("OK");
        }
        catch(Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    private int balance(String line){
        debug("balance");
        String[] split = line.split(SPACE);
        int value = 0;

        if (split.length != 3){
            this.printUsage();
            return 0;
        }
        String server = split[1];
        String username = split[2];

        try{
            debug(String.format("Balance of account with name: %s", username));
            value = userService.balance(server,username);
            System.out.println("OK");
            System.out.println(value);
        }
        catch(Exception e){
            System.out.println("Error: " + e.getMessage());
        }
        return value;
    }

    private void transferTo(String line){
        debug("transferTo");
        String[] split = line.split(SPACE);

        if (split.length != 5){
            this.printUsage();
            return;
        }
        String server = split[1];
        String from = split[2];
        String dest = split[3];
        Integer amount = Integer.valueOf(split[4]);

        try{
            debug(String.format("Transfering %d from %s to %s", amount,from,dest));
            VectorClock TS = userService.transferTo(server,from,dest,amount);
            debug(TS.toString());
            System.out.println("OK");
        }
        catch(Exception e){
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void printUsage() {
        System.out.println("Usage:\n" +
                        "- createAccount <server> <username>\n" +
                        "- balance <server> <username>\n" +
                        "- transferTo <server> <username_from> <username_to> <amount>\n" +
                        "- exit\n");
    }
}