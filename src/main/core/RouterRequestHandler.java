package main.core;

import main.Main;
import main.model.Connectivity;
import main.model.RouterInfo;

import java.io.*;
import java.net.Socket;
import java.util.logging.Logger;

public class RouterRequestHandler extends Thread {
    private Socket socket;
    private OutputStreamWriter writer;
    private Router router;

    public RouterRequestHandler(Router router, Socket socket) {
        this.socket = socket;
        this.router = router;
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
                    case "CONNECTIVITY_TABLE":
                        handleConnectivityTable(reader);
                        sendReadySignal();
                        break;
                    case "SAFE":
                        handleSafe();
                        break;

                    case "ALL_ROUTERS_READY_FOR_ROUTING":
                        receivedAllRoutersReadyForRouting();
                        break;
                }

                if (this.router.hasAllNeighborsAcked()) {
                    sendReadyForRoutingSignal();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receivedAllRoutersReadyForRouting() {
        Main.logger.info(String.format("Router: Router %s received ALL_ROUTER_READY_FOR_ROUTING signal", this.router.getRouterId()));
    }

    private void sendReadyForRoutingSignal() throws IOException {
        writer.write("READY_FOR_ROUTING");
        crlf();
        crlf();
        writer.flush();
    }

    private void handleConnectivityTable(BufferedReader reader) throws IOException {
        int numOfNeighbors = Integer.parseInt(reader.readLine());
        for (int i = 0; i < numOfNeighbors; i++) {
            int routerId = Integer.parseInt(reader.readLine());
            int routerDistance = Integer.parseInt(reader.readLine());
            String[] connectionDetails = reader.readLine().split(" ");
            RouterInfo routerInfo = new RouterInfo(Integer.parseInt(connectionDetails[0]),
                    connectionDetails[1],
                    Integer.parseInt(connectionDetails[2]));

            Connectivity neighbor = new Connectivity(routerId, routerInfo, routerDistance);
            this.router.addNeighbors(neighbor);

        }

        Main.logger.info(String.format("Router: Router %s connectivity table updated" , this.router.getRouterId()));

    }

    private void sendReadySignal() throws IOException {
        writer.write("READY");
        crlf();
        crlf();
        crlf();
        writer.flush();
    }


    public void sendUdpPort() throws IOException {
        Main.logger.info(String.format("Router: Sending router %s udp port to manager", this.router.getRouterId()));
        writer.write("UDP_PORT");
        crlf();
        writer.write(this.router.getInfo().getUdpPort() + "");
        crlf();
        crlf();
        writer.flush();
    }

    private void handleSafe() throws IOException {
        Main.logger.info(String.format("Router: Router %s Received safe signal" , router.getRouterId()));
        this.router.getUdpRequestHandler().sendCheckingConnectionSignal();
    }

    private void crlf() throws IOException {
        writer.write("\r\n");
    }
}
