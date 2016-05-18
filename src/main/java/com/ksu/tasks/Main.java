package com.ksu.tasks;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8888);
        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("New request");
            new Thread(new RequestProcessor(socket)).start();
        }
    }

}
