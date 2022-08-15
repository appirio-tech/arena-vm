package com.topcoder.client.ui.impl.component;

import java.awt.GridBagConstraints;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.topcoder.client.ui.UIComponent;
import com.topcoder.client.ui.UIComponentException;
import com.topcoder.client.ui.UIEventListener;
import com.topcoder.client.ui.event.*;

public abstract class UIAbstractComponent implements UIComponent {
    private Object component;
    private UIComponent parent;
    private boolean created = false;

    protected List propertyOrder = new ArrayList();
    protected Map properties = new HashMap();
    protected List children = new ArrayList();
    protected List constraints = new ArrayList();

    protected abstract Object createComponent();

    protected void initialize() throws UIComponentException {
        component = createComponent();

        created = true;
    }

    public void create() throws UIComponentException {
        if (created) return;

        initialize();

        for (Iterator iter = propertyOrder.iterator(); iter.hasNext();) {
            String key = (String) iter.next();
            setPropertyImpl(key, properties.get(key));
        }

        for (int i = 0; i < children.size(); ++i) {
            addChildImpl((UIComponent) children.get(i), (GridBagConstraints) constraints.get(i));
        }

        propertyOrder = null;
        properties = null;
        children = null;
        constraints = null;
    }

    public void destroy() {
        if (created) {
            destroyImpl();
        }
    }

    protected void destroyImpl() {
    }

    public void addChild(UIComponent component, GridBagConstraints constraints) throws UIComponentException {
        component.setParent(this);
        if (!created) {
            children.add(component);
            this.constraints.add(constraints);
        } else {
            addChildImpl(component, constraints);
        }
    }

    protected abstract void addChildImpl(UIComponent component, GridBagConstraints constraints) throws UIComponentException;

    public void setProperty(String name, Object value) throws UIComponentException {
        if (!created) {
            properties.put(name.toLowerCase(), value);
            propertyOrder.add(name.toLowerCase());
        } else {
            try {
                setPropertyImpl(name, value);
            } catch (ClassCastException e) {
                throw new UIComponentException("The property value class is not valid for property '" + name + "'.", e);
            }
        }
    }

    protected void setPropertyImpl(String name, Object value) throws UIComponentException {
        throw new UIComponentException("The property '" + name + "' is not writeable.");
    }

    public Object getProperty(String name) throws UIComponentException {
        if (!created) {
            create();
        }

        return getPropertyImpl(name);
    }

    protected Object getPropertyImpl(String name) throws UIComponentException {
        throw new UIComponentException("The property '" + name + "' is not readable.");
    }

    public void addEventListener(String name, UIEventListener listener) throws UIComponentException {
        if (!created) {
            create();
        }

        try {
            addEventListenerImpl(name, listener);
        } catch (ClassCastException e) {
            throw new UIComponentException("The event listener class is not valid for event '" + name + "'.", e);
        }
    }

    protected void addEventListenerImpl(String name, UIEventListener listener) throws UIComponentException {
        throw new UIComponentException("The event '" + name + "' does not exist.");
    }

    public void removeEventListener(String name, UIEventListener listener) throws UIComponentException {
        if (!created) {
            create();
        }

        try {
            removeEventListenerImpl(name, listener);
        } catch (ClassCastException e) {
            throw new UIComponentException("The event listener class is not valid for event '" + name + "'.", e);
        }
    }

    protected void removeEventListenerImpl(String name, UIEventListener listener) throws UIComponentException {
        throw new UIComponentException("The event '" + name + "' does not exist.");
    }

    public Object performAction(String name) throws UIComponentException {
        return performAction(name, null);
    }

    protected void assertNull(String name, Object[] args) throws UIComponentException {
        if (args != null) {
            throw new UIComponentException("The action '" + name + "' must have no argument.");
        }
    }

    protected void assertArgs(String name, Object[] args, Class[] expected) throws UIComponentException {
        if ((args == null) || (args.length != expected.length)) {
            throw new UIComponentException("The action '" + name + "' must have " + expected.length + " argument(s).");
        }

        for (int i = 0; i < expected.length; ++i) {
            if ((args[i] != null) && !expected[i].isInstance(args[i])) {
                throw new UIComponentException("The " + (i + 1) + " argument of the action '" + name + "' must be " +
                                               expected[i] + ".");
            }
        }
    }

    public Object performAction(String name, Object[] args) throws UIComponentException {
        if ((args != null) && (args.length == 0)) {
            args = null;
        }

        if (!created) {
            create();
        }

        try {
            return performActionImpl(name, args);
        } catch (NullPointerException e) {
            throw new UIComponentException("One argument of action " + name + " cannot be null.", e);
        }
    }

    private static class CustomObjectInputStream extends ObjectInputStream {
        private ClassLoader loader;

        public CustomObjectInputStream(InputStream is, ClassLoader loader) throws IOException {
            super(is);

            this.loader = loader;
        }

        protected Class resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
            try {
                return Class.forName(desc.getName(), false, loader);
            } catch (Exception e) {
                return super.resolveClass(desc);
            }
        }
    }

    protected Object performActionImpl(String name, Object[] args) throws UIComponentException {
        if ("clone".equalsIgnoreCase(name)) {
            if (component instanceof Serializable) {
                // Clone the object via serialization/deserialization
                Object obj;

                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(component);
                    oos.close();
                    ObjectInputStream ois = new CustomObjectInputStream(new ByteArrayInputStream(baos.toByteArray()), component.getClass().getClassLoader());
                    obj = ois.readObject();
                    ois.close();
                } catch (IOException e) {
                    throw new UIComponentException("I/O error occurred.", e);
                } catch (ClassNotFoundException e) {
                    throw new UIComponentException("The class cannot be found.", e);
                }

                return obj;
            }
        }
 
        throw new UIComponentException("The action '" + name + "' cannot be performed.");
    }

    public Object getEventSource() {
        if (!created) {
            create();
        }

        return component;
    }

    public void setParent(UIComponent parent) {
        this.parent = parent;
    }

    public UIComponent getParent() {
        return parent;
    }
}
