
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JFrame;

import junit.framework.TestCase;

import com.topcoder.client.contestMonitor.view.gui.menu.BeforeContestSubmenu;
import com.topcoder.server.AdminListener.request.SetRoundTermsRequest;
import com.topcoder.server.AdminListener.response.SetRoundTermsAck;

/**
 * Accuracy tests for the Dynamic Loading of Terms requirement
 *
 * @author  TCSDEVELOPER
 * @since   Admin Tool 2.0
 */
public class GroupOnePhaseThreeAccuracyTests extends TestCase {
    /* Used to print something intelligible when a failure occurs */
    private String message = "Test failure";

    public GroupOnePhaseThreeAccuracyTests() {
        super("grouponephasethreeaccuracy");
    }

    /**************************************************************************
     * Setup/teardown
     *************************************************************************/
    protected void setUp() {
        try {
        }
        catch (Exception ex) {

        }
    }

    /**************************************************************************
     * Test the SetRoundTermsRequest
     *************************************************************************/

    /**
     * Test construction of SetRoundTermsRequest object
     */
    public void testSetRoundTermsRequest() {
        SetRoundTermsRequest request;
        try {
            Hashtable props = new Hashtable();
            props.put("test", "testing");

            message = "Construction of SetRoundTermsRequest failed.";
            request = new SetRoundTermsRequest(1000, props);

            assertEquals("Round ids not equal", request.getRoundID(), 1000);

            message = "Call to getProperties failed.";
            Map props2 = request.getProperties();

            assertNotNull("Properties table is null", props2);

            String value = (String)props2.get("test");

            assertNotNull("Value should not be null", value);
            assertEquals("Hashtable entry does not match", value, "testing");
        }
        catch (Exception ex) {
            fail(message);
        }
    }

    /**************************************************************************
     * Test the SetRoundTermsAck
     *************************************************************************/

    /**
     * Test construction of SetRoundTermsAck object
     */
    public void testSetRoundTermsAck() {
        SetRoundTermsAck ack;
        try {
            RuntimeException exception = new RuntimeException("test");
            message = "Construction of SetRoundTermsAck with throwable failed.";
            ack = new SetRoundTermsAck(exception);

            assertFalse("Ack not marked as not successful.",
                        ack.isSuccess());
            assertTrue("Ack does not have exception.", ack.hasException());
            assertEquals("Exceptions don't match.", ack.getException(),
                         exception);

            message = "Construction of SetRoundTermsAck with roundId failed.";
            ack = new SetRoundTermsAck(1000);

            assertTrue("Ack not marked as successful.", ack.isSuccess());
            assertEquals("Round ids not equal", ack.getRoundID(), 1000);
        }
        catch (Exception ex) {
            fail(message);
        }
    }

    /**************************************************************************
     * Test the BeforeContestSubmenu
     *************************************************************************/

    /**
     * Test construction of BeforeContestSubmenu object
     */
    public void testBeforeContestSubmenu() {
        JFrame frame = new JFrame();
        try {
            message = "Construction of BeforeContestSubmenu failed.";
            BeforeContestSubmenu menu = new BeforeContestSubmenu(frame,
                null, null);
        }
        catch (Exception ex) {
            fail(message);
        }
    }
}
