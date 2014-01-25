package util.session;

import java.io.Serializable;

import controllers.Application;

import play.mvc.Http.Session;
import util.logger.log;

public class SessionData implements Serializable {

    private static final long serialVersionUID = 1L;

    public SessionData() {
        Long nanoTime = System.nanoTime();
        uuid = java.util.UUID.randomUUID().toString() + ":" + nanoTime;
    }

    private String uuid;
    private Session session;
    private Long userId;
    private Boolean isAdmin = false;

    public String getUUID() {
        return uuid;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return this.session;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void isAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public Boolean isAdmin() {
        return this.isAdmin;
    }

//    private void readObject(ObjectInputStream aInputStream) throws Exception {
//        aInputStream.defaultReadObject();
//    }
//
//    private void writeObject(ObjectOutputStream aOutputStream) throws Exception {
//        aOutputStream.defaultWriteObject();
//    }

    public void save() throws Exception {
        SessionCache.save(this);
    }
    
    /**
     * Get current session data
     * @return
     * @throws Exception 
     */
    public static SessionData get() throws Exception {
        SessionData sd = null;
        String uuid = Application.session("uuid");
        
        if (uuid==null)
            log.warning("No session UUID");
        else {
            sd = SessionCache.get(uuid);
        }
        
        return sd;
    }
    
}
