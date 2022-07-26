package com.topcoder.shared.netCommon;

/**
 * This interface defines a factory for custom seriailization handlers (<code>CSHandler</code> instances).
 * 
 * @author Timur Zambalayev
 */
public interface CSHandlerFactory {

    /**
     * Creates a new <code>CSHandler</code> instance.
     * 
     * @return a new instance of <code>CSHandler</code>
     */
    CSHandler newInstance();

}
