package at.lechner.util;

import java.lang.management.ThreadMXBean;
import java.lang.management.ManagementFactory;

public class StopWatchCPU {
    private static final double NANOSECONDS_PER_MILLISECOND = 1000000;

    private final ThreadMXBean threadTimer;
    private final long start;
       
    public StopWatchCPU() {  
        threadTimer = ManagementFactory.getThreadMXBean();
        start = threadTimer.getCurrentThreadCpuTime();
    }   
      
    public double elapsedTime() {
        long now = threadTimer.getCurrentThreadCpuTime();
        return (now - start) / NANOSECONDS_PER_MILLISECOND;
    }
    
}