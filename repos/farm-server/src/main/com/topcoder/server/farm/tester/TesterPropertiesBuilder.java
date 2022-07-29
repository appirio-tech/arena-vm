/*
 * TesterPropertiesBuilder
 * 
 * Created 05/01/2007
 */
package com.topcoder.server.farm.tester;

import com.topcoder.farm.controller.model.ProcessorProperties;
import com.topcoder.farm.processor.processorproperties.PropertiesBuilder;

/**
 * @author Diego Belfer (mural)
 * @version $Id: TesterPropertiesBuilder.java 56700 2007-01-29 21:13:11Z thefaxman $
 */
public class TesterPropertiesBuilder extends PropertiesBuilder {

    public TesterPropertiesBuilder(ProcessorProperties properties) {
        super(properties);
        deployedModule(TesterRequerimentsBuilder.SRM_TEST_MODULE);
    }
    
    public void languageIds(int[] languages) {
        String languagesProperty = TesterRequerimentsBuilder.getLanguagesProperty();
        for (int i = 0; i < languages.length; i++) {
            addToCollection(languagesProperty, new Integer(languages[i]));
        }
    }
    
    public void testerActionIds(int[] actions) {
        String actionsProperty = TesterRequerimentsBuilder.getActionProperty();
        for (int i = 0; i < actions.length; i++) {
            addToCollection(actionsProperty, new Integer(actions[i]));
        }
    }
}
