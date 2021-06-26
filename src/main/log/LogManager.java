package main.log;

import main.Main;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class LogManager {
    static final Logger logger = Main.logger;


    public static void init() {

        for (Handler handler : logger.getHandlers()) {
            logger.removeHandler(handler);
        }

        try {
            FileHandler htmlNetworkLog = new FileHandler("network.html");
            htmlNetworkLog.setFormatter(new HtmlLogFormatter());
            logger.addHandler(htmlNetworkLog);

            FileHandler simpleNetworkLog = new FileHandler("network.log");
            simpleNetworkLog.setFormatter(new ConsoleLogFormatter());
            logger.addHandler(simpleNetworkLog);
        } catch (IOException e) {
            e.printStackTrace();
        }


        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new ConsoleLogFormatter());
        logger.addHandler(consoleHandler);
    }

    // logging for configuration
    public static void logC(String log , Object... params) {
        log("Configuration" , log , params);
    }

    // logging for manager
    public static void logM(String log , Object... params) {
        log("Manager" , log , params);
    }

    private static void log(String name,String log , Object... params) {
        logger.info(String.format("[" +name+"] " + log , params));
    }

    // logging for routers
    public static void logR(int routerId , String log , Object... params) {
        logger.info(String.format("[Router " + routerId + " ] " + log , params));
    }


    static String calcDate(long millisecs) {
        SimpleDateFormat date_format = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date resultdate = new Date(millisecs);
        return date_format.format(resultdate);
    }


}
