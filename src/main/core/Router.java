package main.core;

import main.Main;
import main.model.Connectivity;
import main.model.RouterInfo;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Router extends Thread {
    private int routerId;
    private RouterInfo info;
    private List<Connectivity> neighbors;
    private Socket managerSocket;
    private SocketHandler socketHandler;
    private RouterRequestHandler routerRequestHandler;

    public Router(int routerId, RouterInfo info) {
        this.routerId = routerId;
        this.info = info;
        neighbors = new ArrayList<>();

        // TODO: 6/24/2021 init sockets

    }

    @Override
    public synchronized void start() {
        super.start();
        Main.logger.info("Router started");
        try {
            managerSocket = new Socket(info.getTcpAddress(), NetworkConfig.MANAGER_TCP_PORT);
            socketHandler = new SocketHandler(managerSocket);
            routerRequestHandler = new RouterRequestHandler(this, managerSocket);
            routerRequestHandler.start();
            routerRequestHandler.sendUdpPort();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        super.run();
    }

    public RouterInfo getInfo() {
        return info;
    }

    public void setNeighbors(List<Connectivity> neighbors) {
        this.neighbors = neighbors;
    }

    public String getRouterName() {
        return String.format("main.core.Router %d\n", getRouterId());
    }

    public int getRouterId() {
        return routerId;
    }

    public void addNeighbors(Connectivity neighbor) {
        this.neighbors.add(neighbor);

    }
}
