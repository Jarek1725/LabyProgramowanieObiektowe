package org.example;

import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) {
        int port = 8887;
        WSSServer server = new WSSServer(new InetSocketAddress(port));
        server.start();
        System.out.println("Server started on port: " + port);
    }

}
