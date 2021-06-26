package main.core;


import main.Main;
import main.log.LogManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static main.log.LogManager.logM;


public class Manager extends Thread {
    private final NetworkConfig config;
    private ServerSocket serverSocket;
    private final AtomicBoolean safeSent;
    private final AtomicInteger readyRoutersCount;
    private final List<ManagerRequestHandler> handlers;
    private final AtomicBoolean networkReadySent;
    private final AtomicInteger ackedRoutersCount;


    public Manager(String fileName) {
        config = new NetworkConfig(fileName);
        handlers = new ArrayList<>();
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
        while (true) {
            try {
                Socket tcp = serverSocket.accept();
                ManagerRequestHandler handler = ManagerRequestHandler.handle(this , tcp);
                handlers.add(handler);
            } catch (IOException e) {
                e.printStackTrace();
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

    public void incrementNumOfReadyForRoutingRouters() throws IOException {
        if (networkReadySent.get()) return;
        synchronized (this){
            if(networkReadySent.get()) return;
            if (ackedRoutersCount.incrementAndGet() == config.getSize()) { // all routers are acked
                networkReadySent.set(true);
                logM("Network is Ready.");
                for (ManagerRequestHandler handler : handlers) {
                    handler.sendNetworkReady();
                }
            }
        }
    }

}
