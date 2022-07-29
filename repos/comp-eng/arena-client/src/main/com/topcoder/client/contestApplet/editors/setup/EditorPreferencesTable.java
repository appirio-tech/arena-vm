/**
 * EditorPreferencesTable.java
 *
 * Description:		Table used for editing preferences
 * @author			Tim "Pops" roberts
 * @version			1.0
 */

package com.topcoder.client.contestApplet.editors.setup;

import javax.swing.*;
//import javax.swing.border.*;
import javax.swing.table.*;

import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.contestApplet.widgets.*;
import com.topcoder.client.contestApplet.panels.table.*;

import java.awt.*;

public final class EditorPreferencesTable extends TablePanel {

    public EditorPreferencesTable(EditorPreferencesTableModel model) {
        //super(null,"Editors", CommonData.userHeader, "", new String[0][0]);
        super(null, "Editors", new ContestTableModel(CommonData.userHeader, new Class[]{Integer.class, String.class}));
        this.setPreferredSize(new Dimension(600, 200));

        // Set the model to our model
        JTable table = getTable();
        table.setModel(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        // Set the header attributes
        JTableHeader header = table.getTableHeader();
        header.setResizingAllowed(true);

        // set the column attributes
        int[] columnSize = {60,70, 70, 75, 225, 225};
        //TableColumnModel columnModel = table.getColumnModel();
        for (int i = 0; i < columnSize.length; i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            column.setHeaderRenderer(new ContestTableHeaderRenderer(true,null,12));

            // Set the column size
            if (i < 3) {
                column.setMaxWidth(columnSize[i]);
            } else {
                column.setPreferredWidth(columnSize[i]);
            }
        }

    }
}


/* @(#)EditorPreferencesTable.java */
