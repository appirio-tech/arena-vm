/*
 * Copyright (C) - 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.shared.problemParser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.apache.xerces.parsers.DOMParser;
import org.apache.xerces.parsers.StandardParserConfiguration;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.topcoder.shared.problem.Constraint;
import com.topcoder.shared.problem.DataType;
import com.topcoder.shared.problem.DataTypeFactory;
import com.topcoder.shared.problem.Element;
import com.topcoder.shared.problem.InvalidTypeException;
import com.topcoder.shared.problem.ProblemComponent;
import com.topcoder.shared.problem.ProblemMessage;
import com.topcoder.shared.problem.TestCase;
import com.topcoder.shared.util.logging.Logger;

/**
 * This factory does all of the work of parsing an XML description of a problem statement and constructing
 * an appropriate instance of the <code>ProblemComponent</code> class.  Uses xerces2-j for parsing.
 *
 * <p>
 * Version 1.1 (TC Competition Engine Code Execution Time Issue) changes:
 *  <ul>
 *      <li>Update {@link #buildFromXML(Reader, boolean)} method to populate the execution time limit.</li>
 *  </ul>
 * </p>
 * 
 * <p>
 * Version 1.2 (TC Competition Engine - Code Compilation Issues) changes:
 *  <ul>
 *      <li>Update {@link #buildFromXML(Reader, boolean)} method to populate the compile time limit.</li>
 *  </ul>
 * </p>
 *
 * <p>
 * Changes in version 1.3 (TC Competition Engine - CPP Language Upgrade And Customization Support v1.0):
 * <ol>
 * 	    <li>Update {@link #buildFromXML(Reader, boolean)} method for the gcc build command and approved path.</li>
 * 	    <li>Add {@link #GCC_BUILD_COMMAND} field.</li>
 * 	    <li>Add {@link #CPP_APPROVED_PATH} field.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.4 (TC Competition Engine - Python Language Upgrade And Customization Support v1.0):
 * <ol>
 *      <li>Update {@link #buildFromXML(Reader, boolean)} method.</li>
 *      <li>Add {@link #PYTHON_COMMAND} field.</li>
 *      <li>Add {@link #PYTHON_APPROVED_PATH} field.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.5 (TopCoder Competition Engine Improvement Series 3 v1.0):
 * <ol>
 *      <li>Update {@link #parseMemLimit()} method.</li>
 *      <li>Update {@link #buildFromXML(Reader reader, boolean unsafe)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.6 (TopCoder Competition Engine - Stack Size Configuration For SRM Problems v1.0):
 * <ol>
 *      <li>Aded {@link #STACK_LIMIT} constand for stack limit tag name.</li>
 *      <li>Aded {@link #parseStackLimit()} method for parsing stack limit.</li>
 *      <li>Updated {@link #buildFromXML(Reader reader, boolean unsafe)} method to parse stack limit.</li>
 *      <li>Updated {@link #buildFromXML(Reader reader, boolean unsafe)} method to log check type error messages.</li>
 * </ol>
 * </p>
 *
 * @see ProblemComponent
 * @author Logan Hanks, savon_cn, Selena
 * @version 1.6
 */
