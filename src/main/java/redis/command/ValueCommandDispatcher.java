package main.java.redis.command;

import main.java.redis.values.RedisValue;
import main.java.redis.values.ValueWithExpiry;

import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Long.parseLong;

public class ValueCommandDispatcher {

    private final static long MILLIS_PER_SECOND = 1000; // time to live in ms
    private final static String VALUE_WRONG_NUM_ARGS = "-ERR wrong number of arguments";
    private final static String ACK = "+OK";
    private final static String NIL = "+NIL";


    public static String set(String[] args, ConcurrentHashMap<String, RedisValue> store) {
        if (args.length != 3) return "-ERR wrong number of arguments";
        store.put(args[1], new ValueWithExpiry(args[2]));
        return ACK;
    }

    public static String get(String[] args, ConcurrentHashMap<String, RedisValue> store) {
        if (args.length != 2) return "-ERR wrong number of arguments";
        RedisValue val = getValueWithExpiry(args[1], store);
        return val == null ? NIL : "+" + val.getValue();
    }

    public static RedisValue getValueWithExpiry(String k,ConcurrentHashMap<String, RedisValue> store) {
        RedisValue val = store.get(k);
        if (val == null || val.isExpired()) {
            store.remove(k);
            return null;
        }
        return val;
    }

    public static String expire(String [] args, ConcurrentHashMap<String, RedisValue> store ){
        if( args.length<4){
            return VALUE_WRONG_NUM_ARGS;
        }
        long ttl = System.currentTimeMillis() + parseLong(args[3]) * MILLIS_PER_SECOND;
        RedisValue val = getValueWithExpiry(args[2],store);
        if (val == null) return "0";
        val.setTtl(ttl);
        return "1";
    }
}
