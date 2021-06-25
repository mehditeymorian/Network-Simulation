package main.core;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Manager extends Thread {
    private NetworkConfig config;
    private ServerSocket serverSocket;


    public Manager(String fileName) {
        config = new NetworkConfig(fileName);
        initServerSocket();
        // TODO: 6/24/2021 init socket


    }

    private void initServerSocket() {
        try {
            serverSocket = new ServerSocket(NetworkConfig.MANAGER_TCP_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void start() {
        super.start();

    }

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {

        while (true) {
            try {
                Socket tcp = serverSocket.accept();
               RouterRequestHandler.handle(config , tcp);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
