package main.java.redis;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) {

        try(ServerSocket serverSocket = new java.net.ServerSocket(6379)){

            CommandDispatcher dispatcher = new CommandDispatcher();

            while(true){
                Socket client = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(client, dispatcher);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }

        }
        catch (IOException e){
            System.out.print("Caught exception : " + e.getMessage());
        }
    }
}
