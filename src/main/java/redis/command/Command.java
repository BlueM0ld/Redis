package main.java.redis.command;

public enum Command {
    PING, ECHO, SET, GET, EXPIRE, RPUSH, LPUSH, UNKNOWN;

    public static Command fromString(String cmd) {
        try {
            return Command.valueOf(cmd);
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}