/*
 * ControllerCommandLineProcessor
 *
 * Created 09/04/2006
 */
package com.topcoder.farm.controller.commandline;

import java.io.PrintStream;

import com.topcoder.farm.controller.ControllerLocator;
import com.topcoder.farm.controller.ControllerMain;
import com.topcoder.farm.shared.commandline.CommandLineProcessor;

/**
 * Command line processor for Controller node
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ControllerCommandLineProcessor implements CommandLineProcessor {
    private ControllerMain controllerMain;

    public ControllerCommandLineProcessor(ControllerMain controllerMain) {
        this.controllerMain = controllerMain;
    }

    public void displayCommands(PrintStream printStream) {
        printStream.println("Available commands: ");
        printStream.println("?                             Display available commands");
        printStream.println("quit                          Exits console session if possible");
        printStream.println("shutdown                      Shutdown the controller");
        printStream.println("stop                          Stop the controller abruptly (Not recommended)");
        printStream.println("shutdownproc     procname     Shutdown the processor procname (Restart)");
        printStream.println("clearallqueues                Cancels and removes all invocations and shared objects");
        printStream.println("clearclientqueue clientname   Cancels and removes all invocations and shared objects for the client");
        printStream.println("dump                          Dumps the controller status");
        printStream.println("dumpproc         procname     Dumps processor status (controller data)");
        printStream.println("activateproc     procname     Sets processor as active");
        printStream.println("deactivateproc   procname     Sets processor as not active");
        printStream.println("clearitems clientname likeKeyPrefix  Cancels all invocations for the client whose key like \"likeKeyPrefix%\"");
        printStream.println("assign [true|false]           Enables/Disables invocation assignation");
    }

    public boolean process(String cmd, PrintStream printStream) throws Exception {
        if ("stop".equals(cmd)) {
            controllerMain.stop();
        } else if ("shutdown".equals(cmd)) {
            controllerMain.shutdown();
        } else if (cmd.startsWith("shutdownproc ")) {
            ControllerLocator.getController().shutdownProcessor(cmd.substring(cmd.indexOf(' ')+1));
        } else if (cmd.equals("clearallqueues")) {
            ControllerLocator.getController().clearAllQueues();
        } else if (cmd.startsWith("clearclientqueue ")) {
            ControllerLocator.getController().clearClientQueue(cmd.substring(cmd.indexOf(' ')+1));
        } else if (cmd.equals("dump")) {
            ControllerLocator.getController().dumpStatus();
        } else if (cmd.startsWith("dumpproc ")) {
            ControllerLocator.getController().dumpProcessorStatus(cmd.substring(cmd.indexOf(' ')+1));
        } else if (cmd.startsWith("activateproc ")) {
            ControllerLocator.getController().updateProcessorActiveState(cmd.substring(cmd.indexOf(' ')+1), true);
        } else if (cmd.startsWith("deactivateproc ")) {
            ControllerLocator.getController().updateProcessorActiveState(cmd.substring(cmd.indexOf(' ')+1), false);
        } else if (cmd.startsWith("clearitems ")) {
            int clientNameIndex = cmd.indexOf(' ')+1;
            int likeIndex = cmd.indexOf(' ', clientNameIndex)+1;
            String clientName = cmd.substring(clientNameIndex, likeIndex-1);
            String like = cmd.substring(likeIndex);
            ControllerLocator.getController().cancelPendingRequests(clientName, like);
        } else if (cmd.startsWith("assign ")) {
            ControllerLocator.getController().setInvocationAssignationEnabled(Boolean.parseBoolean(cmd.substring(cmd.indexOf(' ')+1)));
        } else {
            return false;
        }
        return true;
    }
}
