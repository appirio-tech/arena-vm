package com.topcoder.shared.util.loader.tc;

import com.topcoder.shared.util.DBMS;
import com.topcoder.shared.util.loader.Launcher;
import com.topcoder.shared.util.sql.InformixSimpleDataSource;
import junit.framework.TestCase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 * @author dok
 * @version $Revision$ Date: 2005/01/01 00:00:00
 *          Create Date: Dec 12, 2006
 */
public class CoderTestCase extends TestCase {



    public void testRun() {
        TCLauncher launcher = new TCLauncher();


        HashMap connMap = new HashMap();
        try {
            connMap.put(Launcher.OLTP, new InformixSimpleDataSource("jdbc:informix-sqli://63.118.154.190:1526/informixoltp:INFORMIXSERVER=devinformix10_shm;user=coder;password=altec"));
            connMap.put(Launcher.DW, new InformixSimpleDataSource("jdbc:informix-sqli://63.118.154.190:1526/topcoder_dw:INFORMIXSERVER=devinformix10_shm;user=coder;password=altec"));

            ArrayList retrievers = new ArrayList();
            retrievers.add(Coder.class.getName());

            ArrayList configurations = new ArrayList();
            Properties p = new Properties();
            p.put(Launcher.SOURCE_DB, Launcher.OLTP);
            p.put(Launcher.TARGET_DB, Launcher.DW);
            configurations.add(p);

            launcher.setConfigurations(configurations);
            launcher.setRetrievers(retrievers);
            launcher.setConnections(connMap);
            launcher.run();
        } catch (SQLException e) {
            DBMS.printSqlException(true, e);
        } catch (Exception e) {
            e.printStackTrace();
        } 



    }

}