public class ProblemComponentFactory
    implements ErrorHandler
{
    static final String TYPE = "type";
    static final String INTRO = "intro";
    static final String SPEC = "spec";
    static final String NOTES = "notes";
    static final String CONSTRAINTS = "constraints";
    static final String CONSTRAINT = "constraint";
    static final String NUMERIC = "numeric";
    static final String ARRAY = "array";
    static final String USER_CONSTRAINT = "user-constraint";
    static final String TEST_CASES = "test-cases";
    static final String TEST_CASE_INPUT = "input";
    static final String TEST_CASE_OUTPUT = "output";
    static final String TEST_CASE_ANNOTATION = "annotation";
    static final String SIGNATURE = "signature";
    static final String SIGNATURE_CLASS = "class";
    static final String SIGNATURE_METHOD = "method";
    static final String SIGNATURE_EXPOSED_METHOD = "exposed_method";
    static final String SIGNATURE_EXPOSED_CLASS = "exposed_class";
    static final String SIGNATURE_RETURN = "return";
    static final String SIGNATURE_PARAMS = "params";
    static final String SIGNATURE_METHOD_NAME = "name";
    static final String SIGNATURE_PARAM = "param";
    static final String SIGNATURE_PARAM_NAME = "name";
    static final String MEM_LIMIT = "memlimit";

    /**
     * Stack limit tag name.
     * @since 1.6
     */
    static final String STACK_LIMIT = "stacklimit";

    static final String ROUND_TYPE = "roundType";
    /**
     * the gcc build command key.
     */
    static final String GCC_BUILD_COMMAND = "gcc_build_command";
    /**
     * the cpp approved path.
     */
    static final String CPP_APPROVED_PATH = "cpp_approved_path";

    /**
     * the python command key.
     * @since 1.4
     */
    static final String PYTHON_COMMAND = "python_command";
    /**
     * the python approved path.
     * @since 1.4
     */
    static final String PYTHON_APPROVED_PATH = "python_approved_path";
    Node doc, root;
    NodeList sections;
    ProblemComponent stmt;
//    Category trace = Category.getInstance(getClass());
    protected static final Logger trace = Logger.getLogger(ProblemComponentFactory.class);

    public ProblemComponentFactory()
    {
    }

    /**
     * Builds a <code>ProblemComponent</code>.
     *
     * @param reader    A reader on some input stream containing the XML description of a problem statement
     * @param unsafe    A flag which, if set, will designate that information suitable only for
     *                  MPSQAS will be included in the <code>ProblemComponent</code>
     * @return This method always returns a <code>ProblemComponent</code> object, and should not
     *         throw any exceptions.  If any parse errors occurred (or anything else of significance),
     *         they will be noted in the problem statement's messages.  Furthermore, if a parse error
     *         occurred, the statement will be marked as invalid.
     *
     * @see ProblemComponent
     */
    synchronized public ProblemComponent buildFromXML(Reader reader, boolean unsafe) {
        stmt = new ProblemComponent(unsafe);
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("ProblemParser");
            DOMParser parser = new DOMParser(new StandardParserConfiguration());

            trace.debug("getting schema from " + bundle.getString("schema"));
            parser.setErrorHandler(this);
            parser.setFeature("http://xml.org/sax/features/validation", true);
            parser.setFeature("http://apache.org/xml/features/validation/schema", true);
            parser.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);
            parser.setProperty("http://apache.org/xml/properties/schema/external-schemaLocation", bundle.getString("schema"));
            parser.parse(new InputSource(reader));
            doc = parser.getDocument();
            root = getChildByName(doc.getChildNodes(), "problem");
            if (root.hasAttributes() && root.getAttributes().getNamedItem("code_length_limit") != null) {
                // No need to verify if it is an integer, since the schema enforced it.
                stmt.setCodeLengthLimit(Integer.parseInt(root.getAttributes().getNamedItem("code_length_limit").getNodeValue()));
            }
            if (root.hasAttributes() && root.getAttributes().getNamedItem("execution_time_limit") != null) {
                stmt.getProblemCustomSettings().setExecutionTimeLimit(Integer.parseInt(root.getAttributes().getNamedItem("execution_time_limit").getNodeValue()));
            }
            if (root.hasAttributes() && root.getAttributes().getNamedItem("compile_time_limit") != null) {
                stmt.getProblemCustomSettings().setCompileTimeLimit(Integer.parseInt(root.getAttributes().getNamedItem("compile_time_limit").getNodeValue()));
            }
            
            //set the gcc build command
            if (root.hasAttributes() && root.getAttributes().getNamedItem(GCC_BUILD_COMMAND) != null) {
                stmt.getProblemCustomSettings().setGccBuildCommand(root.getAttributes().getNamedItem(GCC_BUILD_COMMAND).getNodeValue());
            }
            //set the cpp approved path
            if (root.hasAttributes() && root.getAttributes().getNamedItem(CPP_APPROVED_PATH) != null) {
                stmt.getProblemCustomSettings().setCppApprovedPath(root.getAttributes().getNamedItem(CPP_APPROVED_PATH).getNodeValue());
            }
            
            //set the python command
            if (root.hasAttributes() && root.getAttributes().getNamedItem(PYTHON_COMMAND) != null) {
                stmt.getProblemCustomSettings().setPythonCommand(root.getAttributes().getNamedItem(PYTHON_COMMAND).getNodeValue());
            }
            //set the python approved path
            if (root.hasAttributes() && root.getAttributes().getNamedItem(PYTHON_APPROVED_PATH) != null) {
                stmt.getProblemCustomSettings().setPythonApprovedPath(root.getAttributes().getNamedItem(PYTHON_APPROVED_PATH).getNodeValue());
            }
            
            sections = root.getChildNodes();
            checkTypes(root);
            if(!stmt.isValid()) {
                trace.error("checkTypes failed! Error messages: " + stmt.getMessages().toString());
                return stmt;
            }
            parseSignature();
            parseIntro();
            parseSpec();
            parseNotes();
            parseConstraints();
            parseTestCases();
            parseMemLimit();
            parseStackLimit();
            parseRoundType();
            
            if(!unsafe)
                removeNonExampleTestCases();
        } catch(Exception ex) {
            System.out.println("Exception while parsing statement: " + ex);
            ex.printStackTrace();
            stmt.addMessage(new ProblemMessage(ProblemMessage.FATAL_ERROR, ex.getMessage()));
            stmt.setValid(false);
        } finally {
            ArrayList messages = stmt.getMessages();

            for(int i = 0; i < messages.size(); i++)
                trace.debug(messages.get(i).toString());
        }
        return stmt;
    }

    /**
     * Builds a <code>ProblemComponent</code>.
     *
     * @param content       An XML description of a problem statement
     * @param unsafe    A flag which, if set, will designate that information suitable only for
     *                  MPSQAS will be included in the <code>ProblemComponent</code>
     * @return This method always returns a <code>ProblemComponent</code> object, and should not
     *         throw any exceptions.  If any parse errors occurred (or anything else of significance),
     *         they will be noted in the problem statement's messages.  Furthermore, if a parse error
     *         occurred, the statement will be marked as invalid.
     *
     * @see ProblemComponent
     * @throws IOException
     */
    synchronized public ProblemComponent build(String content, boolean unsafe)
        throws IOException
    {
        return buildFromXML(new StringReader(content), unsafe);
    }

    void traverse(Node node)
    {
        NodeList nl = node.getChildNodes();

        System.out.println("<" + node.getNodeName() + ">");
        if(node.getNodeType() != Node.ELEMENT_NODE)
            System.out.println("<text>" + node.getNodeValue() + "</text>");
        for(int i = 0; i < nl.getLength(); i++)
            traverse(nl.item(i));
        System.out.println("</" + node.getNodeName() + ">");
    }

