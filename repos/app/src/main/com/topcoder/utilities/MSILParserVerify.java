/*
 * MSILParserVerify.java
 *
 * Created on June 20, 2006, 3:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.utilities;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.topcoder.services.compiler.util.MSILParser.MSILParser;
import com.topcoder.services.compiler.util.MSILParser.ParseException;
import com.topcoder.services.compiler.util.MSILParser.SimpleNode;
import com.topcoder.services.compiler.util.MSILParser.security.SecurityCheck;
import com.topcoder.services.compiler.util.MSILParser.visitor.ClassNameVisitor;
import com.topcoder.services.compiler.util.MSILParser.visitor.ClassNameVisitorState;
import com.topcoder.services.compiler.util.MSILParser.visitor.TypeVisitor;

/**
 *
 * @author rfairfax
 */
public class MSILParserVerify {
    
    /** Creates a new instance of MSILParserVerify */
    public MSILParserVerify() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        // TODO code application logic here
        String baseDir = "/home/rfairfax/" + args[0] + "/";

        
        
        for(int i = 0; i < 12000;i++) {
            //System.out.println("STARTING " + i);
            String file = baseDir + i + ".txt";
            FileReader fr = new FileReader(file);
            MSILParser parser = new MSILParser(fr);
            try {
                
                SimpleNode node = parser.ILFile();
                ClassNameVisitor v = new ClassNameVisitor();
                node.jjtAccept(v, new ClassNameVisitorState());
                
                String[] l = v.getClasses();
                
                TypeVisitor tv = new TypeVisitor();
                node.jjtAccept(tv, new Boolean(false));
                
                SecurityCheck check = new SecurityCheck(l, true);
                String r = check.checkTypes(tv.getTypes());
                r += check.checkMethods(tv.getMethods());
                
                if(!r.equals("")) {
                    System.out.println("R(" + i + "): " + r);
                }
                
                //node.dump("");
                
            } catch (ParseException ex) {
                System.err.println("ERROR ON " + i);
                ex.printStackTrace();
                
            }
            
            fr.close();
        }
    }
    
}
