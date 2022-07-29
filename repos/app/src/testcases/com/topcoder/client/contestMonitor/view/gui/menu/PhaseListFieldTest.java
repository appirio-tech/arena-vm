package com.topcoder.client.contestMonitor.view.gui.menu;

import junit.framework.TestCase;

import com.topcoder.netCommon.contest.ContestConstants;

/**
 * Set of tests for the PhaseListField.
 *
 * @author  TCSDEVELOPER
 * @since   Admin Tool 2.0
 */
public class PhaseListFieldTest extends TestCase {

    /** Constant to represent index of not being selected */
    private static final int NOT_SELECTED = -1;

    /**
     * Tests the PhaseListField constructor using all the different
     * parameters and also using the constructor with no parameters.
     * Checks to see if the initial selected field is correct
     */
    public void testConstructor() throws Exception {
        // tests optional mode constructor
        PhaseListField field = new PhaseListField(NOT_SELECTED);
        assertSelected(field, NOT_SELECTED);

        // tests non-optional mode constructor
        field = new PhaseListField();
        assertSelected(field, 0);

        for (int i = 0; i < ContestConstants.PHASE_NAMES.length; i++) {
            field = new PhaseListField(i);
            assertSelected(field, i);
        }
    }

    /**
     * Tests if selection of items works.  Just selects some index
     * and tests if it really was selected.
     *
     * @throws Exception propogated to JUnit, shouldn't happen
     */
    public void testSelection() throws Exception {
        PhaseListField field = new PhaseListField();

        field.setSelectedIndex(4);
        assertSelected(field, 4);

        field.setSelectedIndex(7);
        assertSelected(field, 7);

        field.setSelectedIndex(2);
        assertSelected(field, 2);

        field.setSelectedIndex(10);
        assertSelected(field, 10);
    }

    /**
     * Tests that the construct throws IllegalArgumentException
     * when given incorrect parameters
     */
    public void testIlegalConstructorArgs() {
        try {
            PhaseListField field = new PhaseListField(-2);
            fail("should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException iae) {
            // success
        }

        try {
            PhaseListField field = new PhaseListField(ContestConstants.PHASE_NAMES.length);
            fail("should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException iae) {
            // success
        }

        try {
            PhaseListField field = new PhaseListField(Integer.MAX_VALUE);
            fail("should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException iae) {
            // success
        }

        try {
            PhaseListField field = new PhaseListField(-10);
            fail("should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException iae) {
            // success
        }
    }

    /**
     * Tests that when we clear the different types of PhaseListFields,
     * optional and non-optional, we get the intended initial selection.
     *
     * @throws Exception propogated to JUnit, shouldn't happen
     */
    public void testClear() throws Exception {
        PhaseListField optionalField = new PhaseListField(NOT_SELECTED);
        assertSelected(optionalField, NOT_SELECTED);

        optionalField.clear();
        assertSelected(optionalField, NOT_SELECTED);

        optionalField.setSelectedIndex(9);
        assertSelected(optionalField, 9);

        optionalField.clear();
        assertSelected(optionalField, NOT_SELECTED);
    }

    /**
     * Goes through various selections and tests what the getFieldValue method
     * returns is consistent with ContestConstants.PHASES
     *
     * @throws Exception propogated to JUnit, shouldn't happen
     */
    public void testGetFieldValue() throws Exception {
        PhaseListField field = new PhaseListField();

        for (int i = 0; i < ContestConstants.PHASES.length; i++) {
            field.setSelectedIndex(i);
            assertEquals(ContestConstants.PHASES[i], ((Integer) field.getFieldValue()).intValue());
        }

        // Initial selection in optional mode should be nothing
        field = new PhaseListField(NOT_SELECTED);
        assertNull(field.getFieldValue());
    }

    /**
     * Tests when we have invalid selections, we get an index out of bounds exception
     * when calling getFieldValue().
     */
    public void testGetFieldValueException() {
        try {
            // create a non-optional field and not select anything
            PhaseListField field = new PhaseListField();
            field.setSelectedIndex(NOT_SELECTED);

            field.getFieldValue();
            fail("Should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException e) {
            // success
        } catch (Exception e) {
            fail("Wrong type of exception, expecting ArrayIndexOutOfBounds");
        }
    }

    /**
     * Helper method to assert whether a field is selected or not
     * in our PhaseListField.  Uses getSelectedIndex and getFieldValue methods.
     * When using getFieldValue, we check against the expected integer from
     * <code>ContestConstants.PHASES</code>.  If index is -1 to represent
     * <code>NOT_SELECTED</code>, then we check if getFieldValue returns null.
     *
     * @param field the PhaseListField to test
     * @param index the selection index to check for
     * @throws Exception
     */
    public void assertSelected(PhaseListField field, int index) throws Exception {
        assertEquals(index, field.getSelectedIndex());
        if (index == NOT_SELECTED) {
            assertNull(field.getFieldValue());
        } else {
            assertEquals(ContestConstants.PHASES[index], ((Integer) field.getFieldValue()).intValue());
        }
    }

}
