/*
 * CPPLongCodeGenerator
 * 
 * Created 05/13/2006
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
 * LongComponentCodeGenerator implementation for C++
 * submissions and solutions
 * 
 * @author Diego Belfer (mural)
 * @version $Id: CPPLongCodeGenerator.java 70823 2008-05-27 20:49:33Z dbelfer $
 */
public class CPPLongCodeGenerator implements LongComponentCodeGenerator {
    private static final Logger logger = Logger.getLogger(CPPLongCodeGenerator.class);
    
    /**
     * @see com.topcoder.services.compiler.util.LongComponentCodeGenerator#generateWrapperForUserCode(com.topcoder.shared.problem.ProblemComponent, java.lang.String)
     */
    public String generateWrapperForUserCode(ProblemComponent pc, String packageName) {
        StringBuffer sol = new StringBuffer(1000);
        
        //load the code from the local fs, then replace variables as needed
        try {
            BufferedReader ir = new BufferedReader(new FileReader(ServicesConstants.LONG_CONTEST_CPP_USER_WRAPPER));
            while(ir.ready()) {
                sol.append(ir.readLine());
                sol.append("\n");
            }
            ir.close();
        } catch(IOException ios) {
            logger.error("Error loading wrapper", ios);
        }

        //replace simple things
        Java13Utils.replace(sol, "<LONG_IO_CLASS>", ServicesConstants.LONG_IO);
        Java13Utils.replace(sol, "<CLASS_NAME>", pc.getClassName());
        
        String name = pc.getExposedClassName();
        if(name == null || name.equals("")) {
            name = "ExposedWrapper";
        }
        Java13Utils.replace(sol, "<EXPOSED_WRAPPER_CLASS>", name);

        String innerLoop = sol.substring(indexOf(sol,"<METHODS>"), indexOf(sol,"</METHODS>") + "</METHODS>".length());
        String innerTemplate = innerLoop.substring("<METHODS>".length(), innerLoop.length() - "</METHODS>".length());

        StringBuffer inner = new StringBuffer();

        DataType[] rets = pc.getAllReturnTypes();
        String[] methods = pc.getAllMethodNames();
        DataType[][] params = pc.getAllParamTypes();
        
        for(int i = 1; i<methods.length; i++) {
            inner.append(innerTemplate);

            Java13Utils.replace(inner, "<METHOD_NUMBER>", "" + i);

            //args
            String argsLoop = inner.substring(indexOf(inner,"<ARGS>"), indexOf(inner,"</ARGS>") + "</ARGS>".length());
            String argsTemplate = argsLoop.substring("<ARGS>".length(), argsLoop.length() - "</ARGS>".length());

            StringBuffer args = new StringBuffer();

            boolean[] point = new boolean[params[i].length];
            for(int j = 0; j<params[i].length; j++){
                args.append(argsTemplate);

                String type = params[i][j].getDescriptor(ContestConstants.CPP);

                point[j] = false;
                if(type.startsWith("vector")){
                    type = type.substring(6).trim();
                    type = type.substring(1,type.length()-1).trim();
                    if(type.startsWith("vector")){
                        type = type.substring(6).trim();
                        type = type.substring(1,type.length()-1).trim();
                        type = type + "Array";
                    }
                    type = type + "Array";
                    int idx=type.indexOf(' ');
                    while(idx != -1){
                        type = type.substring(0,idx)+Character.toUpperCase(type.charAt(idx+1))+type.substring(idx+2);
                        idx=type.indexOf(' ');
                    }
                    point[j] = true;
                }else if(type.equals("string")){
                    point[j] = true;
                }
                
                String typeDesc = params[i][j].getDescriptor(ContestConstants.CPP);
                //if(point[j])
                  //  typeDesc += "*";
                
                Java13Utils.replace(args, "<ARG_TYPE>", typeDesc);
                Java13Utils.replace(args, "<ARG_NAME>", "v" + j);

                type = Character.toUpperCase(type.charAt(0))+type.substring(1);

                Java13Utils.replace(args, "<ARG_METHOD_NAME>", "get" + type);
            }

            Java13Utils.replace(inner, argsLoop, args.toString());

            String rtype = rets[i].getDescriptor(ContestConstants.CPP);
            Java13Utils.replace(inner, "<RETURN_TYPE>", rtype);
            Java13Utils.replace(inner, "<METHOD_NAME>", methods[i]);

            String paramsVal = "";
            for(int j = 0; j<params[i].length; j++){
                if(j!=0){
                    paramsVal += ", ";
                }
                //if(point[j]) {
                    //paramsVal += "*";
                //}
                paramsVal += "v";
                paramsVal += j;
            }

            Java13Utils.replace(inner, "<PARAMS>", paramsVal);
            
            String returnPointerVal = "";
            if(rtype.startsWith("vector") || rtype.equals("string")){
                returnPointerVal = "&";
            }

            Java13Utils.replace(inner, "<RETURN_POINTER_VAL>", returnPointerVal);
        }

        Java13Utils.replace(sol, innerLoop, inner.toString());

        return sol.toString();
    }

    /**
     * @see com.topcoder.services.compiler.util.LongComponentCodeGenerator#generateLongTestProxyCode(com.topcoder.shared.problem.ProblemComponent, java.lang.String)
     */
    public String generateLongTestProxyCode(ProblemComponent pc, String packageName) {
        throw new UnsupportedOperationException("CPP primary solutions not implemented");
    }
    
