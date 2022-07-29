/*
 * FarmFactoryProvider
 * 
 * Created 09/15/2006
 */
package com.topcoder.farm.client.util;


/**
 * Simple farm factory provider. It configures the Factory using the properties 
 * obtained from a file or from System properties<p>
 * 
 *  The filename is <pre>farm-config</pre> and must contain the following properties:<p>
 *  <li> host: The hostname/ip address of a controller of the farm
 *  <li> port: The port number where the controller is listening for client connections
 *  <li> threadPoolSize: The numbers of threads the factory will use to process farm messages
 *  <li> ackTimeout Time to wait for ack response
 *  <li> registrationTimeout Time to wait for registration to succeed
 *  <li> inactivityTimeout Time to wait for input channel inactivity before dropping the connection
 *  <li> keepAliveTimeout  Time to wait for ouput channel inactivity before sending a keepalive message
 *  <li> invokers.prefix Prefix used for client generartion.
 * 
 * It is possible to specify properties using system properties. All system properties must be prexied with
 * "farm." . eg: <code>farm.host</code><p>
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
@Deprecated
public class FarmFactoryProvider {
//    /**
//     * 
//     */
//    private static final String SYSTEM_PROP_PREFIX = "farm.";
//    /**
//     * Category for logging.
//     */
//    private static final Log log;
//    
//    static {
//        log = LogFactory.getLog(FarmFactoryProvider.class);
//        configure();
//    }
//
//    private static void configure() {
//        try {
//            ClientConfiguration configuration = new ClientConfiguration();
//            fillConfiguration(configuration);
//            FarmFactory.configure(configuration);
//        } catch (Exception e) {
//            log.fatal("FarmClient couldn't be configured",e);
//        }
//    }
//
//    /**
//     * Returns the configured instance of the FarmFactory.
//     * 
//     * @return the instance
//     */
//    public static FarmFactory getConfiguredFarmFactory() {
//        return FarmFactory.getInstance();
//    }
//
//    private static void fillConfiguration(ClientConfiguration configuration) {
//        try {
//            Properties properties = getConfigurationProperties();
//            String host = properties.getProperty("host");
//            int port = Integer.parseInt((properties.getProperty("port")));
//            int processorThreadPoolSize = Integer.parseInt((properties.getProperty("threadPoolSize")));
//            int ackTimeout = Integer.parseInt((properties.getProperty("ackTimeout")));
//            int registrationTimeout = Integer.parseInt((properties.getProperty("registrationTimeout")));
//            int inactivityTimeout = Integer.parseInt((properties.getProperty("inactivityTimeout")));
//            int keepAliveTimeout = Integer.parseInt((properties.getProperty("keepAliveTimeout")));
//
//            String invokersPrefix = properties.getProperty("invokers.prefix","");
//            configuration.setAddresses(new InetSocketAddress[] {new InetSocketAddress(InetAddress.getByName(host), port)});
//            configuration.setProcessorThreadPoolSize(processorThreadPoolSize);
//            configuration.setInvokersPrefix(invokersPrefix);
//            configuration.setAckTimeout(ackTimeout);
//            configuration.setRegistrationTimeout(registrationTimeout);
//            configuration.setInactivityTimeout(inactivityTimeout);
//            configuration.setKeepAliveTimeout(keepAliveTimeout);
//        } catch (NumberFormatException e) {
//            throw (IllegalStateException) new IllegalStateException("Invalid configuration: port, pool or timeouts invalid").initCause(e);
//        } catch (UnknownHostException e) {
//            throw (IllegalStateException) new IllegalStateException("Invalid configuration: host invalid").initCause(e);
//        }
//    }
//
//    private static Properties getConfigurationProperties() {
//        try {
//            Properties properties = new Properties();
//            properties.load(FarmFactory.class.getResourceAsStream("/farm-config.properties"));
//            return properties;
//        } catch (IOException e) {
//            Properties properties = new Properties();
//            log.info("Could not open /farm-config.properties from classpath, using system properties with prefix '"+SYSTEM_PROP_PREFIX+"'");
//            Properties systemProps = System.getProperties();
//            Set set = systemProps.keySet();
//            int prefixSize = SYSTEM_PROP_PREFIX.length();
//            for (Iterator it = set.iterator(); it.hasNext(); ) {
//                String key = (String) it.next();
//                if (key.startsWith(SYSTEM_PROP_PREFIX)) {
//                    properties.setProperty(key.substring(prefixSize), systemProps.getProperty(key));
//                }
//            }
//            return properties;
//        }
//    }
}
