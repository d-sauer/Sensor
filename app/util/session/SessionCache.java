package util.session;

import java.io.InvalidClassException;
import java.util.Date;
import java.util.HashMap;

import module.core.models.DBCSession;
import util.SerializationUtils;
import util.logger.log;

public class SessionCache {

    private static volatile SessionCache sessionCahe;
    private final HashMap<String, SessionData> sessionCacheData = new HashMap<String, SessionData>();

    private SessionCache() {
    }

    public static SessionCache getInstance() {
        if (sessionCahe == null)
            sessionCahe = new SessionCache();

        return sessionCahe;
    }

    /**
     * Save to database
     * 
     * @param sd
     * @throws Exception
     */
    public static void save(SessionData sd) throws Exception {
        SessionCache sc = SessionCache.getInstance();
        SessionData tmpSd = sc.sessionCahe.sessionCacheData.get(sd.getUUID());

        if (tmpSd == null) {
            sc.sessionCacheData.put(sd.getUUID(), sd);
            log.trace("session %s to memory", sd.getUUID());
        }

        DBCSession dbSession = DBCSession.findByUUID(sd.getUUID());
        if (dbSession == null) {  // insert
            dbSession = new DBCSession();
            dbSession.uuid = sd.getUUID();
            dbSession.created = new java.sql.Timestamp(new Date().getTime());
            dbSession.sessionData = SerializationUtils.writeObjectBase64(sd);
            dbSession.save();

            log.info("session %s insert to db", sd.getUUID());
        } else {                // update
            dbSession.sessionData = SerializationUtils.writeObjectBase64(sd);
            dbSession.save();
            log.info("session %s update to db", sd.getUUID());
        }
    }

    public static SessionData get(String uuid) throws Exception {
        SessionCache sc = SessionCache.getInstance();

        SessionData sd = sc.sessionCacheData.get(uuid);
        if (sd == null) {
            // go to database
            DBCSession dbSession = DBCSession.findByUUID(uuid);
            if (dbSession != null && dbSession.sessionData != null && dbSession.sessionData.length() != 0) {
                try {
                Object o = SerializationUtils.readObject(dbSession.sessionData);
                if (o instanceof SessionData) {
                    sd = (SessionData) o;
                    log.info("session %s get from db", uuid);
                    sc.sessionCacheData.put(uuid, sd);
                } else
                    log.warning("session %s data not instanceof %s", uuid, SessionData.class.getName());
                } catch (InvalidClassException ice) {
                    dbSession.delete();
                    sd = null;
                    log.exception(ice);
                }
            } else {
                log.warning("session %s not in db", uuid);
            }
        } else {
            log.info("session %s get from memory", uuid);
        }

        return sd;
    }
    
    public static void remove(String uuid) throws Exception {
        SessionCache sc = SessionCache.getInstance();
        
        sc.sessionCacheData.remove(uuid);
        DBCSession dbSession = DBCSession.findByUUID(uuid);
        if (dbSession!=null) {
            dbSession.delete();
        }
    }
}
