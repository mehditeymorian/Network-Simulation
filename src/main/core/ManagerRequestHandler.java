package main.core;

import main.Main;
import main.model.Connectivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.Semaphore;

public class ManagerRequestHandler extends Thread {
    private Manager manager;
    private Socket socket;
    private int routerId;
    private OutputStreamWriter writer;
    public Semaphore safeSem;
    private boolean isReadyReceived = false;
    private boolean isSafeSent = false;


    public static ManagerRequestHandler handle(Manager manager, Socket socket) {
        ManagerRequestHandler managerRequestHandler = new ManagerRequestHandler(manager, socket);
        managerRequestHandler.start();
        return managerRequestHandler;
    }

    private ManagerRequestHandler(Manager manager, Socket socket) {
        this.manager = manager;
        this.socket = socket;
        safeSem = new Semaphore(0);
        initSocketOutputWriter(socket);
    }

    private void initSocketOutputWriter(Socket socket) {
        try {
            writer = new OutputStreamWriter(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                String action = reader.readLine();
                switch (action) {
                    case "UDP_PORT":
                        handleUdpPortRequest(reader);
                        break;
                    case "READY":
                        handleReadyRequest(reader);
                        break;
                    case "ACK":
                        handleAckRequest(reader);
                        break;
                }

                if (isReadyReceived && !isSafeSent) {
                    safeSem.acquire();
                    Main.logger.info("Network is ready.");
                    sendSafeMessage();
                    isSafeSent = true;
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleAckRequest(BufferedReader reader) throws IOException {
        Main.logger.info("Ack received");
        reader.readLine();
        reader.readLine();
    }

    private void handleReadyRequest(BufferedReader reader) throws IOException {
        Main.logger.info("Ready received");
        reader.readLine();
        reader.readLine();
        manager.incrementReadyRouterCount();
        isReadyReceived = true;

        // TODO: 6/24/2021 log
    }

    private void handleUdpPortRequest(BufferedReader reader) throws IOException {
        int udpPort = Integer.parseInt(reader.readLine());
        routerId = manager.getConfig().findRouter(udpPort);
        List<Connectivity> routerNeighbors = manager.getConfig().getRouterNeighbors(routerId);
        sendRouterNeighbors(routerNeighbors);
        reader.readLine();
        Main.logger.info(String.format("%s udp port received" , routerId));
    }

    private void sendRouterNeighbors(List<Connectivity> routerNeighbors) throws IOException {

        writer.write("CONNECTIVITY_TABLE");
        crlf();
        writer.write(routerNeighbors.size() + "");
        crlf();
        for (Connectivity routerNeighbor : routerNeighbors) {

            writer.write(routerNeighbor.getId() + "");
            crlf();
            writer.write(routerNeighbor.getDistance() + "");
            crlf();
            writer.write(String.format("%s %s %s", routerNeighbor.getInfo().getTcpPort(),
                    routerNeighbor.getInfo().getTcpAddress(),
                    routerNeighbor.getInfo().getUdpPort()));
            crlf();
        }

        writer.flush();
    }

    private void sendSafeMessage() throws IOException {
        writer.write("SAFE");
        crlf();
        crlf();
        crlf();
        writer.flush();
    }

    private void crlf() throws IOException {
        writer.write("\r\n");
    }
}