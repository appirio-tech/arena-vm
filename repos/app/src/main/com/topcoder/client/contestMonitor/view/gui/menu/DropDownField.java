/*
 * Author: Michael Cervantes (emcee)
 * Date: Jul 9, 2002
 * Time: 11:18:31 PM
 */
package com.topcoder.client.contestMonitor.view.gui.menu;

import javax.swing.JComboBox;

public class DropDownField extends JComboBox implements ViewField {

   /**
    * Creates a <code>DropDownField</code> with a default data model.
    * The default data model is an empty list of objects.
    *
    * @see JComboBox
    */
    public DropDownField() {
        super();
    }

   /**
    * Creates a <code>DropDownField</code> that contains the elements
    * in the specified array.  By default the first item in the array
    * (and therefore the data model) becomes selected.
    *
    * @param items  an array of objects to insert into the drop down field
    * @see JComboBox
    */
    public DropDownField(final Object items[]) {
        super(items);
    }

   /**
     * Creates a <code>DropDownField</code> that contains the elements
     * in the specified array.  The item at index becomes the selected item.
     *
     * @param items  an array of objects to insert into the drop down field
     * @param itemIndex the index of element in items[] to become selected
     */
    public DropDownField(final Object items[], int itemIndex) {
        super(items);
        setSelectedItem(items[itemIndex]);
    }

   /**
    * Returns the current selected item.
    */
    public Object getFieldValue() throws Exception {
        return getSelectedItem();
    }

    /**
     * Resets selection of the <code>DropDownField</code>
     */
    public void clear() {
        setSelectedItem(null);
    }
}
