package main.java.redis;

import java.util.concurrent.ConcurrentHashMap;

public class CommandDispatcher {

    private static ConcurrentHashMap<String,String> store;
    private final static String PONG = "+PONG";
    private final static String PING = "PING";
    private final static String ECHO = "ECHO";
    private final static String SET = "SET";
    private final static String GET = "GET";
    private final static String ACK = "+OK";
    private final static String UNKNOWN_CMD = "-ERR unknown command";
    private final static String WRONG_NUM_ARGS = "-ERR wrong number of arguments";


    public CommandDispatcher() {
        store = new ConcurrentHashMap<>();
    }

    public String handleCommands(String command){

        String [] args = parseCommand(command);

        return switch (command) {
            case PING -> PONG;
            case ECHO -> echo(args);
            case SET -> set(args);
            case GET -> get(args);
            default -> UNKNOWN_CMD;
        };
    }

    private String echo(String [] args) {
        return  "+" + String.join(" ", args);
    }

    private String set(String [] args) {

        if( args.length!= 2){
            return WRONG_NUM_ARGS;
        }
        store.put(args[0], args[1]);
        return  ACK;
    }

    private String get(String [] args) {
        if (args.length != 1 ) {return  WRONG_NUM_ARGS;}
        return  "+" + store.getOrDefault(args[0], "nil");
    }

        private String[] parseCommand(String command) {
        return command.trim().split("\\s+");
    }



}
