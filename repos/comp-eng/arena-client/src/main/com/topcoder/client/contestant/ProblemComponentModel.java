/*
 * User: Michael Cervantes Date: Aug 28, 2002 Time: 11:18:15 PM
 */
package com.topcoder.client.contestant;

import com.topcoder.netCommon.contestantMessages.response.data.ComponentChallengeData;
import com.topcoder.shared.problem.Constraint;
import com.topcoder.shared.problem.DataType;
import com.topcoder.shared.problem.Element;
import com.topcoder.shared.problem.ProblemComponent;
import com.topcoder.shared.problem.TestCase;

/**
 * Defines an interface which represents a problem component.
 * 
 * @author Michael Cervantes
 * @version $Id: ProblemComponentModel.java 71977 2008-07-28 12:55:54Z qliu $
 */
public interface ProblemComponentModel {
    /**
     * Gets the unique ID of the problem component.
     * 
     * @return the unique ID of the problem component.
     */
    Long getID();

    /**
     * Gets the type ID of the component.
     * 
     * @return the type ID of the component.
     */
    Integer getComponentTypeID();

    /**
     * Gets the model of the problem which this problem component belongs to.
     * 
     * @return the model of the problem.
     */
    ProblemModel getProblem();

    /**
     * Gets the maximum points assigned to this problem component.
     * 
     * @return the maximum points.
     */
    Double getPoints();

    /**
     * Gets the problem component wrapped by this model.
     * 
     * @return the problem component instance.
     */
    ProblemComponent getComponent();

    /**
     * Gets a flag indicating if this problem component has a method signature which must be defined by user solutions.
     * 
     * @return <code>true</code> if a method signature is required in the solution; <code>false</code> otherwise.
     */
    boolean hasSignature();

    /**
     * Gets the class name of the user solution.
     * 
     * @return the class name of the user solution.
     */
    String getClassName();

    /**
     * Gets the method name of the user solution.
     * 
     * @return the method name of the user solution.
     */
    String getMethodName();

    /**
     * Gets the return type of the user solution method.
     * 
     * @return the return type of the method.
     */
    DataType getReturnType();

    /**
     * Gets the argument types of the user solution method.
     * 
     * @return the argument types of the method.
     */
    DataType[] getParamTypes();

    /**
     * Gets the argument names of the user solution method.
     * 
     * @return the argument names of the method.
     */
    String[] getParamNames();

    /**
     * Gets the challenge data of this problem component. Challenge data contains all method signature information, such
     * as method name, class name, argument types, etc.
     * 
     * @return the challenge data of this problem component.
     * @see ComponentChallengeData
     */
    ComponentChallengeData getComponentChallengeData();

    /**
     * Gets a flag indicating if the problem component has a problem statement.
     * 
     * @return <code>true</code> if there is a problem statement; <code>false</code> otherwise.
     */
    boolean hasStatement();

    /**
     * Gets a flag indicating if the problem component has a introduction in the problem statement.
     * 
     * @return <code>true</code> if there is a introduction; <code>false</code> otherwise.
     * @see ProblemComponent
     */
    boolean hasIntro();

    /**
     * Gets the element representing the introduction of the problem component. The element is used to render.
     * 
     * @return the element of the introduction.
     */
    Element getIntro();

    /**
     * Gets a flag indicating if the problem component has a specification in the problem statement.
     * 
     * @return <code>true</code> if there is a specification; <code>false</code> otherwise.
     * @see ProblemComponent
     */
    boolean hasSpec();

    /**
     * Gets the element representing the specification of the problem component. The element is used to render.
     * 
     * @return the element of the specification.
     */
    Element getSpec();

    /**
     * Gets a flag indicating if the problem component has test case notes.
     * 
     * @return <code>true</code> if there are test case notes; <code>false</code> otherwise.
     * @see ProblemComponent
     */
    boolean hasNotes();

    /**
     * Gets the elements representing the test case notes of the problem component. The elements are used to render.
     * 
     * @return the elements of the test case notes.
     */
    Element[] getNotes();

    /**
     * Gets a flag indicating if the problem component has constraints in the problem statement.
     * 
     * @return <code>true</code> if there are constraints; <code>false</code> otherwise.
     * @see ProblemComponent
     */
    boolean hasConstraints();

    /**
     * Gets the elements representing the constraints of the problem component. The elements are used to render.
     * 
     * @return the elements of the constraints.
     */
    Constraint[] getConstraints();

    /**
     * Gets a flag indicating if the problem component has test cases.
     * 
     * @return <code>true</code> if there are test cases; <code>false</code> otherwise.
     * @see ProblemComponent
     */
    boolean hasTestCases();

    /**
     * Gets the elements representing the test cases of the problem component. The elements are used to render.
     * 
     * @return the elements of the test cases.
     */
    TestCase[] getTestCases();

    /**
     * Gets a flag indicating if the problem component has a writer-provided solution.
     * 
     * @return <code>true</code> if there is a writer-provided solution; <code>false</code> otherwise.
     * @see ProblemComponent
     */
    boolean hasDefaultSolution();

    /**
     * Gets the source code of the writer-provided solution.
     * 
     * @return the source code of the writer-provided solution.
     */
    String getDefaultSolution();
}
