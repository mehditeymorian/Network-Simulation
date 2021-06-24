package main;

import main.core.Manager;
import main.core.NetworkConfig;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        String fileName = "main/config.txt";
        Manager manager = new Manager(fileName);
        System.out.println(manager);
    }
}
