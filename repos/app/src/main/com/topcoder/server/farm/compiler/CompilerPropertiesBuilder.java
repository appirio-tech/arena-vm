/*
 * CompilerRequerimentsBuilder
 *
 * Created 10/16/2006
 */
package com.topcoder.server.farm.compiler;

import com.topcoder.farm.controller.model.ProcessorProperties;
import com.topcoder.farm.processor.processorproperties.PropertiesBuilder;

/**
 * @author Diego Belfer (mural)
 * @version $Id: CompilerPropertiesBuilder.java 79103 2011-03-22 00:42:07Z mural $
 */
public class CompilerPropertiesBuilder extends PropertiesBuilder {

    public CompilerPropertiesBuilder(ProcessorProperties properties) {
        super(properties);
        deployedModule(CompilerRequerimentsBuilder.COMPILER_MODULE);
    }

    public void languageIds(int[] languages) {
        String languagesProperty = CompilerRequerimentsBuilder.getLanguagesProperty();
        for (int i = 0; i < languages.length; i++) {
            addToCollection(languagesProperty, new Integer(languages[i]));
        }
    }
    
    public void roundTypes(int[] roundTypes) {
        String roundTypesProperty = CompilerRequerimentsBuilder.getRoundTypesProperty();
        for (int i = 0; i < roundTypes.length; i++) {
            addToCollection(roundTypesProperty, new Integer(roundTypes[i]));
        }

    }

    public void compilerActionIds(int[] actions) {
        String actionsProperty = CompilerRequerimentsBuilder.getActionProperty();
        for (int i = 0; i < actions.length; i++) {
            addToCollection(actionsProperty, new Integer(actions[i]));
        }
    }

    public void threadingAllowed(boolean threadingAllowed) {
        setModuleProperty(CompilerRequerimentsBuilder.COMPILER_MODULE, CompilerRequerimentsBuilder.THREADING_PROP_NAME, Boolean.valueOf(threadingAllowed));
    }
}
