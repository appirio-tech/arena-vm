/*
 * Copyright (C) 2005 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.util.syntaxhighlighter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.awt.Color;
import java.awt.Font;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * <p>
 * This class provides methods for inspecting the styles and categories as well as adding new
 * TextStyle at run-time and assigning them to a Category. This class corresponds to the root
 * level element language in the XML configuration file/schema.</p>
 * <p>
 * Each language has a name, set of styles and set of categories.
 * </p>
 * <p>
 * Thread Safety: This class is not thread safe.
 * </p>
 * @author duner, still
 * @version 2.0
 */
public class Language {
    /**
     * <p>The name for categories node.</p>
     */
    private static final String CATEGORY_ROOT_NODE_NAME = "categories";

    /**
     * <p>The name for category node.</p>
     */
    private static final String CATEGORY_NODE_NAME = "category";

    /**
     * <p>The name for styles node.</p>
     */
    private static final String STYLE_ROOT_NODE_NAME = "styles";

    /**
     * <p>The name for style node.</p>
     */
    private static final String STYLE_NODE_NAME = "style";

    /**
     * <p>The attribute name for style name.</p>
     */
    private static final String STYLE_NAME_ATTRIBUTE_NAME = "name";

    /**
     * <p>The name for font node.</p>
     */
    private static final String FONT_NODE_NAME = "font";

    /**
     * <p>The name for bgcolor node.</p>
     */
    private static final String BGCOLOR_NODE_NAME = "bgcolor";

    /**
     * <p>The name for color node.</p>
     */
    private static final String COLOR_NODE_NAME = "color";

    /**
     * <p>The name for font-family node.</p>
     */
    private static final String FONT_FAMILY_NODE_NAME = "family";

    /**
     * <p>The name for font-style node.</p>
     */
    private static final String FONT_STYLE_NODE_NAME = "style";

    /**
     * <p>The name for font-size node.</p>
     */
    private static final String FONT_SIZE_NODE_NAME = "size";

    /**
     * <p>The name for color-red node.</p>
     */
    private static final String COLOR_RED_NODE_NAME = "red";

    /**
     * <p>The name for color-blue node.</p>
     */
    private static final String COLOR_BLUE_NODE_NAME = "blue";

    /**
     * <p>The name for color-green node.</p>
     */
    private static final String COLOR_GREEN_NODE_NAME = "green";

    /**
     * <p>The name for font-style:bold.</p>
     */
    private static final String FONT_BOLD = "BOLD";

    /**
     * <p>The name for font-style:plain.</p>
     */
    private static final String FONT_PLAIN = "PLAIN";

    /**
     * <p>The name for font-style:italic.</p>
     */
    private static final String FONT_ITALIC = "ITALIC";

    /**
     * <p>The max value for a color's red blue or green.</p>
     */
    private static final int MAX_COLOR_VALUE = 255;

    /**
     * <p>
     * Represents the styles contained by this Language instance. This is set in the constructor to a valid
     * Map subclass and is populated in the constructor. It may also be added to via the addStyle method and
     * accessed via the getStyles() method. This is immutable once set (though its contents may change). This
     * maps style names (i.e. TextStyle.getName()) to TextStyle instances.
     * </p>
     *
     */
    private final Map styles;

    /**
     * <p>
     * Represents the categories contained by this Language instance. This is set in the constructor to a
     * valid Map subclass and is populated in the constructor. It may be accessed via the getCategories()
     * method. This is immutable once set (though its contents may change). This maps category names (i.e.
     * Category.getName()) to Category instances.
     * </p>
     *
     */
    private final Map categories;

    /**
     * <p>
     * Represents the name of this Language instance. This is set in the constructor and is not changed
     * afterwards. It is guaranteed to be non-null.
     * </p>
     *
     */
    private final String name;

