package main.core;

import main.model.Connectivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;

public class ManagerSocketHandler extends Thread {
    private NetworkConfig config;
    private Socket socket;
    private int routerId;
    private OutputStreamWriter writer;

    public ManagerSocketHandler(NetworkConfig config , Socket socket) {
        this.config = config;
        this.socket = socket;
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
                        int udpPort = Integer.parseInt(reader.readLine());
                        routerId = config.findRouter(udpPort);
                        List<Connectivity> routerNeighbors = config.getRouterNeighbors(routerId);
                        sendRouterNeighbors(routerNeighbors);
                        reader.readLine();
                        break;

                    case "READY":
                        reader.readLine();
                        reader.readLine();
                        int readyRouters = config.readRouters.incrementAndGet();
                        sendSafeMessage();

                        // TODO: 6/24/2021 log
                        break;
                    case "ACK":

                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendRouterNeighbors(List<Connectivity> routerNeighbors) throws IOException {
        for (Connectivity routerNeighbor : routerNeighbors) {
            writer.write("CONNECTIVITY_TABLE");
            crlf();
            writer.write(routerNeighbor.getId() + "");
            crlf();
            writer.write(routerNeighbor.getDistance() + "");
            crlf();
            writer.write(String.format("%s %s %s" , routerNeighbor.getInfo().getTcpAddress() ,
                    routerNeighbor.getInfo().getTcpPort() ,
                    routerNeighbor.getInfo().getUdpPort()));
            crlf();
            writer.flush();
        }

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
