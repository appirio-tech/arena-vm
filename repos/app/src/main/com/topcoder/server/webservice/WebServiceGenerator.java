package com.topcoder.server.webservice;

/**
 * Title:        WebServiceGenerator
 * Description:  Generates a Web Service from a WebServiceProblem
 * Copyright:    Copyright (c) 2002
 * Company:      TopCoder
 * @author       Jeremy Nuanes
 * @version 1.0
 */

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.server.webservice.exception.WebServiceDeploymentException;
import com.topcoder.server.common.RemoteFile;
import com.topcoder.server.services.TeamServices;
import com.topcoder.server.services.TeamServicesException;
import com.topcoder.server.webservice.remoteserver.CSServer;
import com.topcoder.server.webservice.remoteserver.ICSServer;
import com.topcoder.server.util.FileUtil;
import com.topcoder.shared.util.logging.*;
import com.topcoder.shared.language.*;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.lang.InterruptedException;
import java.lang.Process;
import java.lang.Runtime;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.Iterator;
import javax.xml.parsers.ParserConfigurationException;

public class WebServiceGenerator {

    private static final Logger _logger = Logger.getLogger(WebServiceGenerator.class);
    private static TeamServices _teamServices = TeamServices.getInstance();

    private WebServiceGenerator() {
    }

    // call this before the xrpcc.  The config file will be used by the xrpcc tool
    private static File writeConfigXML(File location, String serviceName, String packageName) throws FileNotFoundException, IOException {
        File file = new File(location, "config.xml");
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        writer.newLine();
        writer.write("<configuration xmlns=\"http://java.sun.com/xml/ns/jax-rpc/ri/config\">");
        writer.newLine();
        writer.write("  <wsdl location=\"http://" + WebServiceGeneratorResources.getProperty(WebServiceGeneratorResources.TOMCAT_SERVER));
        if (WebServiceGeneratorResources.getProperty(WebServiceGeneratorResources.TOMCAT_SERVER_PORT) != null)
            writer.write(":" + WebServiceGeneratorResources.getProperty(WebServiceGeneratorResources.TOMCAT_SERVER_PORT));
        writer.write("/" + serviceName + "/topcoderws?WSDL\"");
        writer.newLine();
        writer.write("    packageName=\"" + packageName + "\"/>");
        writer.newLine();
        writer.write("</configuration>");
        writer.newLine();
        writer.flush();
        writer.close();
        return file;
    }