/*
    public Category getCategory()
    {
        return trace;
    }
*/

    public void error(SAXParseException ex)
        throws SAXException
    {
        trace.error(ex.toString());
        stmt.addMessage(new ProblemMessage(ProblemMessage.ERROR, ex.getMessage(), ex.getLineNumber(), ex.getColumnNumber()));
    }

    public void warning(SAXParseException ex)
        throws SAXException
    {
        trace.info(ex.toString());
        stmt.addMessage(new ProblemMessage(ProblemMessage.WARNING, ex.getMessage(), ex.getLineNumber(), ex.getColumnNumber()));
    }

    public void fatalError(SAXParseException ex)
        throws SAXException
    {
        trace.error(ex.toString());
        stmt.addMessage(new ProblemMessage(ProblemMessage.FATAL_ERROR, ex.getMessage(), ex.getLineNumber(), ex.getColumnNumber()));
    }

    /**
     * Utility method for obtaining the first child of a node with a particular name.
     *
     * @param nl    A <code>NodeList</code>, representing the children of some node
     * @param name  An element name to locate
     * @return If a node in nl has the given name, it is returned.  Otherwise <code>null</code> is returned.
     */
    static public Node getChildByName(NodeList nl, String name)
    {
        //System.out.println("Looking for "+name);
        for(int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);

            //System.out.println("  found "+node.getNodeName());
            if(node.getNodeName().equals(name))
                return node;
        }
        return null;
    }
    
    static public Node[] getChildrenByName(NodeList nl, String name)
    {
        ArrayList list = new ArrayList();
        //System.out.println("Looking for "+name);
        for(int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);

            //System.out.println("  found "+node.getNodeName());
            if(node.getNodeName().equals(name))
                list.add(node);
        }
        Node[] ret = new Node[list.size()];
        for(int i =0 ; i < list.size(); i++) {
            ret[i] = (Node)list.get(i);
        }
        return ret;
    }

    /**
     * Gets the text value of the attribute of the given node with the given name,
     * or <code>null</code> if an attribute of that name is not defined for the node.
     */
    static public String getAttribute(Node node, String name)
    {
        NamedNodeMap nl = node.getAttributes();

        if(nl == null)
            return null;

        Node n = nl.getNamedItem(name);

        if(n == null)
            return null;
        return n.getNodeValue();
    }

    /**
     * If the given node contains a text element, returns the contents of that text element.
     * Otherwise returns <code>null</code>.
     */
    static public String getText(Node node)
    {
        if(node == null)
            return null;
        if(node.getNodeType() != Node.ELEMENT_NODE)
            return null;

        NodeList nl = node.getChildNodes();

        if(nl.getLength() != 1)
            return null;

        Node n = nl.item(0);

        if(n.getNodeType() != Node.TEXT_NODE)
            return null;

        return n.getNodeValue();
    }

    void checkTypes(Node node)
    {
        NodeList nl = node.getChildNodes();

        for(int i = 0; i < nl.getLength(); i++) {
            Node subnode = nl.item(i);

            if(subnode.getNodeType() == Node.ELEMENT_NODE) {
                if(subnode.getNodeName().equals(TYPE)) {
                    String text = getText(subnode);

                    if(text == null) {
                        stmt.addMessage(new ProblemMessage(ProblemMessage.ERROR, "Empty type element"));
                        stmt.setValid(false);
                    } else {
                        try {
                            DataTypeFactory.getDataType(text);
                        } catch(InvalidTypeException ex) {
                            stmt.addMessage(new ProblemMessage(ProblemMessage.ERROR, "Invalid type: " + text));
                            stmt.setValid(false);
                        }
                    }
                } else
                    checkTypes(subnode);
            }
        }
    }

    DataType getType(Node node)
        throws InvalidTypeException
    {
        String value = node.getFirstChild().getNodeValue();

        return DataTypeFactory.getDataType(value);
    }
    
    void removeNonExampleTestCases() {
        ArrayList al_testCases = new ArrayList();
        for (int i = 0; i < stmt.getTestCases().length; i++)
            if (stmt.getTestCases()[i].isExample())
                al_testCases.add(stmt.getTestCases()[i]);

        TestCase[] testCases = new TestCase[al_testCases.size()];
        for (int i = 0; i < testCases.length; i++)
            testCases[i] = (TestCase) al_testCases.get(i);
        stmt.setTestCases(testCases);
    }


    DataType getNestedType(Node node)
        throws InvalidTypeException
    {
        return getType(getChildByName(node.getChildNodes(), TYPE));
    }

    void removeTextChildren(Node node)
    {
        for(Node n = node.getFirstChild(), next; n != null; n = next) {
            next = n.getNextSibling();
            if(n.getNodeType() == Node.TEXT_NODE)
                node.removeChild(n);
        }
    }

    void parseSignature() throws Exception {
        Node node = getChildByName(sections, SIGNATURE);
        NodeList nl = node.getChildNodes();
        Node cls = getChildByName(nl, SIGNATURE_CLASS);
        String className = getText(cls);
        node.removeChild(cls);
        Node[] methodList = getChildrenByName(nl, SIGNATURE_METHOD);

        DataType[] returnTypes = new DataType[methodList.length];
        String[] methodNames = new String[methodList.length];
        DataType[][] paramTypes = new DataType[methodList.length][];
        String[][] paramNames= new String[methodList.length][];

        trace.debug("we're working with " + returnTypes.length + " return types");
        for(int i = 0; i<returnTypes.length; i++){
            NodeList m = methodList[i].getChildNodes();
            methodNames[i] = getText(getChildByName(m,SIGNATURE_METHOD_NAME));
            returnTypes[i] = getNestedType(getChildByName(m,SIGNATURE_RETURN));
            trace.debug("added return type " + returnTypes[i] + " at index " + i);

            Node params = getChildByName(m, SIGNATURE_PARAMS);

            removeTextChildren(params);

            NodeList paramList = params.getChildNodes();
            paramTypes[i] = new DataType[paramList.getLength()];
            paramNames[i] = new String[paramList.getLength()];

            for(int j = 0; j < paramTypes[i].length; j++) {
                Node n = paramList.item(j);

                paramTypes[i][j] = getType(getChildByName(n.getChildNodes(), TYPE));
                paramNames[i][j] = getText(getChildByName(n.getChildNodes(), SIGNATURE_PARAM_NAME));
            }
        }
        
        stmt.setClassName(className);
        stmt.setMethodNames(methodNames);
        stmt.setReturnTypes(returnTypes);
        stmt.setParamTypes(paramTypes);
        stmt.setParamNames(paramNames);
        
        Node exCls = getChildByName(nl, SIGNATURE_EXPOSED_CLASS);
        
        methodList = getChildrenByName(nl, SIGNATURE_EXPOSED_METHOD);
        
        returnTypes = new DataType[methodList.length];
        methodNames = new String[methodList.length];
        paramTypes = new DataType[methodList.length][];
        paramNames= new String[methodList.length][];

        trace.debug("we're working with " + returnTypes.length + " return types");
        for(int i = 0; i<returnTypes.length; i++){
            NodeList m = methodList[i].getChildNodes();
            methodNames[i] = getText(getChildByName(m,SIGNATURE_METHOD_NAME));
            returnTypes[i] = getNestedType(getChildByName(m,SIGNATURE_RETURN));
            trace.debug("added return type " + returnTypes[i] + " at index " + i);

            Node params = getChildByName(m, SIGNATURE_PARAMS);

            removeTextChildren(params);

            NodeList paramList = params.getChildNodes();
            paramTypes[i] = new DataType[paramList.getLength()];
            paramNames[i] = new String[paramList.getLength()];

            for(int j = 0; j < paramTypes[i].length; j++) {
                Node n = paramList.item(j);

                paramTypes[i][j] = getType(getChildByName(n.getChildNodes(), TYPE));
                paramNames[i][j] = getText(getChildByName(n.getChildNodes(), SIGNATURE_PARAM_NAME));
            }
        }
        
        stmt.setExposedMethodNames(methodNames);
        stmt.setExposedReturnTypes(returnTypes);
        stmt.setExposedParamTypes(paramTypes);
        stmt.setExposedParamNames(paramNames);
        
        if(exCls != null)
            stmt.setExposedClassName(getText(exCls));
    }

    void parseIntro()
    {
        Node node = getChildByName(sections, INTRO);

        stmt.setIntro(NodeElementFactory.build(node));
    }

    void parseSpec()
    {
        Node node = getChildByName(sections, SPEC);

        if(node != null)
            stmt.setSpec(NodeElementFactory.build(node));
    }

    void parseNotes()
    {
        Node node = getChildByName(sections, NOTES);

        removeTextChildren(node);

        NodeList nl = node.getChildNodes();
        Element[] notes = new Element[nl.getLength()];

        for(int i = 0; i < notes.length; i++)
            notes[i] = NodeElementFactory.build(nl.item(i));
        stmt.setNotes(notes);
    }

    void parseConstraints()
    {
        Node node = getChildByName(sections, CONSTRAINTS);

        removeTextChildren(node);

        NodeList nl = node.getChildNodes();
        Constraint[] constraints = new Constraint[nl.getLength()];

        for(int i = 0; i < constraints.length; i++)
            constraints[i] = ConstraintFactory.build(nl.item(i));
        stmt.setConstraints(constraints);
    }

    void parseTestCases()
    {
        Node node = getChildByName(sections, TEST_CASES);

        removeTextChildren(node);

        NodeList nl = node.getChildNodes();
        TestCase[] testCases = new TestCase[nl.getLength()];

        for(int i = 0; i < testCases.length; i++)
            testCases[i] = TestCaseFactory.build(nl.item(i));
        stmt.setTestCases(testCases);
    }
    
    /**
     * parse memory limit.
     */
    void parseMemLimit()
    {
        Node node = getChildByName(sections, MEM_LIMIT);
        if (node != null) {
            try {
                int memLimit = Integer.parseInt(getText(node));
                removeTextChildren(node);
                stmt.getProblemCustomSettings().setMemLimit(memLimit);
            } catch (Exception e) {
                stmt.addMessage(new ProblemMessage(ProblemMessage.ERROR, "Non-numeric memory limit: "+getText(node)));
                stmt.setValid(false);
            }
        }
    }
    
    /**
     * Parses stack size limit.
     * @since 1.6
     */
    void parseStackLimit()
    {
        Node node = getChildByName(sections, STACK_LIMIT);
        if (node != null) {
            try {
                int stackLimit = Integer.parseInt(getText(node));
                removeTextChildren(node);
                stmt.getProblemCustomSettings().setStackLimit(stackLimit);
            } catch (Exception e) {
                stmt.addMessage(new ProblemMessage(ProblemMessage.ERROR,
                    "Non-numeric stack size limit: " + getText(node)));
                stmt.setValid(false);
            }
        }
    }

    void parseRoundType()
    {
        Node node = getChildByName(sections, ROUND_TYPE);
        if (node != null) {
            try {
                int roundType = Integer.parseInt(getText(node));
                removeTextChildren(node);
                stmt.setRoundType(roundType);
            } catch (Exception e) {
                stmt.addMessage(new ProblemMessage(ProblemMessage.ERROR, "Non-numeric round type: "+getText(node)));
                stmt.setValid(false);
            }
        }
    }
