package main.java.redis

import spock.lang.Specification
import spock.lang.Unroll

class CommandDispatcherTest extends Specification {

    CommandDispatcher dispatcher

    def setup() {
        dispatcher = new CommandDispatcher()
    }

    def "PING command should return PONG"() {
        expect:
        dispatcher.handleCommands("PING") == "+PONG"
    }

    def "ECHO command should return the echoed message"() {
        expect:
        dispatcher.handleCommands("ECHO hello") == "+ECHO hello"
    }

    def "ECHO command should handle multiple words"() {
        expect:
        dispatcher.handleCommands("ECHO hello world test") == "+ECHO hello world test"
    }

    def "SET command should store a key-value pair and return OK"() {
        expect:
        dispatcher.handleCommands("SET mykey myvalue") == "+OK"
    }

    def "SET command with insufficient arguments should return error"() {
        expect:
        dispatcher.handleCommands("SET onlykey ") == "-ERR wrong number of arguments"
    }

    def "SET command with no arguments should return error"() {
        expect:
        dispatcher.handleCommands("SET") == "-ERR wrong number of arguments"
    }

    def "GET command should retrieve a previously set value"() {
        given:
        dispatcher.handleCommands("SET testkey 0")

        expect:
        dispatcher.handleCommands("GET testkey") == "+0"
    }

    def "GET command for non-existent key should return NIL"() {
        expect:
        dispatcher.handleCommands("GET nonexistent") == "+NIL"
    }

    def "GET command with wrong number of arguments should return error"() {
        expect:
        dispatcher.handleCommands("GET key1 key2") == "-ERR wrong number of arguments"
    }

    def "GET command with no arguments should return error"() {
        expect:
        dispatcher.handleCommands("GET") == "-ERR wrong number of arguments"
    }

    def "EXPIRE command should set TTL on existing key"() {
        given:
        dispatcher.handleCommands("SET mykey myvalue")

        expect:
        dispatcher.handleCommands("EXPIRE SET mykey 10") == "1"
    }

    def "EXPIRE command on non-existent key should return 0"() {
        expect:
        dispatcher.handleCommands("EXPIRE SET nonexistent 10") == "0"
    }

    def "EXPIRE command with insufficient arguments should return error"() {
        expect:
        dispatcher.handleCommands("EXPIRE SET mykey") == "-ERR wrong number of arguments"
    }

    def "GET should return NIL for expired key"() {
        given:
        dispatcher.handleCommands("SET expirekey value")
        dispatcher.handleCommands("EXPIRE SET expirekey 0")

        // Sleep briefly to ensure expiration
        Thread.sleep(10)

        expect:
        dispatcher.handleCommands("GET expirekey") == "+NIL"
    }

    def "GET should return value for non-expired key"() {
        given:
        dispatcher.handleCommands("SET validkey value")
        dispatcher.handleCommands("EXPIRE SET validkey 10")

        expect:
        dispatcher.handleCommands("GET validkey") == "+value"
    }

    def "Unknown command should return error"() {
        expect:
        dispatcher.handleCommands("UNKNOWN") == "-ERR unknown command"
    }

    @Unroll
    def "Unknown command '#command' should return error"() {
        expect:
        dispatcher.handleCommands(command) == "-ERR unknown command"

        where:
        command << ["DELETE", "UPDATE", "RANDOM", "INCR"]
    }

    def "SET and GET workflow should work correctly"() {
        when:
        def setResult = dispatcher.handleCommands("SET user:1 john")
        def getResult = dispatcher.handleCommands("GET user:1")

        then:
        setResult == "+OK"
        getResult == "+john"
    }

    def "Multiple keys should be stored independently"() {
        given:
        dispatcher.handleCommands("SET key1 value1")
        dispatcher.handleCommands("SET key2 value2")
        dispatcher.handleCommands("SET key3 value3")

        expect:
        dispatcher.handleCommands("GET key1") == "+value1"
        dispatcher.handleCommands("GET key2") == "+value2"
        dispatcher.handleCommands("GET key3") == "+value3"
    }

    def "Overwriting a key should replace the old value"() {
        given:
        dispatcher.handleCommands("SET mykey oldvalue")
        dispatcher.handleCommands("SET mykey newvalue")

        expect:
        dispatcher.handleCommands("GET mykey") == "+newvalue"
    }

    // Edge cases
    def "Commands should be case-sensitive"() {
        expect:
        dispatcher.handleCommands("ping") == "-ERR unknown command"
        dispatcher.handleCommands("Ping") == "-ERR unknown command"
    }

    def "Commands with extra whitespace should be parsed correctly"() {
        expect:
        dispatcher.handleCommands("  PING  ") == "+PONG"
        dispatcher.handleCommands("  SET   key   value  ") == "+OK"
    }

    def "Empty command should return error"() {
        expect:
        dispatcher.handleCommands("") == "-ERR unknown command"
    }
}