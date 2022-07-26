/*
 * CSVReader
 * 
 * Created Aug 21, 2008
 */
package com.topcoder.shared.reader.csv;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class CSVReader implements Closeable {
    private BufferedReader reader;
    private String[] values;
    private int count = 0;
    
    public CSVReader(File fileName) throws FileNotFoundException {
        reader = new BufferedReader(new FileReader(fileName));
    }

    public boolean next() throws IOException {
        return processNextList();
    }

    private boolean processNextList() throws IOException {
        String line = reader.readLine();
        if (line == null || line.trim().length() == 0) {
            return false;
        }
        values = line.split(",");
        count++;
        return true;
    }

    public int getInt(int i) {
        return Integer.parseInt(values[i-1]);
    }
    
    public double getDouble(int i) {
        return Double.parseDouble(values[i-1]);
    }

    public int getReadCount() {
        return count;
    }
    
    public void close() throws IOException {
        reader.close();
    }

    public boolean isEmpty(int i) {
        return values[i-1] == null || values[i-1].length() == 0;
    }
}
