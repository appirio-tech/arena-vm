/**
 * Requests the information about the components in a specific round
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */
package com.topcoder.shared.netCommon.messages.spectator;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;
import com.topcoder.shared.netCommon.messages.Message;
import com.topcoder.shared.netCommon.messages.MessagePacket;
import com.topcoder.shared.netCommon.messages.MessageUtil;

public class RequestComponentRoundInfo extends Message implements Serializable, Cloneable, CustomSerializable {
   /** Identifier of the contest the round is associated with */
   private int contestID;

   /** Identifier of the round */
   private int roundID;

   /** Identifier of the component */
   private long componentID;

   /** No arg constructor required for custom serialization */
   public RequestComponentRoundInfo()
   {
   }
   
   /** Constructs the request from the specified contest, round and component identifiers
    * 
    * @param contestID the contest id
    * @param roundID the round id
    * @param componentID the component id
    */
	public RequestComponentRoundInfo(int contestID, int roundID, long componentID) {
		this.contestID = contestID;
		this.roundID = roundID;
		this.componentID = componentID;
	}

	/** The contest identifier the component is in.
	 * @return the contest identifier the component is in.
	 */
	public int getContestID() {
		return contestID;
	}

	/** The round identifier the component is in.
	 * @return the round identifier the component is in.
	 */
	public int getRoundID() {
		return roundID;
	}
	
	/** The component idenfier being requested
	 * @return the component identifier being requested
	 */
	public long getComponentID() {
		return componentID;
	}
	
	public void setContestID(int contestID) {
		this.contestID = contestID;
	}
	public void setRoundID(int roundID) {
		this.roundID = roundID;
	}
	public void setComponentID(long componentID) {
		this.componentID = componentID;
	}

	public void customWriteObject(CSWriter writer) throws IOException {
		writer.writeInt(contestID);
		writer.writeInt(roundID);
		writer.writeLong(componentID);
	}

	public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
		contestID = reader.readInt();
		roundID = reader.readInt();
		componentID = reader.readLong();
	}
	
	public String toString() {
		return "(RequestComponentInfo)[" + contestID + ", " + roundID + ", " + componentID + "]";
	}
	
	public static void main(String[] args) {
//		RequestComponentRoundInfo info = new RequestComponentRoundInfo(1, 5000, 2939);
//		ArrayList cd = new ArrayList();
//		cd.add(new ComponentCoder(11, "sdk", 29393,10));
//		cd.add(new ComponentCoder(21, "abc", 29393,10));
//		cd.add(new ComponentCoder(31, "def", 29393,10));
//		ArrayList rb = new ArrayList();
//		rb.add(new CoderData(1, "slkjsdf", 2939));
//		rb.add(new CoderData(2, "skj", 9483));
//		rb.add(new CoderData(3, "923lkf", 1922));
//		DefineComponentContest cp = new DefineComponentContest(1, 500, new ComponentData(22, "blah", "blbl"),cd, rb);
		ComponentCoder cp = new ComponentCoder(1,"sdf",29339,0);
		MessagePacket mp = new MessagePacket(cp);
		
		try {
//			String queryString = MessageUtil.encodeQueryStringMessage(info);
//			System.out.println(">> QueryString: " + queryString);
//			
//			String xmlString = MessageUtil.encodeXMLMessage(info);
//			System.out.println(">> XMLString: " + xmlString);
//			
//			RequestComponentRoundInfo newInfo = (RequestComponentRoundInfo) MessageUtil.decodeQueryStringMessage(queryString);
//			System.out.println(">> Decoded QueryString: " + newInfo);
//			
//			RequestComponentRoundInfo xmlInfo = (RequestComponentRoundInfo) MessageUtil.decodeXMLMessage(xmlString);
//			System.out.println(">> Decoded XMLString: " + xmlInfo);

			String xmlString = MessageUtil.encodeXMLMessagePacket(mp);
			System.out.println(">> XMLString: " + xmlString);
			
			MessagePacket mp2 = (MessagePacket) MessageUtil.decodeXMLMessagePacket(xmlString);
			System.out.println(">> Decoded XMLString: " + mp2);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
