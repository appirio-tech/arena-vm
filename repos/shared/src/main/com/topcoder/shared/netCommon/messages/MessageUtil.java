package com.topcoder.shared.netCommon.messages;

import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.topcoder.io.serialization.basictype.BasicTypeDataInput;
import com.topcoder.io.serialization.basictype.BasicTypeDataOutput;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.messages.spectator.RequestComponentRoundInfo;
import com.topcoder.shared.netCommon.messages.spectator.RequestComponentUpdate;

public class MessageUtil {
	
	/** Translates between { classname, modulename } */
	final private static String[][] classToModule = { 
				{ RequestComponentRoundInfo.class.getName(), "RoundInfo" },
				{ RequestComponentUpdate.class.getName(), "ComponentUpdate" }
	};
	
	final private static String moduleID = "?module=";
	
	/** private constructor to force util pattern */
	private MessageUtil() {
		
	}

	/** 
	 * Encodes to XML the specified message
	 * @param msg the message to encode
	 * @return the XML representation of the message
	 * @throws Exception if an exception occurs encoding the message
	 */
	public static String encodeXMLMessage(Message msg) throws Exception
	{
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		final XMLEncoder encoder = new XMLEncoder(os);
		final Exception[] exception = new Exception[1];
		encoder.setExceptionListener(new ExceptionListener() {
			public void exceptionThrown(Exception e) {
				exception[0] = e;
			}
		});
		
		encoder.writeObject(msg);
		
		if (exception[0] != null) {
			throw exception[0];
		}
		
		encoder.close();
		return os.toString();
	}
	
	/** 
	 * Encodes to XML the specified message packet
	 * @param msg the message to encode
	 * @return the XML representation of the message
	 * @throws Exception if an exception occurs encoding the message
	 */
	public static String encodeXMLMessagePacket(MessagePacket msg) throws Exception
	{
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		final XMLEncoder encoder = new XMLEncoder(os);
		final Exception[] exception = new Exception[1];
		encoder.setExceptionListener(new ExceptionListener() {
			public void exceptionThrown(Exception e) {
				exception[0] = e;
			}
		});
		
		encoder.writeObject(msg);
		
		if (exception[0] != null) {
			throw exception[0];
		}
		
		encoder.close();
		return os.toString();
	}
	
   /**
	 * Decodes the specified xml to a message
	 * @param xml the xml encoded message
	 * @return the Message represented by the xml
	 * @throws Exception if an exception occurs decoding the message
	 */
	public static Message decodeXMLMessage(String xml) throws Exception 
	{
		final ByteArrayInputStream is = new ByteArrayInputStream(xml.getBytes());
		final XMLDecoder decoder = new XMLDecoder(is);
		
		final Exception[] exception = new Exception[1];
		decoder.setExceptionListener(new ExceptionListener() {
			public void exceptionThrown(Exception e) {
				exception[0] = e;
			}
		});
		
		final Message msg = (Message) decoder.readObject();
		if (exception[0] != null) {
			throw exception[0];
		}
		
		decoder.close();
		return msg;
	}
	
   /**
	 * Decodes the specified xml to a message
	 * @param xml the xml encoded message
	 * @return the Message represented by the xml
	 * @throws Exception if an exception occurs decoding the message
	 */
	public static MessagePacket decodeXMLMessagePacket(String xml) throws Exception 
	{
		final ByteArrayInputStream is = new ByteArrayInputStream(xml.getBytes());
		final XMLDecoder decoder = new XMLDecoder(is);
		
		final Exception[] exception = new Exception[1];
		decoder.setExceptionListener(new ExceptionListener() {
			public void exceptionThrown(Exception e) {
				exception[0] = e;
			}
		});
		
		final MessagePacket msg = (MessagePacket) decoder.readObject();
		if (exception[0] != null) {
			throw exception[0];
		}
		
		decoder.close();
		return msg;
	}
	
