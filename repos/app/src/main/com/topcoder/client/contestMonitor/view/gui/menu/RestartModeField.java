/*
 * @(#) PhaseListField.java
 *
 * 1.0  07/31/2003
 */
package com.topcoder.client.contestMonitor.view.gui.menu;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.server.AdminListener.AdminConstants;

public class RestartModeField extends DropDownField {

    /**
     * Optional mode variable.
     * If it is set to true, don't have anything selected by default.
     * Otherwise, we have the first item selected.
     */
    private boolean optional = false;

    /** Constant to represent index of not being selected */
    private static final int NOT_SELECTED = -1;

    /** Constant to represent the default index to be selected */
    private static final int SELECTED = 0;

    /** Cached Integer objects of ContestConstants.PHASES */
    private static final Integer[] MODES = new Integer[] {
        new Integer(AdminConstants.RESTART_TESTERS_IMMEDIATELY),
        new Integer(AdminConstants.RESTART_TESTERS_NORMAL),
        new Integer(AdminConstants.RESTART_TESTERS_REFERENCE)
    };

    private final static String[] MODE_LIST = new String[] {
      "Immediate Restart",
      "Normal Testing Mode",
      "Reference Testing Mode"
    };
    
    public RestartModeField() {
        this(SELECTED);
    }

    public RestartModeField(int index) {
        super(MODE_LIST);

        if (index < NOT_SELECTED || index >= MODE_LIST.length) {
            throw new IllegalArgumentException("incorrect index");
        }
        if (index == NOT_SELECTED) {
            optional = true;
        }
        super.setSelectedIndex(index);
    }

    /**
     * Gets the Phase ID corresponding to item selected from this
     * PhaseListField. The Phase ID is represented as Integer instance.
     *
     * @return an Integer representing one of the final static int constants
     *         from ContestConstants class representing a contest phase that
     *         corresponds to item selected from this PhaseListField.  If
     *         nothing is selected yet, and we are in optional mode, this
     *         returns null.
     *
     * @throws Exception (an ArrayIndexOutOfBoundsException) if index of
     *         selected item is out of bounds of ContestConstants.PHASES array.
     *         This happens when nothing is selected yet and we're not in
     *         optional mode.
     *
     * @see    ContestConstants.INACTIVE_PHASE
     * @see    ContestConstants.STARTS_IN_PHASE
     * @see    ContestConstants.REGISTRATION_PHASE
     * @see    ContestConstants.ALMOST_CONTEST_PHASE
     * @see    ContestConstants.CODING_PHASE
     * @see    ContestConstants.INTERMISSION_PHASE
     * @see    ContestConstants.CHALLENGE_PHASE
     * @see    ContestConstants.PENDING_SYSTESTS_PHASE
     * @see    ContestConstants.SYSTEM_TESTING_PHASE
     * @see    ContestConstants.CONTEST_COMPLETE_PHASE
     * @see    ContestConstants.PHASES
     * @see    ContestConstants.PHASE_NAMES
     */
    public Object getFieldValue() throws Exception {
        int index = super.getSelectedIndex();
        if (index == NOT_SELECTED && optional) {
            return null;
        }

        // Used cached Integer array of constants
        return MODES[index];
    }

    /**
     * Clears selection of this PhaseListField
     *
     * Sets the selection to the first element if we have a non-optional PhaseListField.
     * Otherwise we set the selection to nothing selected.
     */
    public void clear() {
        if (optional) {
            super.setSelectedIndex(NOT_SELECTED);
        } else {
            super.setSelectedIndex(SELECTED);
        }
    }
}
