package main.core;

import main.Main;
import main.log.LogManager;
import main.model.Connectivity;
import main.model.RouterInfo;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static main.log.LogManager.logC;
import static main.log.LogManager.logR;

public class Router extends Thread {
    private int routerId;
    private RouterInfo info;
    private List<Connectivity> neighbors;
    private Socket managerSocket;
    private UdpRequestHandler udpRequestHandler;
    private RouterRequestHandler routerRequestHandler;
    private AtomicInteger acksFromNeighbors;

    public Router(int routerId, RouterInfo info) {
        this.routerId = routerId;
        this.info = info;
        neighbors = new ArrayList<>();
        acksFromNeighbors = new AtomicInteger();

        // TODO: 6/24/2021 init sockets

    }

    @Override
    public synchronized void start() {
        super.start();
        logR(routerId,"Started Working.");
        try {
            managerSocket = new Socket(info.getTcpAddress(), NetworkConfig.MANAGER_TCP_PORT);
            udpRequestHandler = new UdpRequestHandler(this);
            routerRequestHandler = new RouterRequestHandler(this, managerSocket);
            routerRequestHandler.start();
            routerRequestHandler.sendUdpPort();
            udpRequestHandler.start();
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

    public int getRouterId() {
        return routerId;
    }

    public void addNeighbors(Connectivity neighbor) {
        this.neighbors.add(neighbor);

    }

    public void incrementAcksFromNeighbors(){
        acksFromNeighbors.incrementAndGet();
        if (acksFromNeighbors.get() == getNumberOfNeighbors()) {
            try {
                logR(routerId,"All ACKS Received from Neighbors.");
                routerRequestHandler.sendReadyForRoutingSignal();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int getNumberOfNeighbors(){
        return this.neighbors.size();
    }

    public List<Connectivity> getNeighbors() {
        return neighbors;
    }

    public UdpRequestHandler getUdpRequestHandler() {
        return udpRequestHandler;
    }

    public String getNeighborIds(){
        StringBuilder stringBuilder = new StringBuilder();
        for (Connectivity neighbor : this.neighbors) {
            stringBuilder.append(neighbor.getId());
            stringBuilder.append(" ");
        }

        return stringBuilder.toString();
    }
}

