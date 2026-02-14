package main.java.redis.command;

import main.java.redis.values.RedisValue;

import java.util.concurrent.ConcurrentHashMap;

import static main.java.redis.command.ListCommandDispatcher.lPush;
import static main.java.redis.command.ListCommandDispatcher.rPush;
import static main.java.redis.command.ValueCommandDispatcher.set;
import static main.java.redis.command.ValueCommandDispatcher.get;
import static main.java.redis.command.ValueCommandDispatcher.expire;


public class CommandDispatcher {

    private static ConcurrentHashMap<String, RedisValue> store;

    private final static String PONG = "+PONG";
    private final static String UNKNOWN_CMD = "-ERR unknown command";


    public CommandDispatcher() {
        store = new ConcurrentHashMap<>();
    }

    public String handleCommands(String command){

        String [] args = parseCommand(command);
        Command cmd = Command.fromString(args[0]);


        return switch (cmd) {
            case PING -> PONG;
            case ECHO -> echo(args);
            case SET -> set(args, store);
            case GET -> get(args, store);
            case EXPIRE -> expire(args,store);
            case RPUSH -> rPush(args, store);
            case LPUSH -> lPush(args,store);
            default -> UNKNOWN_CMD;
        };
    }

    private String echo(String [] args) {
        return  "+" + String.join(" ", args);
    }

    private String[] parseCommand(String command) {
        return command.trim().split("\\s+");
    }


}
