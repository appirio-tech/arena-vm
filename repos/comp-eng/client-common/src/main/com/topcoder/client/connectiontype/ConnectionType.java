/*
 * ConnectionType
 * 
 * Created 04/18/2007
 */
package com.topcoder.client.connectiontype;

import java.net.Authenticator;
import java.util.ArrayList;

import com.topcoder.net.httptunnel.client.HTTPTunnelClientConnector;

/**
 * Defines a base class which represents all possible connection types as well as their behaviors.
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public abstract class ConnectionType {
    /** Represents all connection types that is available to this JVM. */
	private static ConnectionType[] availableTypes;
    /** Represents the unique ID of the connection type. */
	private String id;
    /** Represents a name of the connection type. */
    private String name;
    /** Represents a detailed description of the connection type. */
    private String description;
    /** Represents the authenticator which is used when a proxy asks for username and password. */
    private static Authenticator authenticator = null;
    
    /**
     * Creates a new connection type with the given unique ID, name and detailed description.
     * 
     * @param id the unique ID of the connection type.
     * @param name the name of the connection type.
     * @param description the detailed description of the connection type.
     */
    public ConnectionType(String id, String name, String description) {
    	this.id = id;
        this.name = name;
        this.description = description;
    }

    /**
     * Returns the description for this connection type.
     * 
     * @return A string with the description of it.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the name for this connection type.
     * 
     * @return A string with the name of it.
     */
    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }


    /**
     * The Id of this connection type
     * 
     * @return The id
     */
	public String getId() {
		return id;
	}
	
    /**
     * Gets a flag indicating if this connection is tunneled.
     * 
     * @return <code>true</code> if it is tunneled; <code>false</code> otherwise.
     */
    public abstract boolean isTunneled();

    /**
     * Gets a flag indicating if this connection is available to the current JVM.
     * 
     * @return <code>true</code> if it is available; <code>false</code> otherwise.
     */
    public abstract boolean isAvailable(); 

    /**
     * Gets a flag indicating if this connection supports SSL.
     * 
     * @return <code>true</code> if it supports SSL; <code>false</code> otherwise.
     */
    public abstract boolean isSSLSupported();
    
    /**
     * Sets up the connection type settings before it is ready to use.
     */
    public void select() {}

    /**
     * Cleans up the connection type settings before other connection type can be used.
     */
    public void unselect() {};

    /**
     * Returns all availables connection types.
     * 
     * @return the connection types.
     */
    public static ConnectionType[] getAvailableTypes() {
    	if (availableTypes == null) {
    		ArrayList availables = new ArrayList();
    		for (int i = 0; i < CONNECTION_TYPES.length; i++) {
    			if (CONNECTION_TYPES[i].isAvailable()) {
    				availables.add(CONNECTION_TYPES[i]);
    			}
    		}
    		availableTypes = (ConnectionType[]) availables.toArray(new ConnectionType[availables.size()]);
    	}
        return availableTypes;
    }
    
    /**
     * Returns all connection types.
     * 
     * @return the connection types.
     */
    public static ConnectionType[] getTypes() {
        return CONNECTION_TYPES;
    }
    
    /**
     * Gets the instance of the ConnectionType whose id is equal to the given one 
     * @param id The id
     * @return The connection type, <code>null</code> if none matches the given id.
     */
    public static ConnectionType getById(String id) {
		for (int i = 0; i < CONNECTION_TYPES.length; i++) {
			if (CONNECTION_TYPES[i].getId().equals(id)) {
				return CONNECTION_TYPES[i];
			}
		}
		return null;
    }
    
    /**
     * Direct connection. 
     */
    public static final ConnectionType DIRECT = new ConnectionType("DIRECT", "Direct", "Direct connection to the server.") {
        public boolean isTunneled() {
            return false;
        }
        public boolean isAvailable() {
        	return true;
        }
        public boolean isSSLSupported() {
            return true;
        }
        public void select() {
            Authenticator.setDefault(authenticator);
        }
        public void unselect() {
            Authenticator.setDefault(null);
        }
    };
    
    /**
     * Direct connect via a proxy in China.
     */
    public static final ConnectionType CHINA_PROXY = new ConnectionType("CHINA","Direct (China)", "Direct connection to the server via proxy in China.") {
        public boolean isTunneled() {
            return false;
        }
        
        public boolean isAvailable() {
        	String version = System.getProperty("java.version");
        	if (version.charAt(0) > '1') {
        		return true;
        	}
        	if (version.charAt(2) >= '5' || (version.length() > 3 && version.charAt(3) != '.')) {
        		return true;
        	}
        	return false;
        }
        
        private ChinaProxySelector selector = null;
        
        public void select() {
            selector = new ChinaProxySelector();
            selector.setup();
        }
        
        public void unselect() {
            selector.unsetup();
        }

        public boolean isSSLSupported() {
            return true;
        }
    };
    
    /**
     * HTTP Tunneled connection using chunked output stream.
     */
    public static final ConnectionType TUNNEL_CHUNKED = new ConnectionType("TCHUNKED", "HTTP Tunnel A", "HTTP tunneled connection.\nIt is significantly slower than connecting directly and\n should only be used by people who are unable to connect otherwise.") {
        public boolean isTunneled() {
            return true;
        }
        
        public boolean isAvailable() {
        	String version = System.getProperty("java.version");
        	if (version.charAt(0) > '1') {
        		return true;
        	}
        	if (version.charAt(2) >= '5' || (version.length() > 3 && version.charAt(3) != '.')) {
        		return true;
        	}
        	return false;
        }
        
        public void select() {
            System.setProperty(HTTPTunnelClientConnector.USE_CHUNKED_OUTPUT, "true");
            Authenticator.setDefault(authenticator);
        }

        public boolean isSSLSupported() {
            return true;
        }
        public void unselect() {
            Authenticator.setDefault(null);
        }
    };
    
    /**
     * HTTP Tunneled connection not using chunked output stream.
     */
    public static final ConnectionType TUNNEL_NOT_CHUNKED = new ConnectionType("TNCHUNKED", "HTTP Tunnel B", "HTTP tunneled connection (old method).\nIt is significantly slower than connecting directly \nshould only be used by people who are unable to connect otherwise.\nThis is the slowest tunnel option.") {
        public boolean isTunneled() {
            return true;
        }
        public void select() {
            System.setProperty(HTTPTunnelClientConnector.USE_CHUNKED_OUTPUT, "false");
            Authenticator.setDefault(authenticator);
        }
        public void unselect() {
            Authenticator.setDefault(null);
        }
        public boolean isAvailable() {
        	return true;
        }
        public boolean isSSLSupported() {
            return true;
        }
    };

    /** Represents all connection types. */
    private static final ConnectionType[] CONNECTION_TYPES = new ConnectionType[] {DIRECT, CHINA_PROXY, TUNNEL_CHUNKED, TUNNEL_NOT_CHUNKED};

    /**
     * Registers the authenticator for proxy authentication.
     * 
     * @param auth the authenticator for proxy authentication.
     */
    public static void registerAuthenticator(Authenticator auth) {
        authenticator = auth;
    }
}
