/*
 * TestRunner
 *
 * Created 08/18/2006
 */
package com.topcoder.farm;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class TestRunner {

    public static void main(String[] args) throws Exception {
        ProcessBuilder builder = new ProcessBuilder();
        builder.redirectErrorStream(false);
        Process[] processors = new Process[4];
        ProcessBuilder command = builder.command(buildCommand(
                "ControllerRunner", "Controller-1"));
        Process controller = command.start();
        startReading("controller-", 1, controller);
        Process client = null;
        try {
            Thread.sleep(8000);
            processors[0] = builder.command(
                    buildCommand("ProcessorRunner", "PR1-1")).start();
            startReading("processor-", 1, processors[0]);
            processors[1] = builder.command(
                    buildCommand("ProcessorRunner", "PR2-1")).start();
            startReading("processor-", 2, processors[1]);
            processors[2] = builder.command(
                    buildCommand("ProcessorRunner", "PR3-1")).start();
            startReading("processor-", 3, processors[2]);
            processors[3] = builder.command(
                    buildCommand("ProcessorRunner", "PR4-1")).start();
            startReading("processor-", 4, processors[3]);
            BufferedOutputStream controllerCommand = new BufferedOutputStream(
                    controller.getOutputStream());
            client = builder.command(buildCommand("ClientEmulator", ""))
                    .start();
            startReading("client-", 1, client);

            Thread.sleep(35000);

            controllerCommand.write("shutdown\n".getBytes());
            try {
                controllerCommand.flush();
            } catch (Exception e) {
            }
            Thread.sleep(5000);
        } finally {
            for (int i = 0; i < processors.length; i++) {
                Process process = processors[i];
                if (process != null) {
                    process.destroy();
                }
            }
            controller.destroy();
            if (client != null) {
                client.waitFor();
            }
        }
    }

    private static void startReading(final String prefix, final int i,
            final Process process) {
        startReading(prefix, "out", i, process.getInputStream());
        startReading(prefix, "err", i, process.getErrorStream());
    }

    private static void startReading(final String prefix, final String ext,
            final int i, final InputStream is) {
        Thread thread = new Thread() {
            public void run() {
                try {
                    copyToFile(is, prefix + i + "." + ext);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    private static void copyToFile(InputStream is, String fileName)
            throws IOException {
        BufferedInputStream inputStream = new BufferedInputStream(is);
        byte[] b = new byte[2000];
        OutputStream out = new BufferedOutputStream(new FileOutputStream(System
                .getProperty("java.io.tmpdir")
                + fileName));
        int i = 0;
        try {
            while (i != -1) {
                i = inputStream.read(b);
                if (i != -1) {
                    out.write(b, 0, i);
                    out.flush();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        out.flush();
        out.close();
    }

    private static String[] buildCommand(String className, String name) {
        return new String[]{"java", "-cp",
                System.getProperty("java.class.path"),
                TestRunner.class.getPackage().getName() + "." + className, name};
    }

}
