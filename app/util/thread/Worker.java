package util.thread;

import java.util.ArrayList;
import java.util.List;

import util.logger.log;

public abstract class Worker extends Thread {

    private Integer id;
    private Long whenCreated;
    private Long whenStop;
    private Long whenStart;
    private Long whenEnd;
    private Exception th;
    private boolean stopWorker = false;
    private boolean removeFromPool = true;
    private boolean stopOnException = false;
    private WorkerStatus status = WorkerStatus.NEW;
    
    // iteration
    private boolean infinity = false;
    private Integer iteration;

    // sleep
    private Long sleepBetweenIterationMs;
    private Long startSleep;
    private Long stopSleep;
    private boolean isSleep = false;

    // pause
    private Boolean pauseWorker = false;
    private Long startPause;
    private Long stopPause;
    private Long pauseMs;

    // Progress
    public final WorkerProgress progress = new WorkerProgress();

    // Info
    private List<String> info = new ArrayList<String>();

    public Worker() {
        super();
        whenCreated = System.currentTimeMillis();
        id = WorkerPool.getNewJobId();
        setName("worker-" + id);

        WorkerPool.registerJob(this);
    }

    /**
     * Run worker in specific number of itetation
     * 
     * @param iteration
     * @return
     */
    public Worker iterate(int iteration) {
        this.iteration = iteration;
        this.infinity = false;

        return this;
    }

    /**
     * Run worker infinitely
     * 
     * @return
     */
    public Worker infiniteLoop() {
        this.iteration = null;
        this.infinity = true;

        return this;
    }

    /**
     * Infinite loop with sleep beetwen iteration in MS
     * @param sleepMs
     * @return
     */
    public Worker infiniteLoop(long sleepMs) {
        this.iteration = null;
        this.infinity = true;
        sleepBetweenIteration(sleepMs);

        return this;
    }

    public Worker sleepBetweenIteration(long sleepMs) {
        this.sleepBetweenIterationMs = sleepMs;

        return this;
    }

    public Worker noSleepBetweenIteration() {
        this.sleepBetweenIterationMs = null;

        return this;
    }

    /**
     * Keep worker in Worker pool, after worker is finished
     */
    public void keepInPoolAfterFinish(boolean keep) {
        removeFromPool = !keep;
    }

    /**
     * Stop worker loop on exception.
     * Default is FALSE
     */
    public void stopOnException(boolean stop) {
        stopOnException = stop;
    }

    @Override
    public synchronized void start() {
        if (!this.isAlive())
            super.start();
    }

    @Override
    public final void run() {
        try {

            //
            // PROCESS THREAD
            //
            int i = 0;

            while (!stopWorker) {
                if (infinity == false && iteration != null) {
                    i++;
                    if (i > iteration)
                        break;
                }

                //
                // work...
                //
                pauseWorker();

                whenStart = System.currentTimeMillis();
                if (stopWorker == false)
                    try {
                        
                        status = WorkerStatus.RUNNING;
                        progress.resetCountStep();
                        
                        work();
                    
                    } catch (Exception e) {
                        if (stopOnException)
                            throw e;
                        else {
                            th = e;
                            log.exception(e);
                        }
                    }
                whenEnd = System.currentTimeMillis();

                //
                // manage sleep
                //
                if (sleepBetweenIterationMs != null) {
                    try {
                        isSleep = true;
                        startSleep = System.currentTimeMillis();
                        status = WorkerStatus.SLEEP;
                        
                        Thread.sleep(sleepBetweenIterationMs);

                    } catch (InterruptedException ie) {
                        // If worker is't stop by calling doStop method or interupt with pause during sleep
                        if (stopWorker == false && pauseWorker == false)
                            throw ie;
                    } catch (Exception e) {
                        throw e;
                    } finally {
                        stopSleep = System.currentTimeMillis();
                        isSleep = false;
                    }
                }
            }

        } catch (Exception e) {
            th = e;
            log.exception(e);
        } finally {
            whenStop = System.currentTimeMillis();
            status = WorkerStatus.STOP;

            // if thread exited with exception, don't remove from pool
            if (th == null && removeFromPool == true)
                removeFromPool();
        }
    }

