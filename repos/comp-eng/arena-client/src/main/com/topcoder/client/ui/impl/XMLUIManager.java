package com.topcoder.client.ui.impl;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIComponentException;
import com.topcoder.client.ui.UIManager;
import com.topcoder.client.ui.UIManagerConfigurationException;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.UIPageException;
import com.topcoder.client.ui.UIPageNotFoundException;
import com.topcoder.client.ui.UIPropertyValueParser;

/**
 * Defines a class which implements the UIManager interface. It provides the feature to create a UI pages and components
 * from an XML file. It uses SAX parser to parse the XML in order to save memory.<br>
 * The XML file is organized as follows.<br>
 * The root element must be 'scheme'. It has mandatory attributes 'name' and 'description', and optional attribute
 * 'classpath'. 'name' and 'description' defines the name and description of the UI theme. 'classpath' defines the
 * additional classpath used by this theme. The classpath can specify several locations seperated by ';' character. It
 * can be a Jar file or a directory. If the location is relative, it is relative to the directory of the XML file.<br>
 * Under 'scheme' element, there can be one or multiple 'page' elements defining all UI pages in the theme. The 'page'
 * element has two mandatory attributes 'name' and 'class'. 'name' defines the unique name of the UI page in the theme,
 * and 'class' defines the qualified class name of the UIPage instance used.<br>
 * Under 'page' element, there can be one or multiple 'component' elements defining UI components nested directly under
 * the UI page. The 'component' element has two mandatory attributes 'name' and 'class'. 'name' defines the name of the
 * UI component in the UI page. It does not need to be unique even within the UI page. 'class' defines the qualified
 * class name of the UIComponent instance used.<br>
 * Under 'component' element, there can be 'component', 'constraints' and 'property' elements. The 'component' element
 * defines the direct child component. 'constraints' defines the GridBagConstraints of the component. It has optional
 * attributes 'gridx', 'gridy', 'gridwidth', 'gridheight', 'anchor', 'fill', 'ipadx', 'ipady', 'insets', 'weightx', and
 * 'weighty'. The detailed description of these attributes can be found in GridBadConstraints class. The value of the
 * attributes can be either constants defined in GridBagConstraints class (e.g. 'REMAINDER') or numbers. 'insets' is
 * defined by four integers separated by ',' character. The 'property' element defines the property of the component. It
 * has two mandatory attributes 'name' and 'value', and an optional attribute 'parser'. 'name' and 'value' defines the
 * property name and value to be set, while 'parser' defines the UIPropertyValueParser class used to parse the value. If
 * the parser is not specified, the value is directly used as a string to be set. The property is set via
 * <code>setProperty</code> method of the UIComponent instance.<br>
 * The scheme can also call static methods defined in Java classes to do additional initialization. It is done via
 * 'call' element nested anywhere. It has mandatory attributes 'class' and 'method'. The method must be public static.
 * The UIManager is passed in as an argument.<br>
 * <br>
 * Here is an example:<br>
 * &lt;?xml version="1.0" encoding="utf-8"?&gt;<br>
 * &lt;scheme name="Test 1" description="Test UI Design Skin 1" classpath="Test1.jar"&gt;<br>
 * &nbsp;&lt;call class="com.topcoder.client.ui.impl.XMLSwingDefaults" method="set"/&gt;<br>
 * &nbsp;&lt;page name="main" class="com.topcoder.client.ui.impl.UIPageImpl"&gt;<br>
 * &nbsp;&nbsp;&lt;component name="frame" class="com.topcoder.client.ui.test.view.UIFrame"&gt;<br>
 * &nbsp;&nbsp;&nbsp;&lt;property name="onclose" value="EXIT" /&gt;<br>
 * &nbsp;&nbsp;&nbsp;&lt;component name="scheme_select" class="com.topcoder.client.ui.test.view.UIComboBox"&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;constraints gridx="0" gridy="0" gridwidth="remainder" /&gt;<br>
 * &nbsp;&nbsp;&nbsp;&lt;/component&gt;<br>
 * &nbsp;&nbsp;&lt;/component&gt;<br>
 * &nbsp;&lt;/page&gt;<br>
 * &lt;scheme&gt;<br>
 * 
 * @version 1.0
 * @author visualage
 */
public class XMLUIManager implements UIManager {
    // Elements
    private static final String SCHEME_ELEMENT = "scheme";

    private static final String PAGE_ELEMENT = "page";

    private static final String COMPONENT_ELEMENT = "component";

