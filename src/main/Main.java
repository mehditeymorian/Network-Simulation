package main;

import main.core.Manager;
import main.core.NetworkConfig;
import main.log.LogManager;

import java.io.IOException;
import java.util.logging.Logger;

import static main.log.LogManager.init;
import static main.log.LogManager.logC;

public class Main {
    public static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static void main(String[] args) throws IOException {
        init();
        logC("Starting Manager");
        String fileName = "main/config.txt";
        Manager manager = new Manager(fileName);
        manager.start();
        System.out.println(manager);
    }
}
