package main.handlers;

import main.core.Router;
import main.log.LogManager;
import main.model.Connectivity;
import main.model.LSP;
import main.utils.UdpDataBuilder;
import main.utils.Utility;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static main.log.LogManager.logR;

public class UdpRequestHandler extends Thread {
    private Router router;
    private DatagramSocket socket;
    public static final int BUFFER_SIZE = 2048;


    public UdpRequestHandler(Router router) {
        this.router = router;
        initSocket();
    }

    private void initSocket() {
        try {
            socket = new DatagramSocket(router.getInfo().getUdpPort());
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            String[] receivedData = receivePacket().split("\n");
            switch (receivedData[0]) {
                case "CHECK_CONNECTION":
                    handleCheckConnection(receivedData[1],receivedData[2]);
                    break;
                case "ACK":
                    handleReceivedAck();
                    break;
                case "LSP":
                    handleLSP(receivedData);
                    break;
                case "ROUTING":

                    break;
            }

        }
    }

    private void handleLSP(String[] receivedData) {
        int id = Integer.parseInt(receivedData[1]);
        long time = Long.parseLong(receivedData[2]);
        List<int[]> neighbors = new ArrayList<>();
        for (int i = 3; i < receivedData.length-1; i++) {
            String[] line = receivedData[i].split(" ");
            int[] each = {Integer.parseInt(line[0]) , Integer.parseInt(line[1])};
            neighbors.add(each);
        }
        LSP lsp = new LSP(id , time , neighbors);
        router.addLSPToDB(lsp);
        logR(router.getRouterId() , "Receive LSP from Router %d." , id);
    }

    private void handleCheckConnection(String udpPort,String routerId) {
        logR(router.getRouterId() , "Received CHECK_CONNECTION Signal from Router %s.", routerId);
        String response = UdpDataBuilder.forAction("ACK").build();
        sendPacket(response, Integer.parseInt(udpPort));
    }

    private void handleReceivedAck() {
        this.router.incrementAcksFromNeighbors();
    }


    public void sendPacket(String data, int destinationPort) {
        DatagramPacket packet = new DatagramPacket(data.getBytes(), data.length());
        InetAddress ip = null;
        try {
            ip = InetAddress.getByName("127.0.0.1");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        packet.setAddress(ip);
        packet.setPort(destinationPort);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String receivePacket() {
        byte[] buffer = new byte[BUFFER_SIZE];
        DatagramPacket packet = new DatagramPacket(buffer, BUFFER_SIZE);
        try {
            socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] data = Utility.trimData(packet.getData());
        return new String(data);
    }

    public void sendCheckingConnectionSignal() {
        String packet = UdpDataBuilder.forAction("CHECK_CONNECTION")
                .append(String.valueOf(this.router.getInfo().getUdpPort()))
                .append(String.valueOf(router.getRouterId()))
                .build();
        for (Connectivity neighbor : this.router.getNeighbors()) {
            sendPacket(packet, neighbor.getInfo().getUdpPort());
        }
    }

    // send lsp to neighbors
    public void startFlooding() {
        UdpDataBuilder lsp = UdpDataBuilder.forAction("LSP")
                .append(String.valueOf(router.getRouterId()))
                .append(String.valueOf(new Date().getTime()));

        for (Connectivity neighbor : router.getNeighbors()) {
            lsp.append(String.format("%d %d" , neighbor.getId() , neighbor.getDistance()));
        }

        String lspStr = lsp.build();
        for (Connectivity neighbor : router.getNeighbors()) {
            sendPacket(lspStr,neighbor.getInfo().getUdpPort());
        }
    }


}
