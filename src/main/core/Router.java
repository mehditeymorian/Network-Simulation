package main.core;

import main.model.RouterInfo;

import java.net.DatagramSocket;
import java.net.Socket;

public class Router extends Thread{
    private int routerId;
    private RouterInfo info;
    private Socket managerSocket;
    private Socket tcpSocket;
    private DatagramSocket udpSocket;
    private SocketHandler socketHandler;

    public Router(int routerId , RouterInfo info) {
        this.routerId = routerId;
        this.info = info;
        // TODO: 6/24/2021 init sockets
        socketHandler = new SocketHandler(tcpSocket , udpSocket);
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    @Override
    public void run() {
        super.run();
    }


    public String getRouterName() {
        return String.format("main.core.Router %d\n" , getRouterId());
    }

    public int getRouterId() {
        return routerId;
    }
}
