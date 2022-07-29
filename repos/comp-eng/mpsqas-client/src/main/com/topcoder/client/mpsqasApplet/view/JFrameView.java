package com.topcoder.client.mpsqasApplet.view;

import com.topcoder.client.mpsqasApplet.util.Watchable;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * An abstract class for Views that are JFrames.  Ensures that the update
 * method is always called on the Event Dispatching Thread.
 *
 * @author mitalub
 */
public abstract class JFrameView extends JFrame implements View {

    public abstract void init();

    public abstract void update(Object arg);

    /**
     * Calls <code>update(Object arg)</code> on the Event Dispatching Thread
     * using the <code>UpdatePasser</class> and
     * <code>SwingUtilities.invokeLater()</code>.
     */
    public void update(Watchable o, Object arg) {
        SwingUtilities.invokeLater(new UpdatePasser(arg));
    }

    /**
     * Inner class implementing Runnable whose run method just calls
     * update(arg).
     */
    class UpdatePasser implements Runnable {

        Object o;

        public UpdatePasser(Object o) {
            this.o = o;
        }

        public void run() {
            update(o);
        }
    }
}
