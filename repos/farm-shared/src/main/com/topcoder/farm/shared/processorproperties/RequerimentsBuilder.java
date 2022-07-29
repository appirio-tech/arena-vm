/*
 * RequerimentsBuilder
 * 
 * Created 09/05/2006
 */
package com.topcoder.farm.shared.processorproperties;

import com.topcoder.farm.shared.expression.BooleanExpression;
import com.topcoder.farm.shared.expression.Expression;
import com.topcoder.farm.shared.expression.Expressions;
import com.topcoder.farm.shared.invocation.InvocationRequirements;

/**
 * RequerimentsBuilder is the base class for all RequerimentsBuilders
 * It allows you to restrict processors (requirements) based on the 
 * common properties of all processors.
 * 
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class RequerimentsBuilder {
    /**
     * Deployed module prefix. All properties related to the module
     * should be start with MODULE_PREFIX
     */
    public static final String MODULE_PREFIX = "deploy.module.";
    
    /**
     * Number of processors the processor has
     */
    public static final String NUM_OF_PROC_KEY = "processors.number";
    
    /**
     * Memory available to the processor
     */
    public static final String MEM_AVAILABLE_KEY = "memory.available";
    
    /**
     * Operating system type 
     */
    public static final String OS_TYPE_KEY = "os.type";
    
    /**
     * Linux Operating system type
     */
    public static final String OS_TYPE_LINUX = "linux";
    
    /**
     * Windows Operating system type
     */
    public static final String OS_TYPE_WINDOWS = "windows";
    
    /**
     * Deployed modules. A List<String> containig modules deployed in the processor 
     */
    public static final String DEPLOYED_MODULES_KEY = "deployed.modules";
    
    /**
     * Current Expression
     */
    private Expression expression;
    
    /**
     * Creates a new RequerimentsBuilder
     */
    public RequerimentsBuilder() {
        expression = null;
    }
    
    public void osWindows() {
        addNotNullAnd(OS_TYPE_KEY, Expressions.eq(OS_TYPE_KEY, OS_TYPE_WINDOWS));
    }
    
    public void osLinux() {
        addNotNullAnd(OS_TYPE_KEY, Expressions.eq(OS_TYPE_KEY, OS_TYPE_LINUX));
    }
    
    public void minNumberOfProcessor(int minProcessors) {
        addNotNullAnd(NUM_OF_PROC_KEY, Expressions.ge(NUM_OF_PROC_KEY, new Integer(minProcessors)));
    }
    
    public void numberOfprocessors(int numOfProcessors) {
        addNotNullAnd(NUM_OF_PROC_KEY, Expressions.eq(NUM_OF_PROC_KEY, new Integer(numOfProcessors)));
    }

    public void minMemoryAvailable(int minMem) {
        addNotNullAnd(MEM_AVAILABLE_KEY, Expressions.ge(MEM_AVAILABLE_KEY, new Integer(minMem)));
    }
    
    public void deployedModule(String moduleName) {
        andExpression(Expressions.contains(DEPLOYED_MODULES_KEY, moduleName));
    }

    public static String getModulePropertyName(String moduleName, String propertyName) {
        return getModulePrefix(moduleName) + propertyName;
    }

    public static String getModulePrefix(String moduleName) {
        return MODULE_PREFIX+moduleName+".";
    }
    
    protected void addNotNullAnd(String propertyName, Expression expression2) {
        andExpression(Expressions.and(Expressions.isSet(propertyName), expression2));
    }

    protected Expression andExpression(Expression expression2) {
        if (expression == null) {
            expression = expression2;
        } else {
            expression = Expressions.and(expression, expression2);
        }
        return expression;
    }
    
    public InvocationRequirements buildRequeriments() {
        if (expression == null) {
            return new InvocationRequirements(BooleanExpression.TRUE);
        } else {
            return new InvocationRequirements(expression);
        }
    }
}
