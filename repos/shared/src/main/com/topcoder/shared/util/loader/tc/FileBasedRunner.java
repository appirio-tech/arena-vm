package com.topcoder.shared.util.loader.tc;

import com.topcoder.shared.util.logging.Logger;

import java.util.Properties;

/**
 * @author dok
 * @version $Revision$ Date: 2005/01/01 00:00:00
 *          Create Date: Dec 12, 2006
 */
public class FileBasedRunner {

    private static final Logger log = Logger.getLogger(FileBasedRunner.class);
    public static void main(String[] args) {

/*
        ConfigManager conf = ConfigManager.getInstance();
        String namespace = Launcher.class.getName();
        try {
            conf.add(args[0], ConfigManager.EXCEPTIONS_ALL);
            
            Property p = conf.getPropertyObject(namespace, "global_parameters");
            List l = p.list();

            Properties globalProps = new Properties();
            Property curr;
            for (Iterator it = l.iterator(); it.hasNext();) {
                curr = (Property)it.next();
                if (log.isDebugEnabled()) {
                    log.debug("name: " + curr.getName() + " val: " + curr.getValue());
                }
                globalProps.put(curr.getName(), curr.getValue());
            }

            Property loadsProperty = conf.getPropertyObject(namespace, "loads");
            List loads = loadsProperty.list();

            ArrayList loadList = new ArrayList();

            List loadProps;
            Properties loadConfig;
            Property temp;
            Property loadProperty;
            for (Iterator it = loads.iterator(); it.hasNext();) {
                loadProperty = (Property)it.next();
               loadProps = loadProperty.list();
                loadConfig = new Properties();
                for (Iterator it1 = loadProps.iterator(); it1.hasNext();) {
                    temp = (Property)it1.next();
                    loadConfig.put(temp.getName(), temp.getValue());
                }

                Object sort = loadConfig.remove(Launcher.SORT);
                if (sort ==null) {
                    throw new RuntimeException("Missing " + Launcher.SORT + " property in config for " + loadProperty.getName());
                }

                Object className = loadConfig.remove(Launcher.CLASS);
                if (className==null) {
                    throw new RuntimeException("Missing " + Launcher.CLASS + " property in config for " + loadProperty.getName());
                }
                loadConfig.putAll(globalProps);

                loadList.add(new Load(Integer.parseInt((String)sort), (String)className, loadConfig));

            }

            Collections.sort(loadList);
            TCLauncher launcher = new TCLauncher();

            HashMap connMap = new HashMap();
            connMap.put(Launcher.OLTP, new InformixSimpleDataSource((String)globalProps.remove(Launcher.OLTP)));
                connMap.put(Launcher.DW, new InformixSimpleDataSource((String)globalProps.remove(Launcher.DW)));


            ArrayList configurations = new ArrayList(loadList.size());
            ArrayList retrievers = new ArrayList(loadList.size());

            Load tempLoad;
            for (Iterator it = loadList.iterator(); it.hasNext();) {
                tempLoad = (Load)it.next();
                configurations.add(tempLoad.getConfig());
                retrievers.add(tempLoad.getClassName());
            }


            launcher.setConfigurations(configurations);
            launcher.setRetrievers(retrievers);
            launcher.setConnections(connMap);
            launcher.run();






        } catch (Exception e) {
            e.printStackTrace();
        }
*/
    }

    private final static class Load implements Comparable {
        private int sort;
        private String className;
        private Properties config;

        private Load(int sort, String className, Properties config) {
            this.sort = sort;
            this.className = className;
            this.config = config;
        }

        public int compareTo(Object o) {
            Load other = (Load)o;
            return new Integer(sort).compareTo(new Integer(other.sort));
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public Properties getConfig() {
            return config;
        }

        public void setConfig(Properties config) {
            this.config = config;
        }
    }

}
