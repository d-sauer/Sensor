package util.logger;

public enum LogLevel {
    /**
     * FATAL level designates very severe error events that will presumably lead the application to abort
     */
    FATAL(1000),
    
    /**
     * ERROR level designates error events that might still allow the application to continue running. 
     */
    ERROR(2000),

    /**
     * WARN level designates potentially harmful situations. 
     */
    WARNING(3000),
    
    /**
     *  INFO level designates informational messages that highlight the progress of the application at coarse-grained level.
     */
    INFO(4000),
    
    /**
     *  DEBUG Level designates fine-grained informational events that are most useful to debug an application. 
     */
    DEBUG(5000),
    
    /**
     * TRACE Level designates finer-grained informational events than the DEBUG
     */
    TRACE(6000);
    
    private int priority;
    
    LogLevel(int priority) {
        this.priority = priority;
    }
}
