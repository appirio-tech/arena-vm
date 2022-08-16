/*
 * JavaLongCodeGenerator
 * 
 * Created 05/13/2006
 */
package com.topcoder.services.compiler.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.topcoder.server.util.Java13Utils;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.shared.common.ServicesConstants;
import com.topcoder.shared.language.JavaLanguage;
import com.topcoder.shared.problem.DataType;
import com.topcoder.shared.problem.ProblemComponent;
import com.topcoder.shared.problem.ProblemConstants;

/**
 * LongComponentCodeGenerator implementation for
 * Java solutions and submissions
 * 
 * @author Diego Belfer (mural)
 * @version $Id: JavaLongCodeGenerator.java 70823 2008-05-27 20:49:33Z dbelfer $
 */
public class JavaLongCodeGenerator implements LongComponentCodeGenerator {

    private static final Logger logger = Logger.getLogger(JavaLongCodeGenerator.class);

    /** 
     * @see com.topcoder.services.compiler.util.LongComponentCodeGenerator#generateWrapperForUserCode(ProblemComponent, String)
     */
    public String generateWrapperForUserCode(ProblemComponent pc, String packageName) {
        try{
            StringBuffer sol = new StringBuffer(1000);
            
            //Add package declaration
            sol.append("package ");
            sol.append(packageName);
            sol.append(";");
            
            //load the code from the local fs, then replace variables as needed
            try {
                BufferedReader ir = new BufferedReader(new FileReader(ServicesConstants.LONG_CONTEST_USER_WRAPPER));
                while(ir.ready()) {
                    sol.append(ir.readLine());
                    sol.append("\n");
                }
                ir.close();
            } catch(IOException ios) {
                logger.error("Error loading wrapper", ios);
            }
            
            //replace simple things
            Java13Utils.replace(sol, "<WRAPPER_CLASS>", ProblemConstants.WRAPPER_CLASS);
            Java13Utils.replace(sol, "<CLASS_NAME>", pc.getClassName());
            
            String name = pc.getExposedClassName();
            if(name == null || name.equals("")) {
                name = "ExposedWrapper";
            }
            Java13Utils.replace(sol, "<EXPOSED_WRAPPER_CLASS>", name);
            
            
            //get the code from <CALLING_METHODS> to </CALLING_METHODS>, repeat over each method
            String innerLoop = sol.substring(sol.indexOf("<METHODS>"), sol.indexOf("</METHODS>") + "</METHODS>".length());
            String innerTemplate = innerLoop.substring("<METHODS>".length(), innerLoop.length() - "</METHODS>".length());
               
            StringBuffer inner = new StringBuffer();
            
            String[] methods = pc.getAllMethodNames();
            DataType[][] paramTypes = pc.getAllParamTypes();
            DataType[] returnTypes = pc.getAllReturnTypes();
    
            for(int i = 1; i<methods.length; i++) {
                inner.append(innerTemplate);
                
                Java13Utils.replace(inner, "<METHOD_NUMBER>", "" + i);
                
                //args
                String argsLoop = inner.substring(inner.indexOf("<ARGS>"), inner.indexOf("</ARGS>") + "</ARGS>".length());
                String argsTemplate = argsLoop.substring("<ARGS>".length(), argsLoop.length() - "</ARGS>".length());
    
                StringBuffer args = new StringBuffer();
    
                for(int j = 0; j<paramTypes[i].length; j++){
                    args.append(argsTemplate);
                    
                    String type = paramTypes[i][j].getDescriptor(JavaLanguage.ID);
                    
                    Java13Utils.replace(args, "<ARG_TYPE>", type);
                    Java13Utils.replace(args, "<ARG_NAME>", "v" + j);
                    
                    type = Java13Utils.replace(new StringBuffer(type), "[]", "Array").toString();
                    type = Character.toUpperCase(type.charAt(0))+type.substring(1);
                    
                    Java13Utils.replace(args, "<ARG_METHOD_NAME>", "get" + type);
                }
    
                Java13Utils.replace(inner, argsLoop, args.toString());
               
                Java13Utils.replace(inner, "<RETURN_TYPE>", returnTypes[i].getDescriptor(JavaLanguage.ID));
                Java13Utils.replace(inner, "<METHOD_NAME>", methods[i]);
                
                String params = "";
                for(int j = 0; j<paramTypes[i].length; j++){
                    if(j!=0){
                        params += ", ";
                    }
                    params += "v";
                    params += j;
                }
                
                Java13Utils.replace(inner, "<PARAMS>", params);
                
            }
            
            Java13Utils.replace(sol, innerLoop, inner.toString());
            if (logger.isDebugEnabled()) {
                logger.debug("Long user wrapper solution is: \n" + sol.toString());
            }
            return sol.toString();
        }catch(Exception e){
            logger.error("Error building wrapper.",e);
            return "";
        }
    }

