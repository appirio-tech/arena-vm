/**
 * EditorPreferencesTableModel.java
 *
 * Description:		Table model for the editor of plugins
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.contestApplet.editors.setup;

import javax.swing.table.AbstractTableModel;

import com.topcoder.client.contestApplet.editors.*;
import com.topcoder.client.contestApplet.common.*;
import com.topcoder.client.contestApplet.common.LocalPreferences;

import java.util.*;
import java.io.*;
import javax.swing.*;


public class EditorPreferencesTableModel extends AbstractTableModel {

    private ArrayList plugins;
    private static Class[] columnClass = new Class[]{Boolean.class, Boolean.class, String.class, String.class, String.class, String.class};
    private static String[] columnName = new String[]{"Default", "At Startup", "Type", "Name", "EntryPoint", "ClassPath"};
    private static boolean[] columnEditable = new boolean[]{true, true, false, false, false, true};
    private boolean pending = false;
    private JFrame parent = null;
    private JLabel defaultLabel = null;
    private String defaultEditorName = "";

    public static int SAVESUCCESS = 0;
    public static int SAVEEXCEPTION = -1;

    public EditorPreferencesTableModel(JFrame parent) {

        // Save reference to parent
        this.parent = parent;
        //this.defaultLabel = defaultLabel;

        // Set the default
        defaultEditorName = new String(LocalPreferences.getInstance().getDefaultEditorName());

        // Create the plugin array
        plugins = new ArrayList(Arrays.asList(PluginManager.getInstance().getEditorPlugins()));

        // Sort the standard editors
        Collections.sort(plugins, new Comparator() {
            public int compare(Object o1, Object o2) {
                EditorPlugin e1 = (EditorPlugin)o1;
                EditorPlugin e2 = (EditorPlugin)o2;
                
                if(e1.getType()==e2.getType()) return e1.getName().compareToIgnoreCase(e2.getName());
                
                return (e1.getType()==EditorPlugin.STANDARD ? -1 : 1);
            }
        });
        
        // Reset pending false
        pending = false;
    }

    public boolean isCellEditable(int row, int column) {
        // Don't allow edit on standard plugins
        if (((EditorPlugin) plugins.get(row)).getType() == EditorPlugin.STANDARD && column > 0) return false;

        // Return whether the column is editable
        return columnEditable[column];
    }

    public int getRowCount() {
        return plugins.size();
    }

    public int getColumnCount() {
        return columnName.length;
    }

    public Class getColumnClass(int column) {
        return columnClass[column];
    }

    public String getColumnName(int column) {
        return columnName[column];
    }

    public Object getValueAt(int row, int column) {
        // Get the plugin
        EditorPlugin plugin = (EditorPlugin) plugins.get(row);

        // Did we get the default editor
        if (column == 0) return defaultEditorName.equals(plugin.getName()) && !defaultEditorName.equals("") ? Boolean.TRUE : Boolean.FALSE;

        // Determine which value to get
        switch (column) {
        case 1:
            return plugin.getEager() ? Boolean.TRUE : Boolean.FALSE;
        case 2:
            return plugin.getType() == EditorPlugin.STANDARD ? "Standard" : "Local";
        case 3:
            return plugin.getName();
        case 4:
            return plugin.getEntryPoint();
        case 5:
            return plugin.getClassPath();
        }

        return null;
    }

    public void setValueAt(Object value, int row, int column) {
        // Set a change pending
        pending = true;

        // Get the plugin
        EditorPlugin plugin = (EditorPlugin) plugins.get(row);

        // Don't allow edit on standard plugins
        if (plugin.getType() == EditorPlugin.STANDARD && column > 0) return;

        // Set the particular value
        switch (column) {
        case 0:
            defaultEditorName = plugin.getName();
            this.fireTableDataChanged();
            break;
        case 1:
            plugin.setEager(((Boolean)value).booleanValue());
            break;
        case 2:
            return;
        case 3:
            plugin.setName((String) value);
            break;
        case 4:
            plugin.setEntryPoint((String) value);
            break;
        case 5:
            plugin.setClassPath((String) value);
            break;
        }
    }

    public void deleteRow(int row) {

        // Ignore any negative rows
        if (row < 0) {
            Common.showMessage("Cannot Delete", "Please select the editor plugin to delete", parent);
            return;
        }

        // Get the plugin
        EditorPlugin plugin = (EditorPlugin) plugins.get(row);

        // Don't allow deleting of a non-local one
        if (plugin.getType() != EditorPlugin.LOCAL) {
            Common.showMessage("Cannot Delete", "You cannot delete a standard plugin", parent);
            return;
        }

        // Confirm deletion (on a valid plugin)
        if (!plugin.getName().equals("") || !plugin.getEntryPoint().equals("")) {
            if (!Common.confirm("Delete", "Are you sure you wish to delete the " + plugin.getName() + " editor plugin", parent)) return;
        }

        // Set a change pending
        pending = true;

        // Remove the plugin
        plugins.remove(row);

        // If it was the default row - reset the default
        if (defaultEditorName.equals(plugin.getName())) defaultEditorName = "";

        // Let the table know that the row was deleted
        fireTableRowsDeleted(row, row);
    }

    public int addRow() {
        // Create a new plugin
        EditorPlugin newPlugin = new EditorPlugin("", "", "");

        // Show the add dialog
        new AddDialog(parent, newPlugin, plugins);

        // If the name was nothing - they cancelled
        if (newPlugin.getName().equals("")) return -1;

        // Set a change pending
        pending = true;

        // Insert a new row and notify the table
        // (Note: do NOT mark as a pending change - they need to change a value on the new row first)
        plugins.add(newPlugin);
        fireTableRowsInserted(plugins.size() - 1, plugins.size() - 1);

        // Return the row that was inserted
        return plugins.size() - 1;
    }

    public void configure(int row) {
        // Ignore any negative rows
        if (row < 0) {
            Common.showMessage("Cannot Configure", "Please select the editor plugin to configure", parent);
            return;
        }

        // Get the plugin in question
        EditorPlugin plugin = (EditorPlugin) plugins.get(row);

        // Create the dynamic editor and set the editor to the plugin
        DynamicEditor dynamicEditor = null;

        // Create the dynamic editor for this
        try {
            dynamicEditor = PluginManager.getInstance().createEditor(plugin);

            // Call the configure method (if not found, display a message)
            if (!dynamicEditor.configure()) {
                Common.showMessage("Configure", "No configuration available for this editor", parent);
            }
        } catch (InstantiationError e) {
            Common.showMessage("Instantiation Error", "Could not instantiate the editor " + plugin.getName() + " (see the java console for details).", parent);
            return;
        } catch (NoSuchMethodError e) {
            Common.showMessage("Editor Plugin Error", "The editor " + plugin.getName() + " does not implement the required methods for an editor plugin.", parent);
            return;
        } finally {
            if(dynamicEditor!=null) dynamicEditor.dispose();
        }

    }

    public int save() {
        // Validate
        for (int x = plugins.size() - 1; x >= 0; x--) {

            // Get the plugins and skip the non locals
            EditorPlugin plugin = (EditorPlugin) plugins.get(x);
            if (plugin.getType() != EditorPlugin.LOCAL) continue;

            // Skip any plugin that is all blank
            if (plugin.getName().equals("") && plugin.getEntryPoint().equals("") && plugin.getClassPath().equals("")) continue;

            // Validate the name
            if (plugin.getName().equals("")) {
                JOptionPane.showMessageDialog(parent, "You must specify the plugin name", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return x;
            }

            // Validate the entrypoint
            if (plugin.getEntryPoint().equals("")) {
                JOptionPane.showMessageDialog(parent, "You must specify the entry point class for the plugin", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return x;
            }

        }

        // Get the local preferences object
        LocalPreferences localPreferences = LocalPreferences.getInstance();

        // Set the default editor
        localPreferences.setDefaultEditorName(defaultEditorName);

        // Try to write it out
        try {
            PluginManager.getInstance().setEditorPlugins((EditorPlugin[])plugins.toArray(new EditorPlugin[0]));
            pending = false;
            Common.showMessage("Save", "Local plugins were saved successfully", parent);
            return SAVESUCCESS;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(parent, e.toString(), "Error saving preferences", JOptionPane.ERROR_MESSAGE);

            // Reload preferences to get rid of the stuff saved...
            try {
                localPreferences.reload();
            } catch (IOException f) {
            }

            return SAVEEXCEPTION;
        }
    }

    public boolean savePending() {
        return pending;
    }

}


/* @(#)EditorPreferencesTableModel.java */
