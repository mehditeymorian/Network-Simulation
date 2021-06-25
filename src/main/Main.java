package main;

import main.core.Manager;
import main.core.NetworkConfig;

import java.io.IOException;
import java.util.logging.Logger;

public class Main {
    public static Logger logger = Logger.getLogger("logger");

    public static void main(String[] args) throws IOException {
        String fileName = "main/config.txt";
        Manager manager = new Manager(fileName);
        manager.start();
        System.out.println(manager);
    }
}
