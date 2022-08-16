/*
 * DotNetSecurtyChecker
 *
 * Created 12/19/2006
 */
package com.topcoder.services.compiler.util.dotnet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

import com.topcoder.farm.deployer.process.ProcessRunner;
import com.topcoder.server.util.FileUtil;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.services.compiler.util.MSILParser.MSILParser;
import com.topcoder.services.compiler.util.MSILParser.ParseException;
import com.topcoder.services.compiler.util.MSILParser.SimpleNode;
import com.topcoder.services.compiler.util.MSILParser.security.SecurityCheck;
import com.topcoder.services.compiler.util.MSILParser.visitor.ClassNameVisitor;
import com.topcoder.services.compiler.util.MSILParser.visitor.ClassNameVisitorState;
import com.topcoder.services.compiler.util.MSILParser.visitor.TypeVisitor;

/**
 * DotNetSecurityChecker is responsible for .NET code verification. <p>
 *
 * It provides method for compiled class verification.<p>
 *
 * This class makes use of external programs: <p>
 *  <il> MS ildasm.exe is used for compiled classes verification.
 *
 *
 * Code extracted from DotNetCodeCompiler.
 *
 * @author Diego Belfer (mural)
 * @version $Id: DotNetSecurtyChecker.java 82545 2012-12-18 13:50:53Z FireIce $
 */
public class DotNetSecurtyChecker {
    private final Logger logger = Logger.getLogger(DotNetSecurtyChecker.class);
    private final boolean mustCheckMethods = Boolean.valueOf(System.getProperty("com.topcoder.services.compiler.util.dotnet.DotNetSecurtyChecker.mustCheckMethods", "true")).booleanValue();
    private final String ILDASM = System.getProperty("com.topcoder.services.compiler.util.dotnet.DotNetSecurtyChecker.ildasm", "c:\\program files\\microsoft.net\\sdk\\v2.0\\bin\\ildasm.exe");


    /**
     * Checks compiled class file and returns a string containing information about security violationsor missing
     * declarations.
     *
     * @param folder The folder where compiled class reside and where required files will be generated.
     * @param className The name of the class to check for security violations.
     * @param extraAllowedClassNames Class names of any class referenced by the main class that should be allowed.
     * @param threadingAllowed If threading usage should be allowed
     *
     * @return an empty string if no security violations were found or
     *         a string containing security violation information
     *
     * @throws IOException If an IOException is thrown trying to access compiled class file or
     *                      while generating security information
     *
     */
    public String checkCompiledClass(File folder, String className, String fileExtension, String[] extraAllowedClassNames, boolean threadingAllowed) throws IOException {
        return checkCompiledClass(folder, className, fileExtension, extraAllowedClassNames, threadingAllowed, new String[]{});
    }

    /**
     * Checks compiled class file and returns a string containing information about security violations or missing
     * declarations.
     *
     * @param folder The folder where compiled class reside and where required files will be generated.
     * @param className The name of the class to check for security violations.
     * @param extraAllowedClassNames Class names of any class referenced by the main class that should be allowed.
     * @param threadingAllowed If threading usage should be allowed
     * @param expectedMethods The name of the methods the class should declare.
     *
     * @return an empty string if no security violations were found or
     *         a string containing security violation information
     *
     * @throws IOException If an IOException is thrown trying to access compiled class file or
     *                      while generating security information
     *
     */
    public String checkCompiledClass(File folder, String className, String fileExtension, String[] extraAllowedClassNames, boolean threadingAllowed, String[] expectedMethods) throws IOException {
        String[] cmd = new String[]{
                "\"" + ILDASM + "\"",
                "/NOBAR",
                "\"" + new File(folder, className + "."+fileExtension).getAbsolutePath() + "\"",
                "\"/out:" + new File(folder, className + ".il").getAbsolutePath()+"\""};

        ProcessRunner runner = new ProcessRunner(cmd);
        try {
            runner.run();
        } catch (Exception e) {
            logger.error("Exception while runnning disassembler",e);
            return "Internal error: Could not verify assembly";
        }
        return checkDisassembledFile(folder, className, extraAllowedClassNames, threadingAllowed, expectedMethods);
    }


    private String checkDisassembledFile(File folder, String className, String[] extraAllowedClassNames, boolean threadingAllowed, String[] expectedMethods) throws FileNotFoundException {
        //parse
        File ilFile = new File(folder, className + ".il");
        FileReader fileReader = new FileReader(ilFile);
        MSILParser parser = new MSILParser(fileReader);
        SimpleNode node = null;
        try {
            node = parser.ILFile();

            //get the class name visitor going
            ClassNameVisitor cv = new ClassNameVisitor();
            node.jjtAccept(cv, new ClassNameVisitorState());

            //type visitor
            TypeVisitor tv = new TypeVisitor();
            node.jjtAccept(tv, new Boolean(false));

            int extraClassesCount = extraAllowedClassNames == null ? 0 : extraAllowedClassNames.length;
            Set classes = cv.getClassesSet();

            if (!classes.contains(className)) {
                return "Required class '" + className + "' not found.";
            }

            if (mustCheckMethods) {
                Set methods = cv.getMethods();
                for (int i = 0; i < expectedMethods.length; i++) {
                    if (!methods.contains(className+"::"+expectedMethods[i])) {
                        return "Required method '" + expectedMethods[i] + "' not found.";
                    }
                }
            }
            if (extraAllowedClassNames !=  null) {
                for(int i = 0; i < extraClassesCount; i++)
                    classes.add(extraAllowedClassNames[i]);
            }

            SecurityCheck check = new SecurityCheck(classes, threadingAllowed);
            String r = check.checkTypes(tv.getTypes());
            r += check.checkMethods(tv.getMethods());
            return r;
        } catch (ParseException ex) {
            closeFileReader(fileReader);
            String fileContent = "Failed to get IL File";
            try {
                fileContent = FileUtil.getStringContents(ilFile);
            } catch (IOException e) {
                logger.error("Exception obtaining IL file after a parse exception",e);
            }
            logger.error("FIXME: Parse Error ILFile contents=\n"+fileContent, ex);
            return "Failed to verify security.";
        } finally {
            closeFileReader(fileReader);
        }
    }

    private void closeFileReader(FileReader fileReader) {
        try {
            if (fileReader != null) {
                fileReader.close();
            }
        } catch (Exception e) {
        }
    }

}
