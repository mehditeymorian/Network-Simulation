package main;

import main.core.NetworkConfig;

public class Main {

    public static void main(String[] args) {
        NetworkConfig config = new NetworkConfig("main/config.txt");
        System.out.println(config);
    }
}
