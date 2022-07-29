/*
 * Copyright (C) 2005 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.util.syntaxhighlighter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.topcoder.util.syntaxhighlighter.rules.BlockMatchRule;
import com.topcoder.util.syntaxhighlighter.rules.PatternMatchRule;
import com.topcoder.util.syntaxhighlighter.rules.Rule;
import com.topcoder.util.syntaxhighlighter.rules.WordMatchRule;

/**
 * <p>
 * A Category contains a set of matching rules (Rule instances) that dictate what text is to be matched. Each
 * category contains a formatting rule (TextStyle instance) which dictates how matches should be
 * highlighted/formatted. The precedence of the Category, which is defined in the XML configuration file, can be
 * set via the setPrecedence() method. In addition, the style associated with this category can be altered via the
 * setStyle() method (this method is package private, and can be set through the containing element: Language, see
 * addStyleToCategory()). This class corresponds to the category element in the XML configuration file/schema.
 * </p>
 * <p>
 * Thread Safety: This class is not thread safe. Make it thread safe using locking to prevent concurrent access.
 * </p>
 *
 * @author duner, still
 * @version 2.0
 */
public class Category implements Comparable {

    /**
     * <p>The attribute name for precedence.</p>
     */
    private static final String CATEGORY_PRECEDENCE_ATTRIBUTE_NAME = "precedence";

    /**
     * <p>The attribute name for category name.</p>
     */
    private static final String CATEGORY_NAME_ATTRIBUTE_NAME = "name";

    /**
     * <p>The attribute name for category style.</p>
     */
    private static final String CATEGORY_STYLE_ATTRIBUTE_NAME = "style";


    /**
     * <p>The name for blockMatchRule node.</p>
     */
    private static final String BLOCK_MATCH_RULE_NODE_NAME = "blockMatchRule";

    /**
     * <p>The name for blockMatchRule node.</p>
     */
    private static final String PATTERN_MATCH_RULE_NODE_NAME = "patternMatchRule";

    /**
     * <p>The name for blockMatchRule node.</p>
     */
    private static final String WORD_MATCH_RULE_NODE_NAME = "wordMatchRule";


    /**
     * <p>
     * Represents a list of Rules that belong to this category. This should contain only Rule instances. This is
     * set in the constructor and is not changed afterwards. However, its contents may be modified by the addRule
     * method or clearRules method. It is possible for a category to contain no rules serving for no purpose. This
     * will never contain any null elements.
     * </p>
     *
     */
    private final List rules;

    /**
     * <p>
     * Represents the style used to format all rules that apply under this category. That is, if a match
     * occurs under this category, then it is formatted using this TextStyle. This is set in the constructor
     * and cannot be modified by the user afterwards. However, the containing Language may use the setStyle
     * method to alter the Category's style.
     * </p>
     *
     */
    private TextStyle style;

    /**
     * <p>
     * This represents the precedence of this category within the language. This can be any non-negative
     * value. In the scope of the Language class, this value is used to determine which category should be
     * applied first. If two categories have the same precedence, then which one is executed first is
     * undefined . A lower precedence value is processed before those of higher precedence.
     * </p>
     *
     */
    private int precedence;

    /**
     * <p>
     * Represents the name of this Category instance. This is set in the constructor and is not changed
     * afterwards. It may not be null or trimed empty string.
     * </p>
     *
     */
    private final String name;

    /**
     * <p>
     * This constructor is responsible for iterating the Node provided and creating Rule instances and initializing
     * its own internal state. The node given will be the Category element node .
     * </p>
     *
     * @param node
     *            An element node corresponding to the category element in the schema. This must be the right node or
     *            runtime exception will be thrown.
     * @param styles
     *            A mapping of String names to TextStyle instances.
     * @throws ConfigurationException
     *             if any of the values in the configuration file are not right or note is null
     */
    Category(Node node, Map styles) throws ConfigurationException {
        // it is not needed to check node because check is done before this call

        // get name precedence and mapped style
        this.name = ((Element) node).getAttribute(CATEGORY_NAME_ATTRIBUTE_NAME);
        SHHelper.checkConfigString(name, "category_name");
        try {
            precedence = Integer.parseInt(((Element) node).getAttribute(CATEGORY_PRECEDENCE_ATTRIBUTE_NAME));
        } catch (NumberFormatException e) {
            throw new ConfigurationException("Illegal precedence:"
                    + ((Element) node).getAttribute(CATEGORY_PRECEDENCE_ATTRIBUTE_NAME));
        }

        if (precedence < 0) {
            throw new ConfigurationException("Illegal precedence:" + precedence);
        }
        style = (TextStyle) styles.get(((Element) node).getAttribute(CATEGORY_STYLE_ATTRIBUTE_NAME));
        if (style == null) {
            throw new ConfigurationException("Required style not found in style map.");
        }

        // rules may be empty list
        rules = new ArrayList();

        // don't use getElementsByTagName because it visit not only children nodes but also descendant Elements
        // and category node have 4 levels descendants and rule tag name is among 'blockMatchRule'
        // 'patternMatchRule' and 'wordMatchRule'.
        for (Node temp = node.getFirstChild(); temp != null; temp = temp.getNextSibling()) {
            if (temp.getNodeType() == Node.ELEMENT_NODE) {
                // new rule instance according to node name
                String ruleType =  temp.getNodeName();
                try {
                    if (ruleType.equals(BLOCK_MATCH_RULE_NODE_NAME)) {
                        rules.add(new BlockMatchRule(temp));
                    } else if (ruleType.equals(PATTERN_MATCH_RULE_NODE_NAME)) {
                        rules.add(new PatternMatchRule(temp));
                    } else if (ruleType.equals(WORD_MATCH_RULE_NODE_NAME)) {
                        rules.add(new WordMatchRule(temp));
                    } else {
                        // node name is illegal
                        throw new ConfigurationException(
                            "'blockMatchRule' or 'patternMatchRule' or 'wordMatchRule' expected.");
                    }
                } catch (IllegalArgumentException e) {
                    throw new ConfigurationException("There are some errors in the config:" + e.getMessage());
                }
            }
        }
    }

