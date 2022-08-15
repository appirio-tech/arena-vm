/*
 * Copyright (C) -2022 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.services.compiler.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.server.util.Java13Utils;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.shared.common.ServicesConstants;
import com.topcoder.shared.problem.DataType;
import com.topcoder.shared.problem.ProblemComponent;

/**
 * LongComponentCodeGenerator implementation for
 * Python solutions and submissions.
 *
 * <p>
 * Changes in version 1.1 (Python3 Support):
 * <ol>
 *      <li>Added {@link #python3} fields.</li>
 *      <li>Updated constructor to take <code>python3</code> parameter.</li>
 *      <li>Updated {@link #generateWrapperForUserCode(ProblemComponent, String)} and
 *       {@link #generateWrapperForExposedCode(ProblemComponent, String)} methods.</li>
 * </ol>
 * </p>
 *
 * @author Diego Belfer (mural), liuliquan
 * @version 1.1
 */
public class PythonLongCodeGenerator implements LongComponentCodeGenerator {
    private static final Logger logger = Logger.getLogger(PythonLongCodeGenerator.class);
    
    private final boolean python3;

    public PythonLongCodeGenerator(boolean python3) {
        this.python3 = python3;
    }

    /**
     * @see com.topcoder.services.compiler.util.LongComponentCodeGenerator#generateWrapperForUserCode(com.topcoder.shared.problem.ProblemComponent, java.lang.String)
     */
    public String generateWrapperForUserCode(ProblemComponent pc, String packageName) {
        try{
            StringBuffer sol = new StringBuffer(1000);
        
            //load the code from the local fs, then replace variables as needed
            try {
                BufferedReader ir = new BufferedReader(new FileReader(python3 ? ServicesConstants.LONG_CONTEST_PYTHON3_USER_WRAPPER : ServicesConstants.LONG_CONTEST_PYTHON_USER_WRAPPER));
                while(ir.ready()) {
                    sol.append(ir.readLine());
                    sol.append("\n");
                }
                ir.close();
            } catch(IOException ios) {
                logger.error("Error loading wrapper", ios);
            }
            
            //replace simple things
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

                    Java13Utils.replace(args, "<ARG_NAME>", "v" + j);
                    
                    type = Java13Utils.replace(new StringBuffer(type), "[]", "Array").toString();
                    type = Character.toUpperCase(type.charAt(0))+type.substring(1);
                    
                    Java13Utils.replace(args, "<ARG_METHOD_NAME>", "get" + type);
                    
                    
                }

                Java13Utils.replace(inner, argsLoop, args.toString());
               
                Java13Utils.replace(inner, "<METHOD_NAME>", methods[i]);
                
                String rettype = returnTypes[i].getDescriptor(ContestConstants.JAVA);

                rettype = Java13Utils.replace(new StringBuffer(rettype), "[]", "Array").toString();
                rettype = Character.toUpperCase(rettype.charAt(0))+rettype.substring(1);
                
                Java13Utils.replace(inner, "<RET_METHOD_NAME>", "write" + rettype);
                
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
        throw new UnsupportedOperationException("Python primary solutions not implemented");
    }

    private static final int indexOf(StringBuffer s , String v) {
        return s.indexOf(v);
    }
    
    public String generateWrapperForExposedCode(ProblemComponent pc, String packageName) {
        try{
            StringBuffer sol = new StringBuffer(1000);
        
            //load the code from the local fs, then replace variables as needed
            try {
                BufferedReader ir = new BufferedReader(new FileReader(python3 ? ServicesConstants.LONG_CONTEST_PYTHON3_EXPOSED_WRAPPER : ServicesConstants.LONG_CONTEST_PYTHON_EXPOSED_WRAPPER));
                while(ir.ready()) {
                    sol.append(ir.readLine());
                    sol.append("\n");
                }
                ir.close();
            } catch(IOException ios) {
                logger.error("Error loading wrapper", ios);
            }
            
            //replace simple things
            Java13Utils.replace(sol, "<WRAPPER_CLASS>", "Wrapper");
            
            String name = pc.getExposedClassName();
            if(name == null || name.equals("")) {
                name = "ExposedWrapper";
            }
            
            Java13Utils.replace(sol, "<EXPOSED_WRAPPER_CLASS>", name);
            
            //get the code from <CALLING_METHODS> to </CALLING_METHODS>, repeat over each method
            String innerLoop = sol.substring(indexOf(sol,"<EXPOSED_METHODS>"), indexOf(sol,"</EXPOSED_METHODS>") + "</EXPOSED_METHODS>".length());
            String innerTemplate = innerLoop.substring("<EXPOSED_METHODS>".length(), innerLoop.length() - "</EXPOSED_METHODS>".length());
               
            StringBuffer inner = new StringBuffer();
            
            String[] methods = pc.getAllExposedMethodNames();
            DataType[][] paramTypes = pc.getAllExposedParamTypes();
            DataType[] returnTypes = pc.getAllExposedReturnTypes();

            for(int i = 0; i<methods.length; i++) {
                inner.append(innerTemplate);
                
                Java13Utils.replace(inner, "<METHOD_NUMBER>", "" + i);
                
                //args
                String argsLoop = inner.substring(indexOf(inner,"<WRITE_ARGS>"), indexOf(inner,"</WRITE_ARGS>") + "</WRITE_ARGS>".length());
                String argsTemplate = argsLoop.substring("<WRITE_ARGS>".length(), argsLoop.length() - "</WRITE_ARGS>".length());

                StringBuffer args = new StringBuffer();

                for(int j = 0; j<paramTypes[i].length; j++){
                    args.append(argsTemplate);
                    
                    String type = paramTypes[i][j].getDescriptor(ContestConstants.CSHARP);

                    Java13Utils.replace(args, "<ARG_NAME>", "v" + j);
                    
                    type = Java13Utils.replace(new StringBuffer(type), "[]", "Array").toString();
                    type = Character.toUpperCase(type.charAt(0))+type.substring(1);
                    
                    Java13Utils.replace(args, "<ARG_METHOD>", "write" + type);
                }

                Java13Utils.replace(inner, argsLoop, args.toString());
               
                Java13Utils.replace(inner, "<METHOD_NAME>", methods[i]);
                
                String rettype = returnTypes[i].getDescriptor(ContestConstants.JAVA);

                rettype = Java13Utils.replace(new StringBuffer(rettype), "[]", "Array").toString();
                rettype = Character.toUpperCase(rettype.charAt(0))+rettype.substring(1);
                
                Java13Utils.replace(inner, "<RETURN_METHOD_NAME>", "get" + rettype);
                
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
}
