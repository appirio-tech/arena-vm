package com.topcoder.server.webservice;

/**
 * Title:        WebServiceGenerator
 * Description:  Generates a Web Service from a WebServiceProblem
 * Copyright:    Copyright (c) 2002
 * Company:      TopCoder
 * @author       Jeremy Nuanes
 * @version 1.0
 */

import com.topcoder.server.common.RemoteFile;
import com.topcoder.server.webservice.WebServiceProblem;
import com.topcoder.server.services.WebServiceGeneratorService;

import java.io.File;
import java.io.IOException;

public class WebServiceGeneratorTest {

    private WebServiceGeneratorTest() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 1) {
            File rootDirectory = new File(args[0], "webservice");
            if (!rootDirectory.exists())
                throw new IOException("Directory " + rootDirectory.getPath() + " does not exist!");
            File directory = new File(rootDirectory, "hello");
            if (!directory.exists())
                throw new IOException("Directory " + directory.getPath() + " does not exist!");
            WebServiceGeneratorService.sendBuildWebService(new WebServiceProblem(1, "hellotest",
                    new RemoteFile(new File(directory, "HelloIF.java"), directory.getParent()),
                    new RemoteFile(new File(directory, "HelloImpl.java"), directory.getParent()),
                    null), 1);
            directory = new File(rootDirectory, "simplebean");
            if (!directory.exists())
                throw new IOException("Directory " + directory.getPath() + " does not exist!");
            RemoteFile[] helperFiles =
                    {
                        new RemoteFile(new File(directory, "SimpleAccountBean.java"), directory.getParent())
                    };
            WebServiceGeneratorService.sendBuildWebService(new WebServiceProblem(2, "simplebeantest",
                    new RemoteFile(new File(directory, "HelloIF.java"), directory.getParent()),
                    new RemoteFile(new File(directory, "HelloImpl.java"), directory.getParent()),
                    helperFiles), 2);
        } else {
            System.out.println("Usage:  Pass the path to the test data");
            System.out.println("EX:  C:/topcoder/Repository/app/testData");
        }
    }
}