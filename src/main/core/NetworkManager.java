package main.core;


import main.handlers.ManagerRequestHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static main.log.LogManager.logM;


public class NetworkManager extends Thread {
    private final NetworkConfig config;
    private ServerSocket serverSocket;
    private final AtomicBoolean safeSent;
    private final AtomicInteger readyRoutersCount;
    private final List<ManagerRequestHandler> handlers;
    private final AtomicBoolean networkReadySent;
    private final AtomicInteger ackedRoutersCount;
    private int onlineRoutersCount;
    private Semaphore inputCommandsSem;


    public NetworkManager(String fileName) {
        config = new NetworkConfig(fileName);
        handlers = new ArrayList<>();
        inputCommandsSem = new Semaphore(0);
        safeSent = new AtomicBoolean();
        readyRoutersCount = new AtomicInteger();
        networkReadySent = new AtomicBoolean();
        ackedRoutersCount = new AtomicInteger();
        initServerSocket();
    }

    private void initServerSocket() {
        try {
            logM("Listen on port %d",NetworkConfig.MANAGER_TCP_PORT);
            serverSocket = new ServerSocket(NetworkConfig.MANAGER_TCP_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void start() {
        super.start();
        for (Router router : config.getRouters()) {
            router.start();
        }

    }

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        while (++onlineRoutersCount <= config.getSize()) {
            try {
                Socket tcp = serverSocket.accept();
                ManagerRequestHandler handler = ManagerRequestHandler.handle(this , tcp);
                handlers.add(handler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        inputCommands();
    }

    private void inputCommands() {
        try {
            inputCommandsSem.acquire();
        } catch (InterruptedException ignored) {}

        Scanner input = new Scanner(System.in);
        System.out.println("Commands:");
        System.out.println("\tRouting: Example:0 3 // routing from 0 to 3");
        System.out.println("\tQuit: Example:quit 3 // remove router 3 from network");
        while (input.hasNextLine()) {
            try {
                String[] line = input.nextLine().toLowerCase().split(" ");
                if (line[0].equals("quit")) {
                    int routerId = Integer.parseInt(line[1]);
                    killRouter(routerId);
                }else {
                    int src = Integer.parseInt(line[0]);
                    int destination = Integer.parseInt(line[1]);
                    routePacket(src,destination);
                }
            } catch (Exception ignored) {}
        }
    }

    private void killRouter(int routerId) {
        for (ManagerRequestHandler handler : handlers) {
            if (handler.getRouterId() == routerId) {
                handler.sendKillRequest();
                break;
            }
        }
    }

    public NetworkConfig getConfig() {
        return config;
    }

    public void incrementReadyRouterCount() {
        if (safeSent.get()) return;
        synchronized (this){
            if(safeSent.get()) return;
            if (readyRoutersCount.incrementAndGet() == config.getSize()) { // all routers are acked
                safeSent.set(true);
                logM("Network is Safe.");
                for (ManagerRequestHandler handler : handlers) {
                    handler.sendSafeMessage();
                }
            }
        }
    }

    public void incrementAckedRouterCount() throws IOException {
        if (networkReadySent.get()) return;
        synchronized (this){
            if(networkReadySent.get()) return;
            if (ackedRoutersCount.incrementAndGet() == config.getSize()) { // all routers are acked
                networkReadySent.set(true);
                logM("Network is Ready.");
                for (ManagerRequestHandler handler : handlers) {
                    handler.sendNetworkReady();
                }
                releaseInputCommandsSemaphore();
            }
        }
    }

    private void releaseInputCommandsSemaphore() {
        try {
            Thread.sleep(500);
            inputCommandsSem.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void routePacket(int src , int destination) {
        for (ManagerRequestHandler handler : handlers) {
            if (handler.getRouterId() == src) {
                handler.routePacket(destination);
                break;
            }
        }
    }
}
