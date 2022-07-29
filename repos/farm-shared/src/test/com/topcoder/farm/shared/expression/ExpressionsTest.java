/*
 * ExpressionsTest
 * 
 * Created 07/28/2006
 */
package com.topcoder.farm.shared.expression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import junit.framework.TestCase;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ExpressionsTest extends TestCase {
    private Map data = new HashMap();
    private Collection collection = new ArrayList();
    private Collection collectionNull = new ArrayList();
    private Pattern p1;
    private Pattern p3;
    
    public ExpressionsTest() {
        collection.add("a");
        collection.add("b");
        collection.add("c");

        collectionNull.add("a");
        collectionNull.add("b");
        collectionNull.add("c");
        collectionNull.add(null);

        data.put("set", Boolean.TRUE);
        data.put("n1", "1");
        data.put("n2", "2");
        data.put("la", "a");
        data.put("lb", "b");
        data.put("null", null);
        data.put("collection", collection);
        data.put("collectionNull", collectionNull);
        
        p1 = Pattern.compile("1");
        p3 = Pattern.compile("3");
        
        data.put("p1",p1);
        data.put("p3",p3);
    }
    
    
    /**
     * Test equal expression between a property and a given value
     */
    public void testEqual() throws Exception {
        assertTrue(Expressions.eq("n1", "1").eval(data));
        assertTrue(Expressions.eq("null", null).eval(data));
        assertFalse(Expressions.eq("n1", "2").eval(data));
        assertFalse(Expressions.eq("null", "1").eval(data));
        assertFalse(Expressions.eq("n1", null).eval(data));
    }

    /**
     * Test equal expression between two properties
     */
    public void testProps() throws Exception {
        assertTrue(Expressions.eqProps("n1", "n1").eval(data));
        assertTrue(Expressions.eqProps("null", "null").eval(data));
        assertFalse(Expressions.eqProps("n1", "n2").eval(data));
        assertFalse(Expressions.eqProps("n2", "n1").eval(data));
        assertFalse(Expressions.eqProps("null", "n1").eval(data));
        assertFalse(Expressions.eqProps("n1", "null").eval(data));
        
    }
    
    
    /**
     * Test compare expression between a property and a given value
     */
    public void testCompare() throws Exception {
        assertTrue(Expressions.lt("n1", "2").eval(data));
        assertTrue(Expressions.gt("n2", "1").eval(data));
        assertTrue(Expressions.le("n1", "2").eval(data));
        assertTrue(Expressions.ge("n2", "1").eval(data));
        assertTrue(Expressions.le("n1", "1").eval(data));
        assertTrue(Expressions.ge("n1", "1").eval(data));
        
        assertFalse(Expressions.lt("n2", "1").eval(data));
        assertFalse(Expressions.gt("n1", "2").eval(data));
        assertFalse(Expressions.le("n2", "1").eval(data));
        assertFalse(Expressions.ge("n1", "2").eval(data));
        
    }

    /**
     * Test compare expression between 2 properties
     */
    public void testCompareProps() throws Exception {
        assertTrue(Expressions.ltProps("n1", "n2").eval(data));
        assertTrue(Expressions.gtProps("n2", "n1").eval(data));
        assertTrue(Expressions.leProps("n1", "n2").eval(data));
        assertTrue(Expressions.geProps("n2", "n1").eval(data));
        assertTrue(Expressions.leProps("n1", "n1").eval(data));
        assertTrue(Expressions.geProps("n1", "n1").eval(data));
        
        assertFalse(Expressions.ltProps("n2", "n1").eval(data));
        assertFalse(Expressions.gtProps("n1", "n2").eval(data));
        assertFalse(Expressions.leProps("n2", "n1").eval(data));
        assertFalse(Expressions.geProps("n1", "n2").eval(data));
    }
    
    
    
    /**
     * Test Contains expression between a property and a given value
     */
    public void testContains() throws Exception {
        assertTrue(Expressions.contains("collection", "a").eval(data));
        assertTrue(Expressions.contains("collection", "b").eval(data));
        assertTrue(Expressions.contains("collectionNull", null).eval(data));
        assertFalse(Expressions.contains("collection", "x").eval(data));
        assertFalse(Expressions.contains("collection", null).eval(data));
        assertFalse(Expressions.contains("null", "a").eval(data));
        assertFalse(Expressions.contains("null", null).eval(data));
    }
    
    /**
     * Test Contains expression between 2 properties
     */
    public void testContainsProps() throws Exception {
        assertTrue(Expressions.containsProps("collection", "la").eval(data));
        assertTrue(Expressions.containsProps("collection", "lb").eval(data));
        assertTrue(Expressions.containsProps("collectionNull", "null").eval(data));
        assertFalse(Expressions.containsProps("collection", "n1").eval(data));
        assertFalse(Expressions.containsProps("collection", "null").eval(data));
        assertFalse(Expressions.containsProps("null", "la").eval(data));
        assertFalse(Expressions.containsProps("null", "null").eval(data));
    }    
    
    /**
     * Test In expression between a property and a given collection
     */
    public void testIn() throws Exception {
        assertTrue(Expressions.in("la", collection).eval(data));
        assertTrue(Expressions.in("lb", collection).eval(data));
        assertTrue(Expressions.in("null", collectionNull).eval(data));
        assertFalse(Expressions.in("n1", collection).eval(data));
        assertFalse(Expressions.in("null", collection).eval(data));
        assertFalse(Expressions.in("la", null).eval(data));
        assertFalse(Expressions.in("null", null).eval(data));
    }
    
    
    /**
     * Test In expression between 2 properties
     */
    public void testInProps() throws Exception {
        assertTrue(Expressions.inProps("la", "collection").eval(data));
        assertTrue(Expressions.inProps("lb", "collection").eval(data));
        assertTrue(Expressions.inProps("null", "collectionNull").eval(data));
        assertFalse(Expressions.inProps("n1", "collection").eval(data));
        assertFalse(Expressions.inProps("null", "collection").eval(data));
        assertFalse(Expressions.inProps("la", "null").eval(data));
        assertFalse(Expressions.inProps("null", "null").eval(data));
    }
    
    /**
     * Test Matches expression between a property and a given Pattern
     */
    public void testMatches() throws Exception {
        Pattern p1 = Pattern.compile("1");
        Pattern p3 = Pattern.compile("3");
        assertTrue(Expressions.matches("n1", p1).eval(data));
        assertFalse(Expressions.matches("n2", p1).eval(data));
        assertFalse(Expressions.matches("null", p1).eval(data));
        assertFalse(Expressions.matches("n1", p3).eval(data));
        assertFalse(Expressions.matches("null", null).eval(data));
    }
    
    /**
     * Test Matches expression between 2 properties
     */
    public void testPropsMatches() throws Exception {
        assertTrue(Expressions.matchesProps("n1", "p1").eval(data));
        assertFalse(Expressions.matchesProps("n2", "p1").eval(data));
        assertFalse(Expressions.matchesProps("null", "p1").eval(data));
        assertFalse(Expressions.matchesProps("n1", "p3").eval(data));
        assertFalse(Expressions.matchesProps("null", "null").eval(data));
    }
    
    
    /**
     * Test IsSet expression
     */
    public void testIsSet() throws Exception {
        assertTrue(Expressions.isSet("n1").eval(data));
        assertFalse(Expressions.isSet("n4").eval(data));
        assertFalse(Expressions.isSet("null").eval(data));
    }
    
    /**
     * Test And Expression
     */
    public void testAnd() throws Exception {
        assertTrue(Expressions.and(trueExp(), trueExp()).eval(data));
        assertFalse(Expressions.and(falseExp(), trueExp()).eval(data));
        assertFalse(Expressions.and(trueExp(), falseExp()).eval(data));
        assertFalse(Expressions.and(falseExp(), falseExp()).eval(data));
    }
    
    public void testMultiAnd() throws Exception {
        assertTrue(Expressions.and(Expressions.and(trueExp(), trueExp()), trueExp()).eval(data));
        assertFalse(Expressions.and(Expressions.and(trueExp(), trueExp()), falseExp()).eval(data));
        assertFalse(Expressions.and(Expressions.and(falseExp(), trueExp()), trueExp()).eval(data));
        assertTrue(Expressions.and(Expressions.and(trueExp(), trueExp()), Expressions.and(trueExp(), trueExp())).eval(data));
        assertTrue(Expressions.and(Expressions.and(trueExp(), trueExp()), Expressions.and(Expressions.and(trueExp(), trueExp()), trueExp())).eval(data));
        assertTrue(Expressions.and(Expressions.and(Expressions.and(trueExp(), trueExp()), trueExp()),Expressions.and(trueExp(), trueExp())).eval(data));
    }
    
    
    /**
     * Test Or Expression
     */
    public void testOr() throws Exception {
        assertTrue(Expressions.or(trueExp(), trueExp()).eval(data));
        assertTrue(Expressions.or(falseExp(), trueExp()).eval(data));
        assertTrue(Expressions.or(trueExp(), falseExp()).eval(data));
        assertFalse(Expressions.or(falseExp(), falseExp()).eval(data));
    }
    
    /**
     * Test Not Expression
     */
    public void testNot() throws Exception {
        assertTrue(Expressions.not(falseExp()).eval(data));
        assertFalse(Expressions.not(trueExp()).eval(data));
    }

    /**
     * @return an expression that evaluates to True
     */
    private Expression trueExp() {
        return Expressions.eq("n1", "1");
    }

    /**
     * Returns a expression that evaluates to False
     */
    private Expression falseExp() {
        return Expressions.eq("n1", "2");
    }
}