    //Call after the xrpcc tool has generated the server code
    private static void writeWEBXML(String displayName, File webinfLocation) throws FileNotFoundException, IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(webinfLocation, "web.xml")));
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        writer.newLine();
        writer.newLine();
        writer.write("<!DOCTYPE web-app");
        writer.newLine();
        writer.write("  PUBLIC \"-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN\"");
        writer.newLine();
        writer.write("  \"http://java.sun.com/j2ee/dtds/web-app_2_3.dtd\">");
        writer.newLine();
        writer.newLine();
        writer.write("<web-app>");
        writer.newLine();
        writer.write("  <display-name>" + displayName + "</display-name>");
        writer.newLine();
        writer.write("  <description>Generated Web Service containing a JAX-RPC endpoint</description>");
        writer.newLine();
        writer.write("  <session-config>");
        writer.newLine();
        writer.write("    <session-timeout>60</session-timeout>");
        writer.newLine();
        writer.write("  </session-config>");
        writer.newLine();
        writer.write("</web-app>");
        writer.newLine();
        writer.flush();
        writer.close();
    }

    private static void writeJAXRPCRIXML(File webinfLocation, String serviceName, File interfaceFile, File implementationFile) throws FileNotFoundException, IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File(webinfLocation, "jaxrpc-ri.xml")));
        String packageName = FileUtil.getPackageName(interfaceFile);
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        writer.newLine();
        writer.write("<webServices");
        writer.newLine();
        writer.write("    xmlns=\"http://java.sun.com/xml/ns/jax-rpc/ri/dd\"");
        writer.newLine();
        writer.write("    version=\"1.0\"");
        writer.newLine();
        writer.write("    targetNamespaceBase=\"http://www.topcoder.com/wsdl\"");
        writer.newLine();
        writer.write("    typeNamespaceBase=\"http://www.topcoder.com/types\"");
        writer.newLine();
        writer.write("    urlPatternBase=\"/ws\">");
        writer.newLine();
        writer.newLine();
        writer.write("    <endpoint");
        writer.newLine();
        writer.write("        name=\"" + serviceName + "\"");
        writer.newLine();
        writer.write("        displayName=\"" + serviceName + "\"");
        writer.newLine();
        writer.write("        description=\"Generated Web Service\"");
        writer.newLine();
        writer.write("        interface=\"" + packageName + "." + interfaceFile.getName().substring(0, interfaceFile.getName().length() - 5) + "\"");
        writer.newLine();
        writer.write("        implementation=\"" + packageName + "." + implementationFile.getName().substring(0, implementationFile.getName().length() - 5) + "\"/>");
        writer.newLine();
        writer.newLine();
        writer.write("    <endpointMapping");
        writer.newLine();
        writer.write("        endpointName=\"" + serviceName + "\"");
        writer.newLine();
        writer.write("        urlPattern=\"/topcoderws\"/>");
        writer.newLine();
        writer.newLine();
        writer.write("</webServices>");
        writer.newLine();
        writer.flush();
        writer.close();
    }

    //returns the directory that was passed in
    private static File directoryConstructor(File createDirectory) throws IOException {
        if (!createDirectory.exists()) {
            if (!createDirectory.mkdirs())
                throw new IOException("Could not construct directory " + createDirectory.getPath());
        }
        return createDirectory;
    }

    public static void createService(WebServiceProblem problem) throws FileNotFoundException, IOException, InterruptedException, ParserConfigurationException, NotBoundException, MalformedURLException, RemoteException, TeamServicesException, WebServiceDeploymentException {
        File generationLocation = directoryConstructor(new File(WebServiceGeneratorResources.getProperty(WebServiceGeneratorResources.UNIX_TEMP_LOCATION), problem.getServiceName()));
        try {
            //Contains a list of files that were passed in in the WebServiceProblem
            //Used to filter out duplicate files from the Java client stubs.
            HashSet filesGiven = new HashSet();
            //Construct all problem files locally
            File interfaceFile = problem.getInterfaceFile().reconstruct(generationLocation);
            File implementationFile = problem.getImplementationFile().reconstruct(generationLocation);
            File[] helperFiles = null;
            filesGiven.add(interfaceFile.getName());
            filesGiven.add(implementationFile.getName());
            if (problem.getHelperFiles() != null && problem.getHelperFiles().length != 0) {
                helperFiles = new File[problem.getHelperFiles().length];
                for (int i = 0; i != problem.getHelperFiles().length; ++i) {
                    helperFiles[i] = problem.getHelperFiles()[i].reconstruct(generationLocation);
                    filesGiven.add(helperFiles[i].getName());
                }
            }
            File webinfLocation = directoryConstructor(new File(generationLocation, "WEB-INF"));
            File webinfClassesLocation = directoryConstructor(new File(webinfLocation, "classes"));

            //Compile given classes
            String compileString = getCompileString(webinfClassesLocation, interfaceFile, implementationFile, helperFiles);
            _logger.info("Executing: " + compileString);
            Process process = Runtime.getRuntime().exec(compileString);
            if (process.waitFor() != 0)
                throw new IOException("Could not compile " + interfaceFile.getPath() + " " +
                        implementationFile.getPath() + "!  " + getErrorMessage(process.getErrorStream()) +
                        " " + getErrorMessage(process.getInputStream()));

            writeWEBXML(problem.getServiceName(), webinfLocation);
            writeJAXRPCRIXML(webinfLocation, problem.getServiceName(), interfaceFile, implementationFile);

            //construct the portible WAR file this is used to generate the deployed WAR file
            File portableWARFile = new File(generationLocation, problem.getServiceName() + "-portable.war");
            String portableWARStringCommand = WebServiceGeneratorResources.getProperty(WebServiceGeneratorResources.JAVA_LOCATION) +
                    "jar cf " + portableWARFile.getPath() + " WEB-INF";
            _logger.info("Executing: " + portableWARStringCommand);
            process = Runtime.getRuntime().exec(portableWARStringCommand, null, webinfLocation.getParentFile());
            if (process.waitFor() != 0)
                throw new IOException("Could not create portable WAR file: " + portableWARFile.getPath() + "!  " + getErrorMessage(process.getErrorStream()) +
                        " " + getErrorMessage(process.getInputStream()));

            File deployableWARFile = new File(directoryConstructor(new File(WebServiceGeneratorResources.getProperty(WebServiceGeneratorResources.WAR_STORAGE_LOCATION))), problem.getServiceName() + ".war");
            //try to remove the context
            try {
                WebServiceDeploymentUtil.remove(deployableWARFile);
            } catch (Exception e) {
                _logger.error("WARNING: Could not remove web service context - probably no big deal, hasn't been deployed yet", e);
            }

            //Create the deployable WAR file.
            File tempLocation = directoryConstructor(new File(generationLocation, "temp"));
            String deployableWARStringCommand = WebServiceGeneratorResources.getProperty(WebServiceGeneratorResources.WSDEPLOY_FULL_PATH) +
                    " -keep -tmpdir " + tempLocation.getPath() + " -o " + deployableWARFile.getPath() + " " + portableWARFile.getPath();
            _logger.info("Executing: " + deployableWARStringCommand);
            process = Runtime.getRuntime().exec(deployableWARStringCommand);
            if (process.waitFor() != 0)
                throw new IOException("Could not create deployable WAR file: " + deployableWARFile.getPath() + "!  " + getErrorMessage(process.getErrorStream()) +
                        " " + getErrorMessage(process.getInputStream()));

            //Install the context.  Tomcat will redeploy the WebService untile it is removed.
            WebServiceDeploymentUtil.install(deployableWARFile);

            File wsdlFile = findFile(tempLocation, ".wsdl");
            if (wsdlFile == null)
                throw new IOException("Could not find WSDL file!  " + getErrorMessage(process.getErrorStream()) +
                        " " + getErrorMessage(process.getInputStream()));

            LinkedList javaList = generateJavaClient(generationLocation, webinfClassesLocation, problem.getServiceName(), interfaceFile, filesGiven);
            LinkedList cppList = generateCPPClient(wsdlFile, generationLocation);
            //LinkedList cSharpList = generateCSharpClient(problem.getServiceName(), wsdlFile);

            //containers for the files that are generated
            _logger.info("Web Service \"" + problem.getServiceName() + "\" C++ Client Files: " + cppList.size());
            _logger.info("Web Service \"" + problem.getServiceName() + "\" Java Client Files: " + javaList.size());
           // _logger.info("Web Service \"" + problem.getServiceName() + "\" C# Client Files: " + cSharpList.size());
//      System.out.println("Web Service \""+problem.getServiceName()+"\" C++ Client Files: " + cppList.size());
//      System.out.println("Web Service \""+problem.getServiceName()+"\" Java Client Files: " + javaList.size());
//      System.out.println("Web Service \""+problem.getServiceName()+"\" C# Client Files: " + cSharpList.size());

            //All DB operations are done here
            _teamServices.setWebServiceClients(problem.getServiceName(), ContestConstants.JAVA, javaList);
            //_teamServices.setWebServiceClients(problem.getServiceName(), ContestConstants.CSHARP, cSharpList);
            _teamServices.setWebServiceClients(problem.getServiceName(), ContestConstants.CPP, cppList);
        } finally {
            //cleanUp
            if (generationLocation.exists())
                FileUtil.removeFile(generationLocation);
        }
    }

    public static String getCompileString(File destinationDirectory, File interfaceFile, File implementationFile, File[] helperFiles) {
        StringBuffer compileString = new StringBuffer(WebServiceGeneratorResources.getProperty(WebServiceGeneratorResources.JAVA_LOCATION));
        compileString.append("javac -d ");
        compileString.append(destinationDirectory.getPath());
        if (helperFiles != null) {
            for (int i = 0; i != helperFiles.length; ++i) {
                compileString.append(" ");
                compileString.append(helperFiles[i].getPath());
            }
        }
        compileString.append(" ");
        compileString.append(interfaceFile.getPath());
        compileString.append(" ");
        compileString.append(implementationFile.getPath());
        return compileString.toString();
    }

    private static LinkedList generateCSharpClient(String serviceName, File wsdlFile) throws FileNotFoundException, NotBoundException, MalformedURLException, RemoteException, IOException, InterruptedException, ParserConfigurationException {
        //Generate C#
        ICSServer server = CSServer.getInstanceOf(WebServiceGeneratorResources.getProperty(WebServiceGeneratorResources.REMOTE_SERVER),
                WebServiceGeneratorResources.getProperty(WebServiceGeneratorResources.REMOTE_SERVER_PORT));
        BufferedReader reader = new BufferedReader(new FileReader(wsdlFile));
        StringBuffer buffer = new StringBuffer();
        while (reader.ready())
            buffer.append(reader.readLine());
        reader.close();
        LinkedList cSharpList = new LinkedList();
        cSharpList.add(server.generateCSClient(serviceName, new RemoteFile(wsdlFile, wsdlFile.getParent())));
        return cSharpList;
    }

    private static File findFile(File file, String typeLowerCase) throws IOException {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i != files.length; ++i) {
                File fileFound = findFile(files[i], typeLowerCase);
                if (fileFound != null)
                    return fileFound;
            }
        } else if (file.getName().toLowerCase().endsWith(typeLowerCase))
            return file;
        return null;
    }

    private static LinkedList generateJavaClient(File generationLocation, File webinfClassesLocation, String serviceName, File interfaceFile, HashSet givenFiles) throws FileNotFoundException, IOException, InterruptedException, ParserConfigurationException {
        File configXMLFile = writeConfigXML(generationLocation, serviceName, FileUtil.getPackageName(interfaceFile));

        File javaClientLocation = directoryConstructor(new File(generationLocation, "java"));
        LinkedList javaList = new LinkedList();

        String javaClientGenerationString = WebServiceGeneratorResources.getProperty(WebServiceGeneratorResources.WSCOMPILE_FULL_PATH) +
                " -gen:client -keep -classpath " +
                webinfClassesLocation.getPath() + " -d " +
                javaClientLocation.getPath() + " " +
                configXMLFile.getPath();
        _logger.info("Executing: " + javaClientGenerationString);
        Process process = Runtime.getRuntime().exec(javaClientGenerationString);

        if (process.waitFor() != 0)
            throw new IOException("Could not generate client side java classes!  " + getErrorMessage(process.getErrorStream()) +
                    " " + getErrorMessage(process.getInputStream()));
        addFiles(javaList, ".java", javaClientLocation.getPath(), javaClientLocation, JavaLanguage.ID);
        for (int i = 0; i != javaList.size(); ++i) {
            WebServiceRemoteFile file = (WebServiceRemoteFile) javaList.get(i);
            if (givenFiles.contains(file.getName()))
                javaList.remove(i--);
        }
        for (int i = 0; i != javaList.size(); ++i) {
            WebServiceRemoteFile file = (WebServiceRemoteFile) javaList.get(i);
            System.out.println(file.getName());
        }
        return javaList;
    }

    //Client C++
    private static LinkedList generateCPPClient(File wsdlFile, File generationLocation) throws FileNotFoundException, IOException, InterruptedException, ParserConfigurationException {
        LinkedList cppList = new LinkedList();
        File cppClientLocation = directoryConstructor(new File(generationLocation, "cpp"));
        wsdlcpp parserParser = new wsdlcpp();
        parserParser.myParser(wsdlFile, cppClientLocation);
        //Remove unwanted skeleton for a C++ client
        FileUtil.removeFileType(cppClientLocation, ".c");
        File hFile = findFile(cppClientLocation, ".h");
        if (hFile == null)
            throw new IOException("Could not find Header file needed to run C++ GSOAP!");

        String cppClientGenerationString = WebServiceGeneratorResources.getProperty(WebServiceGeneratorResources.SOAPCPP_FULL_PATH) + " -d" + hFile.getParentFile().getPath() + " " + hFile.getName();
        _logger.info("Executing: " + cppClientGenerationString);
        //soapcpp2 is rather finiky so we need to execute it in the same directory as the file is held
        Process process = Runtime.getRuntime().exec(cppClientGenerationString, null, hFile.getParentFile());
        if (process.waitFor() != 0)
            throw new IOException("Could not generate client side C++ classes!  " + getErrorMessage(process.getErrorStream()) +
                    " " + getErrorMessage(process.getInputStream()));

        addFiles(cppList, ".h", cppClientLocation.getPath(), cppClientLocation, CPPLanguage.ID);
        addFiles(cppList, ".cpp", cppClientLocation.getPath(), cppClientLocation, CPPLanguage.ID);
        addFiles(cppList, ".c", cppClientLocation.getPath(), cppClientLocation, CPPLanguage.ID);
        addFiles(cppList, ".nsmap", cppClientLocation.getPath(), cppClientLocation, CPPLanguage.ID);

        //We have to put the #include "something.nsmap" on the top of the soapC.cpp file, after #include "soapH.h"
        int soapCIndex = -1;
        String nsmapName = null;
        for (int i = 0; i < cppList.size(); i++) {
            if (((WebServiceRemoteFile) cppList.get(i)).getName().endsWith("soapC.cpp")) {
                soapCIndex = i;
            } else if (((WebServiceRemoteFile) cppList.get(i)).getName().endsWith(".nsmap")) {
                nsmapName = ((WebServiceRemoteFile) cppList.get(i)).getName();
            }
        }
        if (soapCIndex != -1 && nsmapName != null) {
            WebServiceRemoteFile soapCFile = (WebServiceRemoteFile) cppList.get(soapCIndex);
            _logger.info("prepending #include \"" + nsmapName + "\" to " + soapCFile.getName());
            String formerContents = new String(soapCFile.getContents());
            String soapHInclude = "#include \"soapH.h\"";
            String soapNsmapInclude = "\n#include \"" + nsmapName + "\"\n";
            int index = formerContents.indexOf(soapHInclude);
            if (index != -1) {
                _logger.info("index = " + index);
                index = index + soapHInclude.length();
                soapCFile.setContents((formerContents.substring(0, index) + soapNsmapInclude + formerContents.substring(index)).getBytes());
            }
        }

        return cppList;
    }

    //util to add files to the LinkedList
    //Format List.put(WebServiceRemoteFile)
    public static void addFiles(LinkedList list, String typeLowerCase, String basePath, File file, int languageID) throws FileNotFoundException, IOException {
        if (file.isDirectory()) {
            // add all the files in the directory and travers other directories
            File[] files = file.listFiles();
            for (int i = 0; i != files.length; ++i)
                addFiles(list, typeLowerCase, basePath, files[i], languageID);
        }
        //When we just have a file add it.
        else if (file.getName().toLowerCase().endsWith(typeLowerCase)) {
            if (typeLowerCase.equals(".h") || typeLowerCase.equals(".nsmap"))
                list.add(new WebServiceRemoteFile(file, basePath, WebServiceRemoteFile.WEB_SERVICE_CLIENT_HEADER, languageID));
            else
                list.add(new WebServiceRemoteFile(file, basePath, WebServiceRemoteFile.WEB_SERVICE_CLIENT_SOURCE, languageID));
        }
    }

    private static String getErrorMessage(InputStream inputStream) throws IOException {
        BufferedInputStream stream = new BufferedInputStream(inputStream);
        StringBuffer tempSB = new StringBuffer("");
        while (stream.available() != 0) {
            byte[] bytes = new byte[stream.available()];
            stream.read(bytes);
            tempSB.append(new String(bytes));
        }
        stream.close();
        return tempSB.toString();
    }


    public static void main(String[] args) throws Exception {

        WebServiceGenerator.createService(new WebServiceProblem(-1, "hello",
                new RemoteFile(new File("C:/Work/topcoder/jwsdp-1_0/docs/tutorial/examples/jaxrpc/hello/HelloIF.java"),
                        "C:/Work/topcoder/jwsdp-1_0/docs/tutorial/examples/jaxrpc"),
                new RemoteFile(new File("C:/Work/topcoder/jwsdp-1_0/docs/tutorial/examples/jaxrpc/hello/HelloImpl.java"),
                        "C:/Work/topcoder/jwsdp-1_0/docs/tutorial/examples/jaxrpc"),
                null));
        RemoteFile[] helperFiles =
                {
                    new RemoteFile(new File("C:/Work/topcoder/jwsdp-1_0/docs/tutorial/examples/jaxrpc/simplebean/SimpleAccountBean.java"), "C:/Work/topcoder/jwsdp-1_0/docs/tutorial/examples/jaxrpc")
                };
        WebServiceGenerator.createService(new WebServiceProblem(-1, "simplebean",
                new RemoteFile(new File("C:/Work/topcoder/jwsdp-1_0/docs/tutorial/examples/jaxrpc/simplebean/HelloIF.java"),
                        "C:/Work/topcoder/jwsdp-1_0/docs/tutorial/examples/jaxrpc"),
                new RemoteFile(new File("C:/Work/topcoder/jwsdp-1_0/docs/tutorial/examples/jaxrpc/simplebean/HelloImpl.java"),
                        "C:/Work/topcoder/jwsdp-1_0/docs/tutorial/examples/jaxrpc"),
                helperFiles));
    }
}
