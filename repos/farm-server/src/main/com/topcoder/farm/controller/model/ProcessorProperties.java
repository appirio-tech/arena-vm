/*
* Copyright (C) 2006-2013 TopCoder Inc., All Rights Reserved.
*/

/*
 * ProcessorProperties
 *
 * Created 07/27/2006
 */
package com.topcoder.farm.controller.model;

import java.util.HashMap;
import java.util.Map;

import com.topcoder.farm.shared.invocation.InvocationRequirements;

/**
 * This class represents the properties a processor
 * can have.
 *
 * <p>
 * Changes in version 1.0 (TC Competition Engine - Processor and Controller Handshake Change v1.0):
 * <ol>
 *      <li>Update {@link #name} field and corresponding getter,setter method.</li>
 *      <li>Update {@link #maxRunnableTasks} field and corresponding getter,setter method.</li>
 * </ol>
 * </p>

 * @author Diego Belfer (mural), TCSASSEMBLER
 * @version 1.0
 */
public class ProcessorProperties {
    private Long id;
    private String description;
    private Map<String, Object> properties = new HashMap();
    /**
     * the processor name, generally we can call it group id.
     */
    private String name;
    /**
     * The max number of task this processor can execute simultaneously 
     */
    private int maxRunnableTasks;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public Map getProperties() {
        return properties;
    }

    public void addProperty(String key, Object value) {
        properties.put(key, value);
    }

    public void setProperty(String key) {
        properties.put(key, Boolean.TRUE);
    }

    public Object getProperty(String key) {
        return properties.get(key);
    }

    public void clearAllProperties() {
        properties.clear();
    }
    /**
     * Returns tues if this ProcessorProperties instance <b>match</b> the given
     * InvocationRequirements.
     *
     * @param requeriments The requeriments to match
     * @return true if requeriments are matched
     * @throws MatchProcessException If was not possible to process the matching
     */
    public boolean match(InvocationRequirements requeriments) throws MatchProcessException {
        try {
            return requeriments.getFilterExpression().eval(properties);
        } catch (RuntimeException e) {
            throw new MatchProcessException("Cannot processor match for given properties",e);
        }
    }
    /**
     * <p>
     * Getter the processor name.
     * </p>
     * @return the processor name.
     */
    public String getName() {
        return name;
    }
    /**
     * <p>
     * Setter the processor name.
     * </p>
     * @param name the processor name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * <p>
     * Getter the max runnable tasks.
     * </p>
     * @return the max runnable tasks.
     */
    public int getMaxRunnableTasks() {
        return maxRunnableTasks;
    }
    /**
     * <p>
     * Setter the max runnable tasks.
     * </p>
     * @param maxRunnableTasks the max runnable tasks.
     */
    public void setMaxRunnableTasks(int maxRunnableTasks) {
        this.maxRunnableTasks = maxRunnableTasks;
    }

    public boolean equals(Object obj) {
        return obj != null && getClass().equals(obj.getClass()) && getId().equals(((ProcessorProperties) obj).getId());
    }

    public int hashCode() {
        return getId().hashCode();
    }
}
