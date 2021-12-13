package org.peekmoon.kafkat;

import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

public class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        org.slf4j.Logger log = LoggerFactory.getLogger(Application.class);
        log.error("Uncaugh error", e);
        System.err.println("uncaugh error : " + e.getMessage());
        e.printStackTrace(System.err);
        System.err.println("THREAD DUMP:");
        System.err.println(generateThreadDump());
        System.out.println("Stopping...");
        System.exit(1);
    }


//    private void createHeapDump(boolean live) {
//        try {
//            File file = File.createTempFile("SVMHeapDump-", ".hprof");
//            VMRuntime.dumpHeap(file.getAbsolutePath(), live);
//            System.out.println("  Heap dump created " + file.getAbsolutePath() + ", size: " + file.length());
//        } catch (UnsupportedOperationException unsupported) {
//            System.out.println("  Heap dump creation failed." + unsupported.getMessage());
//        } catch (IOException ioe) {
//            System.out.println("IO went wrong: " + ioe.getMessage());
//        }
//    }


    private String generateThreadDump() {
        try {
            StringBuilder dump = new StringBuilder();
            ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
            ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds(), 100);
            for (ThreadInfo threadInfo : threadInfos) {
                dump.append('"');
                dump.append(threadInfo.getThreadName());
                dump.append("\" ");
                Thread.State state = threadInfo.getThreadState();
                dump.append("\n   java.lang.Thread.State: ");
                dump.append(state);
                final StackTraceElement[] stackTraceElements = threadInfo.getStackTrace();
                for (final StackTraceElement stackTraceElement : stackTraceElements) {
                    dump.append("\n        at ");
                    dump.append(stackTraceElement);
                }
                dump.append("\n\n");
            }
            return dump.toString();
        } catch (Throwable t) {
            System.out.println("Probably in SubstrateVM : " + t.getMessage());
            t.printStackTrace(System.err);
            return "ThreadDump not available";
        }
    }

}
