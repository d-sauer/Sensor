package util.thread;

import java.util.concurrent.TimeUnit;

import util.logger.log;

public class mainTest {

    
    public static void main(String [] args) throws Exception {
        log.debug("create A");
        T1 t1 = new T1("A");
        t1.join();
        
        
        log.debug("start A");
        t1.iterate(2).start();
        log.debug("started A");
        
        TimeUnit.SECONDS.sleep(2);
        log.debug("threads in pool:" + WorkerPool.getPoolSize());
        
        
        log.debug("pause A");
        t1.doPause();
        
        log.debug("create B");
        T1 t2 = new T1(" B");
        TimeUnit.SECONDS.sleep(1);
        log.debug("start B");
        t2.iterate(2).sleepBetweenIteration(2000).start();
        
        
        
        
        
        (new T1("C") {
            public void work() throws Exception {
                
                log.debug("dretva C");
                
            };
        }).iterate(2).start();

        
        TimeUnit.SECONDS.sleep(3);
        log.debug("resume A");
        t1.doResume();
        
        log.debug("end main");
    }
    
    
    
    
    public static class T1 extends Worker {
        public String name;
        
        public T1(String name) {
            this.name = name;
        }
        
        public void work() throws Exception {
            for(int i = 0; i < 4; i++) {
                log.debug(name + "::" + i + " threads in pool:" + WorkerPool.getPoolSize() + "  active:" + WorkerPool.getActiveWorkerCount());
                TimeUnit.SECONDS.sleep(1);
                
                if (hasPauseSignal()) {
                    log.debug("Paused :: " + name);
                    doWait();
                    log.debug("Resume :: " + name);
                }
            }
            
        }
    }
    
    
    
}
