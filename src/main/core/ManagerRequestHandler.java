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


    public static ManagerRequestHandler handle(Manager manager, Socket socket) {
        ManagerRequestHandler managerRequestHandler = new ManagerRequestHandler(manager, socket);
        managerRequestHandler.start();
        return managerRequestHandler;
    }

    private ManagerRequestHandler(Manager manager, Socket socket) {
        this.manager = manager;
        this.socket = socket;
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

                    case "READY_FOR_ROUTING":
                        this.manager.incrementNumOfReadyForRoutingRouters();
                        break;

                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendNetworkReady() throws IOException {
        writer.write("NETWORK_READY");
        crlf();
        crlf();
        writer.flush();
    }

    private void handleAckRequest(BufferedReader reader) throws IOException {
//        Main.logger.info("Ack received");
        reader.readLine();
        reader.readLine();
    }

    private void handleReadyRequest(BufferedReader reader) throws IOException {
        Main.logger.info("Manager: Ready signal received");
        reader.readLine();
        reader.readLine();
        manager.incrementReadyRouterCount();
    }

    private void handleUdpPortRequest(BufferedReader reader) throws IOException {
        int udpPort = Integer.parseInt(reader.readLine());
        Main.logger.info(String.format("Manager: Received router %s udp port" , udpPort));
        routerId = manager.getConfig().findRouter(udpPort);
        List<Connectivity> routerNeighbors = manager.getConfig().getRouterNeighbors(routerId);
//        Main.logger.info(String.format("Manager: Neighbors of router %s are %s" , routerId , routerNeighbors));
        sendRouterNeighbors(routerNeighbors);
        reader.readLine();
//        Main.logger.info(String.format("%s udp port received", routerId));
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

    public void sendSafeMessage() {
        Main.logger.info("Manager: Sending Safe signal");
        try {
            writer.write("SAFE");
            crlf();
            crlf();
            crlf();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void crlf() throws IOException {
        writer.write("\r\n");
    }
}
