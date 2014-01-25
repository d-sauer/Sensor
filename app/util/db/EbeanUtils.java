package util.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.persistence.Table;

import play.db.ebean.Model;
import util.logger.log;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;

public class EbeanUtils {

    public static String getTableName(Class<? extends Model> tableClass) {
        Table ta = tableClass.getAnnotation(Table.class);
        if (ta != null) {
            return ta.name();
        } else {
            return null;
        }
    }

    public static ResultSet executeQuery(String query) throws Exception {
        ResultSet rs = null;
        PreparedStatement st = null;
        try {
            Transaction tran = Ebean.beginTransaction();
            Connection conn = tran.getConnection();
            
            st = conn.prepareStatement(query);
            rs = st.executeQuery();
            
            conn.commit();
            tran.commit();
        } 
        catch (Throwable t) {
            log.error("SQL query: " + query);
            if (rs != null)
                rs.close();
        } 

        return rs;
    }

}
