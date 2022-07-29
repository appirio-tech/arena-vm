package com.topcoder.client.mpsqasApplet.view.component;

/**
 * View abstract class for TestCasePanel.
 *
 * @author mitalub
 */
public abstract class TestCasePanelView extends ComponentView {

    public abstract int getSelectedTestCaseIndex();

    public abstract String[] getArgs();

    public abstract String getAnnotation();

    public abstract String getTestcaseFile();
    
    /**
     * Returns true if the test case at the specified index is checked to be
     * an example.
     */
    public abstract boolean isExample(int index);
    
    /**
     * Returns true if the test case at the specified index is checked to be
     * a system test.
     */
    public abstract boolean isSystemTest(int index);

    
    /**
     * Returns all data that the view contains related to the addition 
     * of test cases from a file.
     */
    public abstract RandomTestCaseData getAddRamdomTestCaseData();
    
    
    
    /**
     * Class used as transfer object between the view and the controller.
     * It contains data that is needed by the controller to add test 
     * cases from a file.
     */
    public class RandomTestCaseData {
        /**
         * The name of the file containing the test cases to add
         */
        public String fileName = null;
        /**
         * Indicates if test cases should be added as example test cases
         */
        public boolean example = false;
        /**
         * Indicates if test cases should be added as system test cases
         */
        public boolean systemTest = false;
        
        /**
         * Indicates that annotation that should be set to all test cases added
         */
        public String annotation = "";
        
        public RandomTestCaseData(String fileName) {
            this.fileName =  fileName;
        }
        public String getAnnotation() {
            return annotation;
        }
        public void setAnnotation(String annotation) {
            this.annotation = annotation;
        }
        public boolean isExample() {
            return example;
        }
        public void setExample(boolean example) {
            this.example = example;
        }
        public String getFileName() {
            return fileName;
        }
        public void setFileName(String filename) {
            this.fileName = filename;
        }
        public boolean isSystemTest() {
            return systemTest;
        }
        public void setSystemTest(boolean systemTest) {
            this.systemTest = systemTest;
        }
    }
}
