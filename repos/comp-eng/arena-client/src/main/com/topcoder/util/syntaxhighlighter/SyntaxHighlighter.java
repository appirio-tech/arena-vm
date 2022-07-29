/*
 * Copyright (C) 2005 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.util.syntaxhighlighter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.topcoder.util.config.ConfigManager;
import com.topcoder.util.config.UnknownNamespaceException;
import com.topcoder.util.log.Log;
import com.topcoder.util.syntaxhighlighter.rules.Rule;
import com.topcoder.util.syntaxhighlighter.rules.Rule.Point;


/**
 * The SyntaxHighlighter performs its tasks by iterating over a language's Rules and applying the Rule to a
 * HighlightedSequence that it maintains internally. Once complete, ordered segments are retrieved from the
 * HighlightedSequence which are passed directly to a HighlightedOutput subclass instance in order to perform
 * the actual highlighting. At this point, once the highlighting is completed, the user will utilize the API
 * of the specific HighlightedOutput subclass in order to handle the highlighted text.
 *
 * Changes by WB:
 *   Optimized highlight algorithm to provide rule hit cache.
 *
 * @author duner, still
 * @author WishingBone
 * @version 2.0
 */
public class SyntaxHighlighter {
    /**
     * <p>
     * A public constant that represents the default namespace this component uses to load configuration data.
     * </p>
     */
    public static final String DEFAULT_NAMESPACE = "com.topcoder.util.syntaxhighlighter";

    /**
     * <p>
     * A constant that represents node name of property which contains the language config.
     * </p>
     */
    private static final String PROPERTY_NAME = "language_files";

    /**
     * <p>
     * Represents a list of languages. This should contain one or more languages that have been loaded from
     * language configuration files in one of the constructors. This is immutable once set and its contents are
     * never changed outside of the constructors. This is accessible via the getLanguages accessor. This will never
     * contain any null elements.
     * </p>
     *
     */
    private final List languages = new ArrayList();

    /**
     * <p>
     * This constructor loading the languages from the necessary source files from default namespace.
     * </p>
     *
     * @throws ConfigurationException
     *             if the namespace could not be found, the necessary properties do not exist, the files
     *             specified in configuration do not exist or there are errors in the configuration files
     *             (i.e. during XML parsing/validation) or if an appropriate parser is not configured.
     */
    public SyntaxHighlighter() throws ConfigurationException {
        this(DEFAULT_NAMESPACE, null);
    }

    /**
     * <p>
     * This constructor loading the languages from the necessary source files from the namespace.
     * </p>
     * @param namespace
     *            The namespace used for retrieving configuration data from the configuration manager. May not
     *            be null.
     *
     * @throws NullPointerException
     *             if namespace is null.
     * @throws ConfigurationException
     *             if the namespace could not be found, the necessary properties do not exist, the files
     *             specified in configuration do not exist or there are errors in the configuration files
     *             (i.e. during XML parsing/validation) or if an appropriate parser is not configured.
     * @throws IllegalArgumentException
     *             if namespace is an empty (trimmed) String.
     */
    public SyntaxHighlighter(String namespace) throws ConfigurationException {
        this(namespace, null);
    }

