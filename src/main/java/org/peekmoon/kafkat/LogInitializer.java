package org.peekmoon.kafkat;

import java.io.*;
import java.util.logging.*;

public class LogInitializer {

    private static final int Mo = 1024 * 1024;

    public static void init() {

        Logger rootLogger = Logger.getLogger("");
        String logDirectory = System.getProperty("log_directory");
        String logFile = (logDirectory == null ? "/Users/j.lelong/Documents/perso/dev/kafkat/target" : logDirectory) + "/kafkat.log";
        try {
            FileHandler logHandler = new FileHandler(logFile, 5 * Mo,
                    2, // one log file at a time
                    true // if it exists: append, don't overwrite
            );
            Level defaultLevel = Level.INFO;

            //System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL %4$-7s [%3$s] %5$s %6$s%n");
            logHandler.setFormatter(new KafkatFormatter());
            logHandler.setLevel(java.util.logging.Level.FINE);
            for (Handler h : rootLogger.getHandlers()) {
                rootLogger.removeHandler(h);
            }
            rootLogger.setLevel(defaultLevel);
            rootLogger.addHandler(logHandler);

            Logger.getLogger("org.peekmoon").setLevel(Level.ALL);

        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

    }

    static class KafkatFormatter extends Formatter {

        @Override
        public String format(LogRecord record) {
            StringBuilder builder = new StringBuilder();
            builder
                    .append(record.getInstant()).append(" ")
                    .append(record.getLevel()).append(" ")
                    .append(String.format("%05d ", record.getLongThreadID()))
                    .append(formatLoggerName(record.getLoggerName())).append(" - ")
                    .append(record.getMessage())
                    .append("\n");
            if (record.getThrown() != null) {
                var sw = new StringWriter();
                var pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                builder.append(sw);
            }
            return builder.toString();
        }

        private String formatLoggerName(String loggerName) {
            int fieldLength = 25;
            int cutBy = loggerName.length() - fieldLength;
            return cutBy >0 ? loggerName.substring(cutBy) : loggerName;
        }

        private void test() {
            ThreadGroup rootGroup = Thread.currentThread().getThreadGroup();
            ThreadGroup parentGroup;
            while ((parentGroup = rootGroup.getParent()) != null) {
                rootGroup = parentGroup;
            }
            Thread[] threads = new Thread[rootGroup.activeCount()];
            while (rootGroup.enumerate(threads, true ) == threads.length) {
                threads = new Thread[threads.length * 2];
            }
        }
    }


}
