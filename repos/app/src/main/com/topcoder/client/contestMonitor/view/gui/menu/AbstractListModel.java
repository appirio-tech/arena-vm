package com.topcoder.client.contestMonitor.view.gui.menu;

import com.topcoder.client.contestMonitor.model.ResponseWaiter;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;
import java.util.List;

/**
 * Abstract class providing the basic functionality common to all
 * ComboBoxModel implementations in this package. Provides the methods to
 * maintain the state of response to request (implementing ResponseWaiter
 * interface), and basic methods to query the underlying List of objects
 * reprsenting data for ComboBoxModel. Also implements the methods from
 * <code>javax.swing.ListModel</code> that is extended by ComboBoxModel
 * interface. So the subclasses of this class should only implement the
 * methods that use specific representation of underlying data, namely :<p>
 * <UL>
 *  <LI>getElementAt(int)</LI>
 *  <LI>getSelectedItem()</LI>
 *  <LI>setSelectedItem(Object)</LI>
 * </UL>
 *
 * @author  Giorgos Zervas
 * @version 11/07/2003
 * @since   Admin Tool 2.0
 */
public abstract class AbstractListModel implements ComboBoxModel,
        ResponseWaiter {

    /**
     * An int constant representing the state of response that is not
     * received yet.
     */
    protected final static int NOT_RECEIVED = 0;

    /**
     * An int constant representing the state of response that is received
     * and is successful.
     */
    protected final static int RECEIVED = 1;

    /**
     * An int constant representing the state of response that is received
     * and is unsuccessful.
     */
    protected final static int ERROR = 2;

    /**
     * An uderlying data maintained by this ComboBoxModel.
     */
    private List data = null;

    /**
     * An int that is a pointer to item currently selected from this
     * model. This variable is changed by <code>setSelectedItem()</code>
     * method.
     */
    private int currentIndex = 0;

    /**
     * A state of response to request for problems data. This variable
     * is maintained by methods of <code>ResponseWaiter</code> interface.
     */
    private int responseState = NOT_RECEIVED;

    /**
     * Sets the selected item. The implementations of this method should
     * parse given Object and find index of corresponding element in <code>
     * data</code> List and set <code>currentIndex</code> variable to
     * point to this found element.
     *
     * @param anItem the list object to select or <code>null</code>
     *        to clear the selection.
     */
    public abstract void setSelectedItem(Object anItem);

    /**
     * Gets the element from underlying data List corresponding to item
     * selected from this ComboBoxModel. The implementations of this method
     * are free to return any kind of data they like.
     *
     * @return an Object representing the selected item.
     */
    public abstract Object getSelectedItem();

    /**
     * Gets the size of the underlying data List that contain the data
     * maintained by this ComboBoxModel.
     *
     * @return the number of elements in underlying data List.
     */
    public int getSize() {
        return data.size();
    }

    /**
     * Gets the element of this ComboBoxModel corresponding to specified
     * index. The implementation of this method is responsible for providing
     * the appropriate representation of specified element. Returned value
     * should be constructed so it can be easily parsed when <code>
     * setSelectedItem()</code> method will be invoked.
     *
     * @param  index the requested index
     * @return the Object representing the element at specified index for
     *         display
     */
    public abstract Object getElementAt(int index);

    /**
     * Adds a listener to the list that's notified each time a change
     * to the data model occurs. Actually this method does nothing
     * since no changes to this model will happen after construction.
     *
     * @param l the <code>ListDataListener</code> to be added
     */
    public void addListDataListener(ListDataListener l) {
    }

    /**
     * Removes a listener from the list that's notified each time a
     * change to the data model occurs. Actually this method does
     * nothing since no changes to this model will happen after
     * construction.
     *
     * @param l the <code>ListDataListener</code> to be removed
     */
    public void removeListDataListener(ListDataListener l) {
    }

    /**
     * This method is invoked when the request for problem data is sent.
     * Sets <code>responseRecieved</code> variable to NOT_RECEIVED.
     */
    public void waitForResponse() {
        responseState = NOT_RECEIVED;
    }

    /**
     * This method is invoked when response indicating about some error
     * with processing the request for problems data occurs. This method
     * sets the <code>responseRecieved</code> variable to ERROR.
     */
    public void errorResponseReceived(Throwable t) {
        responseState = ERROR;
    }

    /**
     * This method is invoked when response to request for problems data
     * is received. This method sets the <code>responseRecieved</code>
     * variable to RECEIVED.
     */
    public void responseReceived() {
        responseState = RECEIVED;
    }

    /**
     * Returns the index of the currently selected item in the ComboBox
     *
     * @return returns the currentIndex variable which represents the index
     *         of the selected item in the combo box
     */
    protected int getCurrentIndex() {
        return currentIndex;
    }

    /**
     * Sets the index of the currently selected item in the ComboBox
     *
     * @throws IllegalArgumentException if given argument is less than -1
     */
    protected void setCurrentIndex(int currentIndex) {
        if (currentIndex < -1) {
            throw new IllegalStateException("currentIndex less than -1");
        } else {
            this.currentIndex = currentIndex;
        }
    }

    /**
     * Gets the underlying data List that contains the data
     * maintained by this ComboBoxModel.
     *
     * @return returns the data stored in this model as a List
     * @see java.util.List
     */
    protected List getData() {
        return data;
    }

    /**
     * @throws IllegalArgumentException if given argument is null
     */
    protected void setData(List data) {
        if (data == null) {
            throw new IllegalArgumentException();
        } else {
            this.data = data;
        }
    }

    /**
     * @return return the responseState variable
     */
    protected int getResponseState() {
        return responseState;
    }
}
