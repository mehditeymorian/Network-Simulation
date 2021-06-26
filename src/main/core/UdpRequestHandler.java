package main.core;

import main.Main;
import main.log.LogManager;
import main.model.Connectivity;
import main.utils.UdpDataBuilder;
import main.utils.Utility;

import java.io.IOException;
import java.net.*;

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

                    break;
                case "ROUTING":

                    break;
            }

        }
    }

    private void handleCheckConnection(String udpPort,String routerId) {
        LogManager.logR(router.getRouterId() , "Received CHECK_CONNECTION Signal from Router %s.", routerId);
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


}
