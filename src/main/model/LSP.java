package main.model;

import java.util.List;

public class LSP {
    private int id;
    private long time;
    private List<int[]> neighbors;

    public LSP(int id , long time , List<int[]> neighbors) {
        this.id = id;
        this.time = time;
        this.neighbors = neighbors;
    }

    public int getId() {
        return id;
    }

    public long getTime() {
        return time;
    }

    public List<int[]> getNeighbors() {
        return neighbors;
    }
}
