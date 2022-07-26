package com.topcoder.client.mpsqasApplet.controller.defaultimpl.component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import com.topcoder.client.mpsqasApplet.common.MPSQASRendererFactory;
import com.topcoder.client.mpsqasApplet.common.UpdateTypes;
import com.topcoder.client.mpsqasApplet.controller.component.TestCasePanelController;
import com.topcoder.client.mpsqasApplet.model.component.ComponentModel;
import com.topcoder.client.mpsqasApplet.model.component.TestCasePanelModel;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.util.ArgumentCaster;
import com.topcoder.client.mpsqasApplet.util.Watchable;
import com.topcoder.client.mpsqasApplet.view.component.ComponentView;
import com.topcoder.client.mpsqasApplet.view.component.TestCasePanelView;
import com.topcoder.client.mpsqasApplet.view.component.TestCasePanelView.RandomTestCaseData;
import com.topcoder.netCommon.mpsqas.MessageConstants;
import com.topcoder.shared.language.JavaLanguage;
import com.topcoder.shared.problem.DataType;
import com.topcoder.shared.problem.DataValueParseException;
import com.topcoder.shared.problem.Element;
import com.topcoder.shared.problem.StructuredTextElement;
import com.topcoder.shared.problem.TestCase;
import com.topcoder.shared.problem.TextElement;

/**
 * Default implementation of TestCasePanelController .
 *
 * @author mitalub
 */
public class TestCasePanelControllerImpl extends TestCasePanelController {

    private TestCasePanelModel model;
    private TestCasePanelView view;

    public void init() {
    }

    public void close() {
    }

    public void setModel(ComponentModel model) {
        this.model = (TestCasePanelModel) model;
    }

    public void setView(ComponentView view) {
        this.view = (TestCasePanelView) view;
    }

    /**
     * Adds a test case to the test case list.
     */
    public void processAddTestCase() {
        saveCurrentCase();

        // displayTestCase should be added automatically as the first method to every long problem;
        // test this afterwards
        int size = model.getComponentInformation().getParamTypes().length;
        TestCase[] oldTestCases = model.getComponentInformation().getTestCases();
        TestCase[] testCases = new TestCase[oldTestCases.length + 1]; 
        String[] args = new String[size];
        Arrays.fill(args, "");

        TestCase testCase = new TestCase(null, args, new TextElement(""), false);

        for (int i = 0; i < oldTestCases.length; i++) {
            testCases[i] = oldTestCases[i];
        }
        testCases[testCases.length - 1] = testCase;

        model.getComponentInformation().setTestCases(testCases);
        model.setCurrentCaseIndex(oldTestCases.length);
        model.setCurrentCase(testCase);
        model.getMainModel().notifyWatchers(UpdateTypes.TEST_CASE_LIST);
    }

    public void processMoveTestCaseDown() {
        saveCurrentCase();
        int index = view.getSelectedTestCaseIndex();
        TestCase[] testCases = model.getComponentInformation().getTestCases();

        if (index != -1 && index != testCases.length - 1) {
            TestCase tc = testCases[index];
            testCases[index] = testCases[index + 1];
            testCases[index + 1] = tc;
            model.setCurrentCaseIndex(++index);
            model.getComponentInformation().setTestCases(testCases);
            model.getMainModel().notifyWatchers(UpdateTypes.TEST_CASE_LIST);
            view.update(UpdateTypes.TEST_CASE_LIST);
            view.update(UpdateTypes.CURRENT_TEST_CASE);
        }
    }


    public void processMoveTestCaseUp() {
        saveCurrentCase();
        int index = view.getSelectedTestCaseIndex();
        TestCase[] testCases = model.getComponentInformation().getTestCases();
        if (index != -1 && index != 0) {
            TestCase tc = testCases[index];
            testCases[index] = testCases[index - 1];
            testCases[index - 1] = tc;
            model.setCurrentCaseIndex(--index);
            model.getComponentInformation().setTestCases(testCases);
            model.getMainModel().notifyWatchers(UpdateTypes.TEST_CASE_LIST);
            view.update(UpdateTypes.TEST_CASE_LIST);
            view.update(UpdateTypes.CURRENT_TEST_CASE);
        }
    }

    /**
     * Removes the selected test case from the test case list.
     */
    public void processDeleteTestCase() {
        int index = view.getSelectedTestCaseIndex();
        if (index != -1) {
            TestCase[] oldTestCases = model.getComponentInformation().getTestCases();
            TestCase[] testCases = new TestCase[oldTestCases.length - 1];
            for (int i = 0; i < oldTestCases.length; i++) {
                if (i != index) {
                    testCases[i - (i > index ? 1 : 0)] = oldTestCases[i];
                }
            }
            if(testCases.length==index)index--;
            model.setCurrentCaseIndex(index);
            model.getComponentInformation().setTestCases(testCases);
            model.getMainModel().notifyWatchers(UpdateTypes.TEST_CASE_LIST);
            view.update(UpdateTypes.TEST_CASE_LIST);
            view.update(UpdateTypes.CURRENT_TEST_CASE);
        }
    }

