/*
 * Expressions
 * 
 * Created 07/28/2006
 */
package com.topcoder.farm.shared.expression;

import java.util.Collection;
import java.util.regex.Pattern;

import com.topcoder.farm.shared.expression.function.CompareFunction;
import com.topcoder.farm.shared.expression.function.ContainsFunction;
import com.topcoder.farm.shared.expression.function.EqualFunction;
import com.topcoder.farm.shared.expression.function.InFunction;
import com.topcoder.farm.shared.expression.function.RegExpMatchFunction;

/**
 * Factory class for building Expressions
 * 
 * It is recommended to use this factory class
 * to create built-in expressions
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class Expressions {
    
    /**
     * Creates and returns an expression that checks that
     * the property value is equal to the value
     *  
     * @param propertyName The property name 
     * @param value The value
     * 
     * @return The created expression
     */
    public static Expression eq(String propertyName, Object value) {
        return new PropertyExpression(propertyName, value, EqualFunction.INSTANCE);
    }

    /**
     * Creates and returns an expression that checks that
     * the property value is less than value
     *  
     * The property value must implement Comparable interface
     *  
     * @param propertyName The property name 
     * @param value The value
     * 
     * @return The created expression
     */
    public static Expression lt(String propertyName, Object value) {
        return new PropertyExpression(propertyName, value, CompareFunction.LT);
    }

    /**
     * Creates and returns an expression that checks that
     * the property value is greater than value
     * 
     * The property value must implement Comparable interface
     * 
     * @param propertyName The property name 
     * @param value The value
     * 
     * @return The created expression
     */
    public static Expression gt(String propertyName, Object value) {
        return new PropertyExpression(propertyName, value, CompareFunction.GT);
    }
    
    /**
     * Creates and returns an expression that checks that
     * the property value is less or equal than value
     * 
     * The property value must implement Comparable interface
     *       
     * @param propertyName The property name 
     * @param value The value
     * 
     * @return The created expression
     */
    public static Expression le(String propertyName, Object value) {
        return new PropertyExpression(propertyName, value, CompareFunction.LE);
    }
    
    /**
     * Creates and returns an expression that checks that
     * the property value is greater or equal than value
     * 
     * The property value must implement Comparable interface
     *  
     * @param propertyName The property name 
     * @param value The value
     * 
     * @return The created expression
     */
    public static Expression ge(String propertyName, Object value) {
        return new PropertyExpression(propertyName, value, CompareFunction.GE);
    }

    /**
     * Creates and returns an expression that checks that
     * the property value is conatined in value collection
     *  
     * @param propertyName The property name 
     * @param value The value collection
     * 
     * @return The created expression
     */
    public static Expression in(String propertyName, Collection value) {
        return new PropertyExpression(propertyName, value, InFunction.INSTANCE);
    }

    /**
     * Creates and returns an expression that checks that
     * the property value contains the value
     * 
     * The property value must be implement Collection interface
     *  
     * @param propertyName The property name 
     * @param value The value collection
     * 
     * @return The created expression
     */
    public static Expression contains(String propertyName, Object value) {
        return new PropertyExpression(propertyName, value, ContainsFunction.INSTANCE);
    }
    
    /**
     * Creates and returns an expression that checks that
     * the property value matches the pattern
     * 
     * The property value must implement CharSequence interface
     *  
     * @param propertyName The property name 
     * @param pattern The pattern 
     * 
     * @return The created expression
     */
    public static Expression matches(String propertyName, Pattern pattern) {
        return new PropertyExpression(propertyName, pattern, RegExpMatchFunction.INSTANCE);
    }

    
    
    /**
     * Creates and returns an expression that checks that
     * the value of property propertyName1 is equal to the value of 
     * property propertyName2
     *  
     * @param propertyName1 The name of the first property 
     * @param propertyName2 The name of the second property
     * 
     * @return The created expression
     */
    public static Expression eqProps(String propertyName1, String propertyName2) {
        return new PropertyToPropertyExpression(propertyName1, propertyName2, EqualFunction.INSTANCE);
    }

    /**
     * Creates and returns an expression that checks that
     * the value of property propertyName1 is less than the value of 
     * property propertyName2
     * 
     * The property value must implement Comparable interface
     *  
     * @param propertyName1 The name of the first property 
     * @param propertyName2 The name of the second property
     *  
     * @return The created expression
     */
    public static Expression ltProps(String propertyName1, String propertyName2) {
        return new PropertyToPropertyExpression(propertyName1, propertyName2, CompareFunction.LT);
    }

    /**
     * Creates and returns an expression that checks that
     * the value of property propertyName1 is greater than the value of 
     * property propertyName2
     * 
     * The property value must implement Comparable interface
     *  
     * @param propertyName1 The name of the first property 
     * @param propertyName2 The name of the second property
     *  
     * @return The created expression
     */
    public static Expression gtProps(String propertyName1, String propertyName2) {
        return new PropertyToPropertyExpression(propertyName1, propertyName2, CompareFunction.GT);
    }
    
    /**
     * Creates and returns an expression that checks that
     * the value of property propertyName1 is less or equal than the value of 
     * property propertyName2
     * 
     * The property value must implement Comparable interface
     *  
     * @param propertyName1 The name of the first property 
     * @param propertyName2 The name of the second property
     *  
     * @return The created expression
     */
    public static Expression leProps(String propertyName1, String propertyName2) {
        return new PropertyToPropertyExpression(propertyName1, propertyName2, CompareFunction.LE);
    }
    
    /**
     * Creates and returns an expression that checks that
     * the value of property propertyName1 is greater or equal than the value of 
     * property propertyName2
     * 
     * The property value must implement Comparable interface
     *  
     * @param propertyName1 The name of the first property 
     * @param propertyName2 The name of the second property
     *  
     * @return The created expression
     */
    public static Expression geProps(String propertyName1, String propertyName2) {
        return new PropertyToPropertyExpression(propertyName1, propertyName2, CompareFunction.GE);
    }

    /**
     * Creates and returns an expression that checks that
     * the value of property propertyName1 is contained in the collection of 
     * property propertyName2
     * 
     * The property value of propertyName2 must implement Collection interface
     *  
     * @param propertyName1 The name of the first property 
     * @param propertyName2 The name of the second property
     *  
     * @return The created expression
     */
    public static Expression inProps(String propertyName1, String propertyName2) {
        return new PropertyToPropertyExpression(propertyName1, propertyName2, InFunction.INSTANCE);
    }

    /**
     * Creates and returns an expression that checks that
     * the value of property propertyName1 contains the value of
     * property propertyName2
     * 
     * The property value of propertyName1 must implement Collection interface
     *   
     * @param propertyName1 The name of the first property 
     * @param propertyName2 The name of the second property
     * 
     * @return The created expression
     */
    public static Expression containsProps(String propertyName1, String propertyName2) {
        return new PropertyToPropertyExpression(propertyName1, propertyName2, ContainsFunction.INSTANCE);
    }
    
    /**
     * Creates and returns an expression that checks that
     * the value of property propertyName1 matches the pattern contained in 
     * property propertyName2
     * 
     * The property value of propertyName1 must implement CharSequence interface
     * The property value of propertyName2 must be a Pattern instance
     *  
     * @param propertyName1 The name of the first property 
     * @param propertyName2 The name of the second property
     * 
     * @return The created expression
     */
    public static Expression matchesProps(String propertyName1, String propertyName2) {
        return new PropertyToPropertyExpression(propertyName1, propertyName2, RegExpMatchFunction.INSTANCE);
    }
    
    /**
     * Creates and returns an expression that checks if a given property is set
     * 
     * @param propertyName The property name 
     * 
     * @return The created expression
     */
    public static Expression isSet(String propertyName) {
        return not(new PropertyExpression(propertyName, null, EqualFunction.INSTANCE));
    }
    
    
   
    /**
     * Creates and returns an expression that evaluates
     * to the disjunction of two expressions
     * 
     * @param expression1 The left-side expression of the OR 
     * @param expression2 The right-side expression of the OR 
     * 
     * @return The created expression
     */
    public static Expression or(Expression expression1, Expression expression2) {
        return new OrExpression(expression1, expression2);
    }
    
    /**
     * Creates and returns an expression that evaluates
     * to the conjunction of two expressions.<p>
     * 
     * @param expression1 The left-side expression of the AND 
     * @param expression2 The right-side expression of the AND 
     * 
     * @return An expression representing the conjunction of both expressions
     */
    public static Expression and(Expression expression1, Expression expression2) {
        return new AndExpression(expression1, expression2);
    }
    
    /**
     * Creates and returns an expression that evaluates
     * to the negation of an expression
     * 
     * @param expression The expression to negate 
     * 
     * @return The created expression
     */
    public static Expression not(Expression expression) {
        return new NotExpression(expression);
    }
}
