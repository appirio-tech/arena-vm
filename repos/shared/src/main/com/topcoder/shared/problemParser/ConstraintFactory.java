package com.topcoder.shared.problemParser;

import com.topcoder.shared.problem.Constraint;
import com.topcoder.shared.problem.ProblemComponent;
import com.topcoder.shared.problem.UserConstraint;

import org.w3c.dom.Node;

/**
 * This factory can construct an appropriate <code>Constraint</code> for a given
 * node-set.
 *
 * @author Logan Hanks
 * @see Constraint
 * @see ProblemComponent
 */
public class ConstraintFactory
{
    /**
     * Builds a <code>Constraint</code> appropriate for the given node-set.
     *
     * @param node  The node-set specifying a constraint
     * @see Constraint
     */
    static public Constraint build(Node node)
    {
        return buildUserConstraint(node);
    }

    /**
     * Builds a <code>UserConstraint</code> from a node-set representing a <code>user-constraint</code> element.
     *
     * @param node  The node-set specifying a constraint
     * @see UserConstraint
     */
    static Constraint buildUserConstraint(Node node)
    {
        return new UserConstraint(NodeElementFactory.build(node));
    }
}

