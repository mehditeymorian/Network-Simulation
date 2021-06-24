import java.net.DatagramSocket;
import java.net.Socket;

public class Router extends Thread{
    private int routerId;
    private Socket tcpSocket;
    private DatagramSocket udpSocket;
    private SocketHandler socketHandler;

    public Router(int routerId) {
        this.routerId = routerId;
        // TODO: 6/24/2021 init sockets
    }




    public String getRouterName() {
        return String.format("Router %d\n" , getRouterId());
    }

    public int getRouterId() {
        return routerId;
    }
}
