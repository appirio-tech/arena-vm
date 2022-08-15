package com.topcoder.shared.security;

/**
 * Minimal implementation of Resource.
 *
 * @author Ambrose Feinstein,dok
 * @version $Id$
 */
public class SimpleResource implements Resource {


    private String name;
    private static final long serialVersionUID = 6349144183436881853L;

    public SimpleResource(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleResource that = (SimpleResource) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    public int hashCode() {
        return (name != null ? name.hashCode() : 0);
    }
}