    /**
     * @see com.topcoder.services.compiler.util.LongComponentCodeGenerator#generateLongTestProxyCode(com.topcoder.shared.problem.ProblemComponent, java.lang.String)
     */
    public String generateLongTestProxyCode(ProblemComponent pc, String packageName) {
        StringBuffer sol = new StringBuffer(1000);
        
        //load the code from the local fs, then replace variables as needed
        try {
            BufferedReader ir = new BufferedReader(new FileReader(ServicesConstants.LONG_CONTEST_MPSQAS_WRAPPER));
            while(ir.ready()) {
                sol.append(ir.readLine());
                sol.append("\n");
            }
            ir.close();
        } catch(IOException ios) {
            logger.error("Error loading wrapper", ios);
        }
        
        //replace simple things
        Java13Utils.replace(sol, "<PACKAGE_NAME>", packageName);
        Java13Utils.replace(sol, "<TESTER_IO_CLASS>", ProblemConstants.TESTER_IO_CLASS);
        
        Java13Utils.replace(sol, "<CLASS_NAME>", pc.getClassName());
        
        //get the code from <CALLING_METHODS> to </CALLING_METHODS>, repeat over each method
        String innerLoop = sol.substring(sol.indexOf("<CALLING_METHODS>"), sol.indexOf("</CALLING_METHODS>") + "</CALLING_METHODS>".length());
        String innerTemplate = innerLoop.substring("<CALLING_METHODS>".length(), innerLoop.length() - "</CALLING_METHODS>".length());
               
        StringBuffer inner = new StringBuffer();
        
        String[] methodNames = pc.getAllMethodNames();
        DataType[] returnTypes = pc.getAllReturnTypes();
        DataType[][] paramTypes = pc.getAllParamTypes();
        for(int i = 1; i<methodNames.length; i++){
            logger.debug("METHOD " + i);
            
            String rt = returnTypes[i].getDescriptor(JavaLanguage.ID);
            
            inner.append(innerTemplate);
            
            Java13Utils.replace(inner, "<RETURN_TYPE>", rt);
            Java13Utils.replace(inner, "<METHOD_NAME>", methodNames[i]);
    
            String params = "";
            for(int j = 0; j<paramTypes[i].length; j++){
                if(j!=0)params += ",";
                params += paramTypes[i][j].getDescriptor(JavaLanguage.ID);
                params += " a";
                params += j;
            }
            
            Java13Utils.replace(inner, "<PARAMS>", params);
            Java13Utils.replace(inner, "<METHOD_NUMBER>", "" + i);
            
            //args
            String argsLoop = inner.substring(inner.indexOf("<WRITE_ARGS>"), inner.indexOf("</WRITE_ARGS>") + "</WRITE_ARGS>".length());
            String argsTemplate = argsLoop.substring("<WRITE_ARGS>".length(), argsLoop.length() - "</WRITE_ARGS>".length());
            
            StringBuffer args = new StringBuffer();
            
            for(int j = 0; j<paramTypes[i].length; j++){
                args.append(argsTemplate);
                Java13Utils.replace(args, "<ARG_NAME>", "a" + j);
            }
            
            Java13Utils.replace(inner, argsLoop, args.toString());
    
            rt= Java13Utils.replace(new StringBuffer(rt), "[]", "Array").toString();
            rt = Character.toUpperCase(rt.charAt(0))+rt.substring(1);
            
            Java13Utils.replace(inner, "<RETURN_METHOD_NAME>", "get" + rt);
    
            String defaultRet = "";
            rt = returnTypes[i].getDescriptor(JavaLanguage.ID);
            if(rt.equals("boolean"))
                defaultRet = "false";
            else if(rt.equals("char"))
                defaultRet = "'\\0'";
            else if(Character.isLowerCase(rt.charAt(0)) && !rt.endsWith("[]"))
                defaultRet = "0";
            else 
                defaultRet = "null";
            
            Java13Utils.replace(inner, "<DEFAULT_RETURN>", defaultRet);
        }
    
        Java13Utils.replace(sol, innerLoop, inner.toString());
        
        //get the code from <EXPOSED_METHODS> to </EXPOSED_METHODS>, repeat over each method
        innerLoop = sol.substring(sol.indexOf("<EXPOSED_METHODS>"), sol.indexOf("</EXPOSED_METHODS>") + "</EXPOSED_METHODS>".length());
        innerTemplate = innerLoop.substring("<EXPOSED_METHODS>".length(), innerLoop.length() - "</EXPOSED_METHODS>".length());
               
        inner = new StringBuffer();
        
        methodNames = pc.getAllExposedMethodNames();
        returnTypes = pc.getAllExposedReturnTypes();
        paramTypes = pc.getAllExposedParamTypes();
        for(int i = 0; i<methodNames.length; i++){
            logger.debug("METHOD " + i);
            
            String rt = returnTypes[i].getDescriptor(JavaLanguage.ID);
            
            inner.append(innerTemplate);
            
            Java13Utils.replace(inner, "<RETURN_TYPE>", rt);
            Java13Utils.replace(inner, "<METHOD_NAME>", methodNames[i]);
    
            String params = "";
            for(int j = 0; j<paramTypes[i].length; j++){
                if(j!=0)params += ",";
                params += " a";
                params += j;
            }
            
            Java13Utils.replace(inner, "<PARAMS>", params);
            Java13Utils.replace(inner, "<METHOD_NUMBER>", "" + i);
            
            //args
            String argsLoop = inner.substring(inner.indexOf("<ARGS>"), inner.indexOf("</ARGS>") + "</ARGS>".length());
            String argsTemplate = argsLoop.substring("<ARGS>".length(), argsLoop.length() - "</ARGS>".length());
            
            StringBuffer args = new StringBuffer();
            
            for(int j = 0; j<paramTypes[i].length; j++){
                args.append(argsTemplate);
                Java13Utils.replace(args, "<ARG_NAME>", "a" + j);
                Java13Utils.replace(args, "<ARG_TYPE>", paramTypes[i][j].getDescriptor(JavaLanguage.ID));
    
                rt = paramTypes[i][j].getDescriptor(JavaLanguage.ID);
                rt= Java13Utils.replace(new StringBuffer(rt), "[]", "Array").toString();
                rt = Character.toUpperCase(rt.charAt(0))+rt.substring(1);
            
                Java13Utils.replace(args, "<ARG_METHOD_NAME>", "get" + rt);
                
            }
            
            Java13Utils.replace(inner, argsLoop, args.toString());
    
        }
    
        Java13Utils.replace(sol, innerLoop, inner.toString());
        
        if (logger.isDebugEnabled()) {
            logger.debug("Long writer wrapper solution is: \n" + sol.toString());
        }
        return sol.toString();
    }
    
