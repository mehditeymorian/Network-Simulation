package main.log;

import main.Main;
import main.utils.TableList;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;
import java.util.stream.IntStream;

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

    public static void logForwardingTable(int routerId , int[][] forwardingTable) {


        String[] titles = IntStream.range(-1 , forwardingTable.length + 1).mapToObj(value -> "Router "+ value).toArray(String[]::new);
        TableList tableList = new TableList(titles);
        titles[0] = "From\\To";
        String[] values = new String[titles.length];
        for (int i = 0; i < forwardingTable.length; i++) {
            values[i + 1] = String.format("%d,L%d" , forwardingTable[i][0] , forwardingTable[i][1]);
        }
        values[0] = "Router " + routerId;
        tableList.addRow(values);
        tableList.withUnicode(true);
        tableList.print("Router-"+routerId+"-spt.log");
    }


    static String calcDate(long millisecs) {
        SimpleDateFormat date_format = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date resultdate = new Date(millisecs);
        return date_format.format(resultdate);
    }


}
