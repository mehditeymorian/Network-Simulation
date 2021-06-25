package main.utils;

public class UdpDataBuilder {
    private StringBuilder builder;


    public static UdpDataBuilder forAction(String action) {
        return new UdpDataBuilder(action);
    }

    private UdpDataBuilder(String action) {
        builder = new StringBuilder(action);
        appendLine();
    }

    public UdpDataBuilder append(String line) {
        builder.append(line);
        appendLine();
        return this;
    }

    public String build() {
        builder.append("$end");
        return builder.toString();
    }


    private void appendLine() {
        builder.append("\n");
    }
}
