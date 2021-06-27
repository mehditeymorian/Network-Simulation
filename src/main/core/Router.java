package main.core;

import main.handlers.RouterRequestHandler;
import main.handlers.UdpRequestHandler;
import main.model.Connectivity;
import main.model.LSP;
import main.model.RouterInfo;
import main.utils.UdpDataBuilder;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static main.log.LogManager.logR;

public class Router extends Thread {
    public static final int FORWARDING_DELAY = 100;
    private final int routerId;
    private final RouterInfo info;
    private RouterManager routerManager;
    private Socket managerSocket;
    private UdpRequestHandler udpRequestHandler;
    private RouterRequestHandler routerRequestHandler;
    private final AtomicInteger acksFromNeighbors;

    public Router(int routerId, RouterInfo info) {
        this.routerId = routerId;
        this.info = info;
        routerManager = new RouterManager(routerId);
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
        routerManager.addNeighbors(neighbor);

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
        return routerManager.getNumberOfNeighbors();
    }

    public List<Connectivity> getNeighbors() {
        return routerManager.getNeighbors();
    }

    public UdpRequestHandler getUdpRequestHandler() {
        return udpRequestHandler;
    }

    public String getNeighborIds(){
        StringBuilder stringBuilder = new StringBuilder();
        for (Connectivity neighbor : getNeighbors()) {
            stringBuilder.append(neighbor.getId());
            stringBuilder.append(" ");
        }

        return stringBuilder.toString();
    }

    public void startFlooding() {
        // add current lsp
        int id = getRouterId();
        int distance = 0;
        List<int[]> neighbors = routerManager.getNeighbors().stream().map(each -> new int[]{each.getId(),each.getDistance()}).collect(Collectors.toList());
        LSP selfLSP = new LSP(id , distance , neighbors);
        addLSPToDB(selfLSP);

        udpRequestHandler.startFlooding();
    }

    public void addLSPToDB(LSP lsp) {
        routerManager.addLSP(lsp);
    }

    public void setNetworkSize(int networkSize) {
        routerManager.setNetworkSize(networkSize);
    }

    public void startRouting(int src , int destination) {
        int nextRouterId = routerManager.getNextRouterIdByDestination(destination);
        int udpPort = routerManager.getUdpPortByRouterId(nextRouterId);
        String packetData = UdpDataBuilder.forAction("ROUTING")
                .append(String.format("%d %d" , src , destination))
                .append(String.valueOf(System.currentTimeMillis()))
                .append(String.valueOf(routerId))
                .build();
        udpRequestHandler.sendPacket(packetData, udpPort);
    }

    public void forwardPacket(String data , int destination) {
        // creating an artificial delay to get a sense :)
        try {
            Thread.sleep(FORWARDING_DELAY);
            int nextRouterId = routerManager.getNextRouterIdByDestination(destination);
            int udpPort = routerManager.getUdpPortByRouterId(nextRouterId);
            udpRequestHandler.sendPacket(data, udpPort);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

