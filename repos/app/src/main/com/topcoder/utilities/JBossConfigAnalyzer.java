/*
 * JBossConfigAnalyzer.java
 *
 * Created on April 27, 2006, 7:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.utilities;

import java.io.*;
import java.util.*;
import java.util.regex.*;
/**
 *
 * @author rfairfax
 */
public class JBossConfigAnalyzer {
    
    private String baseDir;
    
    /** Creates a new instance of JBossConfigAnalyzer */
    public JBossConfigAnalyzer(String dir) {
        this.baseDir = dir;
        
        Map m = new HashMap();
        m.put("JNP Port", "<[\\s]*?mbean[\\s]*?code=\"org\\.jboss\\.naming\\.NamingService\"[\\s]*?name=\"jboss:service=Naming\"[\\s]*?>.*?<[\\s]*?attribute[\\s]*?name=\"Port\"[\\s]*?>[\\s]*?([\\d]*)<[\\s]*?/attribute[\\s]*?>.*?<[\\s]*?/mbean[\\s]*?>");
        m.put("RMI Port", "<[\\s]*?mbean[\\s]*?code=\"org\\.jboss\\.naming\\.NamingService\"[\\s]*?name=\"jboss:service=Naming\"[\\s]*?>.*?<[\\s]*?attribute[\\s]*?name=\"RmiPort\"[\\s]*?>[\\s]*?([\\d]*)<[\\s]*?/attribute[\\s]*?>.*?<[\\s]*?/mbean[\\s]*?>");
        
        files.put("conf/jboss-minimal.xml", m);
        
        m = new HashMap();
        m.put("JNP Port", "<[\\s]*?mbean[\\s]*?code=\"org\\.jboss\\.naming\\.NamingService\"[\\s]*?name=\"jboss:service=Naming\"[\\s]*?xmbean-dd=\"resource:xmdesc/NamingService-xmbean.xml\"[\\s]*?>.*?<[\\s]*?attribute[\\s]*?name=\"Port\"[\\s]*?>[\\s]*?([\\d]*)<[\\s]*?/attribute[\\s]*?>.*?<[\\s]*?/mbean[\\s]*?>");
        m.put("RMI Port", "<[\\s]*?mbean[\\s]*?code=\"org\\.jboss\\.naming\\.NamingService\"[\\s]*?name=\"jboss:service=Naming\"[\\s]*?xmbean-dd=\"resource:xmdesc/NamingService-xmbean.xml\"[\\s]*?>.*?<[\\s]*?attribute[\\s]*?name=\"RmiPort\"[\\s]*?>[\\s]*?([\\d]*)<[\\s]*?/attribute[\\s]*?>.*?<[\\s]*?/mbean[\\s]*?>");
        m.put("Web Service Port", "<[\\s]*?mbean[\\s]*?code=\"org\\.jboss\\.web\\.WebService\"[\\s]*?name=\"jboss:service=WebService\"[\\s]*?>.*?<[\\s]*?attribute[\\s]*?name=\"Port\"[\\s]*?>[\\s]*?([\\d]*)<[\\s]*?/attribute[\\s]*?>.*?<[\\s]*?/mbean[\\s]*?>");
        m.put("JRMP Port", "<[\\s]*?mbean[\\s]*?code=\"org\\.jboss\\.invocation\\.jrmp\\.server\\.JRMPInvoker\"[\\s]*?name=\"jboss:service=invoker,type=jrmp\"[\\s]*?>.*?<[\\s]*?attribute[\\s]*?name=\"RMIObjectPort\"[\\s]*?>[\\s]*?([\\d]*)<[\\s]*?/attribute[\\s]*?>.*?<[\\s]*?/mbean[\\s]*?>");
        
        files.put("conf/jboss-service.xml", m);
        
        m = new HashMap();
        m.put("Admin Password", "admin=([\\w\\d]*)");
              
        files.put("conf/props/jmx-console-users.properties", m);
        
        m = new HashMap();
        m.put("HTTP Port", "<[\\s]*?Service[\\s]*?name=\"jboss.web\"[\\s]*?className=\"org\\.jboss\\.web\\.tomcat\\.tc5\\.StandardService\"[\\s]*?>.*?<[\\s]*?Connector[\\s]*?port=\"([\\d]*)\".*?/>.*?<[\\s]*?/Service[\\s]*?>");
        m.put("AJP13 Port", "<[\\s]*?Service[\\s]*?name=\"jboss.web\"[\\s]*?className=\"org\\.jboss\\.web\\.tomcat\\.tc5\\.StandardService\"[\\s]*?>.*?<[\\s]*?Connector[\\s]*?port=\"([\\d]*)\"[^>]*?protocol=\"AJP/1.3\".*?/>.*?<[\\s]*?/Service[\\s]*?>");
              
        files.put("deploy/jbossweb-tomcat55.sar/server.xml", m);
        
        m = new HashMap();
        m.put("UIL2 Port", "<[\\s]*?mbean[\\s]*?code=\"org\\.jboss\\.mq\\.il\\.uil2\\.UILServerILService\"[\\s]*?name=\"jboss\\.mq:service=InvocationLayer,type=UIL2\"[\\s]*?>.*?<[\\s]*?attribute[\\s]*?name=\"ServerBindPort\"[\\s]*?>[\\s]*?([\\d]*)<[\\s]*?/attribute[\\s]*?>.*?<[\\s]*?/mbean[\\s]*?>");
              
        files.put("deploy/jms/uil2-service.xml", m);
        
    }
    
    private Map files = new HashMap();
    
    public void analyze() {
        System.out.println("Starting on " + baseDir);
        File dir = new File(baseDir);
        if(!dir.exists() || !dir.isDirectory()) {
            System.out.println("Invalid directory");
            System.exit(0);
        }
        
        for(Iterator i = files.keySet().iterator(); i.hasNext();) {
            String file = (String)i.next();
            System.out.println("\nProcessing " + file + "\n");
            
            String text = "";
            try {
                BufferedReader r = new BufferedReader(new FileReader(dir.getPath() + File.separator + file));
                while(r.ready()) {
                    text = text + r.readLine() + "\n";
                }
            } catch(FileNotFoundException e) {
                System.out.println("Cannot find file");
                System.exit(0);
            } catch(IOException ioe) {
                System.out.println("IO Error");
                System.exit(0);
            }
            
            text = text.replace("\n","");
            
            //process text against regexes in map
            Map regexs = (Map)files.get(file);
            for(Iterator j = regexs.keySet().iterator(); j.hasNext(); ) {
                String name = (String)j.next();
                String pattern = (String)regexs.get(name);
                
                Pattern p = Pattern.compile(pattern);
                Matcher m = p.matcher(text);
                
                m.find();
                //System.out.println(m.group());
                System.out.println(name + "(" + file + ") = " + m.group(1));
                //System.exit(0);
            }
            
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        if(args.length != 1) {
            System.out.println("Usage: JBossConfigAnalyzer <server dir>");
            System.out.println("<server dir> is usually <JBOSS>/server/default");
            System.exit(0);
        }
        
        JBossConfigAnalyzer analyzer = new JBossConfigAnalyzer(args[0]);
        analyzer.analyze();
    }
    
}
