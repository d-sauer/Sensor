package module.sensor.sensor;

import java.util.List;

import module.sensor.models.DBSensorGroup;
import util.logger.log;
import util.thread.Worker;
import util.thread.WorkerPool;

public abstract class SensorWorker extends Worker {

    private Long sensorGroupId;

    public static class SensorWorkerInfo {
        public Class<? extends SensorWorker> swClass;
        public Long                          swSleep;
    }

    public void setGroupId(Long groupId) {
        this.sensorGroupId = groupId;
    }

    public Long getGroupId() {
        return sensorGroupId;
    }

    public static void runSensorWorkers() throws Exception {
        List<Worker> lstW = WorkerPool.getWorkers();

        List<DBSensorGroup> lsg = DBSensorGroup.findActive(true);

        for (DBSensorGroup g : lsg) {
            if (g.user.active != 1)
                continue;
            
            SensorGroupInterface sg = g.getGroupClass();

            // Check if SWorker exists, if not exists, create new
            SensorWorker sw = getSWorkerFromPool(lstW, g.id);
            
            if (sw == null || (sw!=null && !sw.isAlive())) {
                sw = createSensorWorker(g.id);
                
//                SensorWorkerInfo swi = sg.getWorker();
//                Class cw = swi.swClass;
//
//                if (cw != null) {
//                    log.debug("create sensor worker:" + g.id);
//                    Object o = cw.newInstance();
//
//                    if (o != null && o instanceof SensorWorker) {
//                        sw = (SensorWorker) o;
//                        sw.setGroupId(g.id);
//                        sw.setName("SensorWorker::gid:" + g.id);
//                        
//                        
//                        log.debug("start sensor worker:" + g.id + " sleep:" + swi.swSleep);
//                        sw.infiniteLoop(swi.swSleep).start();
//                    } else {
//                        log.info(o.getClass().getName() + " is not instanceof " + SensorWorker.class.getName());
//                    }
//                }
            } 
        }
    }

    public static SensorWorker createSensorWorker(Long sensorGroupId) throws Exception {
        SensorWorker sw = null;
        DBSensorGroup g = DBSensorGroup.findById(sensorGroupId);
        
        SensorGroupInterface sg = g.getGroupClass();
        
        SensorWorkerInfo swi = sg.getWorker();
        Class cw = swi.swClass;

        if (cw != null) {
            log.debug("create sensor worker:" + g.id);
            Object o = cw.newInstance();

            if (o != null && o instanceof SensorWorker) {
                sw = (SensorWorker) o;
                sw.setGroupId(g.id);
                sw.setName("SensorWorker::gid:" + g.id);
                
                
                log.debug("start sensor worker:" + g.id + " sleep:" + swi.swSleep);
                sw.infiniteLoop(swi.swSleep).start();
            } else {
                log.info(o.getClass().getName() + " is not instanceof " + SensorWorker.class.getName());
            }
        }
        
        return sw;
    }
    
    private static SensorWorker getSWorkerFromPool(List<Worker> lstW, Long groupId) {
        SensorWorker sw = null;
        for (Worker w : lstW) {
            if (w instanceof SensorWorker) {
                if (groupId.longValue() == ((SensorWorker) w).getGroupId().longValue()) {
                    sw = ((SensorWorker) w);
                    break;
                }

            }
        }

        return sw;
    }

    public static SensorWorker getSensorWorker(Long groupId) {
        return getSWorkerFromPool(WorkerPool.getWorkers(), groupId);
    }
}