    private static final int indexOf(StringBuffer s , String v) {
        return s.indexOf(v);
    }

    public String generateWrapperForExposedCode(ProblemComponent pc, String packageName) {
        StringBuffer sol = new StringBuffer(1000);
        
        //load the code from the local fs, then replace variables as needed
        try {
            BufferedReader ir = new BufferedReader(new FileReader(ServicesConstants.LONG_CONTEST_CPP_EXPOSED_WRAPPER));
            while(ir.ready()) {
                sol.append(ir.readLine());
                sol.append("\n");
            }
            ir.close();
        } catch(IOException ios) {
            logger.error("Error loading wrapper", ios);
        }

        //replace simple things
        Java13Utils.replace(sol, "<LONG_IO_CLASS>", ServicesConstants.LONG_IO);
        
        String name = pc.getExposedClassName();
        if(name == null || name.equals("")) {
            name = "ExposedWrapper";
        }
        Java13Utils.replace(sol, "<EXPOSED_WRAPPER_CLASS>", name);

        String innerLoop = sol.substring(indexOf(sol,"<EXPOSED_METHODS>"), indexOf(sol,"</EXPOSED_METHODS>") + "</EXPOSED_METHODS>".length());
        String innerTemplate = innerLoop.substring("<EXPOSED_METHODS>".length(), innerLoop.length() - "</EXPOSED_METHODS>".length());

        StringBuffer inner = new StringBuffer();

        DataType[] rets = pc.getAllExposedReturnTypes();
        String[] methods = pc.getAllExposedMethodNames();
        DataType[][] params = pc.getAllExposedParamTypes();
        
        for(int i = 0; i<methods.length; i++) {
            inner.append(innerTemplate);

            Java13Utils.replace(inner, "<METHOD_NUMBER>", "" + i);

            //args
            String argsLoop = inner.substring(indexOf(inner,"<WRITE_ARGS>"), indexOf(inner,"</WRITE_ARGS>") + "</WRITE_ARGS>".length());
            String argsTemplate = argsLoop.substring("<WRITE_ARGS>".length(), argsLoop.length() - "</WRITE_ARGS>".length());

            StringBuffer args = new StringBuffer();

            boolean[] point = new boolean[params[i].length];
            for(int j = 0; j<params[i].length; j++){
                args.append(argsTemplate);

                String type = params[i][j].getDescriptor(ContestConstants.CPP);

                point[j] = false;
                if(type.startsWith("vector")){
                    type = type.substring(6).trim();
                    type = type.substring(1,type.length()-1).trim();
                    if(type.startsWith("vector")){
                        type = type.substring(6).trim();
                        type = type.substring(1,type.length()-1).trim();
                        type = type + "Array";
                    }
                    type = type + "Array";
                    int idx=type.indexOf(' ');
                    while(idx != -1){
                        type = type.substring(0,idx)+Character.toUpperCase(type.charAt(idx+1))+type.substring(idx+2);
                        idx=type.indexOf(' ');
                    }
                    point[j] = true;
                }else if(type.equals("string")){
                    point[j] = true;
                }
                
                String typeDesc = params[i][j].getDescriptor(ContestConstants.CPP);
                //if(point[j])
                  //  typeDesc += "*";
                
                Java13Utils.replace(args, "<ARG_TYPE>", typeDesc);
                Java13Utils.replace(args, "<ARG_NAME>", "v" + j);

                String returnPointerVal = "";
                if(typeDesc.startsWith("vector") || typeDesc.equals("string")){
                    returnPointerVal = "&";
                }

                Java13Utils.replace(args, "<RETURN_POINTER_VAL>", returnPointerVal);

                type = Character.toUpperCase(type.charAt(0))+type.substring(1);

                Java13Utils.replace(args, "<ARG_METHOD_NAME>", "get" + type);
            }

            Java13Utils.replace(inner, argsLoop, args.toString());

            String rtype = rets[i].getDescriptor(ContestConstants.CPP);
            Java13Utils.replace(inner, "<RETURN_TYPE>", rtype);
            
            if(rtype.startsWith("vector")){
                rtype = rtype.substring(6).trim();
                rtype = rtype.substring(1,rtype.length()-1).trim();
                if(rtype.startsWith("vector")){
                    rtype = rtype.substring(6).trim();
                    rtype = rtype.substring(1,rtype.length()-1).trim();
                    rtype = rtype + "Array";
                }
                rtype = rtype + "Array";
                int idx=rtype.indexOf(' ');
                while(idx != -1){
                    rtype = rtype.substring(0,idx)+Character.toUpperCase(rtype.charAt(idx+1))+rtype.substring(idx+2);
                    idx=rtype.indexOf(' ');
                }
            }
            rtype = Character.toUpperCase(rtype.charAt(0))+rtype.substring(1);
            Java13Utils.replace(inner, "<RETURN_METHOD_NAME>", "get" + rtype);
            
            Java13Utils.replace(inner, "<METHOD_NAME>", methods[i]);

            String paramsVal = "";
            for(int j = 0; j<params[i].length; j++){
                if(j!=0){
                    paramsVal += ", ";
                }
                paramsVal += params[i][j].getDescriptor(ContestConstants.CPP) + " ";
                paramsVal += "v";
                paramsVal += j;
            }

            Java13Utils.replace(inner, "<PARAMS>", paramsVal);
            
        }

        Java13Utils.replace(sol, innerLoop, inner.toString());

        return sol.toString();
    }
}
