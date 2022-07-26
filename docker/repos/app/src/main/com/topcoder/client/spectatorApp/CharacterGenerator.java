package com.topcoder.client.spectatorApp;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Category;
import org.apache.log4j.PropertyConfigurator;

import com.topcoder.client.spectatorApp.announcer.events.ShowRoundEvent;

/** 
 * Class to be used to generate character definitions
 * 
 * @author Tim Roberts
 *
 */
public class CharacterGenerator {

	/** reference to the logging category */
	private static final Category cat = Category.getInstance(CharacterGenerator.class.getName());

	public static void main(String[] a) {

		// Configure and start
		if(System.getProperty("log4j.configuration")!=null) {
			PropertyConfigurator.configure(System.getProperty("log4j.configuration"));
		}

		// Validate the input file definion
		if(a.length!=2) {
			cat.fatal("You must include the input and output file(s) as the arguments: ");
			cat.fatal("java xxx.CharacterGenerator input.txt output.xml");
			return;
		}
		
		// Allocate for the string file
		StringBuffer buf = new StringBuffer();
		File stringFile = new File(a[0]);
		
		// Read the file into the buffer
		BufferedReader in=null;
		
		cat.info("Reading input file: " + a[0]);
		
		try {
			// Allocate the stream
			in = new BufferedReader(new InputStreamReader(new FileInputStream(stringFile)));
			
			// Read everything into the buffer
			while(true) {
				String r = in.readLine();
				if(r==null) break;
				buf.append(r);
			}
		} catch (FileNotFoundException e) {
			cat.error("Error reading input file", e);
			return;
		} catch (IOException e) {
			cat.error("Error reading input file", e);
			return;
		} finally {
			if(in!=null) {
				try {
					in.close();
				} catch (IOException e1) {
					return;
				}
			}
		}

		cat.info("Done reading input file: " + a[0]);

		// Create a show round event and populate the title with the string
		ShowRoundEvent evt = new ShowRoundEvent();
		evt.setTitle(buf.toString());

		// Encode and write out the event
		cat.info("Writing out to file: " + a[1]);
		XMLEncoder e = null;
		try {
			e = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(a[1])));
			e.writeObject(evt);
		} catch (FileNotFoundException e1) {
			cat.error("Error writing output file", e1);
			return;
		} finally {
			if(e!=null) e.close();
		}

		// Encode and write out the event
		cat.info("Reading file: " + a[1]);
		
		// Decode the file
		XMLDecoder d = null;
		ShowRoundEvent evt1 = null;
		try {
			d = new XMLDecoder(new BufferedInputStream(new FileInputStream(a[1])));
			evt1 = (ShowRoundEvent)d.readObject();
		} catch (FileNotFoundException e1) {
			cat.error("Error reading output file", e1);
			return;
		} finally {
			if(d!=null) d.close();
		}
		
		// Verify
		cat.info("Verifying:");
		if(evt.getTitle().equals(evt1.getTitle())) {
			cat.info("  Success - both are equal");
		} else {
			cat.info("  Unsuccessful.  Original: {" + evt.getTitle() + "},  New: {" + evt1.getTitle() + "}");
		}
	}
}
