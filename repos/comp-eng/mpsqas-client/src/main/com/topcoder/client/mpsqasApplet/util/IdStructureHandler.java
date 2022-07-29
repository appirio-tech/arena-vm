package com.topcoder.client.mpsqasApplet.util;

import com.topcoder.shared.problem.Problem;
import com.topcoder.shared.problem.ProblemComponent;
import com.topcoder.shared.problem.TestCase;
import com.topcoder.shared.problem.WebService;

import com.topcoder.netCommon.mpsqas.ProblemIdStructure;
import com.topcoder.netCommon.mpsqas.ComponentIdStructure;
import com.topcoder.netCommon.mpsqas.WebServiceIdStructure;

/**
 * Handles processing id structures by assigning the values in an id structure to the
 * Problem, Component, and WebService objects.
 *
 * @author mitalub
 */
public class IdStructureHandler {

    /** Stores the problem id of the problem, and does all the components. */
    public static void handleProblemIdStructure(
            ProblemIdStructure structure, Problem problem) {

        problem.setProblemId(structure.getProblemId());

        ComponentIdStructure[] componentIds = structure.getComponents();
        ProblemComponent[] components = problem.getProblemComponents();

        for (int i = 0; i < components.length; i++) {
            for (int j = 0; j < componentIds.length; j++) {
                if (components[i].getClassName().equals(
                        componentIds[j].getComponentName())) {
                    handleComponentIdStructure(componentIds[j], components[i]);
                }
            }
        }

        WebServiceIdStructure[] webServiceIds = structure.getWebServices();
        WebService[] webServices = problem.getWebServices();

        for (int i = 0; i < webServices.length; i++) {
            for (int j = 0; j < webServiceIds.length; j++) {
                if (webServices[i].getName().equals(
                        webServiceIds[j].getWebServiceName())) {
                    handleWebServiceIdStructure(webServiceIds[j],
                            webServices[i]);
                }
            }
        }
    }

    /** Stores the component id of the component, and does the web services. */
    public static void handleComponentIdStructure(
            ComponentIdStructure structure, ProblemComponent component) {

        component.setComponentId(structure.getComponentId());

        // Assign test case IDs
        TestCase[] testCases = component.getTestCases();
        Integer[] testCaseIds = structure.getTestCaseIds();
        if (testCases.length != testCaseIds.length) {
            throw new IllegalArgumentException("Test case length should be consistent. Expect " + testCases.length + ", got " + testCaseIds.length);
        }
        for (int i=0;i<testCases.length;++i) {
            if (testCases[i].getId() == null) {
                testCases[i].setId(testCaseIds[i]);
            } else if (!testCases[i].getId().equals(testCaseIds[i])) {
                throw new IllegalArgumentException("Existing test case ID should match. Expect " + testCases[i].getId() + ", got " + testCaseIds[i]);
            }
        }
    }

    /** Stores the web service id of the web service. */
    public static void handleWebServiceIdStructure(
            WebServiceIdStructure structure, WebService webService) {

        webService.setWebServiceId(structure.getWebServiceId());
    }
}
