package com.topcoder.client.mpsqasApplet.object;

import com.topcoder.client.mpsqasApplet.view.component.*;
import com.topcoder.client.mpsqasApplet.model.component.*;
import com.topcoder.client.mpsqasApplet.controller.component.*;
import com.topcoder.netCommon.mpsqas.object.ObjectFactory;

/**
 * An Object Factory for components, which can have multiple instances
 * over the life of the applet.
 *
 * @author mitalub
 */
public class ComponentObjectFactory extends ObjectFactory {

    private static Object[] combine(ComponentView view, ComponentModel model,
            ComponentController controller) {
        controller.setView(view);
        controller.setModel(model);
        view.setModel(model);
        view.setController(controller);
        return new Object[]{controller, model, view};
    }

    /** Returns Object[] contining instances of SolutionPanel
     * Controller, Model, and View.  They are linked using setModel
     * and setController methods.
     */
    public static Object[] createSolutionPanel() {
        return combine((ComponentView)
                getInstance("SolutionPanelViewClass"),
                (ComponentModel)
                getInstance("SolutionPanelModelClass"),
                (ComponentController)
                getInstance("SolutionPanelControllerClass"));
    }

    /** Returns Object[] contining instances of AllSolutionPanel
     * Controller, Model, and View.  They are linked using setModel
     * and setController methods.
     */
    public static Object[] createAllSolutionPanel() {
        return combine((ComponentView)
                getInstance("AllSolutionPanelViewClass"),
                (ComponentModel)
                getInstance("AllSolutionPanelModelClass"),
                (ComponentController)
                getInstance("AllSolutionPanelControllerClass"));
    }

    /** Returns Object[] contining instances of AllSolutionPanel
     * Controller, Model, and View.  They are linked using setModel
     * and setController methods.
     */
    public static Object[] createAllLongSolutionPanel() {
        return combine((ComponentView)
                getInstance("AllSolutionPanelViewClass"),
                (ComponentModel)
                getInstance("AllSolutionPanelModelClass"),
                (ComponentController)
                getInstance("AllLongSolutionPanelControllerClass"));
    }
    /** Returns Object[] contining instances of GeneralContestInfoPanel
     * Controller, Model, and View.  They are linked using setModel
     * and setController methods.
     */
    public static Object[] createGeneralContestInfoPanel() {
        return combine((ComponentView)
                getInstance("GeneralContestInfoPanelViewClass"),
                (ComponentModel)
                getInstance("GeneralContestInfoPanelModelClass"),
                (ComponentController)
                getInstance("GeneralContestInfoPanelControllerClass"));
    }

    /** Returns Object[] contining instances of RoundProblemsPanel
     * Controller, Model, and View.  They are linked using setModel
     * and setController methods.
     */
    public static Object[] createRoundProblemsPanel() {
        return combine((ComponentView)
                getInstance("RoundProblemsPanelViewClass"),
                (ComponentModel)
                getInstance("RoundProblemsPanelModelClass"),
                (ComponentController)
                getInstance("RoundProblemsPanelControllerClass"));
    }

    /** Returns Object[] contining instances of StatementPanel
     * Controller, Model, and View.  They are linked using setModel
     * and setController methods.
     */
    public static Object[] createStatementPanel() {
        return combine((ComponentView)
                getInstance("StatementPanelViewClass"),
                (ComponentModel)
                getInstance("StatementPanelModelClass"),
                (ComponentController)
                getInstance("StatementPanelControllerClass"));
    }

    /** Returns Object[] contining instances of TestCasePanel
     * Controller, Model, and View.  They are linked using setModel
     * and setController methods.
     */
    public static Object[] createTestCasePanel() {
        return combine((ComponentView)
                getInstance("TestCasePanelViewClass"),
                (ComponentModel)
                getInstance("TestCasePanelModelClass"),
                (ComponentController)
                getInstance("TestCasePanelControllerClass"));
    }

    /** Returns Object[] contining instances of TestCasePanel
     * Controller, Model, and View.  They are linked using setModel
     * and setController methods.
     */
    public static Object[] createLongTestCasePanel() {
        return combine((ComponentView)
                getInstance("LongTestCasePanelViewClass"),
                (ComponentModel)
                getInstance("TestCasePanelModelClass"),
                (ComponentController)
                getInstance("TestCasePanelControllerClass"));
    }
    
    /** Returns Object[] contining instances of CorrespondencePanel
     * Controller, Model, and View.  They are linked using setModel
     * and setController methods.
     */
    public static Object[] createCorrespondencePanel() {
        return combine((ComponentView)
                getInstance("CorrespondencePanelViewClass"),
                (ComponentModel)
                getInstance("CorrespondencePanelModelClass"),
                (ComponentController)
                getInstance("CorrespondencePanelControllerClass"));
    }

    /**
     * Returns an Object[] for the StatementPreviewPanel.
     */
    public static Object[] createStatementPreviewPanel() {
        return combine((ComponentView)
                getInstance("StatementPreviewPanelViewClass"),
                (ComponentModel)
                getInstance("StatementPreviewPanelModelClass"),
                (ComponentController)
                getInstance("StatementPreviewPanelControllerClass"));
    }

    public static Object[] createComponentsPanel() {
        return combine((ComponentView)
                getInstance("ComponentsPanelViewClass"),
                (ComponentModel)
                getInstance("ComponentsPanelModelClass"),
                (ComponentController)
                getInstance("ComponentsPanelControllerClass"));
    }

    public static Object[] createTeamStatementPanel() {
        return combine((ComponentView)
                getInstance("TeamStatementPanelViewClass"),
                (ComponentModel)
                getInstance("TeamStatementPanelModelClass"),
                (ComponentController)
                getInstance("TeamStatementPanelControllerClass"));
    }
    
    public static Object[] createLongStatementPanel() {
        return combine((ComponentView)
                getInstance("LongStatementPanelViewClass"),
                (ComponentModel)
                getInstance("LongStatementPanelModelClass"),
                (ComponentController)
                getInstance("LongStatementPanelControllerClass"));
    }

    public static Object[] createClassEditorPanel() {
        return combine((ComponentView)
                getInstance("ClassEditorPanelViewClass"),
                (ComponentModel)
                getInstance("ClassEditorPanelModelClass"),
                (ComponentController)
                getInstance("ClassEditorPanelControllerClass"));
    }

    public static Object[] createJavaDocPanel() {
        return combine((ComponentView)
                getInstance("JavaDocPanelViewClass"),
                (ComponentModel)
                getInstance("JavaDocPanelModelClass"),
                (ComponentController)
                getInstance("JavaDocPanelControllerClass"));
    }

    public static Object[] createAdminProblemPanel() {
        return combine((ComponentView)
                getInstance("AdminProblemPanelViewClass"),
                (ComponentModel)
                getInstance("AdminProblemPanelModelClass"),
                (ComponentController)
                getInstance("AdminProblemPanelControllerClass"));
    }
    
    public static Object[] createPaymentPanel() {
        return combine((ComponentView)
                getInstance("PaymentPanelViewClass"),
                (ComponentModel)
                getInstance("PaymentPanelModelClass"),
                (ComponentController)
                getInstance("PaymentPanelControllerClass"));
    }

    public static Object[] createApprovalPanel() {
        return combine((ComponentView)
                getInstance("ApprovalPanelViewClass"),
                (ComponentModel)
                getInstance("ApprovalPanelModelClass"),
                (ComponentController)
                getInstance("ApprovalPanelControllerClass"));
    }
}
