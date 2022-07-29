/*
 * DotNetLongCodeGenerator
 * 
 * Created 05/13/2006
 */
package com.topcoder.services.compiler.util;

import com.topcoder.shared.language.CSharpLanguage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.server.util.Java13Utils;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.shared.common.ServicesConstants;
import com.topcoder.shared.problem.DataType;
import com.topcoder.shared.problem.ProblemComponent;
import com.topcoder.shared.problem.ProblemConstants;

/**
 * LongComponentCodeGenerator implementation for
 * .NET solutions and submissions
 * 
 * @author Diego Belfer (mural)
 * @version $Id: DotNetLongCodeGenerator.java 70823 2008-05-27 20:49:33Z dbelfer $
 */
public class DotNetLongCodeGenerator implements LongComponentCodeGenerator {
    private static final Logger logger = Logger.getLogger(DotNetLongCodeGenerator.class);
    
    /**
     * @see com.topcoder.services.compiler.util.LongComponentCodeGenerator#generateWrapperForUserCode(com.topcoder.shared.problem.ProblemComponent, java.lang.String)
     */
    public String generateWrapperForUserCode(ProblemComponent pc, String packageName) {
        try{
            StringBuffer sol = new StringBuffer(1000);
        
            //load the code from the local fs, then replace variables as needed
            try {
                BufferedReader ir = new BufferedReader(new FileReader(ServicesConstants.LONG_CONTEST_DOTNET_USER_WRAPPER));
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
            String innerLoop = sol.substring(indexOf(sol,"<METHODS>"), indexOf(sol,"</METHODS>") + "</METHODS>".length());
            String innerTemplate = innerLoop.substring("<METHODS>".length(), innerLoop.length() - "</METHODS>".length());
               
            StringBuffer inner = new StringBuffer();
            
            String[] methods = pc.getAllMethodNames();
            DataType[][] paramTypes = pc.getAllParamTypes();
            DataType[] returnTypes = pc.getAllReturnTypes();

            for(int i = 1; i<methods.length; i++) {
                inner.append(innerTemplate);
                
                Java13Utils.replace(inner, "<METHOD_NUMBER>", "" + i);
                
                //args
                String argsLoop = inner.substring(indexOf(inner,"<ARGS>"), indexOf(inner,"</ARGS>") + "</ARGS>".length());
                String argsTemplate = argsLoop.substring("<ARGS>".length(), argsLoop.length() - "</ARGS>".length());

                StringBuffer args = new StringBuffer();

                for(int j = 0; j<paramTypes[i].length; j++){
                    args.append(argsTemplate);
                    
                    String type = paramTypes[i][j].getDescriptor(ContestConstants.CSHARP);
                    
                    Java13Utils.replace(args, "<ARG_TYPE>", type);
                    Java13Utils.replace(args, "<ARG_NAME>", "v" + j);
                    
                    type = Java13Utils.replace(new StringBuffer(type), "[]", "Array").toString();
                    type = Character.toUpperCase(type.charAt(0))+type.substring(1);
                    
                    Java13Utils.replace(args, "<ARG_METHOD_NAME>", "Get" + type);
                }

                Java13Utils.replace(inner, argsLoop, args.toString());
               
                Java13Utils.replace(inner, "<RETURN_TYPE>", returnTypes[i].getDescriptor(ContestConstants.CSHARP));
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
        throw new RuntimeException("Not supported");
    }

    private static final int indexOf(StringBuffer s , String v) {
        return s.indexOf(v);
    }
    
    public String generateWrapperForExposedCode(ProblemComponent pc, String packageName) {
        try{
            StringBuffer sol = new StringBuffer(1000);
        
            //load the code from the local fs, then replace variables as needed
            try {
                BufferedReader ir = new BufferedReader(new FileReader(ServicesConstants.LONG_CONTEST_DOTNET_EXPOSED_WRAPPER));
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
            
            String[] methods = pc.getAllExposedMethodNames();
            DataType[][] paramTypes = pc.getAllExposedParamTypes();
            DataType[] returnTypes = pc.getAllExposedReturnTypes();

            for(int i = 0; i<methods.length; i++) {
                inner.append(innerTemplate);
                
                String rt = returnTypes[i].getDescriptor(CSharpLanguage.ID);
                
                Java13Utils.replace(inner, "<METHOD_NUMBER>", "" + i);
                
                Java13Utils.replace(inner, "<RETURN_TYPE>", rt);
                Java13Utils.replace(inner, "<METHOD_NAME>", methods[i]);
                
                String params = "";
                for(int j = 0; j<paramTypes[i].length; j++){
                    if(j!=0)params += ",";
                    params += paramTypes[i][j].getDescriptor(CSharpLanguage.ID);
                    params += " v";
                    params += j;
                }

                Java13Utils.replace(inner, "<PARAMS>", params);
                
                //args
                String argsLoop = inner.substring(indexOf(inner,"<WRITE_ARGS>"), indexOf(inner,"</WRITE_ARGS>") + "</WRITE_ARGS>".length());
                String argsTemplate = argsLoop.substring("<WRITE_ARGS>".length(), argsLoop.length() - "</WRITE_ARGS>".length());

                StringBuffer args = new StringBuffer();

                for(int j = 0; j<paramTypes[i].length; j++){
                    args.append(argsTemplate);
                    Java13Utils.replace(args, "<ARG_NAME>", "v" + j);
                }

                Java13Utils.replace(inner, argsLoop, args.toString());
               
                String defaultRet = "";
                if(rt.equals("bool"))
                    defaultRet = "false";
                else if(rt.equals("char"))
                    defaultRet = "'\\0'";
                else if(rt.equals("string")) 
                    defaultRet = "null";
                else if(Character.isLowerCase(rt.charAt(0)) && !rt.endsWith("[]"))
                    defaultRet = "0";
                else 
                    defaultRet = "null";
                
                Java13Utils.replace(inner, "<DEFAULT_RETURN>", defaultRet);
                
                rt= Java13Utils.replace(new StringBuffer(rt), "[]", "Array").toString();
                rt = Character.toUpperCase(rt.charAt(0))+rt.substring(1);

                Java13Utils.replace(inner, "<RETURN_METHOD_NAME>", "Get" + rt);
                
                
            }
            
            Java13Utils.replace(sol, innerLoop, inner.toString());
            
            return sol.toString();
        }catch(Exception e){
            logger.error("Error building wrapper.",e);
            return "";
        }
    }
}