    /**
     * Implement Worker logic
     * 
     * @throws Exception
     */
    public abstract void work() throws Exception;

    public boolean isFinishWithException() {
        return th != null ? true : false;
    }

    public boolean isFinish() {
        return whenEnd != null ? true : false;
    }

    public Exception getException() {
        return th;
    }

    public Integer getWorkerId() {
        return id;
    }
    
    /**
     * Set flag for gracefully stop worker.
     */
    public void doStop() {
        stopWorker = true;
    }


    /**
     * Force stop with {@link #interrupt()}
     */
    public void doForceStop() {
        doStop();
        interrupt();
    }

    /**
     * True if worker is need gracefully to stop
     * 
     * @return
     */
    public boolean hasStopSignal() {
        return stopWorker;
    }

    /**
     * True if worker is stopperd
     * 
     * @return
     */
    public boolean isStopped() {
        if (stopWorker == true && whenEnd != null)
            return true;
        else
            return false;
    }

    public boolean isPaused() {
        if (status == WorkerStatus.PAUSED)
            return true;
        else
            return false;
    }

    public boolean isNew() {
        if (status == WorkerStatus.NEW)
            return true;
        else
            return false;
    }
    
    public boolean isSleep() {
        return isSleep;
    }
    
    public Long sleepUntil() {
        return startSleep + sleepBetweenIterationMs;
    }

    public Long remainSleep() {
        return sleepUntil() - System.currentTimeMillis();
    }

    
    private void pauseWorker() throws Exception {
        if (pauseWorker == true) {
            synchronized (pauseWorker) {
                try {
                    startPause = System.currentTimeMillis();
                    status = WorkerStatus.PAUSED;

                    // double check for wait
                    if (pauseMs == null) {
                        while (hasPauseSignal()) {
                            pauseWorker.wait();
                        }
                    } else {
                        long pauseTo = startPause + pauseMs;

                        while (hasPauseSignal() && pauseMs != null) {
                            pauseWorker.wait(pauseMs);

                            // double check, provjeri dali je isteklo vrijeme
                            if (pauseMs != null) {
                                long remainingMs = pauseTo - System.currentTimeMillis();
                                if (remainingMs > 0)
                                    pauseMs = remainingMs;
                                else
                                    pauseMs = null;
                            }
                        }
                    }

                } catch (Exception e) {
                    throw e;
                } finally {
                    stopPause = System.currentTimeMillis();
                    status = WorkerStatus.RUNNING;
                }

            }
        }
    }

    
    
    public void doPause() {
        pauseWorker = true;
        if (isSleep) {
            this.interrupt();
        }
    }

    public void doPause(Long pauseMs) {
        pauseWorker = true;
        this.pauseMs = pauseMs;
    }

    /**
     * If worker has pause signal, call method <b>pauseWorker</b> to pause
     * @return
     */
    public boolean hasPauseSignal() {
        return pauseWorker;
    }
    
    /**
     * If worker is in pause, wait for object to resume.
     * Use this method inside implemented logic in {@link #work()}
     * 
     * @throws Exception
     */
    public void doWait() throws Exception {
        pauseWorker();
    }

    public void doResume() {
        // if worker is paused - resume
        if (pauseWorker == true) {
            synchronized (pauseWorker) {
                pauseWorker.notifyAll();
                pauseWorker = false;
                pauseMs = null;
            }
        } else if (isSleep()) {
            this.interrupt();
        } else if (hasStopSignal()) {
            stopWorker = false;
        }
    }

    /**
     * Remove from pool if worker is finished
     * 
     * @return
     */
    public boolean removeFromPool() {
        if (getStatus() == WorkerStatus.STOP)
            return WorkerPool.removeJob(this);
        else
            return false;
    }

    public List<String> getInfo() {
        return info;
    }

    public void setInfo(List<String> info) {
        this.info = info;
    }

    public List<String> addInfo(String info) {
        this.info.add(info);

        return this.info;
    }
    
    public WorkerStatus getStatus() {
        return this.status;
    }
    
    public Long getWhenCreated() {
        return whenCreated;
    }

    public Long getWhenEnded() {
        return whenEnd;
    }
    
    public Long getWhenStarted() {
        return whenStart;
    }
    
}
