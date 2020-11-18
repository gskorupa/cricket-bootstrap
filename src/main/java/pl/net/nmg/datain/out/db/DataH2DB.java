package pl.net.nmg.datain.out.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import org.cricketmsf.out.db.H2EmbededDB;
import org.cricketmsf.out.db.KeyValueDBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.net.nmg.datain.exceptions.DatabaseException;

/**
 *
 * @author greg
 */
public class DataH2DB extends H2EmbededDB implements IntervalDataDao {

    private static final Logger logger = LoggerFactory.getLogger(H2EmbededDB.class);

    @Override
    public void loadProperties(HashMap<String, String> properties, String adapterName) {
        super.loadProperties(properties, adapterName);
    }

    public void createIntervalTables(ArrayList<Long> intervals) {
        for (int i = 0; i < intervals.size(); i++) {
            try {
                addTable("interval" + intervals.get(i), 0, true);
            } catch (KeyValueDBException ex) {
                // tabela może już istnieć
                if (ex.getCode() != KeyValueDBException.CANNOT_CREATE) {
                    logger.warn(ex.getMessage());
                }
            }
        }
    }

    @Override
    public void addTable(String tableName, int maxSize, boolean persistent) throws KeyValueDBException {
        String query;
        StringBuilder sb = new StringBuilder();
        //sb.append("create sequence if not exists user_number_seq;");
        sb.append("create table ? (")
                .append("tstamp bigint primary key,")
                .append("source bigint,")
                .append("obis1 double,")
                .append("obis2 double)");
        query = sb.toString();
        try (Connection conn = getConnection()) {
            PreparedStatement pst;
            pst = conn.prepareStatement(query);
            pst.setString(1, tableName);
            boolean updated = pst.executeUpdate() > 0;
            pst.close();
            conn.close();
            if (!updated) {
                throw new KeyValueDBException(KeyValueDBException.CANNOT_CREATE, "unable to create table " + tableName);
            }
        } catch (SQLException e) {
            throw new KeyValueDBException(e.getErrorCode(), e.getMessage());
        }
    }
    
    @Override
    public void saveData(ArrayList<IntervalData> data) throws DatabaseException{
        
    }

}
