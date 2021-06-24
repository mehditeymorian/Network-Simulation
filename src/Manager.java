import java.net.Socket;

public class Manager extends Thread{
    private NetworkConfig config;
    private SocketHandler socketHandler;
    private Socket tcp;

    public Manager(String fileName) {
        config = new NetworkConfig(fileName);
        // TODO: 6/24/2021 init socket
        socketHandler = new SocketHandler(tcp , null);
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    @Override
    public void run() {
        super.run();
    }
}
