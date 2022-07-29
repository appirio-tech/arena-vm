/*
 * ProcessorCommandLineProcessor
 * 
 * Created 09/04/2006 
 */
package com.topcoder.farm.processor.commandline;

import java.io.PrintStream;

import com.topcoder.farm.processor.ProcessorMain;
import com.topcoder.farm.shared.commandline.CommandLineProcessor;

/**
 * Command Line processor for processor node
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ProcessorCommandLineProcessor implements CommandLineProcessor {
    private ProcessorMain processor;


    public ProcessorCommandLineProcessor(ProcessorMain processor) {
        this.processor = processor;
    }


    public void displayCommands(PrintStream printStream) {
        printStream.println("Available commands: ");
        printStream.println("?               Display available commands");
        printStream.println("quit            Exits console session if possible");
        printStream.println("shutdown        Shutdown the processor");
        printStream.println("stop            Stop the processor abruptly (Not recommended)");
    }        


    /**
     * @see CommandLineProcessor#process(String, PrintStream)
     */
    public boolean process(String cmd, PrintStream writer) {
        if ("stop".equals(cmd)) {
            processor.stop();
        } else if ("shutdown".equals(cmd)) {
            processor.shutdown();
        } else {
            return false;
        }
        return true;
    }
}
