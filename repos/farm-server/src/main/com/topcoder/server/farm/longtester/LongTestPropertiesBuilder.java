/*
 * LongTestRequerimentsBuilder
 * 
 * Created 14/09/2006
 */
package com.topcoder.server.farm.longtester;

import com.topcoder.farm.controller.model.ProcessorProperties;
import com.topcoder.farm.processor.processorproperties.PropertiesBuilder;

/**
 * @author Diego Belfer (mural)
 * @version $Id: LongTestPropertiesBuilder.java 54869 2006-12-01 18:02:46Z thefaxman $
 */
public class LongTestPropertiesBuilder extends PropertiesBuilder {

    public LongTestPropertiesBuilder(ProcessorProperties properties) {
        super(properties);
        deployedModule(LongTestRequerimentsBuilder.LONG_TEST_MODULE);
    }
    
    public void languageIds(int[] languages) {
        String languagesProperty = LongTestRequerimentsBuilder.getLanguagesProperty();
        for (int i = 0; i < languages.length; i++) {
            addToCollection(languagesProperty, new Integer(languages[i]));
        }
    }
    
    public void roundTypes(int[] roundTypes) {
        String roundTypesProperty = LongTestRequerimentsBuilder.getRoundTypesProperty();
        for (int i = 0; i < roundTypes.length; i++) {
            addToCollection(roundTypesProperty, new Integer(roundTypes[i]));
        }
    }
}
