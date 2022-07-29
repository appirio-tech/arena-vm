/*
 * Copyright (C) 2006-2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.client.mpsqasApplet.controller.defaultimpl.component;

import com.topcoder.client.mpsqasApplet.common.UpdateTypes;
import com.topcoder.client.mpsqasApplet.common.OpenMinSizeConstraint;
import com.topcoder.client.mpsqasApplet.common.OpenMaxSizeConstraint;
import com.topcoder.client.mpsqasApplet.common.OpenValidValuesConstraint;
import com.topcoder.client.mpsqasApplet.model.defaultimpl.component.StatementPanelModelImpl;
import com.topcoder.client.mpsqasApplet.controller.component.StatementPanelController;
import com.topcoder.client.mpsqasApplet.model.component.StatementPanelModel;
import com.topcoder.client.mpsqasApplet.view.component.StatementPanelView;
import com.topcoder.client.mpsqasApplet.model.component.ComponentModel;
import com.topcoder.client.mpsqasApplet.view.component.ComponentView;
import com.topcoder.client.mpsqasApplet.object.MainObjectFactory;
import com.topcoder.client.mpsqasApplet.util.Watchable;
import com.topcoder.netCommon.mpsqas.ComponentInformation;
import com.topcoder.shared.problem.*;

import java.util.*;

/**
 * Default implementation of StatementPanelController .
 *
 * <p>
 * Changes in (Round Type Option Support For SRM Problem):
 * <ol>
 * <li>Updated {@link #processStatementChange()}  method to store the round type.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.1 (TC Competition Engine - C++ and Python Customization Support for SRM v1.0):
 * <ol>
 *      <li>Updated {@link #PARTS} field.</li>
 *      <li>Updated {@link #processStatementChange()}  method to store the cpp and python.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (TopCoder Competition Engine - Customize Execution Time and Memory Limits for SRM v1.0):
 * <ol>
 *      <li>Updated {@link #processStatementChange()}  method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.3 (TopCoder Competition Engine Improvement Series 3 v1.0):
 * <ol>
 *      <li>Updated {@link #processStatementChange()}  method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.4 (TopCoder Competition Engine - Stack Size Configuration For SRM Problems v1.0):
 * <ol>
 *      <li>Updated {@link #processStatementChange()} method to support stack limit.</li>
 * </ol>
 * </p>
 *
 * @author mitalub, savon_cn, Selena
 * @version 1.4
 */
public class StatementPanelControllerImpl extends StatementPanelController {

    /**
     * <p>the statement panel parts</p>
     */
    public final static int[] PARTS =
            {StatementPanelModelImpl.DEFINITION,
             StatementPanelModelImpl.INTRODUCTION,
             StatementPanelModelImpl.NOTES,
             StatementPanelModelImpl.CONSTRAINTS,
             StatementPanelModelImpl.SETTINGS};

    private StatementPanelModel model;
    private StatementPanelView view;

    public void init() {
    }

    public void close() {
    }

    public void setModel(ComponentModel model) {
        this.model = (StatementPanelModel) model;
    }

    public void setView(ComponentView view) {
        this.view = (StatementPanelView) view;
    }