    /**
     * Tests the selected test case.
     */
    public void processTestTestCase() {
        saveCurrentCase();

        int index = view.getSelectedTestCaseIndex();

        //try to get the arguments
        String[] args = model.getComponentInformation().getTestCases()[index]
                .getInput();
        DataType[] dataTypes = model.getComponentInformation().getParamTypes();
        Object[] dataValues = null;
        try {
            dataValues = ArgumentCaster.castArgs(dataTypes, args);
        } catch (IOException ioe) {
ioe.printStackTrace();
            //error casting args
            MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                    "Cannot parse args: " + ioe.toString(), true);
        } catch (DataValueParseException dvpe) {
dvpe.printStackTrace();
            MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                    "Cannot parse args: " + dvpe.getMessage(), true);
        }

        if (dataValues != null) {
            //test it
            MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                    "Testing...", false);

            MainObjectFactory.getSolutionRequestProcessor().test(dataValues,
                    MessageConstants.TEST_ALL);
        }
    }

    /**
     * Displays the selected test case.
     */
    public synchronized void processTestCaseSelected() {
        //first, store the current test case
/*Moved this to the view, since it caused a timing bug like this.  -Lars
        saveCurrentCase();
        int index = view.getSelectedTestCaseIndex();
        if (index == -1) {
            model.setCurrentCaseIndex(-1);
        } else {
            model.setCurrentCaseIndex(index);
            model.setCurrentCase(model.getComponentInformation()
                    .getTestCases()[index]);
        }
*/        model.notifyWatchers(UpdateTypes.CURRENT_TEST_CASE);
    }

    /**
     * Saves the current case to keep view in sync with model.
     */
    public void processCurrentCaseChange() {
        saveCurrentCase();
    }

    /**
     * Saves the flags states of the selected test case.
     */
    public void processFlagsChange() {
        int row = view.getSelectedTestCaseIndex();
        if (row != -1) {
            TestCase[] cases = model.getComponentInformation().getTestCases();
            if (view.isExample(row) && view.isSystemTest(row)) {
                MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                        "A test case cannot be an example test and a system test at the same time", true);
                view.update(UpdateTypes.CURRENT_TEST_CASE);
                return;
            }
            cases[row].setExample(view.isExample(row));
            cases[row].setSystemTest(view.isSystemTest(row));
        }
    }

    /**
     * If there is a currently selected test case, the test case is saved
     * in the ProblemComponent.
     */
    private void saveCurrentCase() {
        int index = model.getCurrentCaseIndex();
        if (index != -1) {
            if (!Arrays.equals(view.getArgs(), model.getCurrentCase().getInput())
                    || (model.getCurrentCase().getAnnotation() == null &&
                    !view.getAnnotation().equals(""))
                    || (model.getCurrentCase().getAnnotation() != null &&
                    ! getTextRepresentation(model.getCurrentCase().getAnnotation()).equals(view.getAnnotation()))) {
                
                TestCase testCase = new TestCase(model.getCurrentCase().getId(), view.getArgs(),
                                new StructuredTextElement("annotation", view.getAnnotation()),
                                view.isExample(index));
                testCase.setSystemTest(view.isSystemTest(index));
                model.getComponentInformation().getTestCases()[index] = testCase;
            }
        }
    }

    /**
     * Passes the notification to the component model's watchers.
     */
    public void update(Watchable w, Object arg) {
        if (model.getCurrentCaseIndex() >=
                model.getComponentInformation().getTestCases().length) {
            model.setCurrentCaseIndex(-1);
        }

        model.notifyWatchers(arg);
    }

    public void processAddRandomTestCases() {
        saveCurrentCase();
        try {
            RandomTestCaseData testCaseData = view.getAddRamdomTestCaseData();
            BufferedReader br = new BufferedReader(new FileReader(testCaseData.getFileName()));
            int count = Integer.parseInt(br.readLine().trim());
            int size = model.getComponentInformation().getParamTypes().length;
            TestCase[] oldTestCases = model.getComponentInformation().getTestCases();
            TestCase[] testCases = new TestCase[oldTestCases.length + count];
            for (int i = 0; i < oldTestCases.length; i++) {
                testCases[i] = oldTestCases[i];
            }
            TestCase testCase = null;
            for (int i = 0; i < count; i++) {
                String[] args = new String[size];
                for (int j = 0; j < size; j++) {
                    args[j] = br.readLine();
                }
                testCase = new TestCase(null, args, new TextElement(testCaseData.getAnnotation()), testCaseData.isExample());
                testCase.setSystemTest(testCaseData.isSystemTest());
                testCases[oldTestCases.length + i] = testCase;
            }
            br.close();
            model.getComponentInformation().setTestCases(testCases);
            model.setCurrentCaseIndex(testCases.length - 1);
            model.setCurrentCase(testCase);
            model.getMainModel().notifyWatchers(UpdateTypes.TEST_CASE_LIST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private String getTextRepresentation(Element e) {
        try {
            return MPSQASRendererFactory.getInstance().getRenderer(e).toHTML(JavaLanguage.JAVA_LANGUAGE);
        } catch (Exception e1) {
            return "Error trying to render element " + e.toXML();
        }
    }
}
