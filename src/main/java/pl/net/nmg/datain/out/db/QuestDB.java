package pl.net.nmg.datain.out.db;

import io.questdb.cairo.CairoConfiguration;
import io.questdb.cairo.CairoEngine;
import io.questdb.cairo.DefaultCairoConfiguration;
import io.questdb.cairo.TableWriter;
import io.questdb.griffin.SqlCompiler;
import io.questdb.griffin.SqlException;
import io.questdb.griffin.SqlExecutionContextImpl;
import java.util.ArrayList;
import java.util.HashMap;
import org.cricketmsf.Adapter;
import org.cricketmsf.out.OutboundAdapter;
import org.slf4j.LoggerFactory;
import pl.net.nmg.datain.exceptions.DatabaseException;

/**
 *
 * @author greg
 */
public class QuestDB extends OutboundAdapter implements Adapter, IntervalDataDao {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(QuestDB.class);
    private CairoEngine engine;
    private String folderLocation;
    private String[] intervalsString, onlineIntervalsString;
    private ArrayList<Long> intervals = new ArrayList<>();
    private ArrayList<Long> onlineIntervals = new ArrayList<>();
    private boolean ready;

    @Override
    public void loadProperties(HashMap<String, String> properties, String adapterName) {
        super.loadProperties(properties, adapterName);
        ready = false;
        folderLocation = properties.getOrDefault("location", "data_dir");
        final CairoConfiguration configuration = new DefaultCairoConfiguration(folderLocation);
        try {
            engine = new CairoEngine(configuration);
        } catch (Exception e) {
            engine = null;
        }
        intervalsString = properties.getOrDefault("intervals", "").split(",");
        onlineIntervalsString = properties.getOrDefault("intervals-online", "").split(",");
        for (String intervalsString1 : intervalsString) {
            try {
                intervals.add(Long.parseLong(intervalsString1));
            } catch (NumberFormatException ex) {
                logger.warn(ex.getMessage());
            }
        }
        for (String intervalsString1 : onlineIntervalsString) {
            try {
                onlineIntervals.add(Long.parseLong(intervalsString1));
            } catch (NumberFormatException ex) {
                logger.warn(ex.getMessage());
            }
        }
        try {
            createIntervalTables(intervals);
        } catch (DatabaseException ex) {
            logger.error(ex.getMessage());
        }
        ready = true;
    }

    @Override
    public void createIntervalTables(ArrayList<Long> intervals) throws DatabaseException {
        final SqlExecutionContextImpl ctx;
        if (null == engine) {
            throw new DatabaseException(DatabaseException.NOT_AVAILABLE);
        }
        if (!ready) {
            throw new DatabaseException(DatabaseException.NOT_CONFIGURED);
        }
        ctx = new SqlExecutionContextImpl(engine, 1);
        SqlCompiler compiler = new SqlCompiler(engine);
        for (int i = 0; i < intervals.size(); i++) {
            try {
                compiler.compile("create table interval" + intervals.get(i) + " (value double, device long, datasource long, rawstatus long, status long, tz string, ts timestamp, ats timestamp) timestamp(ts)", ctx);
            } catch (SqlException ex) {
                //[59] unsupported column type: timest
                //[13] table already exists
                //[83] unexpected token: xyz
                if (!ex.getMessage().startsWith("[13]")) {
                    logger.error(ex.getMessage());
                    throw new DatabaseException(DatabaseException.TABLE_CREATION_ERROR, ex.getMessage());
                }
            }
        }

    }

    @Override
    public void saveData(ArrayList<IntervalData> data) throws DatabaseException {
        final SqlExecutionContextImpl ctx;
        if (null == engine) {
            throw new DatabaseException(DatabaseException.NOT_AVAILABLE);
        }
        if (!ready) {
            throw new DatabaseException(DatabaseException.NOT_CONFIGURED);
        }
        ctx = new SqlExecutionContextImpl(engine, 1);
        try{
            TableWriter writer = engine.getWriter(ctx.getCairoSecurityContext(), "interval1000");
            TableWriter.Row row;
            for (int i = 0; i < data.size(); i++) {
                row = writer.newRow();
                row.putLong(0, data.get(i).deviceId);
                row.putLong(1, data.get(i).datasourceId);
                row.putTimestamp(2, data.get(i).timestamp);
                row.putLong(3, data.get(i).rawStatus);
                row.putLong(4, data.get(i).status);
                row.putDouble(5, data.get(i).value);
                row.putStr(6, data.get(i).timeZone);
                row.putLong(7, data.get(i).acquisitionTimestamp);
                row.append();
            }
            writer.commit();
        }catch(Exception ex){
            ex.printStackTrace();
            throw new DatabaseException(DatabaseException.UNKNOWN, ex.getMessage());
        }
    }

    @Override
    public void destroy() {
        if (null != engine) {
            engine.close();
        }
        super.destroy();
    }
}
