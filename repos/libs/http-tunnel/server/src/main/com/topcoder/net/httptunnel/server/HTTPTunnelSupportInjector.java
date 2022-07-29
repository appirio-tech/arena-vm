/*
 * HTTPTunnelSupportInjector
 *
 * Created 04/16/2007
 */
package com.topcoder.net.httptunnel.server;

import java.util.Collection;

import com.topcoder.server.listener.CompositeListenerBuilder;
import com.topcoder.server.listener.ConnectionStatusMonitorProcessorDecorator;
import com.topcoder.server.listener.ListenerFactory;
import com.topcoder.server.listener.ListenerInterface;
import com.topcoder.server.listener.ProcessorInterface;
import com.topcoder.server.listener.monitor.MonitorInterface;
import com.topcoder.shared.netCommon.CSHandlerFactory;

/**
 * HTTPTunnelSupportInjector is responsible for building a listener that will add
 * to any listener the capability of accepting HTTP tunneled connections.
 *
 * @author Diego Belfer (mural)
 * @version $Id: HTTPTunnelSupportInjector.java 71106 2008-06-10 04:28:58Z
 *          dbelfer $
 */
public class HTTPTunnelSupportInjector {

	/**
	 * Creates a new Listener that will be a composition of the Listener
	 * returned by the given Factory and and HTTPTunnelListener.
	 *
	 * @param listenerFactory
	 *            The factory for the default listener.
	 * @param csHandlerFactory
	 *            The CSHandlerFctory to use for object serialization
	 * @param processor
	 *            The processor to use for the listener
	 * @param port
	 *            The port where the default listener will bind to
	 * @param monitorDataHandler
	 *            The monitor to use
	 * @param numAcceptThreads
	 *            Number of accept threads for the default listener
	 * @param numReadThreads
	 *            Number of read threads for the default listener
	 * @param numWriteThreads
	 *            Number of write threads for the default listener
	 * @param bannedIPs
	 *            A list of banned IPs
	 * @param httpIp
	 *            The ip where the HTTP Tunnel Listener will bind to
	 * @param httpPort
	 *            The port where the HTTP Tunnel Listener will bind to
	 * @param httpNumAcceptThreads
	 *            Number of accept threads for the HTTP Tunnel Listener
	 * @param httpNumReadThreads
	 *            Number of read threads for the HTTP Tunnel Listener
	 * @param httpNumWriteThreads
	 *            Number of write threads for the HTTP Tunnel Listener
	 * @param httpBannedIPs
	 *            A list of banned IPs for the HTTP Tunnel Listener
	 *
	 * @return The built listener
	 */
	public ListenerInterface build(ListenerFactory listenerFactory, CSHandlerFactory csHandlerFactory,
			ProcessorInterface processor, int port, MonitorInterface monitorDataHandler, int numAcceptThreads,
			int numReadThreads, int numWriteThreads, Collection bannedIPs, String httpIp, int httpPort,
			int httpNumAcceptThreads, int httpNumReadThreads, int httpNumWriteThreads, Collection httpBannedIPs) {
		return build(listenerFactory, csHandlerFactory, processor, port, "0.0.0.0", monitorDataHandler,
				numAcceptThreads, numReadThreads, numWriteThreads, bannedIPs, httpIp, httpPort, httpNumAcceptThreads,
				httpNumReadThreads, httpNumWriteThreads, httpBannedIPs);
	}

	/**
	 * Creates a new Listener that will be a composition of the Listener
	 * returned by the given Factory and and HTTPTunnelListener.
	 *
	 * @param listenerFactory
	 *            The factory for the default listener.
	 * @param csHandlerFactory
	 *            The CSHandlerFctory to use for object serialization
	 * @param processor
	 *            The processor to use for the listener
	 * @param port
	 *            The port where the default listener will bind to
	 * @param listenerIp
	 *            The ip address to listen on
	 * @param monitorDataHandler
	 *            The monitor to use
	 * @param numAcceptThreads
	 *            Number of accept threads for the default listener
	 * @param numReadThreads
	 *            Number of read threads for the default listener
	 * @param numWriteThreads
	 *            Number of write threads for the default listener
	 * @param bannedIPs
	 *            A list of banned IPs
	 * @param httpIp
	 *            The ip where the HTTP Tunnel Listener will bind to
	 * @param httpPort
	 *            The port where the HTTP Tunnel Listener will bind to
	 * @param httpNumAcceptThreads
	 *            Number of accept threads for the HTTP Tunnel Listener
	 * @param httpNumReadThreads
	 *            Number of read threads for the HTTP Tunnel Listener
	 * @param httpNumWriteThreads
	 *            Number of write threads for the HTTP Tunnel Listener
	 * @param httpBannedIPs
	 *            A list of banned IPs for the HTTP Tunnel Listener
	 *
	 * @return The built listener
	 */
	public ListenerInterface build(ListenerFactory listenerFactory, CSHandlerFactory csHandlerFactory,
			ProcessorInterface processor, int port, String listenerIp, MonitorInterface monitorDataHandler,
			int numAcceptThreads, int numReadThreads, int numWriteThreads, Collection bannedIPs, String httpIp,
			int httpPort, int httpNumAcceptThreads, int httpNumReadThreads, int httpNumWriteThreads,
			Collection httpBannedIPs) {

		ListenerFactory httpListenerFactory = new HTTPTunnelListenerFactory();
		CompositeListenerBuilder builder = new CompositeListenerBuilder(new ConnectionStatusMonitorProcessorDecorator(
				processor));
		builder.addListener(listenerFactory.createListener(listenerIp, port, builder.getProcessorForInnerListeners(),
				monitorDataHandler, numAcceptThreads, numReadThreads, numWriteThreads, csHandlerFactory, bannedIPs,
				false, 0, Integer.MAX_VALUE / 2));

		builder.addListener(httpListenerFactory.createListener(httpIp, httpPort,
				builder.getProcessorForInnerListeners(), monitorDataHandler, httpNumAcceptThreads, httpNumReadThreads,
				httpNumWriteThreads, csHandlerFactory, httpBannedIPs, false, Integer.MAX_VALUE / 2 + 1,
				Integer.MAX_VALUE));

		return builder.buildListener();
	}
}
