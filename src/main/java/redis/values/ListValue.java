package main.java.redis.values;

import java.util.concurrent.ConcurrentLinkedDeque;

public class ListValue extends RedisValue{

    private ConcurrentLinkedDeque<String> list;

    public ListValue() {
        this.list = new ConcurrentLinkedDeque<>();
    }

    public void pushR(String elem){
        list.addFirst(elem);
    }

    public void pushL(String elem){
        list.addLast(elem);
    }

}
