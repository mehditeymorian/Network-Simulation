package main.core;


import main.Main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class Manager extends Thread {
    private final NetworkConfig config;
    private ServerSocket serverSocket;
    private final AtomicInteger readRouters;
    private final List<ManagerRequestHandler> handlers;


    public Manager(String fileName) {
        config = new NetworkConfig(fileName);
        handlers = new ArrayList<>();
        readRouters = new AtomicInteger();
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
        for (Router router : config.getRouters()) {
            router.start();
        }

    }

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
//        Main.logger.info("Manager started");
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
        int i = readRouters.incrementAndGet();
        if (i == config.getSize())
            for (ManagerRequestHandler handler : handlers)
                handler.safeSem.release();
    }
}
