package main.core;

import main.utils.Utility;

import java.io.IOException;
import java.net.*;

public class UdpRequestHandler extends Thread{
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
            String s = receivePacket();
            switch (s) {
                case "CHECK_CONNECTION":

                    break;
                case "ACK":

                    break;
                case "LSP":

                    break;
                case "ROUTING":

                    break;
            }
        }
    }


    public void sendPacket(DatagramPacket packet,int destinationPort) {
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
        DatagramPacket packet = new DatagramPacket(buffer , BUFFER_SIZE);
        try {
            socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] data = Utility.trimData(packet.getData());
        return new String(data);
    }


}
