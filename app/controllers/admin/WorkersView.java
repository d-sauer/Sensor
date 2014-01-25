package controllers.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;

import play.api.templates.Html;
import play.mvc.Controller;
import play.mvc.Result;
import util.logger.log;
import util.thread.Worker;
import util.thread.WorkerPool;
import util.thread.WorkerStatus;
import controllers.ModuleController;

public class WorkersView extends Controller {

    public static Result refresh() throws Exception {
        return loadModule();
    }
    
    public static Result loadModule() throws Exception {
        List<Worker> lw = WorkerPool.getWorkers();
        List<Thread> lt = getAllthread();
        
        Html h = views.html.admin.WorkersView.render(lw, lt);
        return ok(ModuleController.result(WorkersView.class, h));
    }
    
    public static List<Thread> getAllthread() {
        List<Thread> lT = new ArrayList<Thread>();
        
        Set<Thread> ts = Thread.getAllStackTraces().keySet();
        for(Thread t : ts) {
            lT.add(t);
        }
        
        
        return lT;
    }
    
    
    public static Result pause(Integer workerId) throws Exception {
        Worker worker = WorkerPool.getWorker(workerId);
        if (worker!=null) {
            log.info("Worker id %d found - do pause", workerId);
            
            worker.doPause();
            
        } else {
            log.warning("Worker id %d not found!", workerId);
        }
        
        return loadModule();
    }

    public static Result resume(Integer workerId) throws Exception {
        Worker worker = WorkerPool.getWorker(workerId);
        if (worker != null) {
            log.info("Worker id %d found - do pause", workerId);

            worker.doResume();

        } else {
            log.warning("Worker id %d not found!", workerId);
        }
        return loadModule();
    }

    public static Result stop(Integer workerId) throws Exception {
        Worker worker = WorkerPool.getWorker(workerId);
        if (worker != null) {
            log.info("Worker id %d found - do stop", workerId);
            
            if (worker.getStatus()==WorkerStatus.SLEEP)
                worker.doForceStop();
            else
                worker.doStop();
            
        } else {
            log.warning("Worker id %d not found!", workerId);
        }
        return loadModule();
    }

//    public static Result start(Integer workerId) throws Exception {
//        Worker worker = WorkerPool.getWorker(workerId);
//        if (worker != null) {
//            log.info("Worker id %d found - do stop", workerId);
//            
//            worker.doStartNew();
//            
//        } else {
//            log.warning("Worker id %d not found!", workerId);
//        }
//        return loadModule();
//    }

    public static Result stopAndDelete(Integer workerId) throws Exception {
        Worker worker = WorkerPool.getWorker(workerId);
        if (worker != null) {
            log.info("Worker id %d found - do stop", workerId);

            worker.keepInPoolAfterFinish(false);
            worker.doStop();
            
        } else {
            log.warning("Worker id %d not found!", workerId);
        }
        return loadModule();
    }
    
    
}
