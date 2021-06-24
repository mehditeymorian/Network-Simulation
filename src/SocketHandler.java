import java.net.DatagramSocket;
import java.net.Socket;

public class SocketHandler {
    private Socket tcp;
    private DatagramSocket udp;

    public SocketHandler(Socket tcp , DatagramSocket udp) {
        this.tcp = tcp;
        this.udp = udp;
    }

    public void sendTcp() {

    }

    public void sendUdp() {

    }


}