    private static final String CONSTRAINTS_ELEMENT = "constraints";

    private static final String PROPERTY_ELEMENT = "property";

    private static final String ACTION_ELEMENT = "action";

    private static final String PARAM_ELEMENT = "param";

    private static final String COMPONENTREF_ELEMENT = "componentref";

    private static final String CALL_ELEMENT = "call";

    // Attributes
    private static final String NAME_ATTR = "name";

    private static final String DESCRIPTION_ATTR = "description";

    private static final String VALUE_ATTR = "value";

    private static final String VALUE_PARSER_ATTR = "parser";

    private static final String CLASSPATH_ATTR = "classpath";

    private static final String CLASS_NAME_ATTR = "class";

    private static final String METHOD_NAME_ATTR = "method";

    private static final String CONSTRAINTS_GRIDX_ATTR = "gridx";

    private static final String CONSTRAINTS_GRIDY_ATTR = "gridy";

    private static final String CONSTRAINTS_GRIDWIDTH_ATTR = "gridwidth";

    private static final String CONSTRAINTS_GRIDHEIGHT_ATTR = "gridheight";

    private static final String CONSTRAINTS_ANCHOR_ATTR = "anchor";

    private static final String CONSTRAINTS_FILL_ATTR = "fill";

    private static final String CONSTRAINTS_IPADX_ATTR = "ipadx";

    private static final String CONSTRAINTS_IPADY_ATTR = "ipady";

    private static final String CONSTRAINTS_INSETS_ATTR = "insets";

    private static final String CONSTRAINTS_WEIGHTX_ATTR = "weightx";

    private static final String CONSTRAINTS_WEIGHTY_ATTR = "weighty";

    /** Represents the map from page names to pages. */
    private Map pages = new HashMap();

    /** Represents the theme name. */
    private String name;

    /** Represents the theme description. */
    private String description;

    /** Represents the XML configuration file. */
    private URL xmlConfig;

    /** Represents the class loader used to create UIPage, UIComponent and UIPropertyValueParser instances. */
    private ClassLoader loader = null;

    private UIPage currentPage;

    /**
     * Defines the SAX parser content handler to parse the XML configuration file.
     * 
     * @version 1.0
     * @author visualage
     */
    private class ConfigurationContentHandler implements ContentHandler {
        private Stack elements = new Stack();

        private Stack constraints = new Stack();

        private URL dir;

        private String pageName;

        private boolean skipping = false;

        private Locator locator = null;

        private class PerformAction {
            private String name;

            private List params = new ArrayList();
        }

        private String getMandatoryAttribute(Attributes atts, String name) throws SAXException {
            String value = atts.getValue(name);

            if (value == null) {
                throw new SAXParseException("Attribute " + name + " must exist.", locator);
            }

            return value;
        }

        private int parseConstraintsConstants(String name) {
            try {
                return GridBagConstraints.class.getField(name.toUpperCase()).getInt(null);
            } catch (Exception e) {
                throw new IllegalArgumentException("The value is not a valid GridBagConstraints constant.");
            }
        }

        private Insets parseIntsets(String value) {
            String[] values = value.split(",");

            if (values.length != 4) {
                throw new IllegalArgumentException("The value is not a valid inset value.");
            }

            return new Insets(Integer.parseInt(values[0]), Integer.parseInt(values[1]), Integer.parseInt(values[2]),
                Integer.parseInt(values[3]));
        }

        private ConfigurationContentHandler(URL dir, String pageName) {
            this.dir = dir;
            this.pageName = pageName;
        }

        public void characters(char[] ch, int start, int length) throws SAXException {
        }

        public void endDocument() throws SAXException {
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (COMPONENT_ELEMENT.equals(localName) && !skipping) {
                // When the component is an element, pop it and add to its parent
                UIComponent component = (UIComponent) elements.pop();
                GridBagConstraints constraint = (GridBagConstraints) constraints.pop();
                Object parent = elements.peek();

                if (parent instanceof UIComponent) {
                    try {
                        ((UIComponent) parent).addChild(component, constraint);
                    } catch (UIComponentException e) {
                        throw new SAXParseException("The parent component cannot have child.", locator, e);
                    }
                }
            } else if (ACTION_ELEMENT.equals(localName) && !skipping) {
                PerformAction action = (PerformAction) elements.pop();
                UIComponent component = (UIComponent) elements.peek();

                try {
                    component.performAction(action.name, action.params.toArray());
                } catch (UIComponentException e) {
                    throw new SAXParseException("Performing action failed.", locator, e);
                }
            } else {
                // Otherwise, pop directly
                if (!skipping) {
                    elements.pop();
                } else if (PAGE_ELEMENT.equals(localName)) {
                    skipping = false;
                }
            }
        }

