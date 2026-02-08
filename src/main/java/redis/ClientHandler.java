package main.java.redis;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientHandler implements Runnable{
    private static final AtomicInteger GLOBAL_CLIENT_COUNTER = new AtomicInteger();
    private final String clientId;
    private final Socket client;
    private static CommandDispatcher dispatcher;


    public ClientHandler(Socket client, CommandDispatcher dispatcher) {
        this.clientId = "CLIENT_SOCKET_" + GLOBAL_CLIENT_COUNTER.getAndIncrement();
        this.client = client;
        ClientHandler.dispatcher = dispatcher;

    }

    @Override
    public void run() {

        try {
            while (client.isConnected()){
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                PrintWriter out = new PrintWriter(client.getOutputStream(), true);

                String message = in.readLine();
                System.out.println(clientId +" says : " + message);
                out.println("Message received by the server.");
                String handleCommands = dispatcher.handleCommands(message);
                out.println("Server returns: " + handleCommands);
            }
            client.close();
        } catch (Exception e) {
            System.out.println(clientId + " has failed with error : " + e.getMessage());

        }

    }
}
