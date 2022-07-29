package com.topcoder.shared.problemParser;

import com.topcoder.shared.problem.Element;
import com.topcoder.shared.problem.TestCase;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

/**
 * This factory builds a <code>TestCase</code> from a node-set representing one.
 */
public class TestCaseFactory
{
    /**
     * Builds a <code>TestCase</code> from a node-set representing one.
     *
     * @param node  The node-set specifying a test case
     * @return A <code>TestCase</code> object representing the same information contained by the given node-set
     * @see TestCase
     */
    static public TestCase build(Node node)
    {
        NodeList nl = node.getChildNodes();
        Node output = ProblemComponentFactory.getChildByName(nl, ProblemComponentFactory.TEST_CASE_OUTPUT);
        Node annotation = ProblemComponentFactory.getChildByName(nl, ProblemComponentFactory.TEST_CASE_ANNOTATION);
        Element ne_annotation = annotation == null ? null : NodeElementFactory.build(annotation);
        String idStr = ProblemComponentFactory.getAttribute(node, "id");
        Integer id = (idStr == null) ? null : Integer.valueOf(idStr);
        boolean example = ProblemComponentFactory.getAttribute(node, "example") != null;
        boolean systemTest = ProblemComponentFactory.getAttribute(node, "systemTest") != null;
        ArrayList inputList = new ArrayList();

        for(int i = 0; i < nl.getLength(); i++) {
            Node n = nl.item(i);

            if(n.getNodeName().equals(ProblemComponentFactory.TEST_CASE_INPUT))
                inputList.add(n.getFirstChild().getNodeValue());
        }

        String[] input = new String[inputList.size()];

        for(int i = 0; i < input.length; i++)
            input[i] = (String)inputList.get(i);
        return new TestCase(id, input, output.getFirstChild().getNodeValue(), ne_annotation, example, systemTest);
    }
}

