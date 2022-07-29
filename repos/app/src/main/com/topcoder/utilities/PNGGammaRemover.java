/*
 * PNGGammaRemover.java
 *
 * Created on Sep 25, 2007, 2:48:22 PM
 *
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.topcoder.utilities;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import org.w3c.dom.Node;

/**
 *
 * @author rfairfax
 */
public class PNGGammaRemover {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        String filename = args[0];
        System.out.println("FILE: " + filename);
        File infile = new File(filename);
        ImageInputStream stream = ImageIO.createImageInputStream(infile);
        Iterator iter = ImageIO.getImageReaders(stream);
        ImageReader reader = (ImageReader) iter.next();
        reader.setInput(stream);

        
        BufferedImage im = reader.read(0);
        IIOMetadata m = reader.getImageMetadata(0);
        Node root = m.getAsTree("javax_imageio_png_1.0");
        Node n = root.getFirstChild();
        Node g = null;
        while(n != null) {
            if(n.getNodeName().equals("gAMA")) {
                Node v = n.getAttributes().getNamedItem("value");
                g = n;
                System.out.println("VAL: " + v.getNodeValue());
                break;
            }
            n = n.getNextSibling();
        }
        
        if(g != null) {
            System.out.println("HERE");
            root.removeChild(g);
            g = null;
            m.setFromTree("javax_imageio_png_1.0", root);
        }
        ImageWriter writer = (ImageWriter)ImageIO.getImageWritersByFormatName("png").next();
        writer.setOutput(ImageIO.createImageOutputStream(infile));
        
        writer.write(new IIOImage(im, null, m));
    }
}