package com.topcoder.client.contestApplet.widgets;

/**
 * SortedComboBoxModel.java
 *
 * Description:		ComboBox model that keeps the items sorted.  Note: class is thread safe
 * @author			Tim "Pops" Roberts (troberts@bigfoot.com)
 * @version			1.0
 */


import java.util.*;
//import java.text.*;
import javax.swing.*;

public class SortedComboBoxModel extends AbstractListModel implements MutableComboBoxModel {

    ArrayList data;
    Object selectedItem;
    Comparator c;

    public SortedComboBoxModel() {
        this(20, null);
    }

    public SortedComboBoxModel(int initCapacity) {
        this(initCapacity, null);
    }

    public SortedComboBoxModel(Comparator c) {
        this(20, c);
    }

    public SortedComboBoxModel(int initCapacity, Comparator c) {
        data = new ArrayList(initCapacity);
        this.c = c;
    }

    public synchronized Object getSelectedItem() {
        // Return selected items
        return selectedItem;
    }

    public synchronized void setSelectedItem(Object selectedItem) {
        // Ignore null items
        if (selectedItem == null) return;

        // Set the selected item
        this.selectedItem = selectedItem;

        // Undocumented behaviour (see DefaultComboBoxModel)
        // Need to fire this to have the editor updated.
        fireContentsChanged(this, -1, -1);
    }

    public synchronized int getSize() {
        // Return the size of the data
        return data.size();
    }

    public synchronized Object getElementAt(int pos) {
        // Return the object at a specific location
        if (pos >= data.size()) return null;
        Object item = data.get(pos);
        return item;
    }

    public synchronized void addElement(Object item) {

        // Look where to put the item
        // (not the fastest algo but it works well and is easily maintainable for the sizes expected)
        for (int x = data.size() - 1; x >= 0; x--) {
            Object other = data.get(x);

            // Compare the two items
            int comparerc = compare(item, other);

            // Are they equal - return - no changes
            if (comparerc == 0) return;

            // Less than - keep going
            if (comparerc < 0) continue;

            // Add it at the specified spot
            addElement(item, x + 1);
            return;

        }

        // Hooray - the item is the first in the list
        addElement(item, 0);
    }

    public synchronized void insertElementAt(Object item, int pos) {
        // Override the position and insert the items via addElement
        addElement(item);
    }

    public synchronized void removeElement(Object item) {

        // Look for the item and return remove it if found
        for (int x = data.size() - 1; x >= 0; x--) {
            if (compare(item, data.get(x)) == 0) removeElementAt(x);
        }
    }

    public synchronized void removeElementAt(int pos) {
        // Eliminate the desired item
        if (pos >= 0 && pos < data.size()) {
            data.remove(pos);
            fireIntervalRemoved(this, pos, pos);
        }
    }

    public synchronized void clear() {
        int size = data.size();
        data.clear();
        fireIntervalRemoved(this, 0, size);
    }

    public synchronized Object[] toArray() {
        return data.toArray();
    }

    private final void addElement(Object item, int pos) {
        // Add the item into the desired position and fire the notification
        data.add(pos, item);
        fireIntervalAdded(this, pos, pos);
    }

    private final int compare(Object a, Object b) {
        // If we have a comparator - use it to compare with
        if (c != null) {
            return c.compare(a, b);

            // If the items are instances of Comparable - use it to compare with
        } else if (a instanceof Comparable && b instanceof Comparable) {
            return ((Comparable) a).compareTo(b);

            // Ugh - we have nothing - compare on hashcodes
        } else {
            return a.hashCode() - b.hashCode();
        }
    }

}
