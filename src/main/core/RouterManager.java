package main.core;

import main.model.Connectivity;
import main.model.LSP;

import java.util.*;

public class RouterManager {
    private List<LSP> db;
    private final List<Connectivity> neighbors;
    private List<Integer> forwardingTable;
    private int receivedLSPs;


    public RouterManager() {
        db = new ArrayList<>();
        neighbors = new ArrayList<>();
        forwardingTable = new ArrayList<>();
    }


    public void addLSP(LSP lsp) {
        Iterator<LSP> iterator = db.iterator();
        while (iterator.hasNext()) {
            LSP next = iterator.next();
            if (next.getId() == lsp.getId()) {
                iterator.remove();
                break;
            }
        }
        db.add(lsp);
        if (++receivedLSPs == neighbors.size())
            calculateSPT();
    }

    public void calculateSPT() {
        forwardingTable = new ArrayList<>(); // init with some Number as Infinity as networkSize - 1
        List<LSP> openSet = getSortedByDistance();

        while (!openSet.isEmpty()) {
            LSP lsp = openSet.remove(0);
            int distance = getDistanceToRouter(lsp.getId());
            for (int[] lspNeighbor : lsp.getNeighbors()) {
                int distanceToLspNeighbor = distance + lspNeighbor[1];
                forwardingTable.set(lspNeighbor[0] , Math.min(forwardingTable.get(lspNeighbor[0]) , distanceToLspNeighbor));
            }
        }
    }

    private List<LSP> getSortedByDistance() {
        return null; // TODO: 6/26/2021
    }

    private int getDistanceToRouter(int routerId) {
        return 0; // TODO: 6/26/2021
    }

    public void addNeighbors(Connectivity neighbor) {
        neighbors.add(neighbor);
    }

    public int getNumberOfNeighbors(){
        return this.neighbors.size();
    }

    public List<Connectivity> getNeighbors() {
        return neighbors;
    }

}
