public class RouterInfo {
    private int tcpPort;
    private String tcpAddress;
    private int udpPort;

    public RouterInfo(int tcpPort , String tcpAddress , int udpPort) {
        this.tcpPort = tcpPort;
        this.tcpAddress = tcpAddress;
        this.udpPort = udpPort;
    }

    public int getTcpPort() {
        return tcpPort;
    }

    public String getTcpAddress() {
        return tcpAddress;
    }

    public int getUdpPort() {
        return udpPort;
    }
}