/*
    static public void main(String[] args)
        throws Exception
    {
        JavaLanguage java = new JavaLanguage();
        CPPLanguage cpp = new CPPLanguage();
        CSharpLanguage csharp = new CSharpLanguage();
        DataType t_string = new DataType(18, "String");
        DataType t_astring = new DataType(22, "String[]");
        DataType t_aastring = new DataType(22, "String[][]");
        DataType t_int = new DataType(1, "int");
        DataType t_aint = new DataType(20, "int[]");
        DataType t_long = new DataType(14, "long");
        DataType t_matrix2d = new DataType(23, "Matrix2D");
        HashMap map = new HashMap();

        map.put(new Integer(java.ID), "String");
        map.put(new Integer(cpp.ID), "string");
        map.put(new Integer(csharp.ID), "String");
        t_string.setTypeMapping(map);

        map = new HashMap();
        map.put(new Integer(java.ID), "String[]");
        map.put(new Integer(cpp.ID), "vector<string>");
        map.put(new Integer(csharp.ID), "String[]");
        t_astring.setTypeMapping(map);

        map = new HashMap();
        map.put(new Integer(java.ID), "String[][]");
        map.put(new Integer(cpp.ID), "vector< vector<string> >");
        map.put(new Integer(csharp.ID), "String[][]");
        t_aastring.setTypeMapping(map);

        map = new HashMap();
        map.put(new Integer(java.ID), "int");
        map.put(new Integer(cpp.ID), "int");
        map.put(new Integer(csharp.ID), "int");
        t_int.setTypeMapping(map);

        map = new HashMap();
        map.put(new Integer(java.ID), "int[]");
        map.put(new Integer(cpp.ID), "vector<int>");
        map.put(new Integer(csharp.ID), "int[]");
        t_aint.setTypeMapping(map);

        map = new HashMap();
        map.put(new Integer(java.ID), "long");
        map.put(new Integer(cpp.ID), "long long");
        map.put(new Integer(csharp.ID), "long");
        t_long.setTypeMapping(map);

        HashMap types = new HashMap();

        types.put("String", t_string);
        types.put("String[]", t_astring);
        types.put("String[][]", t_aastring);
        types.put("int", t_int);
        types.put("int[]", t_aint);
        types.put("long", t_long);
        types.put("Matrix2D", t_matrix2d);

        FileReader reader = new FileReader(args[0]);

        ProblemComponentFactory factory = new ProblemComponentFactory();
        ProblemComponent stmt = factory.buildFromXML(reader, true);
        ArrayList messages = stmt.getMessages();

        for(int i = 0; i < messages.size(); i++)
            ((ProblemMessage)messages.get(i)).log(factory.trace);

        if(stmt.isValid()) {
            System.out.println("XML:\n\n" + stmt.toXML());
//            System.out.println("\nHTML (Java):\n\n" + stmt.toHTML(new JavaLanguage()));
//            System.out.println("\nHTML (C++):\n\n" + stmt.toHTML(new CPPLanguage()));
        } else
            System.out.println("Problem statement not valid!");
    }
*/
}