	/**
	 * Encodes a message into a query string
	 * @param msg the message to encode
	 * @return the query string version of the message
	 */
	public static String encodeQueryStringMessage(Message msg) {
		StringBuffer buf = new StringBuffer(200);
		buf.append(moduleID);
		buf.append(encodeString(getModuleName(msg.getClass().getName())));

		final QueryWriter writer = new QueryWriter();
		try {
			msg.customWriteObject(writer);
		} catch (IOException e) {
			// ignore becuase it won't happen
		}
		
		buf.append(writer.toString());
		
		return buf.toString();
	}
	
	/**
	 * Encodes a message into a query string
	 * @param msg the message to encode
	 * @return the query string version of the message
	 * @throws ClassNotFoundException if the message isn't found
	 * @throws IllegalAccessException if the message can't be accessed
	 * @throws InstantiationException if the message can't be instantiated
	 */
	public static Message decodeQueryStringMessage(String queryString) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		if (queryString.length() == 0) return null;
		if (!queryString.startsWith("?")) queryString = "?" + queryString;
		
		int startPos = queryString.indexOf(moduleID) + moduleID.length();
		if (startPos < moduleID.length()) {
			throw new ClassNotFoundException("module identifier was not in the query string");
		}
		
		int endPos = queryString.indexOf("&");
		if (endPos < 0) endPos = queryString.length();
		
		// Extract out the class name
		final String moduleName = queryString.substring(startPos, endPos);
		
		// Create it with the noarg constructor
		final Class msgClass = MessageUtil.class.getClassLoader().loadClass(decodeString(getClassName(moduleName)));
		final Message msg = (Message) msgClass.newInstance();
		
		final QueryReader reader = new QueryReader(queryString.substring(endPos));
		try {
			msg.customReadObject(reader);
		} catch (IOException e) {
			throw new InstantiationException("Exception in customReadObject: " + e.toString());
		}
		
