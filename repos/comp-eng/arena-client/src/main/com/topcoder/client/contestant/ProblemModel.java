/*
 * User: Michael Cervantes Date: Aug 28, 2002 Time: 11:20:29 PM
 */
package com.topcoder.client.contestant;

import com.topcoder.shared.problem.Problem;

/**
 * Defines an interface which represents a problem. A problem can contain multiple problem components and may be
 * associated with a round/division.
 * 
 * @author Michael Cervates
 * @version $Id: ProblemModel.java 71977 2008-07-28 12:55:54Z qliu $
 */
public interface ProblemModel {
    /**
     * Gets the unique ID of the problem.
     * 
     * @return the unique ID of the problem.
     */
    Long getProblemID();

    /**
     * Gets the model of the round which this problem belongs to.
     * 
     * @return the model of the round of this problem.
     */
    RoundModel getRound();

    /**
     * Gets the division which this problem belongs to.
     * 
     * @return the division of the problem.
     */
    Integer getDivision();

    /**
     * Gets the type of this problem.
     * 
     * @return the type of this problem.
     */
    Integer getProblemType();

    /**
     * Gets the name of the problem.
     * 
     * @return the name of the problem.
     */
    String getName();

    /**
     * Gets a flag indicating if the problem contains any problem components.
     * 
     * @return <code>true</code> if there are problem components; <code>false</code> otherwise.
     */
    boolean hasComponents();

    /**
     * Gets all problem components of this problem.
     * 
     * @return problem components of this problem.
     */
    ProblemComponentModel[] getComponents();

    /**
     * Gets the primary problem component of this problem if there are multiple problem components. For problems which
     * contain single problem component, the only problem component is the primary problem component.
     * 
     * @return the primary problem component.
     */
    ProblemComponentModel getPrimaryComponent();

    /**
     * Gets a flag indicating if the problem has a introduction in the problem.
     * 
     * @return <code>true</code> if there is a introduction; <code>false</code> otherwise.
     */
    boolean hasIntro();

    /**
     * Gets the introduction of the problem.
     * 
     * @return the introduction.
     */
    String getIntro();

    /**
     * Gets a flag indicating if the problem has a problem statement.
     * 
     * @return <code>true</code> if there is a problem statement; <code>false</code> otherwise.
     */
    boolean hasProblemStatement();

    /**
     * Gets the problem statement of the problem.
     * 
     * @return the problem statement.
     */
    String getProblemStatement();

    /**
     * Gets the problem wrapped by this model.
     * 
     * @return the problem instance.
     */
    Problem getProblem();

    /**
     * Defines an interface which can receive events caused by this problem.
     * 
     * @author Michael Cervantes
     * @version $Id: ProblemModel.java 71977 2008-07-28 12:55:54Z qliu $
     */
    interface Listener {
        /**
         * Called when the problem model is updated.
         * 
         * @param problemModel the problem model updated.
         */
        void updateProblemModel(ProblemModel problemModel);

        /**
         * Called when the problem model is updated in read-only mode.
         * 
         * @param problemModel the problem model updated.
         */
        void updateProblemModelReadOnly(ProblemModel problemModel);
    }

    /**
     * Registers a listener to receive events caused by this problem.
     * 
     * @param listener the listener to receive events.
     */
    void addListener(Listener listener);

    /**
     * Unregisters a listener to stop receiving events caused by this problem.
     * 
     * @param listener the listener to stop receiving events.
     */
    void removeListener(Listener listener);
}
