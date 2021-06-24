package main.core;

import main.Main;
import main.model.RouterInfo;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NetworkConfig {
    public static final int MANAGER_TCP_PORT = 1000;
    private final String tcpAddress = "localhost";

    private String fileName; // config file name
    private int size; // routers count
    private int[][] distances; // distances between routers
    private List<Router> routers;


    public NetworkConfig(String fileName) {
        this.fileName = fileName;
        routers = new ArrayList<>();
        try {
            readConfigFile();
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    private void readConfigFile() throws URISyntaxException, IOException {
        URL resource = Main.class.getClassLoader().getResource(fileName);
        URI uri = Objects.requireNonNull(resource).toURI();
        Path path = Paths.get(uri);
        List<String> lines = Files.readAllLines(path);
        // first line is routers number
        size = Integer.parseInt(lines.get(0));
        distances = new int[size][size];

        for (int i = 1; i < lines.size(); i++) {
            String[] split = lines.get(i).split(" ");
            int from = Integer.parseInt(split[0]);
            int to = Integer.parseInt(split[1]);
            int distance = Integer.parseInt(split[2]);
            distances[from][to] = distance;
//            distances[to][from] = distance;

            if (i <= size) {
                RouterInfo routerInfo = new RouterInfo(MANAGER_TCP_PORT + i , tcpAddress , MANAGER_TCP_PORT + i + lines.size());
                Router router = new Router(i - 1 , routerInfo);
                routers.add(router);
            }

        }
    }


    public int distanceBetween(int from , int to) {
        return distances[from][to];
    }

    public List<Router> getRouters() {
        return routers;
    }

    public int[][] getDistances() {
        return distances;
    }
}
