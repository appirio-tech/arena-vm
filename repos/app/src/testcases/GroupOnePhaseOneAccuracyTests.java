
import junit.framework.TestCase;

import com.topcoder.client.contestMonitor.view.gui.menu.*;
import com.topcoder.netCommon.contest.ContestConstants;

/**
 * Accuracy tests for the Add Time requirement
 *
 * @author  TCSDEVELOPER
 * @since   Admin Tool 2.0
 */
public class GroupOnePhaseOneAccuracyTests extends TestCase {
    /* Used to print something intelligible when a failure occurs */
    private String message = "Test failure";

    public GroupOnePhaseOneAccuracyTests() {
        super("grouponephaseoneaccuracy");
    }

    /**************************************************************************
     * Setup/teardown
     *************************************************************************/
    protected void setUp() {
        message = "Test failure";
    }

    /**************************************************************************
     * Test the PhaseListField object for accuracy
     *************************************************************************/

    /**
     * Build a PhaseListFieldTest object to make sure it can be constructed
     * properly.
     */
    public void testPhaseListField() {
        PhaseListField plf;
        Integer value;

        try {
            /*
             * We should be able to construct this with any index value that
             * is in the range of values found in ContestConstants.PHASE_NAMES
             */
            message = "PhaseListFieldTest default constructor failed.";
            plf = new PhaseListField();
            value = (Integer)plf.getFieldValue();
            assertEquals("Default selected value should be " +
                         ContestConstants.PHASES[0],
                         value.intValue(), ContestConstants.PHASES[0]);

            for (int i = 0; i < ContestConstants.PHASE_NAMES.length; i++) {
                message = "PhaseListFieldTest with index " + i +
                    " constructor failed.";
                plf = new PhaseListField(i);
                value = (Integer)plf.getFieldValue();
                assertEquals("Selected value should be " +
                             ContestConstants.PHASES[i],
                             value.intValue(), ContestConstants.PHASES[i]);
            }

            plf = new PhaseListField(ContestConstants.PHASES.length / 2);
            plf.clear();
            value = (Integer)plf.getFieldValue();
            assertEquals("Selected value should be " +
                         ContestConstants.PHASES[0],
                         value.intValue(), ContestConstants.PHASES[0]);
        }
        catch (Exception ex) {
            fail(message);
        }
    }

    /**
     * Tests setting the selection of the PhaseListField object
     */
    public void testPhaseListFieldSelection() {
        PhaseListField plf;
        Integer value;

        try {
            plf = new PhaseListField();
            for (int i = 0; i < ContestConstants.PHASE_NAMES.length; i++) {
                message = "PhaseListFieldTest set selection failed.";
                plf.setSelectedIndex(i);
                value = (Integer)plf.getFieldValue();
                assertEquals("Selected value should be " +
                             ContestConstants.PHASES[i],
                             value.intValue(), ContestConstants.PHASES[i]);
            }

            plf = new PhaseListField(-1);
            plf.setSelectedIndex(-1);
            value = (Integer)plf.getFieldValue();
            assertEquals("Selected value should be null", value, null);
        }
        catch (Exception ex) {
            fail(message);
        }
    }

    /**
     * Make sure the clear() method works for both types of PhaseListField
     * objects.
     */
    public void testPhaseListFieldClear() {
        PhaseListField plf;
        Integer value;

        try {
            message = "PhaseListFieldTest construction with -1 failed.";
            plf = new PhaseListField(-1);
            plf.clear();
            value = (Integer)plf.getFieldValue();
            assertEquals("Selected value should be null.",
                         value, null);

            message = "PhaseListFieldTest construction failed.";
            plf = new PhaseListField();
            plf.clear();
            value = (Integer)plf.getFieldValue();
            assertEquals("Selected value should be " +
                         ContestConstants.PHASES[0],
                         value.intValue(), ContestConstants.PHASES[0]);
        }
        catch (Exception ex) {
            fail(message);
        }
    }
}