        public void endPrefixMapping(String prefix) throws SAXException {
        }

        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        }

        public void processingInstruction(String target, String data) throws SAXException {
        }

        public void setDocumentLocator(Locator locator) {
            this.locator = locator;
        }

        public void skippedEntity(String name) throws SAXException {
        }

        public void startDocument() throws SAXException {
            if (pageName == null) {
                pages.clear();
            }
        }

        public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
            if (SCHEME_ELEMENT.equals(localName)) {
                if (!elements.isEmpty()) {
                    // SCHEME element must be root
                    throw new SAXParseException("scheme element must be the root.", locator);
                }

                // Extract properties and create new class loader
                name = getMandatoryAttribute(atts, NAME_ATTR);
                description = getMandatoryAttribute(atts, DESCRIPTION_ATTR);

                if (loader == null) {
                    String classpath = atts.getValue(CLASSPATH_ATTR);

                    String[] paths = (classpath != null) ? classpath.split(";") : new String[0];
                    URL[] urls;

                    if (dir.getProtocol().equalsIgnoreCase("jar")) {
                        urls = new URL[paths.length + 1];
                        try {
                            urls[paths.length] = ((JarURLConnection) dir.openConnection()).getJarFileURL();
                        } catch (IOException e) {
                            throw new SAXParseException("The jar file is invalid.", locator, e);
                        }
                    } else {
                        urls = new URL[paths.length];
                    }

                    for (int i = 0; i < paths.length; ++i) {
                        try {
                            urls[i] = new URL(dir, paths[i]);
                        } catch (MalformedURLException e) {
                            throw new SAXParseException("The classpath is invalid.", locator, e);
                        }
                    }

                    if (urls.length == 0) {
                        loader = getClass().getClassLoader();
                    } else {
                        loader = URLClassLoader.newInstance(urls, getClass().getClassLoader());
                    }
                }

                elements.push(localName);
            } else if (CALL_ELEMENT.equals(localName)) {
                if (!skipping) {
                    try {
                        String className = getMandatoryAttribute(atts, CLASS_NAME_ATTR);
                        String methodName = getMandatoryAttribute(atts, METHOD_NAME_ATTR);

                        Class.forName(className, true, loader).getMethod(methodName, new Class[] {UIManager.class})
                            .invoke(null, new Object[] {XMLUIManager.this});
                    } catch (ClassNotFoundException e) {
                        throw new SAXParseException("The invocation class cannot be loaded.", locator, e);
                    } catch (NoSuchMethodException e) {
                        throw new SAXParseException("The invocation method cannot be found.", locator, e);
                    } catch (IllegalAccessException e) {
                        throw new SAXParseException("The invocation method cannot be accessed.", locator, e);
                    } catch (InvocationTargetException e) {
                        throw new SAXParseException("The invocation method causes an error.", locator, (Exception) e
                            .getTargetException());
                    }

                    elements.push(localName);
                }
            } else if (PAGE_ELEMENT.equals(localName)) {
                // Page element must be directly under root
                if (elements.size() != 1) {
                    throw new SAXParseException("Page can only be defined under scheme.", locator);
                }

                // When page name is specified, and the current page name is not the given name
                // Skip all the inner components without creating the page itself.
                skipping = ((pageName != null) && !pageName.equals(getMandatoryAttribute(atts, NAME_ATTR)));

                try {
                    if (!skipping) {
                        String className = getMandatoryAttribute(atts, CLASS_NAME_ATTR);
                        UIPage page = (UIPage) Class.forName(className, true, loader).newInstance();

                        pages.put(getMandatoryAttribute(atts, NAME_ATTR), page);
                        elements.push(page);
                        currentPage = page;
                    }
                } catch (ClassCastException e) {
                    throw new SAXParseException("The page is not an UIPage.", locator, e);
                } catch (InstantiationException e) {
                    throw new SAXParseException("The page does not have a default constructor.", locator, e);
                } catch (ClassNotFoundException e) {
                    throw new SAXParseException("The page class cannot be loaded.", locator, e);
                } catch (IllegalAccessException e) {
                    throw new SAXParseException("The default constructor of the page class is not public.", locator, e);
                }
            } else if (COMPONENTREF_ELEMENT.equals(localName)) {
                if (skipping) {
                    return;
                }

                if (!(elements.peek() instanceof UIPage)) {
                    throw new SAXParseException("Component reference can be only used under page.", locator);
                }

                try {
                    elements.push(currentPage.getComponent(getMandatoryAttribute(atts, NAME_ATTR)));
                } catch (UIPageException e) {
                    throw new SAXParseException("The component reference cannot be found in the page.", locator, e);
                }
            } else if (COMPONENT_ELEMENT.equals(localName)) {
                if (skipping) {
                    return;
                }

                if (!(elements.peek() instanceof UIPage) && !(elements.peek() instanceof UIComponent)) {
                    throw new SAXParseException("Only page or component can be added with child components.", locator);
                }

                try {
                    UIComponent component = (UIComponent) Class.forName(getMandatoryAttribute(atts, CLASS_NAME_ATTR),
                        true, loader).newInstance();

                    constraints.push(new GridBagConstraints());
                    elements.push(component);

                    currentPage.addComponent(getMandatoryAttribute(atts, NAME_ATTR), component);
                } catch (ClassCastException e) {
                    throw new SAXParseException("The component is not an UIComponent.", locator, e);
                } catch (InstantiationException e) {
                    throw new SAXParseException("The component does not have a default constructor.", locator, e);
                } catch (ClassNotFoundException e) {
                    throw new SAXParseException("The component class cannot be loaded.", locator, e);
                } catch (IllegalAccessException e) {
                    throw new SAXParseException("The default constructor of the component class is not public.",
                        locator, e);
                } catch (UIPageException e) {
                    throw new SAXParseException("Adding component to the page fails.", locator, e);
                }
            } else if (CONSTRAINTS_ELEMENT.equals(localName)) {
                if (skipping) {
                    return;
                }

                if (!(elements.peek() instanceof UIComponent)) {
                    throw new SAXParseException("Only component can be set with constraints.", locator);
                }

                GridBagConstraints constraint = (GridBagConstraints) constraints.peek();

                // Enumerate all properties of the constraints
                for (int i = 0; i < atts.getLength(); ++i) {
                    String attrName = atts.getLocalName(i);
                    String attrValue = atts.getValue(i);
                    boolean isConst = true;
                    int constValue = 0;

                    try {
                        // Parse any possible constants
                        constValue = parseConstraintsConstants(attrValue);
                    } catch (IllegalArgumentException e) {
                        isConst = false;
                    }

                    try {
                        // parse and set the proper properties of the constraints
                        if (CONSTRAINTS_INSETS_ATTR.equals(attrName)) {
                            constraint.insets = parseIntsets(attrValue);
                        } else if (CONSTRAINTS_WEIGHTX_ATTR.equals(attrName)) {
                            constraint.weightx = Double.parseDouble(attrValue);
                        } else if (CONSTRAINTS_WEIGHTY_ATTR.equals(attrName)) {
                            constraint.weighty = Double.parseDouble(attrValue);
                        } else if (CONSTRAINTS_ANCHOR_ATTR.equals(attrName)) {
                            constraint.anchor = isConst ? constValue : Integer.parseInt(attrValue);
                        } else if (CONSTRAINTS_FILL_ATTR.equals(attrName)) {
                            constraint.fill = isConst ? constValue : Integer.parseInt(attrValue);
                        } else if (CONSTRAINTS_GRIDHEIGHT_ATTR.equals(attrName)) {
                            constraint.gridheight = isConst ? constValue : Integer.parseInt(attrValue);
                        } else if (CONSTRAINTS_GRIDWIDTH_ATTR.equals(attrName)) {
                            constraint.gridwidth = isConst ? constValue : Integer.parseInt(attrValue);
                        } else if (CONSTRAINTS_GRIDX_ATTR.equals(attrName)) {
                            constraint.gridx = isConst ? constValue : Integer.parseInt(attrValue);
                        } else if (CONSTRAINTS_GRIDY_ATTR.equals(attrName)) {
                            constraint.gridy = isConst ? constValue : Integer.parseInt(attrValue);
                        } else if (CONSTRAINTS_IPADX_ATTR.equals(attrName)) {
                            constraint.ipadx = isConst ? constValue : Integer.parseInt(attrValue);
                        } else if (CONSTRAINTS_IPADY_ATTR.equals(attrName)) {
                            constraint.ipady = isConst ? constValue : Integer.parseInt(attrValue);
                        } else {
                            throw new SAXParseException("Unknown constraints " + attrName + ".", locator);
                        }
                    } catch (IllegalArgumentException e) {
                        throw new SAXParseException("The value of the constraints is invalid.", locator, e);
                    }
                }

                elements.push(localName);
            } else if (PROPERTY_ELEMENT.equals(localName)) {
                if (skipping) {
                    return;
                }

                if (!(elements.peek() instanceof UIComponent)) {
                    throw new SAXParseException("Only component can be set with properties.", locator);
                }

                UIComponent component = (UIComponent) elements.peek();
                Object value;

                // Set the property using the UIComponent interface
                if (atts.getValue(VALUE_PARSER_ATTR) != null) {
                    try {
                        UIPropertyValueParser parser = (UIPropertyValueParser) Class.forName(
                            atts.getValue(VALUE_PARSER_ATTR), true, loader).newInstance();

                        value = parser.parse(currentPage, getMandatoryAttribute(atts, VALUE_ATTR), loader);
                    } catch (ClassCastException e) {
                        throw new SAXParseException("The parser is not an UIPropertyValueParser.", locator, e);
                    } catch (InstantiationException e) {
                        throw new SAXParseException("The parser does not have a default constructor.", locator, e);
                    } catch (ClassNotFoundException e) {
                        throw new SAXParseException("The parser class cannot be loaded.", locator, e);
                    } catch (IllegalAccessException e) {
                        throw new SAXParseException("The default constructor of the parser class is not public.",
                            locator, e);
                    } catch (IllegalArgumentException e) {
                        throw new SAXParseException("The property value is invalid for the parser.", locator, e);
                    } catch (Exception e) {
                        throw new SAXParseException("Other error occurred when parsing the value.", locator, e);
                    }
                } else {
                    value = getMandatoryAttribute(atts, VALUE_ATTR);
                }

                try {
                    component.setProperty(getMandatoryAttribute(atts, NAME_ATTR), value);
                } catch (UIComponentException e) {
                    throw new SAXParseException("The property cannot be set.", locator, e);
                }

                elements.push(localName);
            } else if (ACTION_ELEMENT.equals(localName)) {
                if (skipping) {
                    return;
                }

                if (!(elements.peek() instanceof UIComponent)) {
                    throw new SAXParseException("Only components can perform actions.", locator);
                }

                PerformAction action = new PerformAction();

                action.name = getMandatoryAttribute(atts, NAME_ATTR);

                elements.push(action);
            } else if (PARAM_ELEMENT.equals(localName)) {
                if (skipping) {
                    return;
                }

                if (!(elements.peek() instanceof PerformAction)) {
                    throw new SAXParseException("Only actions can have parameters.", locator);
                }

                PerformAction action = (PerformAction) elements.peek();
                Object value;

                // Set the property using the UIComponent interface
                if (atts.getValue(VALUE_PARSER_ATTR) != null) {
                    try {
                        UIPropertyValueParser parser = (UIPropertyValueParser) Class.forName(
                            atts.getValue(VALUE_PARSER_ATTR), true, loader).newInstance();

                        value = parser.parse(currentPage, getMandatoryAttribute(atts, VALUE_ATTR), loader);
                    } catch (ClassCastException e) {
                        throw new SAXParseException("The parser is not an UIPropertyValueParser.", locator, e);
                    } catch (InstantiationException e) {
                        throw new SAXParseException("The parser does not have a default constructor.", locator, e);
                    } catch (ClassNotFoundException e) {
                        throw new SAXParseException("The parser class cannot be loaded.", locator, e);
                    } catch (IllegalAccessException e) {
                        throw new SAXParseException("The default constructor of the parser class is not public.",
                            locator, e);
                    } catch (IllegalArgumentException e) {
                        throw new SAXParseException("The property value is invalid for the parser.", locator, e);
                    }
                } else {
                    value = getMandatoryAttribute(atts, VALUE_ATTR);
                }

                action.params.add(value);

                elements.push(localName);
            } else {
                throw new SAXParseException("Illegal element '" + localName + "' in the configuration file.", locator);
            }
        }

        public void startPrefixMapping(String prefix, String uri) throws SAXException {
        }
    }

    /**
     * Creates a new instance of XMLUIManager class. The XML configuration file is given.
     * 
     * @param xmlConfig the XML configuration file.
     * @throws UIManagerConfigurationException if the XML configuration file is not valid.
     */
    public XMLUIManager(URL xmlConfig) throws UIManagerConfigurationException {
        this.xmlConfig = xmlConfig;

        // Set the default locale to US
        Locale locale = Locale.getDefault();
        Locale.setDefault(Locale.US);

        // Load the name and description only
        InputStream is = null;

        try {
            is = xmlConfig.openStream();

            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);

            XMLReader parser = factory.newSAXParser().getXMLReader();
            parser.setContentHandler(new DefaultHandler() {
                private String getMandatoryAttribute(Attributes atts, String name) throws SAXException {
                    String value = atts.getValue(name);

                    if (value == null) {
                        throw new SAXException("Attribute " + name + " must exist.");
                    }

                    return value;
                }

                public void startElement(String uri, String localName, String qName, Attributes atts)
                    throws SAXException {
                    if (SCHEME_ELEMENT.equals(localName)) {
                        // Extract properties and create new class loader
                        name = getMandatoryAttribute(atts, NAME_ATTR);
                        description = getMandatoryAttribute(atts, DESCRIPTION_ATTR);
                    }
                }
            });
            parser.parse(new InputSource(is));
        } catch (IOException e) {
            throw new UIManagerConfigurationException("The configuration file cannot be read.", e);
        } catch (ParserConfigurationException e) {
            throw new UIManagerConfigurationException("The SAX parser cannot be created.", e);
        } catch (SAXParseException e) {
            throw new UIManagerConfigurationException("The XML configuration for this UI is invalid at line "
                + e.getLineNumber() + " column " + e.getColumnNumber() + ".", e);
        } catch (SAXException e) {
            throw new UIManagerConfigurationException("The XML configuration for this UI is invalid.", e);
        } finally {
            // Restore the default locale
            Locale.setDefault(locale);
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public void create() throws UIManagerConfigurationException {
        create(null);
    }

    /**
     * Gets the URL of the XML configuration file.
     * 
     * @return the URL of the XML configuration file.
     */
    public URL getXMLConfig() {
        return xmlConfig;
    }

    /**
     * Gets the current parsing UI page.
     * 
     * @return the current parsing UI page.
     */
    public UIPage getCurrentPage() {
        return currentPage;
    }

    /**
     * Gets the class loader used to load the UI elements.
     * 
     * @return the class loader used to load the UI elements.
     */
    public ClassLoader getClassLoader() {
        return loader;
    }

    /**
     * Creates UI pages according to the page name. If the name is <code>null</code>, all pages are created.
     * 
     * @param name the page name of the page to be created.
     * @throws UIManagerConfigurationException if the XML configuration file is not valid.
     */
    private void create(String name) throws UIManagerConfigurationException {
        // Set the default locale to US
        Locale locale = Locale.getDefault();
        Locale.setDefault(Locale.US);

        InputStream is = null;

        try {
            is = xmlConfig.openStream();

            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);

            XMLReader parser = factory.newSAXParser().getXMLReader();
            ConfigurationContentHandler handler = new ConfigurationContentHandler(xmlConfig, name);

            parser.setContentHandler(handler);
            parser.parse(new InputSource(is));
        } catch (IOException e) {
            throw new UIManagerConfigurationException("The configuration file cannot be read.", e);
        } catch (ParserConfigurationException e) {
            throw new UIManagerConfigurationException("The SAX parser cannot be created.", e);
        } catch (SAXParseException e) {
            throw new UIManagerConfigurationException("The XML configuration for this UI is invalid at line "
                + e.getLineNumber() + " column " + e.getColumnNumber() + ".", e);
        } catch (SAXException e) {
            throw new UIManagerConfigurationException("The XML configuration for this UI is invalid.", e);
        } finally {
            // Restore the default locale
            Locale.setDefault(locale);
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public void destroy() {
        for (Iterator iter = pages.values().iterator(); iter.hasNext();) {
            UIPage page = (UIPage) iter.next();

            page.destroy();
        }
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public UIPage getUIPage(String name) throws UIPageNotFoundException {
        return getUIPage(name, false);
    }

    public UIPage getUIPage(String name, boolean recreate) throws UIPageNotFoundException {
        UIPage page = (UIPage) pages.get(name);

        if (page == null) {
            throw new UIPageNotFoundException("The UI page " + name + " is not available.");
        }

        if (recreate) {
            // Recreate the page
            try {
                create(name);
            } catch (UIManagerConfigurationException e) {
                throw new UIPageNotFoundException("The UI page cannot be re-created.", e);
            }

            page.destroy();
            page = (UIPage) pages.get(name);
        }

        return page;
    }
}