    /**
     * <p>
     * This constructor is responsible for iterating the Document provided and creating TextStyle and Category
     * instances from it. The following should be done: 1) The name attribute should be extracted from the
     * root node. This should be stored in the name attribute. 2) The constructor should handle all styles on
     * its own (see schema and sample XML). For each style element (child of styles elements), the child
     * elements should be retrieved and Color and Font instances should be created (the values are guaranteed
     * to be correct due to schema data validation). With those Color and Font instances, a TextStyle instance
     * should be created and added to the styles map. 3) For each category node (child of categories element),
     * a Category should be constructed by passing that Category element node to the Category constructor. The
     * Category constructor is responsible for parsing and handling this node and the child rule nodes. These
     * child nodes are ignored by this constructor. Add the Category, once constructed, to the categories Map.
     * </p>
     *
     *
     * @param document
     *            the document to read language configuration data from. This is guaranteed to be non-null (
     *            the constructor is package private and the caller should not pass a null document).
     * @throws ConfigurationException when config error
     *
     */
    Language(Document document) throws ConfigurationException {
        // 1) get the root element
        Element langNode = document.getDocumentElement();

        // 2) extracted the name attribute of this language
        this.name = langNode.getAttribute("name");

        SHHelper.checkConfigString(this.name, "language-name");

        // 3) get all the styles and make a style-name ~ style map
        this.styles = getStylesFromLanguageNode(langNode);

        // 4) use the style map and the config to get categories
        this.categories = getCategoriesFromLanguageNode(langNode, this.styles);
    }

    /**
     * <p>
     * This method returns the values of the styles map as an array.
     * </p>
     *
     * @return an array corresponding to the values contained in the styles map.
     */
    public TextStyle[] getStyles() {
        return (TextStyle[]) styles.values().toArray(new TextStyle[styles.size()]);
    }

    /**
     * <p>
     * This method returns the values of the categories map as an array under its natural, sorted order.
     * </p>
     *
     *
     * @return the values of the categories map as a sorted array by precedence.
     */
    public Category[] getCategories() {
        Object[] objects = categories.values().toArray(new Category[categories.size()]);

        // objects are array of category which is comparable
        Arrays.sort(objects);

        return (Category[]) objects;
    }

    /**
     * <p>
     * This method adds a TextStyle to the styles Map which allows it to be referenced in the addStyleToCategory Map.
     * </p>
     *
     * @param style
     *            The TextStyle instance to add to the Map. The name is contained by the TextStyle instance.
     * @throws NullPointerException
     *             if TextStyle is null.
     * @throws IllegalArgumentException
     *             if style.getName() is already contained in the Map.
     */
    public void addStyle(TextStyle style) {
        SHHelper.checkNull(style, "style");

        if (styles.containsKey(style.getName())) {
            throw new IllegalArgumentException("'" + style.getName() + "'is already contained in the Map.");
        }

        styles.put(style.getName(), style);
    }

    /**
     * <p>
     * Sets the given style as the TextStyle of the given category.
     * </p>
     *
     * @param categoryName
     *            The name of the category that is affected by this change. Must not be null.
     * @param styleName
     *            The name of the style that is to be used. Must not be null.
     * @throws NullPointerException
     *             if either parameter is null.
     * @throws IllegalArgumentException
     *             if either parameter cannot be found in their respective maps.
     */
    public void addStyleToCategory(String categoryName, String styleName) {
        SHHelper.checkString(categoryName, "categoryName");
        SHHelper.checkString(styleName, "styleName");

        TextStyle style = (TextStyle) styles.get(styleName);
        Category category = (Category) (categories.get(categoryName));

        if (style == null) {
            throw new IllegalArgumentException(
                "styleName cannot be found in their respective maps.");
        }
        if (category == null) {
            throw new IllegalArgumentException(
                "categoryName cannot be found in their respective maps.");
        }
        category.setStyle(style);
    }

    /**
     * <p>
     * Removes style information from the given category.
     * </p>
     *
     *
     * @param categoryName
     *            The name of the category that is affected by this change. Must not be null.
     * @throws NullPointerException
     *             if categoryName is null.
     * @throws IllegalArgumentException
     *             if categoryName is not contained within the categories Map.
     */
    public void removeStyleFromCategory(String categoryName) {
        SHHelper.checkString(categoryName, "categoryName");

        Category category = getCategory(categoryName);

        if (category == null) {
            throw new IllegalArgumentException("Category doesn't exist.");
        }

        // set null style is legal
        category.setStyle(null);
    }

    /**
     * <p>
     * This method returns name of this language.
     * </p>
     *
     *
     * @return the name attribute.
     */
    public String getName() {
        return name;
    }

    /**
     * <p>
     * This method returns a category with the given name from the Categories map. This method will return null if the
     * given category does not exist.
     * </p>
     *
     *
     * @param categoryName
     *            the categoryName of the category to retrieve.
     * @return A Category instance with the given name, null if no such Category exists in the map.
     * @throws NullPointerException
     *             if categoryName is null.
     * @throws IllegalArgumentException
     *             if categoryName is a trimmed, empty string.
     */
    public Category getCategory(String categoryName) {
        SHHelper.checkString(categoryName, "categoryName");

        return (Category) categories.get(categoryName);
    }

