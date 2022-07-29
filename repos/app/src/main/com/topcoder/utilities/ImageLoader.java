/*
 * WikiLoader.java
 *
 * Created on Jun 12, 2007, 11:01:28 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.utilities;

import com.topcoder.services.persistentcache.PersistentCache;
import com.topcoder.services.persistentcache.PersistentCacheException;
import com.topcoder.services.persistentcache.impl.PersistentCacheManager;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import javax.imageio.ImageIO;

/**
 *
 * @author rfairfax
 */
public class ImageLoader {

    /** Creates a new instance of WikiLoader */
    public ImageLoader() {
    }

    //private static final String PATH = "C:/cygwin/home/farm/wiki";
    private static final String PATH = "/home/farm/image/syst.txt";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws PersistentCacheException, FileNotFoundException, IOException, Exception {
        // TODO code application logic here
        PersistentCacheManager mgr = new PersistentCacheManager();
        PersistentCache cache = mgr.getCache("MM_ImageReconstruction");
        cache.setMinimalVersion(1);
        //cache.put("filled", new Boolean(true));
        //File dir = new File(PATH);
        //process
        //BufferedReader br = new BufferedReader(new FileReader(dir));
        //br.readLine();
        //for(int i = 1; i <= 500; i++) {
        //   String s;
        //  s = br.readLine();
        // s = s.substring(1, s.length()-1);
        //cache.put("s"+i, s);
        //System.err.println("PUT " + i);
        //}
        //br.close();
	System.out.println("500");
        //figure out what I need here
        for (int i = 1; i <= 500; i++) {
            String s = (String) cache.get("s" + i);
            loadTest(s);
            System.out.print("\"s" + i + ",");
            System.out.print(imageSource + ",");
            System.out.print(pieceSize + ",");
            System.out.print(columns + ",");
            System.out.print(rows + ",");
            System.out.println(s.substring(0,s.indexOf(',')) + "\"");
        }
    }
    
    
    protected final static int[] POSSIBLE_SIZES = { 10, 20, 30 };

    protected static int pieceSize, columns, rows;
    protected static int[] pixels;
    protected static String imageSource;
    protected static ArrayList currentPieceLocations;
    protected static int moves;

        private  static int decodeBase64Char(char c) {
        if (c == '=')
            return 0;
        if (c >= 'A' && c <= 'Z')
            return c-'A';
        if (c >= 'a' && c <= 'z')
            return c-'a'+26;
        if (c >= '0' && c <= '9')
            return c-'0'+52;
        if (c == '+')
            return 62;
        return 63;
    }
        
        private static byte[] decodeBase64(String s) {
        s = s.replaceAll("[^+/=A-Za-z0-9]", "");
        int length = s.length()/4*3;
        if (s.endsWith("=="))
            length -= 2;
        else if (s.endsWith("="))
            --length;
        byte[] result = new byte[length];
        for (int i = 0, d = 0; i < s.length(); i+=4, d+=3) {
            int n = (decodeBase64Char(s.charAt(i)) << 18) |
                    (decodeBase64Char(s.charAt(i+1)) << 12) |
                    (decodeBase64Char(s.charAt(i+2)) << 6) |
                    decodeBase64Char(s.charAt(i+3));
            result[d] = (byte)((n >>> 16) & 0xff);
            if (d+1 < length)
                result[d+1] = (byte)((n >>> 8) & 0xff);
            if (d+2 < length)
                result[d+2] = (byte)(n & 0xff);
        }
        return result;
    }

    protected  static void loadTest(String test) throws Exception {
        String[] elements = test.split(",");
        if (elements.length != 3) {
            throw new IllegalArgumentException("wrong number of elements");
        }
        SecureRandom r = SecureRandom.getInstance("SHA1PRNG");
        r.setSeed(Integer.parseInt(elements[0]));

        pieceSize = POSSIBLE_SIZES[r.nextInt(POSSIBLE_SIZES.length)];

        imageSource = elements[1];

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(decodeBase64(elements[2])));

        columns = image.getWidth() / pieceSize;
        rows = image.getHeight() / pieceSize;

        final int xOffset = (columns * pieceSize - image.getWidth()) / 2;
        final int yOffset = (rows * pieceSize - image.getHeight()) / 2;

        currentPieceLocations = new ArrayList(rows * columns);
        for (int i = 0; i < rows * columns; ++i) {
            currentPieceLocations.add(new Integer(i));
        }
        Collections.shuffle(currentPieceLocations, r);

        pixels = new int[rows * columns * pieceSize * pieceSize];

        int ofs = 0;
        for (int i = 0; i < rows * columns; ++i) {
            for (int ip = 0; ip < pieceSize; ++ip) {
                for (int jp = 0; jp < pieceSize; ++jp) {
                    int x = jp + (((Integer)currentPieceLocations.get(i)).intValue() % columns) * pieceSize - xOffset;
                    int y = ip + (((Integer)currentPieceLocations.get(i)).intValue() / columns) * pieceSize - yOffset;
                    pixels[ofs++] = image.getRGB(x, y) & 0xffffff;
                }
            }
        }
        moves = 0;
    }
}
