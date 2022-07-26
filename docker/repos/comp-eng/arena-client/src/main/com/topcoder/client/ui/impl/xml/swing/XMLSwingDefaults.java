package com.topcoder.client.ui.impl.xml.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.io.InputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import javax.swing.Icon;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.IconUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.topcoder.client.ui.UIManagerConfigurationException;
import com.topcoder.client.ui.UIPage;
import com.topcoder.client.ui.UIPropertyValueParser;
import com.topcoder.client.ui.impl.XMLUIManager;

public class XMLSwingDefaults {
    private static final String SWING_CONFIG_XML = "swing.xml";
    
    private XMLSwingDefaults() {
    }

    private static class SwingDefaultsHandler extends DefaultHandler {
        private ClassLoader loader;
        private Locator locator;
        private boolean valid;
        private UIPage currentPage;

        public SwingDefaultsHandler(ClassLoader loader, UIPage page) {
            this.loader = loader;
            valid = false;
            currentPage = page;
        }

        public void setDocumentLocator(Locator locator) {
            this.locator = locator;
        }

        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if ("swing".equals(localName)) {
                if (valid) {
                    throw new SAXParseException("Swing element must be the root.", locator);
                } else {
                    valid = true;
                }
            } else if ("property".equals(localName)) {
                if (!valid) {
                    throw new SAXParseException("Property element must be under the swing element.", locator);
                }

                String name = attributes.getValue("name");
                String value = attributes.getValue("value");
                String parser = attributes.getValue("parser");

                if ((name == null) || (value == null)) {
                    throw new SAXParseException("Name and value must exist for property.", locator);
                }

                if ("swing.defaultlaf".equals(name)) {
                    try {
                        UIManager.setLookAndFeel(value);
                    } catch (Exception e) {
                        throw new SAXParseException("The look and feel is not supported.", locator, e);
                    }
                } else if ("metal.theme".equals(name)) {
                    try {
                        MetalLookAndFeel.setCurrentTheme((MetalTheme)Class.forName(value, true, loader).newInstance());
                    } catch (Exception e) {
                        throw new SAXParseException("The metal theme is not supported.", locator, e);
                    }
                } else if (parser == null) {
                    UIManager.getDefaults().put(name, value);
                } else {
                    try {
                        UIPropertyValueParser parse = (UIPropertyValueParser) Class.forName(parser, true, loader).newInstance();
                        Object obj = parse.parse(currentPage, value, loader);

                        if (obj instanceof Font) {
                            obj = new FontUIResource((Font) obj);
                        } else if (obj instanceof Color) {
                            obj = new ColorUIResource((Color) obj);
                        } else if (obj instanceof Icon) {
                            obj = new IconUIResource((Icon) obj);
                        } else if (obj instanceof Border) {
                            obj = new BorderUIResource((Border) obj);
                        } else if (obj instanceof Insets) {
                            Insets in = (Insets) obj;
                            obj = new InsetsUIResource(in.top, in.left, in.bottom, in.right);
                        } else if (obj instanceof Dimension) {
                            Dimension dim = (Dimension) obj;
                            obj = new DimensionUIResource(dim.width, dim.height);
                        }

                        UIManager.getDefaults().put(name, obj);
                    } catch (ClassCastException e) {
                        throw new SAXParseException("The parser is not an UIPropertyValueParser.", locator, e);
                    } catch (InstantiationException e) {
                        throw new SAXParseException("The parser does not have a default constructor.", locator, e);
                    } catch (ClassNotFoundException e) {
                        throw new SAXParseException("The parser class cannot be loaded.", locator, e);
                    } catch (IllegalAccessException e) {
                        throw new SAXParseException("The default constructor of the parser class is not public.", locator, e);
                    } catch (IllegalArgumentException e) {
                        throw new SAXParseException("The property value is invalid for the parser.", locator, e);
                    }
                }
            } else {
                throw new SAXParseException("Unknown element '" + localName + "'.", locator);
            }
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {
            if ("swing".equals(localName)) {
                valid = false;
            }
        }
    }

    public static void set(com.topcoder.client.ui.UIManager _manager) throws UIManagerConfigurationException {
        URL xml;
        XMLUIManager manager = (XMLUIManager) _manager;
        URL configXml = manager.getXMLConfig();

        try {
            xml = new URL(configXml, SWING_CONFIG_XML);
        } catch (MalformedURLException e) {
            // Fall back to a simple manipulation of URL
            String strUrl = configXml.toExternalForm();
            String url = strUrl.substring(0, strUrl.lastIndexOf('/') + 1) + SWING_CONFIG_XML;
            try {
                xml = new URL(url);
            } catch (MalformedURLException ee) {
                throw new UIManagerConfigurationException("The URL '" + url + "' is malformed.", ee);
            }
        }

        ClassLoader loader = manager.getClassLoader();
        UIManager.getDefaults().put("ClassLoader", loader);

        // Set the default locale to US
        Locale locale = Locale.getDefault();
        Locale.setDefault(Locale.US);

        InputStream is = null;

        try {
            is = xml.openStream();

            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);

            XMLReader parser = factory.newSAXParser().getXMLReader();

            parser.setContentHandler(new SwingDefaultsHandler(loader, manager.getCurrentPage()));
            parser.parse(new InputSource(is));
        } catch (IOException e) {
            throw new UIManagerConfigurationException("The configuration file cannot be read.", e);
        } catch (ParserConfigurationException e) {
            throw new UIManagerConfigurationException("The SAX parser cannot be created.", e); 
        } catch (SAXParseException e) {
            e.printStackTrace();
            throw new UIManagerConfigurationException("The Swing defaults XML configuration is invalid at line " + e.getLineNumber() + " column " + e.getColumnNumber() + ".", e);
        } catch (SAXException e) {
            throw new UIManagerConfigurationException("The Swing defaults XML configuration is invalid.", e);
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
}
