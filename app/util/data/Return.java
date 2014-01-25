package util.data;

public class Return<R> {

    private boolean   isOk = true;
    private R         returnObject;
    private String    message;
    private Throwable t;
    
    public Return() {
    }

    public Return(R returnObject) {
        setReturnObject(returnObject);
    }

    public Return(boolean isOk) {
        setOk();
    }

    public Return(boolean isOk, String message) {
        setOk();
        setMessage(message);
    }

    public Return(boolean isOk, R returnObject) {
        setOk();
        setMessage(message);
        setReturnObject(returnObject);
    }

    public void set(boolean isOk) {
        this.isOk = isOk;
    }

    public void set(boolean isOk, R returnObject) {
        this.isOk = isOk;
        setReturnObject(returnObject);
    }

    public void set(boolean isOk, String message) {
        this.isOk = isOk;
        setMessage(message);
    }
    
    //
    // OK ...
    //
    public void setOk() {
        isOk = true;
    }

    public void setOk(String message) {
        setOk();
        setMessage(message);
    }

    public void setOk(R returnObject) {
        setOk();
        setReturnObject(returnObject);
    }

    public void setOk(String message, R returnObject) {
        setOk();
        setMessage(message);
        setReturnObject(returnObject);
    }

    //
    // ERROR ...
    //
    public void setError() {
        isOk = false;
    }

    public void setError(String message) {
        setError();
        setMessage(message);
    }

    public void setError(String message, R returnObject) {
        setError();
        setMessage(message);
        setReturnObject(returnObject);
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setError(R returnObject) {
        setError();
        setReturnObject(returnObject);
    }

    public void setReturnObject(R obj) {
        this.returnObject = obj;
    }

    public R getReturnObject() {
        return this.returnObject;
    }

    public void setThrowable(Throwable t) {
        this.t = t;
    }

    public Throwable getThrowable() {
        return this.t;
    }
}
