package com.topcoder.client.contestApplet.uilogic.panels;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestant.ProblemComponentModel;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.shared.language.BaseLanguage;
import com.topcoder.shared.language.Language;
import com.topcoder.shared.problem.DataType;

public class MultiMethodProblemInfoPanel implements ProblemInfoComponent {
    private UIComponent text = null;
    private int firstMethod;
    private UIPage page;
    
    public MultiMethodProblemInfoPanel(ContestApplet ca, UIPage page) {
        this(ca,0,page);
    }

    public MultiMethodProblemInfoPanel(ContestApplet ca, int firstMethodIndex, UIPage page) {
        this.firstMethod = firstMethodIndex;
        this.page = page;
        create();
    }

    private void create() {
        text = page.getComponent("multimethod_problem_info_text");
    }

    public void updateComponentInfo(ProblemComponentModel problemComponent, int language) {
        StringBuffer sb = new StringBuffer(200);
        sb.append("ClassName: ").append(problemComponent.getClassName()).append("\n");
        String[] allMethodNames = problemComponent.getComponent().getAllMethodNames();
        String[][] allParamNames = problemComponent.getComponent().getAllParamNames();
        DataType[][] allParamTypes = problemComponent.getComponent().getAllParamTypes();
        DataType[] allReturnTypes = problemComponent.getComponent().getAllReturnTypes();
        Language lang = BaseLanguage.getLanguage(language);
        for (int i = firstMethod; i < allMethodNames.length; i++) {
            sb.append(lang.getMethodSignature(allMethodNames[i], allReturnTypes[i], allParamTypes[i], allParamNames[i])).append("\n");
        }
        sb.setLength(sb.length()-1);
        text.setProperty("Text", sb.toString());
        text.setProperty("CaretPosition", new Integer(0));
        text.performAction("repaint");
    }

    public void clear() {
    }
}
