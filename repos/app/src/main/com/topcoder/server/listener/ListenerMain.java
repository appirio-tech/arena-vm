/*
 * Copyright (C) - 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.server.listener;

import java.io.IOException;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.topcoder.net.httptunnel.server.HTTPTunnelSupportInjector;
import com.topcoder.server.listener.monitor.EmptyMonitor;
import com.topcoder.server.listener.monitor.MonitorCSHandlerFactory;
import com.topcoder.server.listener.monitor.MonitorChatHandler;
import com.topcoder.server.listener.monitor.MonitorDataHandler;
import com.topcoder.server.listener.monitor.MonitorInterface;
import com.topcoder.server.listener.monitor.MonitorProcessor;
import com.topcoder.server.listener.monitor.MonitorProperties;
import com.topcoder.server.listener.monitor.MonitorTask;
import com.topcoder.server.listener.monitor.NullArenaMonitor;
import com.topcoder.shared.netCommon.CSHandlerFactory;
import com.topcoder.shared.netCommon.ReflectUtils;

/**
 * The entry point for the listener component and the server.
 * <p>
 * Version 1.1 - Module Assembly - TopCoder Competition Engine - Web Socket Listener
 * <ol>Add web socket connector</ol>
 * </p>
 * <p>
 * Version 1.2 - Module Assembly - Web Socket Listener - Porting Round Load Related Events
 * <ol>
 *      <li>Set monitorProcessor object to WebSocketConnector to enable it process monitor request</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.3 (Make Admin Listener Work With Main Listener Through Loopback Address v1.0)
 * <ol>
 *      <li>Update {@link #start()} method.</li>
 * </ol>
 * </p>
 * @author  Timur Zambalayev, gondzo, TCSASSEMBLER
 * @version 1.3
 */
public final class ListenerMain {

    static final int DEFAULT_PORT = 5001;

    private static final long MONITOR_DELAY = 1000;
    private static final int MONITOR_PORT_INC = 1;
    private static final ListenerFactory DEFAULT_LISTENER_FACTORY = new NBIOListenerFactory();
    private static final ArenaProcessor DEFAULT_PROCESSOR = new DefaultProcessor();
    private static final String CLASS_NAME = "ListenerMain";
    private static final Thread SHUTDOWN_HOOK = new Thread(new Runnable() {
        public void run() {
            stop();
        }
    });

    private static final Logger cat = Logger.getLogger(ListenerConstants.PACKAGE_NAME + CLASS_NAME);

    private static int port = DEFAULT_PORT;
    private static ArenaProcessor processor = DEFAULT_PROCESSOR;
    private static ListenerFactory listenerFactory = DEFAULT_LISTENER_FACTORY;
    private static ListenerInterface listener;
    private static ListenerInterface monitorListener;
    private static int monitorPort;
    private static Timer monitorTimer;
    private static WebSocketConnector wsc;
    private static int WEB_SOCKET_CONNECTOR_PORT=5555;

    private ListenerMain() {
    }

    private static void setPort(int p) {
        port = p;
    }

    private static void setMonitorPort(int p) {
        monitorPort = p;
    }

    private static void setProcessor(ArenaProcessor proc) {
        processor = proc;
    }

    private static void setListenerFactory(ListenerFactory lf) {
        listenerFactory = lf;
    }

    /**
     * Public api to get web socket connector
     *
     * @return WebSocketConnector
     */
    public static WebSocketConnector getSocketConnector(){
        return wsc;
    }

    private static void start() throws IOException {
        info("starting, port=" + port + ", processor=" + processor + ", monitorPort=" + monitorPort +
                ", listenerFactory=" + listenerFactory);
        MonitorInterface monitor;
        if (monitorPort > 0) {
            MonitorDataHandler monitorDataHandler = new MonitorDataHandler();
            monitor = monitorDataHandler;
            processor.setArenaMonitor(monitorDataHandler);
        } else {
            monitor = new EmptyMonitor();
            processor.setArenaMonitor(new NullArenaMonitor());
        }
        listener = buildListener(monitor);
        MonitorProcessor monitorProcessor = new MonitorProcessor(SHUTDOWN_HOOK,
                (MonitorDataHandler) monitor, listener);
        if (monitorPort > 0) {
            ((MonitorDataHandler) monitor).setChatHandler(new MonitorChatHandler(monitorProcessor));
            Collection monitorAllowedIPs = MonitorProperties.getAllowedIPs();
            monitorListener = listenerFactory.createListener(IPBlocker.ALLOWED_LOOP_BACK_IP, monitorPort, monitorProcessor, new EmptyMonitor(), 0, 0, 0,
                    new MonitorCSHandlerFactory(), monitorAllowedIPs, true);
            monitorListener.start();
            monitorTimer = new Timer(true);
            TimerTask task = new MonitorTask(monitorProcessor);
            monitorTimer.schedule(task, MONITOR_DELAY, MONITOR_DELAY);
        }
        listener.start();
        info("started");

        wsc = new WebSocketConnector(WEB_SOCKET_CONNECTOR_PORT);
        wsc.setProcessor(processor);
        wsc.setMonitorProcessor(monitorProcessor);
        boolean started = wsc.start();
        info("started web socket connector: " + started);

    }

