package main.java.redis;

public class ValueWithExpiry {
    private final String value;
    private long ttl;

    public ValueWithExpiry(String value){
        this.value = value;
        this.ttl = 0;
    }


    public long getTtl() {
        return ttl;
    }

    public void setTtl(Long expiryTimestamp) {
        this.ttl = expiryTimestamp;
    }

    public String getValue() {
        return value;
    }

    private boolean hasNoExpiration(){
        return getTtl()==0L;
    }

    public boolean isExpired(){
        return !hasNoExpiration() && (System.currentTimeMillis()> getTtl());
    }

}
