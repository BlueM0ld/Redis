package main.java.redis.values;

public class ValueWithExpiry extends RedisValue{
    private final String value;

    public ValueWithExpiry(String value){
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

}
