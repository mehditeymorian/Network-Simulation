package main.log;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class ConsoleLogFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
        return String.format("[%s]%s\n",LogManager.calcDate(record.getMillis()),record.getMessage());
    }
}