    /**
     * <p>
     * This method load a map of categories from language cofig. The caller should ensure
     * langNode is element and not null.
     * </p>
     * @param langNode the root node of  language cofig.
     * @param styles the name<->style map.
     * @return the map of categories of this language.
     * @throws ConfigurationException if config is not right or no category obtained
     */
    private static Map getCategoriesFromLanguageNode(Node langNode, Map styles)
        throws ConfigurationException {

        Map map = new HashMap();

        // get categories node
        Element categoriesNode = getSingleChildElementByName((Element) langNode, CATEGORY_ROOT_NODE_NAME);

        // get each category node, new a Category instance with the node

        NodeList categoryNodes = categoriesNode.getElementsByTagName(CATEGORY_NODE_NAME);
        for (int i = 0; i < categoryNodes.getLength(); ++i) {
            Node categoryNode = categoryNodes.item(i);
            if (categoryNode.getParentNode() != categoriesNode) {
                throw new ConfigurationException("some category nodes are not the categories's children");
            }
            Category category;
            category = new Category(categoryNode, styles);
            map.put(category.getName(), category);
        }

        if (map.size() <= 0) {
            throw new ConfigurationException("No categories defined.");
        }

        return map;
    }

    /**
     * <p>This method load a map of styles from language cofig.he caller should ensure
     * langNode is element and not null.</p>
     *
     * @return the mapping of color ids to StyleAttribute instances.
     * @param langNode the node from its children to parse style nodes.
     * @throws ConfigurationException if it fails to parse styles from the node.
     */
    private static Map getStylesFromLanguageNode(Node langNode)
        throws ConfigurationException {
        // the result mapping
        Map map = new HashMap();


        // get styles node
        Element styleRoot = getSingleChildElementByName((Element) langNode, STYLE_ROOT_NODE_NAME);

        // for each style
        NodeList styleNodes = styleRoot.getElementsByTagName(STYLE_NODE_NAME);
        for (int i = 0; i < styleNodes.getLength(); ++i) {
            Node styleNode = styleNodes.item(i);
            // NOTE: style node conflicts with font's style node
            // so if styleNode is not the right child is legal(it may be a font style node)
            if (styleNode.getParentNode() != styleRoot) {
                continue;
            }
            String name = ((Element) styleNode).getAttribute(STYLE_NAME_ATTRIBUTE_NAME);
            SHHelper.checkConfigString(name, "style-name");
            Color color = null;
            Color bgColor = null;
            Font font = null;

            // get color and bgcolor and font if it's configed

            // don't use getElementsByTagName because it visit not only children nodes but also descendant Elements
            // and style node have 4 levels descendants and rule tag name is among 'color' 'bgcolor' and 'font'

            for (Node temp = styleNode.getFirstChild(); temp != null; temp = temp.getNextSibling()) {
                if (temp.getNodeType() == Node.ELEMENT_NODE) {
                    if (temp.getNodeName().equals(COLOR_NODE_NAME)) {
                        if (color != null) {
                            throw new ConfigurationException("color for style is duplicate.");
                        }
                        color = parseColor(temp);
                    } else if (temp.getNodeName().equals(BGCOLOR_NODE_NAME)) {
                        if (bgColor != null) {
                            throw new ConfigurationException("bgcolor for style is duplicate.");
                        }
                        bgColor = parseColor(temp);
                    } else if (temp.getNodeName().equals(FONT_NODE_NAME)) {
                        if (font != null) {
                            throw new ConfigurationException("font for style is duplicate.");
                        }
                        font = parseFont(temp);
                    }
                }
            }
            // color must be specfied
            if (color == null) {
                throw new ConfigurationException("color for style must be contained.");
            }
            // create a color style attribute and add it to the result map
            map.put(name, new TextStyle(name, font, color, bgColor));
        }


        if (map.size() <= 0) {
            throw new ConfigurationException("No styles defined.");
        }

        // return the result mapping
        return map;
    }

    /**
     * <p>Parse the color nodes from the given node, the caller should ensure the node is element and not null.
     * </p>
     * @param node the node to be parse
     * @return the passed color
     * @throws ConfigurationException if it fails to parse colors from the node.
     */
    private static Color parseColor(Node node) throws ConfigurationException {
        // parse the red, green and blue values

        int r = SHHelper.getNodeInteger(getSingleChildElementByName((Element) node, COLOR_RED_NODE_NAME));
        int g = SHHelper.getNodeInteger(getSingleChildElementByName((Element) node, COLOR_GREEN_NODE_NAME));
        int b = SHHelper.getNodeInteger(getSingleChildElementByName((Element) node, COLOR_BLUE_NODE_NAME));

        // check if color is in [0-255]
        checkColorValue(r);
        checkColorValue(g);
        checkColorValue(b);

        return new Color(r, g, b);
    }