    /**
     * <p>
     * This method load language xml from configed namespace, and new a language for each xml. It throws
     * ConfigurationException when config is not right.</p>
     * @param namespace
     *            The namespace used for retrieving configuration data from the configuration manager. May not
     *            be null.
     * @param log
     *            The Log instance used to perform logging of errors. This may be null to indicate no logging.
     * @throws ConfigurationException
     * @throws NullPointerException
     *             if namespace is null.
     * @throws ConfigurationException
     *             if the namespace could not be found, the necessary properties do not exist, the files
     *             specified in configuration do not exist or there are errors in the configuration files
     *             (i.e. during XML parsing/validation) or if an appropriate parser is not configured.
     * @throws IllegalArgumentException
     *             if namespace is an empty (trimmed) String.
     */
    public SyntaxHighlighter(String namespace, Log log)
        throws ConfigurationException {
        SHHelper.checkString(namespace, "namespace");

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // set it to validating
            factory.setValidating(false);
            
            // set the appropriate attributes
            try {
                //factory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
                //factory.setAttribute(JAXP_SCHEMA_SOURCE, SyntaxHighlighter.class.getResource(SCHEMA_FILE).getPath());
            } catch (IllegalArgumentException e) {
                // this parser does not support schema validation, set
                // validating to false and ignore.
                factory.setValidating(false);
            }

            ConfigManager cm = ConfigManager.getInstance();

            // get language xml file name
            String[] properties = cm.getStringArray(namespace, PROPERTY_NAME);
            
            if ((properties != null) && (properties.length > 0)) {
                for (int i = 0; i < properties.length; ++i) {
                    // get DocumentBuilder instance and set error handler
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    builder.setErrorHandler(new LoggingErrorHandler(log));

                    // parse and validate document
                    InputStream is = this.getClass().getResourceAsStream("/" + properties[i]);
                    Document doc = builder.parse(is);
                    Language language = new Language(doc);

                    // the same language name is legal
                    languages.add(language);
                }
            } else {
                throw new ConfigurationException("Language files must be specified.");
            }
        } catch (UnknownNamespaceException e) {
            throw new ConfigurationException("Namespace cannot be found.");
        } catch (ParserConfigurationException e) {
            throw new ConfigurationException("There are some errors in parser configuration.");
        } catch (SAXException e) {
            throw new ConfigurationException("There are errors in the configuration files.");
        } catch (IOException e) {
            throw new ConfigurationException("There are errors in reading the configuration files.");
        }
    }

    /**
     * <p>
     * This constructor loads the languages from the necessary source files from default namespace.
     * </p>
     * @param log
     *            The Log instance used to perform logging of errors.
     *
     *
     * @throws ConfigurationException
     *             if the namespace could not be found, the necessary properties do not exist, the files
     *             specified in configuration do not exist or there are errors in the configuration files
     *             (i.e. during XML parsing/validation) or if an appropriate parser is not configured.
     */
    public SyntaxHighlighter(Log log) throws ConfigurationException {
        this(DEFAULT_NAMESPACE, log);
    }

    /**
     * <p>
     * This method return the contents of the list in an array.
     * </p>
     *
     *
     * @return an array corresponding to the elements of the languages List.
     */
    public Language[] getLanguages() {
        return (Language[]) languages.toArray(new Language[languages.size()]);
    }

    /**
     * <p>
     * Highlights a string of text using the given language and outputPlugin.
     * </p>
     *
     * @param text
     *            The text to be highlighted. May not be null. May not be empty.
     * @param languageName
     *            The language whose rules should be used to highlight. May not be null or empty(trimmed).
     * @param outputPlugin
     *            the HighlightedOutput instance to use to send the highlighted segments. May not be null.
     *
     * @throws NullPointerException
     *             if text, language or outputPluging are null.
     * @throws RuleMatchException
     *             if any of the Rules throw a RuleMatchException due to an error.
     * @throws HighlightingException
     *             if the output plugin cannot appropriately render the Highlighted text.
     * @throws IllegalArgumentException if languageName or text is empty
     */
    public void highlightText(String text, String languageName, HighlightedOutput outputPlugin)
        throws RuleMatchException, HighlightingException {
        SHHelper.checkNull(text, "text");
        //System.out.println("highlightText begins");

        // don't trim to enable only space contents
        if (text.length() == 0) {
            throw new IllegalArgumentException("'text' may not be empty.");
        }

        SHHelper.checkString(languageName, "languageName");
        SHHelper.checkNull(outputPlugin, "outputPlugin");

        // get the language
        Language language = null;
        for (Iterator it = languages.iterator(); it.hasNext();) {
            Language tempLanguage = (Language) it.next();

            if (tempLanguage.getName().equals(languageName)) {
                language = tempLanguage;
                break;
            }
        }
        if (language == null) {
            throw new IllegalArgumentException("Language " + languageName + " was not found.");
        }

        HighlightedSequence hs = new HighlightedSequence(text);

        // Notes:
        // here is changed by fix
        // for each rule in the categories with the same percedence,
        // found the matched segment, and apply the first start segment with the associated rule
        // and ignore others.
        Category[] categories = language.getCategories();
        int categoryIndex = 0;
        while (categoryIndex < categories.length) {
            // get all rules in the categories with the same percedence
            // and save it into allRules
            int persistence = categories[categoryIndex].getPrecedence();

            // contains all the rules
            List allRules = new ArrayList();
            List allStyles = new ArrayList();
            while ((categoryIndex < categories.length) && (persistence == categories[categoryIndex].getPrecedence())) {
                Rule[] rules = categories[categoryIndex].getRules();
                for (int j = 0; j < rules.length; ++j) {
                    allRules.add(rules[j]);
                    allStyles.add(categories[categoryIndex].getStyle());
                }
                ++categoryIndex;
            }

            Rule[] rules = (Rule[]) allRules.toArray(new Rule[allRules.size()]);
            TextStyle[] styles = (TextStyle[]) allStyles.toArray(new TextStyle[allStyles.size()]);
            Point[] tokens = new Point[rules.length];
            boolean[] exhausted = new boolean[rules.length];

            // iterate each rule, find the first start segment, and apply the rule.
            while (true) {
                Point minPoint = null;
                TextStyle style = null;

                // find the first start segment.
                for (int ruleIndex = 0; ruleIndex < rules.length; ruleIndex++) {
                    if (exhausted[ruleIndex]) {
                        continue;
                    }
                    Point currPoint = tokens[ruleIndex];
                    if (currPoint == null) {
                        currPoint = rules[ruleIndex].getToken(hs);
                        if (currPoint == null) {
                            exhausted[ruleIndex] = true;
                            continue;
                        }
                        tokens[ruleIndex] = currPoint;
                    }
                    if (minPoint == null || minPoint.getStart() > currPoint.getStart()) {
                        minPoint = currPoint;
                        style = styles[ruleIndex];
                    }
                }
                if (minPoint == null) {
                    break;
                }
                
                hs.highlight(minPoint.getStart(), minPoint.getEnd(), style);

                // adjust the hit cache
                int length = minPoint.getEnd() - minPoint.getStart();
                for (int ruleIndex = 0; ruleIndex < rules.length; ruleIndex++) {
                    if (exhausted[ruleIndex]) {
                        continue;
                    }
                    Point currPoint = tokens[ruleIndex];
                    if (currPoint.getStart() < minPoint.getEnd()) {
                        tokens[ruleIndex] = null;
                    } else {
                        tokens[ruleIndex] = new Point(currPoint.getStart() - length, currPoint.getEnd() - length);
                    }
                }
            }
        }

        // get the whole text segments
        ContentSegment[] segments = hs.getOrderedSegments();
        outputPlugin.setText(segments);
    }
}