package main.model;

public class Connectivity {
    private int id;
    private RouterInfo info;
    private int distance;

    public Connectivity(int id , RouterInfo info , int distance) {
        this.id = id;
        this.info = info;
        this.distance = distance;
    }

    public int getId() {
        return id;
    }

    public RouterInfo getInfo() {
        return info;
    }

    public int getDistance() {
        return distance;
    }
}
