package main.core;


import java.io.IOException;
import java.net.Socket;


public class Manager extends Thread{
    private NetworkConfig config;


    public Manager(String fileName) {
        config = new NetworkConfig(fileName);
        // TODO: 6/24/2021 init socket


    }

    @Override
    public synchronized void start() {
        super.start();

    }

    @Override
    public void run() {
        Socket tcp;
        while (true) {
            try {
//                tcp = new ServerSocket(NetworkConfig.MANAGER_TCP_PORT).accept();
                tcp = new Socket("localhost",NetworkConfig.MANAGER_TCP_PORT);
                new ManagerSocketHandler(config , tcp).start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
