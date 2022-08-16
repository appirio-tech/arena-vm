/**
 * Defines the information for a specific component
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
package com.topcoder.shared.netCommon.messages.spectator;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import com.topcoder.shared.netCommon.messages.Message;

public class ComponentData extends Message implements Serializable, Cloneable, CustomSerializable  {
	
	/** The identifier of the component */
	private long componentID;
	
   /** The name of the component */
   private String name;
   
   /** The component catalog */
   private String catalog;
   
   /** Noarg constructor required by custom serializable */
   public ComponentData() {
	}
   
   /** 
    * Defines information about a component
    * @param name the name of the component
    * @param catalog the name of the component catalog the component belongs to
    */
	public ComponentData(long componentID, String name, String catalog) {
		this.componentID = componentID;
		this.name = name;
		this.catalog = catalog;
	}

	/** The component idenfier being requested
	 * @return the component identifier being requested
	 */
	public long getComponentID() {
		return componentID;
	}

	/** The name of the component
	 * @return the name of the component
	 */
	public String getName() {
		return name;
	}

	/** The component catalog the component belongs to
	 * @return the component catalog the component belongs to
	 */
	public String getCatalog() {
		return catalog;
	}

	public void setComponentID(long componentID) {
		this.componentID = componentID;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	public void customWriteObject(CSWriter writer) throws IOException {
		writer.writeLong(componentID);
		writer.writeString(name);
		writer.writeString(catalog);
	}

	public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
		componentID = reader.readLong();
		name = reader.readString();
		catalog = reader.readString();
	}
	
	public String toString() {
		return "(ComponentData)[" + componentID + ", " + name + ", " + catalog + "]";
	}
}
