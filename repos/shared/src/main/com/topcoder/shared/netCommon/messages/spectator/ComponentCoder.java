/**
 * The specific component coder and the wager that coder has made
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

public class ComponentCoder extends CoderData implements CustomSerializable, Cloneable, Serializable {
	/** The actual wager made */
	private int wager;


	/** Noarg constructor needed by custom serializable */
	public ComponentCoder() {
	}
	
	/** 
	 * Constructs the wager from the coder and wager
	 * @param coderID the coder making the wager
	 * @param handle the handle of the coder
	 * @param rank the rank (rating) of the coder
	 * @param wager the wager made
	 */
	public ComponentCoder(int coderID, String handle, int rank, int wager) {
		super(coderID, handle, rank);
		this.wager = wager;
	}

	/** 
	 * The wager that was made
	 * @return the wager that was made
	 */ 
	public int getWager() {
		return wager;
	}

	public void setWager(int wager) {
		this.wager = wager;
	}

	public void customWriteObject(CSWriter writer) throws IOException {
		super.customWriteObject(writer);
		writer.writeInt(wager);
	}

	public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
		super.customReadObject(reader);
		wager = reader.readInt();
	}
	
	public String toString() {
		return "(ComponentCoder)[" + getCoderID() + ", " + getHandle() + ", " + getRank() + ", " + wager + "]";
	}
}
