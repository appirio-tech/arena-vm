package com.topcoder.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogParser {

    public static void main(String args[]) {
        String fileName = args[0];
        
        try {
            FileInputStream fis = new FileInputStream(new File(fileName));
            BufferedReader r = new BufferedReader(new InputStreamReader(fis));
            
            String line = null;
            long last5 = 0;
            long last6 = 0;
            long total = 0;
            int count = 0;
            long worst = 0;
            String lastline = null;
            while( (line = r.readLine()) != null ) {
                if(line.endsWith("pre5")) {
                    String[] s = line.split(" ");
                    String date = s[0] + " " + s[1];
                    SimpleDateFormat df = new SimpleDateFormat("y-M-d H:m:s,S");
                    Date d = df.parse(date);
                    if(last5 != 0) {
                        System.out.println("ERROR: last5");
                    }
                    last6 = 0;
                    last5 = d.getTime();
                    lastline = line;
                    
                } else if(line.endsWith("pre6")) {
                    String[] s = line.split(" ");
                    String date = s[0] + " " + s[1];
                    SimpleDateFormat df = new SimpleDateFormat("y-M-d H:m:s,S");
                    Date d = df.parse(date);
                    if(last6 != 0) {
                        System.out.println("ERROR: last6");
                    }
                    
                    last6 = d.getTime();
                    
                    if((last6-last5) > 100) {
                        System.out.println(lastline);
                        System.out.println(line);
                        System.out.println("Total: " + (last6-last5));
                        if((last6-last5) > worst)
                            worst = last6-last5;
                    }
                    total += (last6-last5);
                    count++;
                    last5 = 0;
                }
            }
            System.out.println("Grand Total: " + total);
            System.out.println("Worst: " + worst);
            System.out.println("Avg: " + ((double)total / (double)count));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
