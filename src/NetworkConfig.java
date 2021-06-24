import java.util.ArrayList;
import java.util.List;

public class NetworkConfig {
    private String fileName; // config file name
    private int size; // routers count
    private int[][] distances; // distances between routers
    private List<RouterInfo> routerInfoList; // port and address of routers

    public NetworkConfig(String fileName) {
        this.fileName = fileName;
        routerInfoList = new ArrayList<>();
        distances = new int[size][size];

    }

    private void readConfigFile() {

    }


    public int distanceBetween(int from , int to) {
        return distances[from][to];
    }

    public RouterInfo getInfo(int routerId) {
        return routerInfoList.get(routerId);
    }



}
