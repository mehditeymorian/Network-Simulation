package main.handlers;

import main.core.Router;
import main.model.Connectivity;
import main.model.RouterInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import static main.log.LogManager.logR;

public class RouterRequestHandler extends Thread {
    private Socket socket;
    private OutputStreamWriter writer;
    private Router router;

    public RouterRequestHandler(Router router , Socket socket) {
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

                    case "NETWORK_READY":
                        logR(router.getRouterId() , "Received Network Ready Signal.");
                        int networkSize = Integer.parseInt(reader.readLine());
                        router.setNetworkSize(networkSize);
                        router.startFlooding();
                        break;
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendReadyForRoutingSignal() throws IOException {
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
            RouterInfo routerInfo = new RouterInfo(Integer.parseInt(connectionDetails[0]) ,
                    connectionDetails[1] ,
                    Integer.parseInt(connectionDetails[2]));

            Connectivity neighbor = new Connectivity(routerId , routerInfo , routerDistance);
            this.router.addNeighbors(neighbor);

        }

        logR(router.getRouterId() , "Connectivity Table Received. Neighbors-Ids: %s." , router.getNeighborIds());
    }

    private void sendReadySignal() throws IOException {
        writer.write("READY");
        crlf();
        crlf();
        crlf();
        writer.flush();
    }

    public void sendUdpPort() throws IOException {
        writer.write("UDP_PORT");
        crlf();
        writer.write(this.router.getInfo().getUdpPort() + "");
        crlf();
        crlf();
        writer.flush();
    }

    private void handleSafe() throws IOException {
        logR(router.getRouterId() , "Received Safe Signal.");
        this.router.getUdpRequestHandler().sendCheckingConnectionSignal();
    }

    private void crlf() throws IOException {
        writer.write("\r\n");
    }
}
