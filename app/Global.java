import module.sensor.sensor.SensorWorker;
import play.Application;
import play.GlobalSettings;
import util.logger.log;

public class Global extends GlobalSettings {

    
    @Override
    public void onStart(Application app) {
        super.onStart(app);

        String absPath = app.path().getAbsolutePath();
        log.info("Application start (" + absPath + ")...");
        
        
        // Start sensor threads
        try {
            SensorWorker.runSensorWorkers();
        } catch (Exception e) {
            log.exception(e);
        }
    }

    @Override
    public void onStop(Application app) {
        super.onStop(app);

        log.info("Application stop.");
    }

}
