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
    private UdpRequestHandler udpRequestHandler;
    private RouterRequestHandler routerRequestHandler;
    private int numOfAckedNeighbors;

    public Router(int routerId, RouterInfo info) {
        this.routerId = routerId;
        this.info = info;
        neighbors = new ArrayList<>();
        numOfAckedNeighbors = 0;

        // TODO: 6/24/2021 init sockets

    }

    @Override
    public synchronized void start() {
        super.start();
        Main.logger.info(String.format("Router: Router %s started" , this.getRouterId()));
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

    public void incrementAckedNeighbors(){
         this.numOfAckedNeighbors++ ;
    }

    public int getNumOfAckedNeighbors() {
        return numOfAckedNeighbors;
    }

    public boolean hasAllNeighborsAcked(){
        return this.numOfAckedNeighbors == getNumberOfNeighbors();
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

