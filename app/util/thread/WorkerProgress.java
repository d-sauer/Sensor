package util.thread;

public class WorkerProgress {
    private String  status = "";
    private Integer currentStep = 1;
    private Integer maxSteps = 1;
    private Integer countSteps = 0;

    public void setProgressMaxStep(Integer maxSteps) {
        this.maxSteps = maxSteps;
    }

    public void setProgress(String status, Object ... args) {
        setProgress(String.format(status, args));
    }

    public void setProgress(String status) {
        this.status = status;
        
        countSteps++;
        
        if(countSteps > maxSteps)
            maxSteps = countSteps;
    }


    public String getProgress() {
        return this.status;
    }

    public Integer getCurrentStep() {
        return this.currentStep;
    }

    public Integer getProgressPercentage() {
        return (currentStep / maxSteps) * 100;
    }

    public Integer getMaxStep() {
        return maxSteps;
    }
    
    public void resetCountStep() {
        countSteps = 0;
    }
}