    private static ListenerInterface buildListener(MonitorInterface monitorDataHandler) {
        int numAcceptThreads = ListenerProperties.numThreads(ListenerProperties.NUM_ACCEPT_THREADS);
        int numReadThreads = ListenerProperties.numThreads(ListenerProperties.NUM_READ_THREADS);
        int numWriteThreads = ListenerProperties.numThreads(ListenerProperties.NUM_WRITE_THREADS);
        Collection bannedIPs = ListenerProperties.getBannedIPs();

        if (ListenerProperties.useHTTPTunnel()) {
            int httpPort = ListenerProperties.bindPort(ListenerProperties.HTTP_LISTENER_PREFIX);
            String httpIp   = ListenerProperties.bindIp(ListenerProperties.HTTP_LISTENER_PREFIX);
            int httpNumAcceptThreads = ListenerProperties.numThreads(ListenerProperties.HTTP_LISTENER_PREFIX, ListenerProperties.NUM_ACCEPT_THREADS);
            int httpNumReadThreads = ListenerProperties.numThreads(ListenerProperties.HTTP_LISTENER_PREFIX, ListenerProperties.NUM_READ_THREADS);
            int httpNumWriteThreads = ListenerProperties.numThreads(ListenerProperties.HTTP_LISTENER_PREFIX, ListenerProperties.NUM_WRITE_THREADS);
            Collection httpBannedIPs = ListenerProperties.getBannedIPs(ListenerProperties.HTTP_LISTENER_PREFIX);

            return new HTTPTunnelSupportInjector().build(listenerFactory, new DefaultCSHandlerFactory(), processor, port,
                    monitorDataHandler, numAcceptThreads, numReadThreads, numWriteThreads,
                    bannedIPs, httpIp, httpPort, httpNumAcceptThreads, httpNumReadThreads, httpNumWriteThreads, httpBannedIPs);
        } else {
            return listenerFactory.createListener(
                    port,
                    new ConnectionStatusMonitorProcessorDecorator(processor),
                    monitorDataHandler,
                    numAcceptThreads,
                    numReadThreads,
                    numWriteThreads,
                    new DefaultCSHandlerFactory(),
                    bannedIPs,
                    false);
        }
    }

    private static void stop() {
        info("stopping");
        if (monitorListener != null) {
            monitorTimer.cancel();
            monitorListener.stop();
        }
        listener.stop();
        info("stopped");
    }

    public static void info(String msg) {
        cat.info(msg);
    }

    public static void error(Object msg) {
        cat.error(msg);
    }

    private static String getClassName(String simpleName) {
        if (simpleName.indexOf('.') != -1) {
            return simpleName;
        }
        return "com.topcoder.server.listener." + simpleName;
    }

    /**
     * The main method (the entry point). For more information see app/docs/how_to_start_the_listener.txt.
     *
     * @param   argv        the parameters.
     */
    public static void main(String[] argv) {
        int len = argv.length;
        if (len > 0) {
            setPort(Integer.parseInt(argv[0]));
            if (len > 1) {
                String simpleName = argv[1];
                if (simpleName.equals("d")) {
                    simpleName = "DefaultProcessor";
                }
                String className = getClassName(simpleName);
                ArenaProcessor p = (ArenaProcessor) ReflectUtils.newInstance(className);
                if (p == null) {
                    System.out.println("processor is null");
                    return;
                }
                setProcessor(p);
                if (len > 2) {
                    setMonitorPort(Integer.parseInt(argv[2]));
                    if (len > 3) {
                        simpleName = argv[3];
                        if (simpleName.equals("d")) {
                            simpleName = "NBIOListenerFactory";
                        }
                        className = getClassName(simpleName);
                        ListenerFactory lf = (ListenerFactory) ReflectUtils.newInstance(className);
                        if (lf == null) {
                            System.out.println("listener factory is null");
                            return;
                        }
                        setListenerFactory(lf);

                        if (len > 4) {
                            setDotNetPort(Integer.parseInt(argv[4]));
                            if (len>5){
                                WEB_SOCKET_CONNECTOR_PORT = Integer.parseInt(argv[5]);
                            }
                        }
                    }
                }
            }
        }
        if (monitorPort < 0) {
            setMonitorPort(port + MONITOR_PORT_INC);
        }
        Runtime.getRuntime().addShutdownHook(SHUTDOWN_HOOK);
        try {
            start();
        } catch (IOException e) {
            error(e);
            error("failed to start the listener");
            return;
        }
    }

    private static int dotNetPort;

    private static void setDotNetPort(int port) {
        dotNetPort = port;
    }

    public static int getDotNetPort() {
        return dotNetPort;
    }

}
