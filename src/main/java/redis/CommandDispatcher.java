package main.java.redis;

import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Long.parseLong;

public class CommandDispatcher {

    private static ConcurrentHashMap<String,ValueWithExpiry> store;

    private final static long MILLIS_PER_SECOND = 1000; // time to live in ms

    private final static String PONG = "+PONG";
    private final static String PING = "PING";
    private final static String ECHO = "ECHO";
    private final static String SET = "SET";
    private final static String GET = "GET";
    private final static String EXPIRE = "EXPIRE";
    private final static String ACK = "+OK";
    private final static String NIL = "+NIL";
    private final static String UNKNOWN_CMD = "-ERR unknown command";
    private final static String WRONG_NUM_ARGS = "-ERR wrong number of arguments";


    public CommandDispatcher() {
        store = new ConcurrentHashMap<>();
    }

    public String handleCommands(String command){

        String [] args = parseCommand(command);

        return switch (args[0]) {
            case PING -> PONG;
            case ECHO -> echo(args);
            case SET -> set(args);
            case GET -> get(args);
            case EXPIRE -> expire(args);
            default -> UNKNOWN_CMD;
        };
    }

    private String echo(String [] args) {
        return  "+" + String.join(" ", args);
    }

    private String set(String [] args) {

        if( args.length!= 3){
            return WRONG_NUM_ARGS;
        }
        store.put(args[1], new ValueWithExpiry(args[2]));
        return  ACK;
    }

    private String get(String [] args) {
        if (args.length != 2 ) {return  WRONG_NUM_ARGS;}
        ValueWithExpiry val = getValueWithExpiry(args[1]);
        if (val == null) return NIL;
        return "+" + val.getValue();
    }

    private static ValueWithExpiry getValueWithExpiry(String k) {
        ValueWithExpiry val = store.get(k);
        if (val == null || val.isExpired()) {
            store.remove(k);
            return null;
        }
        return val;
    }

    private String[] parseCommand(String command) {
        return command.trim().split("\\s+");
    }

    private String expire(String [] args){
        if( args.length<4){
            return WRONG_NUM_ARGS;
        }
        long ttl = System.currentTimeMillis() + parseLong(args[3]) * MILLIS_PER_SECOND;
        ValueWithExpiry val = getValueWithExpiry(args[2]);
        if (val == null) return "0";
        val.setTtl(ttl);
        return "1";
    }

}
