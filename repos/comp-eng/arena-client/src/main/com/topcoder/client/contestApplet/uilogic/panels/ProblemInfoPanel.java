package com.topcoder.client.contestApplet.uilogic.panels;

import com.topcoder.client.contestApplet.ContestApplet;
import com.topcoder.client.contestant.ProblemComponentModel;
import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIPage;
import com.topcoder.shared.problem.DataType;

public class ProblemInfoPanel implements ProblemInfoComponent {
    private UIComponent className;
    private UIComponent methodName;
    private UIComponent returnType;
    private UIComponent argTypes;

    public ProblemInfoPanel(ContestApplet ca, UIPage page) {
        className = page.getComponent("problem_info_class_name");
        methodName = page.getComponent("problem_info_method_name");
        returnType = page.getComponent("problem_info_return_type");
        argTypes = page.getComponent("problem_info_arg_types");
    }

    public void updateComponentInfo(ProblemComponentModel problemComponent, int language) {
        className.setProperty("Text", problemComponent.getClassName());
        methodName.setProperty("Text", problemComponent.getMethodName());
        returnType.setProperty("Text", problemComponent.getReturnType().getDescriptor(language));

        String args = "(";
        DataType[] paramTypes = problemComponent.getParamTypes();
        if (paramTypes.length > 0) {
            args += paramTypes[0].getDescriptor(language);
            for (int i = 1; i < paramTypes.length; i++) {
                args += "," + paramTypes[i].getDescriptor(language);
            }
        }
        argTypes.setProperty("Text", args + ")");
        argTypes.performAction("repaint");
    }

    public void clear() {
    }
}
