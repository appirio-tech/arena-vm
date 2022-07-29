package com.topcoder.client.mpsqasApplet.controller.defaultimpl;

import java.io.IOException;
import java.util.Arrays;

import com.topcoder.client.mpsqasApplet.common.ResponseClassTypes;
import com.topcoder.client.mpsqasApplet.common.UpdateTypes;
import com.topcoder.client.mpsqasApplet.controller.TestController;
import com.topcoder.client.mpsqasApplet.messaging.ArgEntryResponseProcessor;
import com.topcoder.client.mpsqasApplet.model.TestModel;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.util.ArgumentCaster;
import com.topcoder.client.mpsqasApplet.view.TestView;
import com.topcoder.shared.problem.DataType;
import com.topcoder.shared.problem.DataValueParseException;

/**
 * Default implementation of TestController, which handles user testing.
 */
public class TestControllerImpl implements TestController,
        ArgEntryResponseProcessor {

    private TestModel model;
    private TestView view;

    public void init() {
        model = MainObjectFactory.getTestModel();
        view = MainObjectFactory.getTestView();

        MainObjectFactory.getResponseHandler().registerResponseProcessor(this,
                ResponseClassTypes.ARG_ENTRY);
    }

    public void takeOffHold() {
    }

    public void placeOnHold() {
    }

    /**
     * Handles a user's request to test by getting the arguments and sending
     * them to the SolutionRequestProcessor.
     */
    public void processTest() {
        //hide test window.
        model.setIsVisible(false);
        model.notifyWatchers(UpdateTypes.VISIBILITY);

        //try to get the arguments
        String[] args = view.getArgs();
        DataType[] dataTypes = model.getDataTypes();
        Object[] dataValues = null;
        try {
            dataValues = ArgumentCaster.castArgs(dataTypes, args);
        } catch (IOException ioe) {
            //error casting args
            MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                    "Cannot parse args: " + ioe.toString(), true);
        } catch (DataValueParseException dvpe) {
            MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                    "Cannot parse args: " + dvpe.getMessage(), true);
        }

        if (dataValues != null) {
            //test it
            MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                    "Testing...", false);

            MainObjectFactory.getSolutionRequestProcessor().test(dataValues,
                    model.getTestType());
        }
    }

    /**
     * Makes the test window data types match the specified arg types and
     * set the window visible.
     */
    public void getArgs(DataType[] dataTypes, int testType) {
        model.setTestType(testType);
        if (Arrays.equals(dataTypes, model.getDataTypes())) {
            //same arg types as last time, just show the window
            model.setIsVisible(true);
            model.notifyWatchers(UpdateTypes.VISIBILITY);
        } else {
            //save arg types and show window
            model.setDataTypes(dataTypes);
            model.setIsVisible(true);
            model.notifyWatchers();
        }
    }
    
    /**
     * @see com.topcoder.client.mpsqasApplet.controller.TestController#close()
     */
    public void close() {
       model.setIsVisible(false);
       model.notifyWatchers(UpdateTypes.VISIBILITY);
    }
}
