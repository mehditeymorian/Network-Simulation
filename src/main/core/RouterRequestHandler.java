package main.core;

import main.model.Connectivity;
import main.model.RouterInfo;

import java.io.*;
import java.net.Socket;

public class RouterRequestHandler extends Thread {
    private Socket socket;
    private OutputStreamWriter writer;
    private Router router;

    public RouterRequestHandler(Router router, Socket socket) {
        this.socket = socket;
        this.router = router;
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
                        break;
                    case "SAFE":
                        handleSafe();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleConnectivityTable(BufferedReader reader) throws IOException {
        crlf();
        int numOfNeighbors = Integer.parseInt(reader.readLine());
        crlf();
        for (int i = 0; i < numOfNeighbors; i++) {
            int routerId = Integer.parseInt(reader.readLine());
            crlf();
            int routerDistance = Integer.parseInt(reader.readLine());
            crlf();
            String[] connectionDetails = reader.readLine().split(" ");
            RouterInfo routerInfo = new RouterInfo(Integer.parseInt(connectionDetails[0]),
                    connectionDetails[1],
                    Integer.parseInt(connectionDetails[2]));

            Connectivity neighbor = new Connectivity(routerId, routerInfo, routerDistance);
            this.router.addNeighbors(neighbor);

        }
    }

    private void handleSafe() {
        // TODO
    }

    private void crlf() throws IOException {
        writer.write("\r\n");
    }
}
