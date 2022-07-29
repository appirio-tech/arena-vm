package com.topcoder.shared.problemParser;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.topcoder.shared.problem.DataType;
import com.topcoder.shared.problem.DataTypeFactory;
import com.topcoder.shared.problem.Element;
import com.topcoder.shared.problem.InvalidTypeException;
import com.topcoder.shared.problem.NodeElement;
import com.topcoder.shared.problem.TextElement;

/**
 * This factory can build <code>Element</code>s from arbitrary node-sets, corresponding
 * to structured text.  That is, the intro and spec sections, or the contents of notes,
 * user constraints, and annotations in general.
 *
 * @see Element
 * @author Logan Hanks
 */
public class NodeElementFactory
{
    
    /**
     * Builds an <code>Element</code> appropriate for the given node-set.
     *
     * @param node  The node-set specifying the element
     * @see Element
     */
    static public Element build(Node node) {
        return build(node, node);
    }
    
    static private Element build(Node rootNode, Node node)
    {
        if(node.getNodeType() != Node.ELEMENT_NODE)
            return buildTextNode(rootNode, node);

        String name = node.getNodeName();
        HashMap attributes = new HashMap();
        ArrayList children = new ArrayList();
        StringBuffer text = new StringBuffer(128);
 
        NamedNodeMap map = node.getAttributes();
 
        for(int i = 0; i < map.getLength(); i++) {
            Node n = map.item(i);
  
            attributes.put(n.getNodeName(), n.getNodeValue());
        }
 
        NodeList nl = node.getChildNodes();
 
        for(int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);
 
            if(n.getNodeType() == Node.ELEMENT_NODE) {
                if(n.getNodeName().equals(ProblemComponentFactory.TYPE)) {
                    try {
                        DataType type = DataTypeFactory.getDataType(n.getFirstChild().getNodeValue());

                        children.add(type);
                    } catch(InvalidTypeException ex) {
                    }
                } else
                    children.add(build(rootNode, n));
            } else {
                children.add(buildTextNode(rootNode, n));
            }
            text.append(n.getNodeValue());
        }

        return new NodeElement(name, attributes, children, text.toString());
    }

    private static TextElement buildTextNode(Node rootNode, Node textNode) {
        Node escapedAttr = rootNode.getAttributes().getNamedItem("escaped");
        if (escapedAttr != null && "1".equals(escapedAttr.getNodeValue())) {
            return new TextElement(true, textNode.getNodeValue());
        }
        return new TextElement(textNode.getNodeValue());
    }
}

