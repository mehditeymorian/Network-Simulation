package main.handlers;


import main.core.NetworkManager;
import main.model.Connectivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;

import static main.log.LogManager.logM;

public class ManagerRequestHandler extends Thread {
    private NetworkManager networkManager;
    private Socket socket;
    private int routerId;
    private OutputStreamWriter writer;


    public static ManagerRequestHandler handle(NetworkManager networkManager , Socket socket) {
        ManagerRequestHandler managerRequestHandler = new ManagerRequestHandler(networkManager , socket);
        managerRequestHandler.start();
        return managerRequestHandler;
    }

    private ManagerRequestHandler(NetworkManager networkManager , Socket socket) {
        this.networkManager = networkManager;
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
                        this.networkManager.incrementNumOfReadyForRoutingRouters();
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
        logM("Received Ack Signal from router %d." , routerId);
        reader.readLine();
        reader.readLine();
    }

    private void handleReadyRequest(BufferedReader reader) throws IOException {
        logM("Received Ready Signal from router %d." , routerId);
        reader.readLine();
        reader.readLine();
        networkManager.incrementReadyRouterCount();
    }

    private void handleUdpPortRequest(BufferedReader reader) throws IOException {
        int udpPort = Integer.parseInt(reader.readLine());
        routerId = networkManager.getConfig().findRouter(udpPort);
        List<Connectivity> routerNeighbors = networkManager.getConfig().getRouterNeighbors(routerId);
        sendRouterNeighbors(routerNeighbors);
        reader.readLine();
        logM("UDP Port %d Received from Router %d." , udpPort , routerId);
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