    /**
     * Parses the param types entered by the users and updates the list in
     * the model.
     */
    public void processParamTypeChange() {
        try {
            StringTokenizer st = new StringTokenizer(view.getParameters(), ",");
            ArrayList paramsAL = new ArrayList();
            ArrayList namesAL = new ArrayList();
            DataType[] params = null;
            String[] names = null;
            DataType[] oldParams = model.getComponentInformation().getParamTypes();
            String[] oldNames = model.getComponentInformation().getParamNames();
            String param, type, name;
            while (st.hasMoreTokens()) {
                param = st.nextToken().trim();
                type = param.substring(0, param.indexOf(" "));
                name = param.substring(param.lastIndexOf(" ") + 1, param.length());
                paramsAL.add(new DataType(type.trim()));
                namesAL.add(name.trim());
            }

            params = new DataType[paramsAL.size()];
            names = new String[namesAL.size()];  //better be same size as params
            for (int i = 0; i < params.length; i++) {
                params[i] = (DataType) paramsAL.get(i);
                names[i] = (String) namesAL.get(i);
            }

            if (!Arrays.equals(params, oldParams)) {
                model.getComponentInformation().setParamTypes(params);
                model.getComponentInformation().setParamNames(names);
                model.getComponentInformation().setTestCases(new TestCase[0]);
                buildSpecifiedConstraints();
                model.getMainModel().notifyWatchers(UpdateTypes.PARAM_TYPES);
                model.getMainModel().notifyWatchers(UpdateTypes.TEST_CASE_LIST);
            } else if (!Arrays.equals(names, oldNames)) {
                model.getComponentInformation().setParamNames(names);
                buildSpecifiedConstraints();
                model.getMainModel().notifyWatchers(UpdateTypes.PARAM_TYPES);
            }
        } catch (Exception e) {
            MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                    "Invalid parameters format.", true);
            model.getMainModel().notifyWatchers(UpdateTypes.PARAM_TYPES);
        }
    }

    /**
     * Stores all the fields in the problem statement because focus changed and
     * the user may have changed something.  Keeps model up to date with view.
     */
    public void processStatementChange() {
        int i;

        //store definition stuffs
        ComponentInformation comp = model.getComponentInformation();
        comp.setClassName(view.getClassName());
        comp.setMethodName(view.getMethodName());
        comp.setReturnType(new DataType(
                view.getReturnType()));
        //set the round type
        comp.setRoundType(view.getRoundType());
        comp.setIntro(new StructuredTextElement("intro", view.getIntroduction()));
        comp.getProblemCustomSettings().setGccBuildCommand(view.getGccBuildCommand());
        comp.getProblemCustomSettings().setCppApprovedPath(view.getCppApprovedPath());
        comp.getProblemCustomSettings().setPythonCommand(view.getPythonCommand());
        comp.getProblemCustomSettings().setPythonApprovedPath(view.getPythonApprovedPath());
        comp.getProblemCustomSettings().setExecutionTimeLimit(view.getExecutionTimeLimit());
        comp.getProblemCustomSettings().setMemLimit(view.getMemLimit());
        comp.getProblemCustomSettings().setStackLimit(view.getStackLimit());

        //parse & store notes
        ArrayList al = new ArrayList();
        StringTokenizer st = new StringTokenizer(view.getNotes(), "\n");
        while (st.hasMoreTokens()) {
            al.add(st.nextToken());
        }
        Element[] notes = new Element[al.size()];
        for (i = 0; i < notes.length; i++) {
            notes[i] = new StructuredTextElement("note", (String) al.get(i));
        }
        comp.setNotes(notes);

        //parse & store constraints
        al = new ArrayList();
        //combine user input with open constraints to get constraint list.
        String[] inputConstraints = view.getSpecifiedConstraints();
        Constraint[] openConstraints = model.getSpecifiedConstraints();
        ArrayList valids;
        String temp;
        boolean isRange, isOpen;
        int j, index = 0;
        for (i = 0; i < openConstraints.length; i++) {
            if (!inputConstraints[i].trim().equals("")) {
                try {
                    if (openConstraints[i] instanceof OpenMinSizeConstraint) {
                        ((OpenMinSizeConstraint) openConstraints[i])
                                .setSize(Integer.parseInt(inputConstraints[i]));
                    } else if (openConstraints[i] instanceof OpenMaxSizeConstraint) {
                        ((OpenMaxSizeConstraint) openConstraints[i])
                                .setSize(Integer.parseInt(inputConstraints[i]));
                    } else if (openConstraints[i] instanceof OpenValidValuesConstraint) {
                        valids = new ArrayList();

                        //list of comma delimited values or ranges
                        st = new StringTokenizer(inputConstraints[i], ",");
                        while (st.hasMoreTokens()) {
                            temp = st.nextToken();
                            isRange = false;
                            isOpen = false; //are we in the middle of open quotes

                            //find out if this is a range - that is, there is a dash that is
                            //not in quotes.
                            for (j = 0; j < temp.length(); j++) {
                                if (temp.charAt(j) == '\'') {
                                    isOpen = !isOpen;
                                } else if (temp.charAt(j) == '-' && !isOpen) {
                                    isRange = true;
                                    index = j;
                                }
                            }

                            //add the new Range or Value to the list of valids for this
                            //constraint
                            if (isRange) {
                                valids.add(new Range(temp.substring(0, index).trim(),
                                        temp.substring(index + 1).trim()));
                            } else {
                                valids.add(new Value(temp.trim()));
                            }

                            //set the valids
                            ((OpenValidValuesConstraint) openConstraints[i])
                                    .setValidValues(valids);
                        }
                    }
                    al.add(openConstraints[i]);
                } catch (Exception e) {
                    e.printStackTrace();
                    MainObjectFactory.getIStatusMessageRequestProcessor().addMessage(
                            "Invalid constraints format. ", true);
                    view.setConstraintText("", i);
                }
            }
        }

        //add free form constraints too
        st = new StringTokenizer(view.getFreeFormConstraints(), "\n");
        while (st.hasMoreTokens()) {
            al.add(new UserConstraint(st.nextToken()));
        }
        Constraint[] constraints = new Constraint[al.size()];
        for (i = 0; i < constraints.length; i++) {
            constraints[i] = (Constraint) al.get(i);
        }
        comp.setConstraints(constraints);

        //set checked categories
        ArrayList cat = model.getComponentInformation().getCategories();
        for(i = 0; i<cat.size(); i++){
            ComponentCategory cc = (ComponentCategory)cat.get(i);
            cc.setChecked(view.isCategoryChecked(i));
        }
        
    }

    /**
     * Makes the currently shown problem statement part match the selected
     * part in the combo box.
     */
    public void processPartSelection() {
        int index = view.getSelectedPart();
        model.setCurrentPart(PARTS[index]);
        model.getMainModel().notifyWatchers(UpdateTypes.STATEMENT_PART_CHANGE);
    }

    /**
     * Makes a list of open specified constraints that can be populated in
     * the constraints section.  Uses list of parameters in the ProblemComponent.
     * Guarentees each OpenMinSizeConstraints is followed immediately by
     * an OpenMaxValueConstraint.
     * <p>
     * For every numeric value, enter its minimum and maximum value<br>
     * For every character value, enter the valid values<br>
     * For every array (interpret a String as an array of char):<br>
     *  - Enter its minimum and maximum length<br>
     *  - Specify constraints on values each element of the array may have
     *    (this means that for a 2d array or a String[], you'd have the same
     *     sort of array constraints to specify)
     */
    public void buildSpecifiedConstraints() {
        /**** didn't have time to put this into the xml, so we'll scratch it for now**
         DataType[] types = model.getComponentInformation().getParamTypes();
         String[] names = model.getComponentInformation().getParamNames();
         ArrayList constraintsAL = new ArrayList();
         int i, j;

         for(i = 0; i < types.length; i++)
         {
         for(j = 0; j < types[i].getDimension() +
         (types[i].getBaseName().equals("String") ? 1 : 0); j++)
         {  //a String is essentially a char[], so add a dimension for String
         constraintsAL.add(getMinSizeConstraint(names[i], j));
         constraintsAL.add(getMaxSizeConstraint(names[i], j));
         }
         constraintsAL.add(getValidValuesConstraint(names[i],
         types[i].getDimension() +
         (types[i].getBaseName().equals("String") ? 1 : 0)));
         }

         Constraint[] constraints = new Constraint[constraintsAL.size()];
         for(i = 0; i < constraints.length; i++)
         {
         constraints[i] = (Constraint)constraintsAL.get(i);
         }
         model.setSpecifiedConstraints(constraints);
         */
        model.setSpecifiedConstraints(new Constraint[0]);
    }

    /**
     * Checks the problem statement to see if there already is a min size
     * constraint for this, and if there is returns a corresponding
     * OpenMinSizeConstraint, and if not returns a new unpopulated
     * OpenMinSizeConstraint.
     */
    private Constraint getMinSizeConstraint(String paramName, int dimension) {
        Constraint[] constraints = model.getComponentInformation().getConstraints();
        for (int i = 0; i < constraints.length; i++) {
            if (constraints[i] instanceof MinSizeConstraint
                    && ((MinSizeConstraint) constraints[i]).getDimension() == dimension
                    && ((MinSizeConstraint) constraints[i]).getParamName()
                    .equals(paramName)) {
                return new OpenMinSizeConstraint(
                        ((MinSizeConstraint) constraints[i]).getSize(), dimension,
                        paramName);
            }
        }
        return new OpenMinSizeConstraint(dimension, paramName);
    }

    /**
     * Checks the problem statement to see if there already is a max size
     * constraint for this, and if there is returns a corresponding
     * OpenMaxSizeConstraint, and if not returns a new unpopulated
     * OpenMaxSizeConstraint.
     */
    private Constraint getMaxSizeConstraint(String paramName, int dimension) {
        Constraint[] constraints = model.getComponentInformation().getConstraints();
        for (int i = 0; i < constraints.length; i++) {
            if (constraints[i] instanceof MaxSizeConstraint
                    && ((MaxSizeConstraint) constraints[i]).getDimension() == dimension
                    && ((MaxSizeConstraint) constraints[i]).getParamName()
                    .equals(paramName)) {
                return new OpenMaxSizeConstraint(
                        ((MaxSizeConstraint) constraints[i]).getSize(), dimension,
                        paramName);
            }
        }
        return new OpenMaxSizeConstraint(dimension, paramName);
    }

    /**
     * Checks the problem statement to see if there already is a valid value
     * constraint constraint for this, and if there is returns a corresponding
     * OpenValidValuesConstraint, and if not returns a new unpopulated
     * OpenValidValuesConstraint.
     */
    private Constraint getValidValuesConstraint(String paramName, int dimension) {
        Constraint[] constraints = model.getComponentInformation().getConstraints();
        for (int i = 0; i < constraints.length; i++) {
            if (constraints[i] instanceof ValidValuesConstraint
                    && ((ValidValuesConstraint) constraints[i]).getDimension() == dimension
                    && ((ValidValuesConstraint) constraints[i]).getParamName()
                    .equals(paramName)) {
                return new OpenValidValuesConstraint(
                        ((ValidValuesConstraint) constraints[i]).getValidValues(),
                        dimension, paramName);
            }
        }
        return new OpenValidValuesConstraint(dimension, paramName);
    }

    /**
     * Passes the notification to the component model's watchers.
     */
    public void update(Watchable w, Object arg) {
        model.notifyWatchers(arg);
    }
}
