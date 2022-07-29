/*
 * RequerimentsBuilder
 * 
 * Created 09/05/2006
 */
package com.topcoder.farm.processor.processorproperties;

import java.util.Collection;
import java.util.HashSet;

import com.topcoder.farm.controller.model.ProcessorProperties;
import com.topcoder.farm.shared.processorproperties.RequerimentsBuilder;

/**
 * PropertiesBuilder is the base clase for all PropertiesBuilder
 * It allows you to generate and update processors properties based on the 
 * common properties of all processors.
 * 
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class PropertiesBuilder {

    /**
     * Current ProcessorProperties
     */
    private ProcessorProperties procProperties;
    
    /**
     * Creates a new RequerimentsBuilder
     */
    public PropertiesBuilder(ProcessorProperties properties) {
        procProperties = properties;
    }
    
    public void osWindows() {
        procProperties.addProperty(RequerimentsBuilder.OS_TYPE_KEY, RequerimentsBuilder.OS_TYPE_WINDOWS);
    }
    
    public void osLinux() {
        procProperties.addProperty(RequerimentsBuilder.OS_TYPE_KEY, RequerimentsBuilder.OS_TYPE_LINUX);
    }
    
    public void numberOfProcessor(int numProcessors) {
        procProperties.addProperty(RequerimentsBuilder.NUM_OF_PROC_KEY, new Integer(numProcessors));
    }
    

    public void memoryAvailable(int memMB) {
        procProperties.addProperty(RequerimentsBuilder.MEM_AVAILABLE_KEY, new Integer(memMB));
    }
    
    public void deployedModule(String moduleName) {
        addToCollection(RequerimentsBuilder.DEPLOYED_MODULES_KEY, moduleName);
    }
    
    protected void addToCollection(String property, Object value) {
        Collection col = (Collection) procProperties.getProperty(property);
        if (col == null) {
            col = new HashSet();
            procProperties.addProperty(property, col);
        }
        col.add(value);
    }

    public void setModuleProperty(String moduleName, String propertyName, Object value) {
        procProperties.addProperty(RequerimentsBuilder.getModulePropertyName(moduleName, propertyName), value);
    }
}
