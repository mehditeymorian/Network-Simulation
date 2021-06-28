package main.core;

import main.log.LogManager;
import main.model.Connectivity;
import main.model.LSP;

import java.util.*;

public class RouterManager {
    public static final int DISTANCE = 0;
    public static final int INTERFACE = 1;
    public static final int INFINITY = 100_000;
    public static final int UNKNOWN = -1;
    public static final int SELF_INTERFACE = -2;

    private List<LSP> db;
    private final List<Connectivity> neighbors;
    private int[][] forwardingTable;
    private int routerId;
    private int receivedLSPs;
    private int networkSize;


    public RouterManager(int routerId) {
        this.routerId = routerId;
        db = new ArrayList<>();
        neighbors = new ArrayList<>();
    }


    public void addLSP(LSP lsp) {
        boolean recalculate = receivedLSPs == neighbors.size() + 1;

        Iterator<LSP> iterator = db.iterator();
        while (iterator.hasNext()) {
            LSP next = iterator.next();
            if (next.getId() == lsp.getId()) {
                iterator.remove();
                break;
            }
        }
        db.add(lsp);
        if (++receivedLSPs == neighbors.size() + 1 || recalculate)
            calculateSPT();
    }

    public void calculateSPT() {
        forwardingTable = new int[networkSize][2];
        initForwardingTable();
        List<LSP> openSet = new ArrayList<>(db);

        while (!openSet.isEmpty()) {
            Object[] closestRouter = getClosestRouter(openSet);
            LSP lsp = (LSP) closestRouter[0];
            int distance = (int) closestRouter[1];

            if (lsp == null) continue;

            for (int[] lspNeighbor : lsp.getNeighbors()) {
                int lspNeighborId = lspNeighbor[0];
                int distanceToLspNeighbor = distance + lspNeighbor[1];
                if (distanceToLspNeighbor < forwardingTable[lspNeighborId][DISTANCE]) {
                    forwardingTable[lspNeighborId][DISTANCE] = distanceToLspNeighbor;
                    forwardingTable[lspNeighborId][INTERFACE] = lsp.getId() == routerId ? lspNeighborId : lsp.getId();
                }
            }
        }
        LogManager.logR(routerId, "Forwarding Table Updated.");
        LogManager.logForwardingTable(routerId,forwardingTable);
    }

    private void initForwardingTable() {
        for (int i = 0; i < forwardingTable.length; i++) {
            forwardingTable[i][DISTANCE] = INFINITY;
            forwardingTable[i][INTERFACE] = UNKNOWN;

            if (i == routerId) {
                forwardingTable[i][DISTANCE] = 0;
                forwardingTable[i][INTERFACE] = SELF_INTERFACE;
            }
        }
    }

    private Object[] getClosestRouter(List<LSP> openSet) {

        int index = -1;
        int minDistance = INFINITY;
        LSP result = null;
        for (int i = 0; i < openSet.size(); i++) {
            LSP next = openSet.get(i);
            if (forwardingTable[next.getId()][DISTANCE] < minDistance) {
                index = i;
                result = next;
                minDistance = forwardingTable[next.getId()][DISTANCE];
            }
        }
        if (index != -1)
            openSet.remove(index);
        return new Object[]{result , minDistance};
    }


    public void addNeighbors(Connectivity neighbor) {
        neighbors.add(neighbor);
    }

    public int getNumberOfNeighbors() {
        return this.neighbors.size();
    }

    public List<Connectivity> getNeighbors() {
        return neighbors;
    }

    public void setNetworkSize(int networkSize) {
        this.networkSize = networkSize;
    }

    public int getNextRouterIdByDestination(int end) {
        return forwardingTable[end][INTERFACE];
    }

    public int getUdpPortByRouterId(int nextRouterId) {
        for (Connectivity neighbor : neighbors) {
            if (neighbor.getId() == nextRouterId) {
                return neighbor.getInfo().getUdpPort();
            }
        }
        return -1;
    }
}
