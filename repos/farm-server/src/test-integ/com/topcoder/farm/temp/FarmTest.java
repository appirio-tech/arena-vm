package com.topcoder.farm.temp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FarmTest {

	public static void main(String[] args) throws Exception {
		CalloutTest ct = new CalloutTest();
		ct.testCallout();
	}

	
	static class CalloutTest {
		public void testCallout() throws Exception {
			URL url = new URL("http://echo.jsontest.com/key/value/one/two");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
	 
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}
	 
			BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));
	 
			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}
		}
		
	}
	
}