    /**
     * <p>
     * Parse the font nodes from the given node, the caller should ensure the node is element and not null.
     * </p>
     * @param node the node to be parse
     * @return the passed font
     * @throws ConfigurationException if it fails to parse font from the node.
     */
    private static Font parseFont(Node node) throws ConfigurationException {
        // string of font-family(must be specified)
        String fontFamily = SHHelper.getNodeText(getSingleChildElementByName((Element) node, FONT_FAMILY_NODE_NAME));

        // integer of font-size(must be specified)
        int size = SHHelper.getNodeInteger(getSingleChildElementByName((Element) node, FONT_SIZE_NODE_NAME));

        // Font.PLAIN is 0
        // use | to make combination of font-style like "bold/italic" have effect
        int type = Font.PLAIN;

        // font-style is optional
        NodeList list = ((Element) node).getElementsByTagName(FONT_STYLE_NODE_NAME);
        if (list.getLength() > 0) {
            if (list.getLength() > 1) {
                throw new ConfigurationException("font-style property is duplicate.");
            }
            Node fontStyleNode = list.item(0);
            if (fontStyleNode.getParentNode() != node) {
                throw new ConfigurationException("font-style property should be right under the font node.");
            }
            String fontStyle = SHHelper.getNodeText(fontStyleNode);
            // split combined apart
            String[] fontStyles = fontStyle.split("\\|");
            for (int i = 0; i < fontStyles.length; i++) {
                type |= getFontType(fontStyles[i]);
            }
        }
        // if size <= 0, size is not properly set
        if (size <= 0) {
            throw new ConfigurationException("Property 'size' in font must be larger than 0.");
        }

        return new Font(fontFamily, type, size);
    }

    /**
     * <p>
     * This method checks if  value is a valid color value([0-255]).
     * </p>
     * @param value the color value to be checked.
     * @throws ConfigurationException if check failed.
     */
    private static void checkColorValue(int value) throws ConfigurationException {
        if ((value >= 0) && (value <= MAX_COLOR_VALUE)) {
            return;
        }
        if (value < 0) {
            throw new ConfigurationException("Color with negative value is illegal.");
        }
        if (value > MAX_COLOR_VALUE) {
            throw new ConfigurationException("Color value is too large.");
        }
    }

    /**
     * <p>
     * This method convert a font-type string to a font-type, for instance, return Font.Plain if "plain" is passed
     * in.
     * </p>
     * @param type the string of font type.
     * @return the accordingly font-type.
     * @throws ConfigurationException if type is not among "plain" "bold" and "italic".
     */
    private static int getFontType(String type) throws ConfigurationException {
        type = type.trim();
        if (type.equalsIgnoreCase(FONT_PLAIN)) {
            return Font.PLAIN;
        }

        if (type.equalsIgnoreCase(FONT_ITALIC)) {
            return Font.ITALIC;
        }

        if (type.equalsIgnoreCase(FONT_BOLD)) {
            return Font.BOLD;
        }

        throw new ConfigurationException("Font-type not found.");
    }
    /**
     * <p>Checks if parent have only one child element named elementName and return the child element. The caller
     * should ensure parent and elementName not be null and elementName not be empty.</p>
     * @param parent the parent from which to find element.
     * @param elementName the element name of the expected element.
     * @return the child element named elementName of parent.
     * @throws ConfigurationException if the element is not properly found.
     */
    private static Element getSingleChildElementByName(Element parent, String elementName)
        throws ConfigurationException {
        NodeList list = parent.getElementsByTagName(elementName);
        if (list.getLength() > 1) {
            throw new ConfigurationException(parent.getNodeName() + " have more than one " + elementName
                + " elements.");
        } else if (list.getLength() == 0) {
            throw new ConfigurationException(parent.getNodeName() + " don't have " + elementName
                + " element.");
        }
        Node node = list.item(0);
        if (node.getParentNode() == parent) {
            return (Element) node;
        } else {
            throw new ConfigurationException(parent.getNodeName() + " don't have child " + elementName
                + " element.");
        }
    }
}