    public String generateWrapperForExposedCode(ProblemComponent pc, String packageName) {
        StringBuffer sol = new StringBuffer(1000);
        
        //load the code from the local fs, then replace variables as needed
        try {
            BufferedReader ir = new BufferedReader(new FileReader(ServicesConstants.LONG_CONTEST_EXOPOSED_WRAPPER));
            while(ir.ready()) {
                sol.append(ir.readLine());
                sol.append("\n");
            }
            ir.close();
        } catch(IOException ios) {
            logger.error("Error loading wrapper", ios);
        }
        
        //replace simple things
        String name = pc.getExposedClassName();
        if(name == null || name.equals("")) {
            name = "ExposedWrapper";
        }
        Java13Utils.replace(sol, "<EXPOSED_WRAPPER_CLASS>", name);
        
        //get the code from <CALLING_METHODS> to </CALLING_METHODS>, repeat over each method
        String innerLoop = sol.substring(sol.indexOf("<EXPOSED_METHODS>"), sol.indexOf("</EXPOSED_METHODS>") + "</EXPOSED_METHODS>".length());
        String innerTemplate = innerLoop.substring("<EXPOSED_METHODS>".length(), innerLoop.length() - "</EXPOSED_METHODS>".length());
               
        StringBuffer inner = new StringBuffer();
        
        String[] methodNames = pc.getAllExposedMethodNames();
        DataType[] returnTypes = pc.getAllExposedReturnTypes();
        DataType[][] paramTypes = pc.getAllExposedParamTypes();
        for(int i = 0; i<methodNames.length; i++){
            logger.debug("METHOD " + i);
            
            String rt = returnTypes[i].getDescriptor(JavaLanguage.ID);
            
            inner.append(innerTemplate);
            
            Java13Utils.replace(inner, "<RETURN_TYPE>", rt);
            Java13Utils.replace(inner, "<METHOD_NAME>", methodNames[i]);
    
            String params = "";
            for(int j = 0; j<paramTypes[i].length; j++){
                if(j!=0)params += ",";
                params += paramTypes[i][j].getDescriptor(JavaLanguage.ID);
                params += " a";
                params += j;
            }
            
            Java13Utils.replace(inner, "<PARAMS>", params);
            Java13Utils.replace(inner, "<METHOD_NUMBER>", "" + i);
            
            //args
            String argsLoop = inner.substring(inner.indexOf("<WRITE_ARGS>"), inner.indexOf("</WRITE_ARGS>") + "</WRITE_ARGS>".length());
            String argsTemplate = argsLoop.substring("<WRITE_ARGS>".length(), argsLoop.length() - "</WRITE_ARGS>".length());
            
            StringBuffer args = new StringBuffer();
            
            for(int j = 0; j<paramTypes[i].length; j++){
                args.append(argsTemplate);
                Java13Utils.replace(args, "<ARG_NAME>", "a" + j);
            }
            
            Java13Utils.replace(inner, argsLoop, args.toString());
    
            rt= Java13Utils.replace(new StringBuffer(rt), "[]", "Array").toString();
            rt = Character.toUpperCase(rt.charAt(0))+rt.substring(1);
            
            Java13Utils.replace(inner, "<RETURN_METHOD_NAME>", "get" + rt);
    
            String defaultRet = "";
            rt = returnTypes[i].getDescriptor(JavaLanguage.ID);
            if(rt.equals("boolean"))
                defaultRet = "false";
            else if(rt.equals("char"))
                defaultRet = "'\\0'";
            else if(Character.isLowerCase(rt.charAt(0)) && !rt.endsWith("[]"))
                defaultRet = "0";
            else 
                defaultRet = "null";
            
            Java13Utils.replace(inner, "<DEFAULT_RETURN>", defaultRet);
        }
    
        Java13Utils.replace(sol, innerLoop, inner.toString());
        
        if (logger.isDebugEnabled()) {
            logger.debug("Long writer wrapper solution is: \n" + sol.toString());
        }
        return sol.toString();
    }
    
}