    /**
     * <p>
     * Constructor which simply initializes all parameters to their corresponding attributes.
     * </p>
     *
     *
     * @param name
     *            The name of the Category. May not be null.
     * @param rules
     *            A list of rules that pertain to this Category. May not be empty.
     * @param style
     *            The TextStyle that applies to this category. May be null to signify no formatting.
     * @param precedence
     *            The precedence assigned to this Category (lower precedence figure means it is processed
     *            first).
     * @throws NullPointerException
     *             if name is null
     * @throws IllegalArgumentException
     *             if precedence is less than 0 or if rules is empty or if any element in rules is not a Rule
     *             instance or name is empty or if rules contains any null elements.
     */
    public Category(String name, List rules, TextStyle style, int precedence) {

        SHHelper.checkString(name, "name");
        SHHelper.checkNegative(precedence, "precedence");
        SHHelper.checkList(rules, "rules", Rule.class);
        if (rules.size() == 0) {
            throw new IllegalArgumentException("Parameter 'rules' is empty.");
        }

        this.name = name;
        this.rules = new ArrayList(rules);
        this.style = style;
        this.precedence = precedence;
    }

    /**
     * <p>
     * This method returns the rules List as an array of Rules of this category.
     * </p>
     *
     * @return the rules attribute expressed as an array.
     */
    public Rule[] getRules() {
        return (Rule[]) rules.toArray(new Rule[rules.size()]);
    }

    /**
     * <p>
     * This method returns the style attribute of this category.
     * </p>
     *
     * @return the style attribute.
     */
    public TextStyle getStyle() {
        return style;
    }

    /**
     * <p>
     * This method returns the precedence attribute of this category.
     * </p>
     *
     *
     * @return the precedence attribute.
     */
    public int getPrecedence() {
        return precedence;
    }

    /**
     * <p>
     * Sets the newStyle to the style attribute. Package private mutator for style in category.
     * </p>
     *
     *
     * @param newStyle
     *            The new TextStyle that pertains to this category. This may be null to indicate no/default
     *            formatting.
     */
    void setStyle(TextStyle newStyle) {
        style = newStyle;
    }

    /**
     * <p>
     * This method adds a Rule to the rules list.
     * </p>
     *
     *
     * @param rule
     *            The Rule instance to be added to the rules list.
     * @throws NullPointerException
     *             if rule is null.
     */
    public void addRule(Rule rule) {
        SHHelper.checkNull(rule, "rule");
        rules.add(rule);
    }

    /**
     * <p>
     * This method clears the rules list.
     * </p>
     *
     */
    public void clearRules() {
        rules.clear();
    }

    /**
     * <p>
     * Compares two Categories by precedence for ordering (in the Language class).
     * </p>
     *
     * @param obj
     *            The Object to compare to this instance.
     * @return < 0 if precedence is less than the object's precedence, 0 is the two have equal precedence, > 0
     *         otherwise.
     * @throws ClassCastException
     *             if obj is not a Category instance.
     */
    public int compareTo(Object obj) {
        SHHelper.checkNull(obj, "obj");
        return precedence - ((Category) obj).getPrecedence();
    }

    /**
     * <p>
     * Sets the precedence of this category. Package private mutator for precedence in category.
     * </p>
     *
     * @param precedence
     *            The precedence of this category.
     * @throws IllegalArgumentException
     *             if precedence is less than zero.
     */
    public void setPrecedence(int precedence) {
        SHHelper.checkNegative(precedence, "precedence");
        this.precedence = precedence;
    }

    /**
     * <p>
     * This method returns name of this category.
     * </p>
     *
     * @return the name attribute.
     */
    public String getName() {
        return name;
    }


}