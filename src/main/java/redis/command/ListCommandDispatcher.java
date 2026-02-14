package main.java.redis.command;

import main.java.redis.values.ListValue;
import main.java.redis.values.RedisValue;

import java.util.concurrent.ConcurrentHashMap;

import static main.java.redis.values.RedisValue.ERROR_CLASS_TYPE;

public class ListCommandDispatcher {

    private final static String LIST_WRONG_NUM_ARGS = "-ERR wrong number of arguments";
    private final static String ACK = "+OK";

    public static String lPush(String[] args, ConcurrentHashMap<String, RedisValue> store) {
        if( args.length!= 3){
            return LIST_WRONG_NUM_ARGS;
        }
        ListValue value = getOrCreateList(args[1], store);
        value.pushR(args[2]);
        return ACK;
    }

    public static String rPush(String[] args, ConcurrentHashMap<String, RedisValue> store) {
        if( args.length!= 3){
            return LIST_WRONG_NUM_ARGS;
        }
        ListValue value = getOrCreateList(args[1], store);
        value.pushL(args[2]);

        return ACK;
    }

    private static ListValue getOrCreateList(String key, ConcurrentHashMap<String, RedisValue> store) {
        RedisValue val = store.get(key);
        if (val == null) {
            ListValue listValue = new ListValue();
            store.put(key, listValue);
            return listValue;
        } else if (val instanceof ListValue listValue) {
            return listValue;
        } else {
            throw new IllegalArgumentException(ERROR_CLASS_TYPE);
        }
    }
}
