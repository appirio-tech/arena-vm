package com.topcoder.shared.problem;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.topcoder.shared.netCommon.*;

import java.io.*;

/**
 * This class contains data for a problem. Each problem consists of a number of components. The actual program interface
 * data (className, etc.) of each component is contained in <code>ProblemComponent</code> objects. For single-coder
 * contests, each problem has only one component.
 * 
 * @author Jing Xue
 * @author Hao Kung
 * @version $Id: Problem.java 71771 2008-07-18 05:34:07Z qliu $
 */
public class Problem extends BaseElement implements Element, Serializable, CustomSerializable {
    /** Represents the type of an algorithm problem. */
    public static final int TYPE_SINGLE = 1;

    /** Represents the type of a team algorithm problem. */
    public static final int TYPE_TEAM = 2;

    /** Represents the type of a marathon problem. */
    public static final int TYPE_LONG = 3;

    /**
     * The components within the problem
     * 
     * @see ProblemComponent
     */
    private ProblemComponent[] problemComponents = new ProblemComponent[0];

    /**
     * The unique identifier of the problem
     */
    private int problemId = -1;

    /**
     * The name of the problem
     */
    private String name = "";

    /**
     * The problem type
     */
    private int problemTypeID = -1;

    /**
     * Text for the problem
     */
    private String problemText = "";

    /**
     * The webservices available to the problem
     * 
     * @see WebService
     */
    private WebService[] webServices = new WebService[0];

    /**
     * Returns the cache key for this problem
     * 
     * @return the cache key for this problem
     */
    @JsonIgnore
    public final String getCacheKey() {
        return "Problem." + problemId;
    }

    /**
     * Empty constructor required by custom serialization
     */
    public Problem() {
    }

    /**
     * Utility method to return the cache key given a problem id
     * 
     * @param problemid the unique problem identifier
     * @return the cache key for the problem identifier
     */
    public static String getCacheKey(int problemid) {
        return "Problem." + problemid;
    }

    /**
     * Custom serialization
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeInt(problemComponents.length);
        for (int i = 0; i < problemComponents.length; i++)
            writer.writeObject(problemComponents[i]);
        writer.writeInt(problemId);
        writer.writeString(name);
        writer.writeInt(problemTypeID);
        writer.writeString(problemText);
        writer.writeObjectArray(webServices);
    }

    /**
     * Custom serialization
     */
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        int count = reader.readInt();
        problemComponents = new ProblemComponent[count];
        for (int i = 0; i < count; i++)
            problemComponents[i] = (ProblemComponent) reader.readObject();
        problemId = reader.readInt();
        name = reader.readString();
        problemTypeID = reader.readInt();
        problemText = reader.readString();
        Object[] o_webServices = reader.readObjectArray();
        if (o_webServices == null)
            o_webServices = new Object[0];
        webServices = new WebService[o_webServices.length];
        for (int i = 0; i < o_webServices.length; i++)
            webServices[i] = (WebService) o_webServices[i];
    }

    /**
     * Sets the unique problem identifier
     * 
     * @param problemId the problem identifier
     */
    public void setProblemId(int problemId) {
        this.problemId = problemId;
    }

    /**
     * Returns the unique problem identifier
     * 
     * @return the unique problem identifier
     */
    public int getProblemId() {
        return this.problemId;
    }

    /**
     * Clones the problem object (note: incomplete)
     * 
     * @return the cloned problem
     */
    public Object clone() {
        Problem p = new Problem();
        p.setProblemId(problemId);
        p.setProblemText(problemText);
        p.setProblemComponents(problemComponents);
        return p;
    }

    /**
     * Sets the name of the problem
     * 
     * @param name the name of the problem
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the problem name
     * 
     * @return the problem name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the problem text
     * 
     * @return the problem text
     */
    public String getProblemText() {
        return problemText;
    }

    /**
     * Sets the problem text
     * 
     * @param problemText the problem text
     */
    public void setProblemText(String problemText) {
        this.problemText = problemText;
    }

    /**
     * Returns the components for this problem
     * 
     * @return the components for this problem
     * @see ProblemComponent
     */
    public ProblemComponent[] getProblemComponents() {
        return problemComponents;
    }

    /**
     * Sets the components for this problem
     * 
     * @param problemComponents the components for this problem
     * @see ProblemComponent
     */
    public void setProblemComponents(ProblemComponent[] problemComponents) {
        this.problemComponents = problemComponents;
    }

    /**
     * Returns the problem component considered the main component. ie whose getComponentTypeID() returns
     * ProblemConstants.MAIN_COMPONENT
     * 
     * @return the main component of the problem or null if none found
     * @see ProblemComponent
     * @see ProblemConstants
     */
    @JsonIgnore
    public ProblemComponent getPrimaryComponent() {
        for (int i = 0; i < problemComponents.length; i++) {
            if (problemComponents[i].getComponentTypeID() == ProblemConstants.MAIN_COMPONENT) {
                return problemComponents[i];
            }
        }
        return null;
    }

    /**
     * Returns the problem type identity
     * 
     * @return the problem type identity
     */
    public int getProblemTypeID() {
        return problemTypeID;
    }

    /**
     * Sets the problem type identity
     * 
     * @param problemTypeID the problem type identity
     */
    public void setProblemTypeID(int problemTypeID) {
        this.problemTypeID = problemTypeID;
    }

    /**
     * Gets the Problem Component at the given index
     * 
     * @param i the index position
     * @return the problem component at the given index or null if i doesn't exist (note: will throw an
     *         IndexOutOfBoundsException if < 0)
     */
    public ProblemComponent getComponent(int i) {
        if (i >= problemComponents.length)
            return null;
        return problemComponents[i];
    }

    /**
     * Returns a user readable version of the problem
     * 
     * @return a user readable version of the problem
     */
    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.shared.problem.Problem) [");
        ret.append("problemComponents = ");
        if (problemComponents == null) {
            ret.append("null");
        } else {
            ret.append("{");
            for (int i = 0; i < problemComponents.length; i++) {
                ret.append(problemComponents[i].toString() + ",");
            }
            ret.append("}");
        }
        ret.append(", ");
        ret.append("problemId = ");
        ret.append(problemId);
        ret.append(", ");
        ret.append("name = ");
        if (name == null) {
            ret.append("null");
        } else {
            ret.append(name.toString());
        }
        ret.append(", ");
        ret.append("problemTypeID = ");
        ret.append(problemTypeID);
        ret.append(", ");
        ret.append("problemText = ");
        if (problemText == null) {
            ret.append("null");
        } else {
            ret.append(problemText.toString());
        }
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }

    /**
     * Returns whether the problem is valid or not. (Simply if all the components are valid)
     * 
     * @return true if valid, false if not
     */
    @JsonIgnore
    public boolean isValid() {
        boolean valid = true;
        for (int i = 0; i < problemComponents.length; i++) {
            valid = valid && problemComponents[i].isValid();
        }
        return valid;
    }

    /**
     * Sets the web services for the problem
     * 
     * @param webServices the web services for the problem
     */
    public void setWebServices(WebService[] webServices) {
        this.webServices = webServices;
    }

    /**
     * Returns the web services for the problem
     * 
     * @return the web services for the problem
     */
    public WebService[] getWebServices() {
        return webServices;
    }

    /**
     * Returns an XML representation of the problem TODO: does anyone care this doesn't do anything?
     * 
     * @return blank string
     */
    public String toXML() {
        StringBuffer xml = new StringBuffer();
        return xml.toString();
    }

}
