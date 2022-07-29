/*
 * @(#) PhaseListField.java
 *
 * 1.0  07/31/2003
 */
package com.topcoder.client.contestMonitor.view.gui.menu;

import com.topcoder.netCommon.contest.ContestConstants;

/**
 * A class facilitating the choice of Contest Phase from list of String
 * representations of Contest Phases.
 *
 * Represents a <code>JComboBox</code> containing the String representations of
 * Contest Phases taken from <code>ContestConstants.PHASE_NAMES</code> array.
 * After selection returns one of the <code>ContestConstants.*_PHASE</code>
 * constants represented as <code>Integer</code>.  Two types of PhaseListFields
 * can be created.  Optional and non-optional versions.  To create an optional version
 * use the constructor and pass in a <code>-1</code as the parameter.  The optional
 * version has nothing selected by default.  The non-optional version has the first
 * element selected by default.  Any other constructor creates a non-optional version.
 *
 * @author  isv
 * @author  TCSDEVELOPER
 * @version 1.0 07/31/2003
 * @since   Admin Tool 2.0
 *
 * @see     ContestConstants
 */
public class SegmentListField extends DropDownField {

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
    private static final Integer[] SEGMENTS;

    /**
     * Cache the constants into the PHASES array so we
     * don't need to keep instantiating new Integer Objects
     */
    static {
        SEGMENTS = new Integer[ContestConstants.SEGMENTS.length];
        for (int i = 0; i < ContestConstants.SEGMENTS.length; i++) {
            SEGMENTS[i] = new Integer(ContestConstants.SEGMENTS[i]);
        }
    }

    /**
     * Constructs new PhaseListField that will contain the elements of <code>
     * ContestConstants.PHASE_NAMES</code> array as items with first element
     * selected.
     *
     * @see ContestConstants.PHASE_NAMES
     */
    public SegmentListField() {
        this(SELECTED);
    }

    /**
     * Constructs new PhaseListField that will contain the elements of <code>
     * ContestConstants.PHASE_NAMES</code> array as items with element at
     * specified index selected.
     *
     * @param  index an integer specifying the list item to select, where 0
     *         specifies the first item in the list and -1 indicates no
     *         selection and also indicates turning on optional mode
     * @throws IllegalArgumentException if index < -1 or index is greater than
     *         or equal to number of elements in the list
     * @see    ContestConstants.PHASE_NAMES
     */
    public SegmentListField(int index) {
        super(ContestConstants.SEGMENT_NAMES);

        if (index < NOT_SELECTED || index >= ContestConstants.SEGMENT_NAMES.length) {
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
        return SEGMENTS[index];
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
