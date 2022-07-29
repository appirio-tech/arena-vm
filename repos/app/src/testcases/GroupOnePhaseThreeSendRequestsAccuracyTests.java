
import java.util.Hashtable;
import java.util.Map;

import junit.framework.TestCase;

import com.topcoder.client.contestMonitor.model.MonitorTestClient;
import com.topcoder.server.AdminListener.request.SetRoundTermsRequest;
import com.topcoder.server.AdminListener.response.SetRoundTermsAck;


/**
 * Accuracy tests for the Dynamic Loading of Terms requirement. This actually
 * attempts to send a set round terms request when the system is up and
 * running.
 *
 * @author  TCSDEVELOPER
 * @since   Admin Tool 2.0
 */
public class GroupOnePhaseThreeSendRequestsAccuracyTests extends TestCase implements
    MonitorTestClient.Client {
    private MonitorTestClient monitorTestClient;
    private Class             expectedClass;
    private Object            receivedObject;
    private int               roundId;
    private Hashtable         termsProps;



    /* Used to print something intelligible when a failure occurs */
    private String message = "Test failure";

    public GroupOnePhaseThreeSendRequestsAccuracyTests() {
        super("grouponephasetwoaccuracy");
    }

    /**************************************************************************
     * Setup/teardown
     *************************************************************************/
    protected void setUp() {
        try {
            monitorTestClient = new MonitorTestClient("192.168.255.247",
                2004,
                0,
                this);
            monitorTestClient.connect();

            roundId = 1000;
            termsProps = new Hashtable();
            termsProps.put("round_name", "Test Round Match " + roundId);
            termsProps.put("round_date", "12/01/03");
            termsProps.put("coders_per_room", "20");
            termsProps.put("appeal_max_date", "12/04/03");
            termsProps.put("appeal_max_time", "11:00 AM Eastern Time");
            termsProps.put("email", "service@topcoder.com");
            termsProps.put("reg_start_time", "7:00 PM");
            termsProps.put("reg_end_time", "7:55 PM ET");
            termsProps.put("reg_date", "12/01/2003");
            termsProps.put("round_start", "8:00 PM ET");
            termsProps.put("suc_challenge_points", "75");
            termsProps.put("unsuc_challenge_points", "75");
            termsProps.put("min_age", "18");
            termsProps.put("site", "http://www.topcoder.com");
            termsProps.put("tc_address",
                           "TopCoder, Inc., 703 Hebron Ave, Glastonbury, CT 06033.");
        }
        catch (Exception ex) {
            fail("Unable to create MonitorTestClient and connect.");
        }
    }

    protected void tearDown() {
        try {
            monitorTestClient.disconnect();
            monitorTestClient = null;
        }
        catch (Exception ex) {
        }
    }

    /**************************************************************************
     * receivedObject method
     *************************************************************************/
    public void receivedObject(int id, Object obj, long elapsedTime)
    {
        synchronized(this) {
            receivedObject = obj;
        }
    }

    /**************************************************************************
     * Test dynamic loading of terms
     *************************************************************************/

    /**
     * Test loading dynamic terms by sending an actual request to the
     * running system. We assume the admin listener (and other environment
     * components) are up and running.
     */
    public void testDynamicLoadOfTerms() {
        SetRoundTermsRequest request;

        message = "Construction of SetRoundTermsRequest failed.";
        try {
            request = new SetRoundTermsRequest(roundId, termsProps);

            assertEquals("Round ids not equal", request.getRoundID(), roundId);

            message = "Call to getProperties failed.";
            Map props2 = request.getProperties();

            assertNotNull("Properties table is null", props2);

            // Send request
            expectedClass = SetRoundTermsAck.class;
            monitorTestClient.sendRequest(request, expectedClass);
            waitForResult();

            if(!(receivedObject instanceof SetRoundTermsAck)) {
                fail("Object not of correct type!");
            }

            assertTrue("Set round terms not successful.",
                       ((SetRoundTermsAck)receivedObject).isSuccess());
        }
        catch (Exception ex) {
            fail(message);
        }
    }

    private void waitForResult() {
        synchronized(this) {
            receivedObject = null;
        }

        while(true) {
            synchronized(this) {
                if (receivedObject != null) {
                    break;
                }
            }

            try {
                Thread.sleep(100);
            }
            catch (Exception ex) {
            }
        }
    }
}
