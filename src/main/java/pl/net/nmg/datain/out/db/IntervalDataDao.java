package pl.net.nmg.datain.out.db;

import java.util.ArrayList;
import pl.net.nmg.datain.exceptions.DatabaseException;

/**
 *
 * @author greg
 */
public interface IntervalDataDao {
    public void createIntervalTables(ArrayList<Long> intervals) throws DatabaseException;
    public void saveData(ArrayList<IntervalData> data) throws DatabaseException;
}
