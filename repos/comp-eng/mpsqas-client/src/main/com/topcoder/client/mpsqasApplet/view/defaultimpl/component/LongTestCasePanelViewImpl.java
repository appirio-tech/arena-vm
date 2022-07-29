/*
 * LongTestCasePanelViewImpl
 * 
 * Created 05/04/2006
 */
package com.topcoder.client.mpsqasApplet.view.defaultimpl.component;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import com.topcoder.shared.problem.TestCase;

/**
 * Test case panel for a long problem. 
 * Redefine necessary behavior of the TestCasePanelViewImpl
 *  
 * @author Diego Belfer (mural)
 * @version $Id: LongTestCasePanelViewImpl.java 47978 2006-07-07 15:28:06Z thefaxman $
 */
public class LongTestCasePanelViewImpl extends TestCasePanelViewImpl {
    private static final String[] TEST_CASE_HEADERS = {"Case", "Example", "System"};
    private static final int[] TEST_CASE_WIDTHS = {70, 75, 75};
    private static final boolean[] TEST_CASE_EDITABLES = {false, true, true};
    private JCheckBox addAsSystemTestCheck;
    
    public LongTestCasePanelViewImpl() {
        super();
    }

    /**
     * @see com.topcoder.client.mpsqasApplet.view.defaultimpl.component.TestCasePanelViewImpl#getColumnHeaders()
     */
    protected String[] getColumnHeaders() {
        return TEST_CASE_HEADERS;
    }
    
    /**
     * @see com.topcoder.client.mpsqasApplet.view.defaultimpl.component.TestCasePanelViewImpl#getColumnWidths()
     */
    protected int[] getColumnWidths() {
        return TEST_CASE_WIDTHS;
    }

    /**
     * @see com.topcoder.client.mpsqasApplet.view.defaultimpl.component.TestCasePanelViewImpl#getColumnEditables()
     */
    protected boolean[] getColumnEditables() {
        return TEST_CASE_EDITABLES;
    }
    
    /**
     * @see com.topcoder.client.mpsqasApplet.view.defaultimpl.component.TestCasePanelViewImpl#getTestCaseData()
     */
    protected Object[][] getTestCaseData() {
        TestCase[] testCases = getModel().getComponentInformation().getTestCases();
        Object[][] data = new Object[testCases.length][3];
        for (int i = 0; i < testCases.length; i++) {
            data[i][0] = new Integer(i);
            data[i][1] = new Boolean(testCases[i].isExample());
            data[i][2] = new Boolean(testCases[i].isSystemTest());
        }
        return data;
    }
    
    /**
     * @see com.topcoder.client.mpsqasApplet.view.component.TestCasePanelView#isSystemTest(int)
     */
    public boolean isSystemTest(int index) {
        return ((Boolean) getTestCaseTable().getAbsoluteValueAt(index, 2)).booleanValue();
    }
    
    /**
     * @see com.topcoder.client.mpsqasApplet.view.component.TestCasePanelViewImpl#arrangeFromFileLayoutComponents(JComponent)
     */ 
    protected void arrangeFromFileLayoutComponents(JComponent containingBox) {
        addAsSystemTestCheck = new JCheckBox("as System");
        addAsSystemTestCheck.setToolTipText("Add test cases as system tests");
        containingBox.add(getRandomButton());
        containingBox.add(addAsSystemTestCheck);
        containingBox.add(getTestcaseFileComponent());
    }
    
    /**
     * @see com.topcoder.client.mpsqasApplet.view.component.TestCasePanelView#getAddRamdomTestCaseData()
     */
    public RandomTestCaseData getAddRamdomTestCaseData() {
        RandomTestCaseData data = new RandomTestCaseData(getTestcaseFile());
        data.setSystemTest(addAsSystemTestCheck.isSelected());
        return data;
    }
}

