package com.topcoder.client.contestMonitor.view.gui.menu;

import com.topcoder.client.contestMonitor.view.gui.MonitorFrame;
import com.topcoder.server.contest.RoundComponentData;

import java.util.List;

/**
 * A class providing the possibility to select an ID of problem assigned 
 * to currently selected round from list of problem names instead of specifying
 * the problem ID directly.<p>
 * Represents a <code>JComboBox</code> containing the String names of problems
 * assigned to currently selected round. After selection returns an ID of 
 * problem corresponding to item selected from this ProblemListField
 * represented as <code>Integer</code>.<p>
 * Contains a private inner class implementing <code>javax.swing.ComboBoxModel
 * </code> interface that serves as loader of problems data on demand. This 
 * allows to refresh the content of ProblemListField on demand, namely 
 * each time when "Component Broadcast" item of BroadcastSubmenu is selected, so
 * the request to Admin Listener server will be made only when it really will
 * be needed.<p>
 * This version of ProblemListField does not cache the problem data loaded
 * for specific round, this result in fact that each time a "Component 
 * Broadcast" item of BroadcastSubmenu is selected the problem data for 
 * currently selected round is loaded from database server.
 *
 * @author  Giorgos Zervas
 * @version 11/07/2003
 * @since   Admin Tool 2.0
 */
public class ProblemListField extends DropDownField {

    /**
     * A main frame of the Admin Tool Client application maintaining the
     * ID of currently selected round. The ID of this currently selected
     * round will be used each time when <code>clear()</code> method will be 
     * invoked to load problems assigned to currently selected round.
     *
     * @see MonitorFrame#getRoundId()
     */
    private MonitorFrame frame = null;

    /**
     * Constructs new instance of <code>ProblemListField</code> with
     * specified <code>MonitorFrame</code>. This <code>MonitorFrame</code>
     * will be used to get the ID of currently selected round to load 
     * assigned problems which names should be presented to select.
     *
     * @param  frame a MonitorFrame
     * @throws IllegalArgumentException if given MonitorFrame is null
     */
    public ProblemListField(MonitorFrame frame) {
        /*
        * Create a default DropDownField for the time being.
        * When the field is actully shown we create a new
        * ProblemListModel for it.
        */
        super();
        if (frame == null) {
            throw new IllegalArgumentException("Attempt to create ProblemListField will null MonitorFrame");
        } else {
            this.frame = frame;
        }
    }

    /**
     * Gets the problem ID corresponding to item selected from this
     * ProblemListField. The Problem ID is represented as Integer instance.
     *
     * @return an Integer representing the ID of problem (assigned to currently
     *         selected round) that was selected from this ProblemListField.
     *         This ID may be used to broadcast message. If no selection was
     *         made returns Integer(0).
     * @throws Exception this should never happen
     */
    public Object getFieldValue() throws Exception {
        String[] problemFields = ((String) getModel().getSelectedItem()).split("\\.");
        return new Integer(problemFields[0]);
    }

    /**
     * Refreshes this ProblemListField, i.e. qeuries <code>MonitorFrame
     * </code> for ID of currently selected round and loads problems assigned
     * to this round. After that sets 1st item as selected.<p>
     * In other words this methods creates new <code>ProblemListModel</code>
     * that loads the problems data for currently selected round and sets this
     * model as current for this ProblemListField.
     *
     * @see ProblemListModel
     * @see javax.swing.JComboBox#setModel(javax.swing.ComboBoxModel aModel)
     */
    public void clear() {
        ProblemListModel model = new ProblemListModel();
        setModel(model);
    }

    /**
     * An inner class that is used to load the data for problems assigned to
     * currently selected round. Instances of this class are created each time
     * the "Component Broadcast" item of <code>BroadcastSubmenu</code> is 
     * selected.
     */
    private class ProblemListModel extends AbstractListModel {
        /**
         * Constructs new ProblemListModel that contains data for problems
         * assigned to currently selected round.<p>
         * In order to load the problems data this method gets access to
         * <code>ContestManagementController</code> and sends the request
         * for problems data with <code>
         * ContestManagementController.getProblems()</code> method. Then this
         * method waits while value of <code>responseReceived</code> changes
         * , queries ContestManagementController for assigned problems and 
         * fills <code>problems</code> variable with data for problems 
         * assigned to currently selected round.
         *
         * @throws IllegalStateException if for some reason the request for
         *         problems data failed, i.e. error response was received.
         */
        private ProblemListModel() {
            /**
             * Get the problem components for all problems of current round
             */
            frame.getContestSelectionFrame().
                    getContestManagementController().
                    getComponents(frame.getRoundId(), this);

            while (getResponseState() == NOT_RECEIVED) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (getResponseState() == ERROR) {
                throw new IllegalStateException("Error response received while getting components");
            }

            setData(
                    (List) frame.getContestSelectionFrame().
                    getContestManagementController().
                    getProblemComponentsTableModel().
                    getComponents()
            );
        }

        /**
         * Sets the selected item. Parses given String for problem and
         * division names and sets <code>currentIndex</code> variable to
         * point to RoundProblemData with specified problem and division
         * names.
         *
         * @param anItem the list object to select or <code>null</code>
         *        to clear the selection.
         */
        public void setSelectedItem(Object anItem) {
            Object[] components = getData().toArray();
            for (int i = 0; i < components.length; i++) {
                /*
                * Instead of parsing anItem we compare it with a reconstruction
                * of the string representation of problems.
                *
                * This avoids the potential risk of parsing the string in a wrong manner
                * e.g. if the problem name contains the '/' character.
                */
                String currentproblem =
                        ((RoundComponentData) components[i]).getComponentData().getId()
                        + ". "
                        + ((RoundComponentData) components[i]).getComponentData().getClassName()
                        + " / "
                        + ((RoundComponentData) components[i]).getDivision();

                if (currentproblem.equals(anItem)) {
                    setCurrentIndex(i);
                    return;
                }
            }
        }

        /**
         * Gets the problem ID corresponding to item selected from this
         * ComboBoxModel. The Problem ID is represented as Integer instance.
         *
         * @return an String representing the problem (assigned to
         *         currently selected round) that was selected from this
         *         ProblemListField.
         */
        public Object getSelectedItem() {
            return getElementAt(getCurrentIndex());
        }

        /**
         * Gets the element of this ComboBoxModel corresponding to specified 
         * index. This element is constructed with problem name and division
         * name. This element should be constructed so it can be easily parsed
         * to get the problem name and division name when <code>
         * setSelectedItem()</code> method will be invoked.
         *
         * @param  index the requested index
         * @return the String containing the name of problem and division 
         *         name corresponding to specified <code>index</code>
         */
        public Object getElementAt(int index) {
            if (index >= 0 && index < getData().size()) {
                RoundComponentData component = (RoundComponentData) getData().toArray()[index];
                return
                        component.getComponentData().getId()
                        + ". "
                        + component.getComponentData().getClassName()
                        + " / "
                        + component.getDivision();
            } else {
                return null;
            }
        }
    }
}
