package util.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class WorkerPool {
    
    private static final AtomicInteger workerIndexer = new AtomicInteger(0);
    private static final List<Worker> workerPool = new ArrayList<Worker>();

    
    public static Integer getNewJobId() {
        Integer newJobId = workerIndexer.getAndIncrement();
        return newJobId;
    }
    
    public static void registerJob(Worker _work) {
        workerPool.add(_work);
    }
    
    public static boolean removeJob(Worker _work) {
        return workerPool.remove(_work);
    }

    public static List<Worker> getWorkers() {
        return workerPool;
    }

    public static Worker getWorker(Integer workerId) {
        Worker w = null;
        for(Worker tmpW : getWorkers()) {
            if (tmpW.getWorkerId().intValue() == workerId.intValue()) {
                w = tmpW;
                break;
            }
        }
        
        return w;
    }
    
    /**
     * Get number of workers in pool
     * @return
     */
    public  static int getPoolSize() {
        return workerPool.size();
    }
    
    public  static int getActiveWorkerCount() {
        int i = 0;
        for(Worker w : workerPool) {
            if (w.isAlive())
                i++;
        }
        
        return i;
    }
}
