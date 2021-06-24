package main.core;

import main.model.Connectivity;
import main.model.RouterInfo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Manager extends Thread{
    private NetworkConfig config;
    private Socket tcp;

    public Manager(String fileName) throws IOException {
        config = new NetworkConfig(fileName);
        // TODO: 6/24/2021 init socket
        initRouterConnectivityTable();
        tcp = new ServerSocket(9000).accept();

    }

    @Override
    public synchronized void start() {
        super.start();

    }

    @Override
    public void run() {
        super.run();
    }

    private void initRouterConnectivityTable() {
        int[][] distances = config.getDistances();
        for (int i = 0, distancesLength = distances.length; i < distancesLength; i++) {
            List<Connectivity> neighbors = new ArrayList<>();
            int[] x = distances[i];
            for (int j = 0; j < x.length; j++) {
                if (x[j] != 0) {
                    RouterInfo neighborInfo = config.getRouters().get(j).getInfo();
                    Connectivity connectivity = new Connectivity(j , neighborInfo , x[j]);
                    neighbors.add(connectivity);
                }
            }
            config.getRouters().get(i).setNeighbors(neighbors);
        }
    }
}