		return msg;
	}
	
	private static String getModuleName(String className)
	{
		for(int x = classToModule.length - 1; x >= 0; x--) {
			if (classToModule[x][0].equals(className)) {
				return classToModule[x][1];
			}
		}
		return className;
	}
	
	private static String getClassName(String moduleName)
	{
		for(int x = classToModule.length - 1; x >= 0; x--) {
			if (classToModule[x][1].equals(moduleName)) {
				return classToModule[x][0];
			}
		}
		return moduleName;
	}
	
	private static String encodeString(String s) 
	{
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
	
	private static String decodeString(String s) 
	{
		try {
			return URLDecoder.decode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
	
	private static class QueryWriter implements CSWriter {

		private int id = 0;
		private StringBuffer buf = new StringBuffer(200);
		
		public void setDataOutput(BasicTypeDataOutput output) {
			throw new UnsupportedOperationException();
		}

		public String toString() {
			return buf.toString();
		}
		
		private void writeBuffer(String s) {
			buf.append("&id");
			buf.append(id++);
			buf.append("=");
			buf.append(encodeString(s));
		}
		
		public void writeByte(byte v) throws IOException {
			writeBuffer(Byte.toString(v));
		}

		public void writeShort(short v) throws IOException {
			writeBuffer(Short.toString(v));
		}

		public void writeInt(int v) throws IOException {
			writeBuffer(Integer.toString(v));
		}

		public void writeLong(long v) throws IOException {
			writeBuffer(Long.toString(v));
		}

		public void writeBoolean(boolean v) throws IOException {
			writeBuffer(Boolean.toString(v));
		}

		public void writeDouble(double v) throws IOException {
			writeBuffer(Double.toString(v));
		}

		public void writeString(String string) throws IOException {
			writeBuffer(string);
		}

		public void writeByteArray(byte[] byteArray) throws IOException {
			throw new UnsupportedOperationException();
		}

		public void writeCharArray(char[] charArray) throws IOException {
			throw new UnsupportedOperationException();
		}

		public void writeObjectArray(Object[] objectArray) throws IOException {
			throw new UnsupportedOperationException();
		}

		public void writeObjectArrayArray(Object[][] objectArrayArray) throws IOException {
			throw new UnsupportedOperationException();
		}

		public void writeArrayList(ArrayList list) throws IOException {
			throw new UnsupportedOperationException();
		}

		public void writeHashMap(HashMap map) throws IOException {
			throw new UnsupportedOperationException();
		}

		public void writeObject(Object object) throws IOException {
			throw new UnsupportedOperationException();
		}

        public void writeClass(Class clazz) throws IOException {
            throw new UnsupportedOperationException();
        }

        public void writeList(List list) throws IOException {
            throw new UnsupportedOperationException();            
        }

        public void writeMap(Map map) throws IOException {
            throw new UnsupportedOperationException();            
        }

        public void writeUTF(String s) throws IOException {
            throw new UnsupportedOperationException();
        }
        
        public void writeCollection(Collection collection) throws IOException {
            throw new UnsupportedOperationException();            
        }
        
        public void writeEncrypt(Object object) throws IOException {
            throw new UnsupportedOperationException();            
        }
	}
	
	private static class QueryReader implements CSReader
	{
		int idx = 0;
		final String[] strings;
		public QueryReader(String queryString) {
			// This can be screwed up by embedded "&" in the string
			final StringTokenizer tok = new StringTokenizer(queryString, "&");
			strings = new String[tok.countTokens()];
			for(int x= 0;x < strings.length; x++) {
				String s = tok.nextToken();
				int pos = s.indexOf("=");
				if (pos >= 0) {
					strings[x] = decodeString(s.substring(pos+1));
				} else {
					throw new IllegalArgumentException("String is not well formed: " + s);
				}
			}
		}
		
		public String readNext() throws IOException {
			if (idx >= strings.length) {
				throw new EOFException();
			}
			return strings[idx++];
		}

        public void setMemoryUsageLimit(long limit) {
            throw new UnsupportedOperationException();
        }

        public void resetMemoryUsage() {
            throw new UnsupportedOperationException();
        }
		
		public void setDataInput(BasicTypeDataInput input) {
			throw new UnsupportedOperationException();
		}

		public byte readByte() throws IOException {
			return Byte.parseByte(readNext());
		}

		public short readShort() throws IOException {
			return Short.parseShort(readNext());
		}

		public int readInt() throws IOException {
			return Integer.parseInt(readNext());
		}

		public long readLong() throws IOException {
			return Long.parseLong(readNext());
		}

		public boolean readBoolean() throws IOException {
			return new Boolean(readNext()).booleanValue();
		}

		public double readDouble() throws IOException {
			return Double.parseDouble(readNext());
		}

		public String readString() throws IOException {
			return readNext();
		}

		public byte[] readByteArray() throws IOException {
			throw new UnsupportedOperationException();
		}

		public char[] readCharArray() throws IOException {
			throw new UnsupportedOperationException();
		}

		public Object[] readObjectArray() throws IOException {
			throw new UnsupportedOperationException();
		}

		public Object[] readObjectArray(Class clazz) throws IOException {
			throw new UnsupportedOperationException();
		}

		public Object[][] readObjectArrayArray(Class clazz) throws IOException {
			throw new UnsupportedOperationException();
		}

		public Object[][] readObjectArrayArray() throws IOException {
			throw new UnsupportedOperationException();
		}

		public ArrayList readArrayList() throws IOException {
			throw new UnsupportedOperationException();
		}

		public HashMap readHashMap() throws IOException {
			throw new UnsupportedOperationException();
		}

		public Object readObject() throws IOException {
			throw new UnsupportedOperationException();
		}

        public Class readClass() throws IOException {
            throw new UnsupportedOperationException();
        }

        public List readList(List listInstance) throws IOException {
            throw new UnsupportedOperationException();        }

        public Map readMap(Map mapInstance) throws IOException {
            throw new UnsupportedOperationException();
        }

        public String readUTF() throws IOException {
            throw new UnsupportedOperationException();
        }
        public Collection readCollection(Collection collection) throws IOException {
            throw new UnsupportedOperationException();
        }
        public Object readEncrypt() throws IOException {
            throw new UnsupportedOperationException();
        }
	}
}
