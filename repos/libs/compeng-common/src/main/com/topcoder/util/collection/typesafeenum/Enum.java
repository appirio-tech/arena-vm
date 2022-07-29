/* 
 * TCS Typesafe Enum pattern implementation
 *
 * @(#)Enum.java
 *
 * Copyright (c) 2003 TopCoder, Inc.  All rights reserved
 */
package com.topcoder.util.collection.typesafeenum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * <p> This class provides as much support as is possible in Java for correct
 * implementation of the &quot;typesafe enum&quot; pattern.
 * With some care, Java programs can make use of C-style enums,
 * with all the benefits of type safety, but with even more functionality.
 * The way to accomplish this is well-understood; this class encapsulates as
 * much of these best practices as possible, so that applications create
 * enumerated types with minimal work by subclassing Enum. </p>
 * <p> An enumerated type can be created simply by subclassing Enum,
 * making the constructor private, and providing a few
 * <code>public static final</code> instances of the type: </p>
 * <p> <pre>
 *  public class MyBool extends Enum {
 *     public static final MyBool TRUE = new MyBool();
 *     public static final MyBool FALSE = new MyBool();
 *     private MyBool() { }
 * }
 * </pre> </p>
 * <p> But more is possible; the enumerated type classes can include properties,
 * methods, logic, etc. In fact as long as the class is written to be
 * immutable, anything is allowed. Note also that the ordering of the
 * declaration of the enumerated values corresponds to their sort ordering,
 * which also resembles the behavior of C-style enums:
 * </p> <pre>
 * public class Suit extends Enum {
 *    public static final Suit CLUBS = new Suit(&quot;Clubs&quot;, SuitColor.BLACK);
 *    public static final Suit DIAMONDS = new Suit(&quot;Diamonds&quot;, SuitColor.RED);
 *    public static final Suit HEARTS = newSuit(&quot;Hearts&quot;, SuitColor.RED);
 *    public static final Suit SPADES = new Suit(&quot;Spades&quot; SuitColor.BLACK);
 *    private final String name;
 *    private final SuitColor color;
 *    private Suit(String name, SuitColor color) {
 *       this.name = name;
 *       this.color = color;
 *    }
 *    public String getName() {  return name; }
 *    public SuitColor getColor() { return color; }
 *    public String toString() { return getName(); }
 * }
 * </pre> </p> <p> <pre>
 * public class SuitColor extends Enum {
 *    public static final SuitColor RED = new SuitColor(&quot;red&quot;);
 *    public static final SuitColor BLACK = new SuitColor(&quot;black&quot;);
 *    private final String color;
 *    private SuitColor(String color) {  this.color = color; }
 *    public String getColor() { return color; }
 *    public String toString() { return getColor(); }
 * }
 * </pre> </p>
 * <p> Enum does not override equals() or hashCode(), since the default
 * implementation in java.lang.Object is sufficient.
 * Subclasses may override these methods, though this is typically not
 * necessary. </p>
 * <p> Note that JDK 1.5 will support enumerations in the Java
 * language itself; this class is intended for use with
 * JDK 1.4 and earlier. </p>
 *
 * @author TCSDEVELOPER
 * @version 1.0
 * @see <A href="http://developer.java.sun.com/developer/Books/shiftintojava/page1.html#replaceenums">
 * Shift Into Java(Sun)</a>
 */
public abstract class Enum implements Comparable, Serializable {

    ///////////////////////////////////////
    // attributes

    // Error messages to be displayd
    // Moved to separate contants to allow easy localisation
    private static final String ERR_ENUM_CLASS_NULL =
        "enumClass can not be null";
    private static final String ERR_ENUM_SUBCLASS =
        "enumClass is not a subclass of Enum";
    private static final String ERR_STRING_VALUE_NULL =
        "stringValue can not be null";
    private static final String ERR_COMPARE_DIFFERENT =
        "unable to compare Enum for different types";

    /**
     * <p> This data structure is used to store all enumeration values used by
     * all subclasses of Enum. It maps subclasses (represented by Class objects) 
     * to ArrayLists. Each ArrayList has all enumeration values for that subclass, 
     * ordered by ordinal value; These ArrayLists are used frequently to look 
     * up enumeration values and return ordered collections of enumeration values.
     * </p> 
     * <p> This Map is synchronized, to be safe, to avoid problems when two Enum
     * subclasses are simultaneously loaded. </p> 
     * <p>ArrayLists in this Map, however, do not need to be synchronized; they 
     * are each modified by exactly one thread (the one that loads a given Enum 
     * subclass) when the subclass is initialized, and never modified again. 
     * </p>
     * @see #getEnumByOrdinal(int,Class)
     * @associates ArrayList
     */
    private static final Map enumsByClass =
        Collections.synchronizedMap(new HashMap());

    /**
     * <p> This data structure is used to store Map's of toString() -&gt;
     * Enum  values used by all subclasses of Enum.
     * It maps subclasses (represented by Class objects) to Maps.
     * Each Map has all enumeration values for that subclass,
     * keyed by toString() value; that is, the string representation values
     * (String's)  are mapped to the actual instances of the Enum subclass.
     * These Maps are used and filled only during getEnumByStringValue() to
     * look up enumeration instances for corresponding Class'es. </p>
     * <p> This Map is synchronized, to be safe, to avoid problems when two
     * searches for the same Enum subclasses are
     * simultaneously started in different threads. </p>
     * <p> By default it's not filled with data, but each time
     * <code>getEnumByStringValue(String,Class)</code> called for a new class
     * it create a Map to answer all future queries for the same class</p>
     * @see #getEnumByStringValue(String,Class)
     * @associates Map
     */
    private static final Map stringSearchByClass =
        Collections.synchronizedMap(new HashMap());

    /**
     * <p> The ordinal value that uniquely identifies an instance of a
     * subclass of Enum among all other instances of the same class.
     * This value is assigned in the Enum constructor, which
     * ensures that the value is unique. Furthermore, the values assigned are
     * always increasing, starting from an initial value of 0. This means that
     * the enumeration values defined first sort before enumeration values
     * assigned later, which makes the ordering of enumeration values natural
     * and consistent with C/C++ enums. </p>
     * <p>
     * This value is used throughout Enum to efficiently compare Enum subclass
     * instances.
     * </p>
     */
    private final int ordinal;

    ///////////////////////////////////////
    // operations

    /**
     * <p> This constructor adds a newly-created instance of an Enum subclass
     * to Enum's internal data structures, and in the process,
     * assigns it an ordinal value that is unique among all instances of
     * that subclass, starting from 0. </p>
     */
    protected Enum() {
        synchronized(enumsByClass) {
            if (!enumsByClass.containsKey(this.getClass())) {
                enumsByClass.put(this.getClass(), new ArrayList());
            }
        }
        final ArrayList allEnums =
            (ArrayList)enumsByClass.get(this.getClass());
        this.ordinal = allEnums.size();
        allEnums.add(this);
    } // end Enum

    /**
     * <p> Returns this object's ordinal value, which is a nonnegative number
     * unique among all instances of the same Enum subclass. </p>
     * @return this object's ordinal value
     */
    public int getOrdinal() {
        return this.ordinal;
    }
    // end getOrdinal

    /**
     * <p> Find an instance of the given Enum subclass whose ordinal equals
     * the given value. If there is no such instance, null is returned. </p>
     * @param ordinal value to search for
     * @param enumClass Class of Enum to search for
     * @return an instance of the given Enum subclass whose ordinal equals the
     * given value, or null if there is no such instance
     * @throws IllegalArgumentException if enumClass is null or does not
     * specify a subclass of Enum
     */
    public static Enum getEnumByOrdinal(int ordinal, Class enumClass)
        throws IllegalArgumentException {
            validateEnumClass(enumClass);
            final ArrayList allEnums = (ArrayList)enumsByClass.get(enumClass);
            if (allEnums == null) {
                return null;
            }
            if ((ordinal >= allEnums.size()) || (ordinal < 0)) {
                return null;
            }
            return (Enum)allEnums.get(ordinal);
    } // end getEnumByOrdinal

    /**
     * <p> Find an instance of the given Enum subclass
     * whose String representation (as given by toString()) equals the
     * given String. If there is no such instance, this method returns null. </p>
     * <p> This method runs in <i>O(n)</i> for a first call for each class of
     * Enums and construct a Map of toString() -&gt; Enum values, next time it
     * will answer queries for the same class in <i>O(1)</i></p>
     * @param stringValue String representation to search for
     * @param enumClass Class of Enum to search for
     * @return an instance of the given Enum subclass whose String
     * representation equals the given String, or null if
     * there is no such instance
     * @throws IllegalArgumentException if string value is null, if enumClass
     * is null or does not specify a subclass of Enum
     */
    public static Enum getEnumByStringValue(String stringValue,
        Class enumClass) {
            validateEnumClass(enumClass);
            if (stringValue == null) {
                throw new IllegalArgumentException(ERR_STRING_VALUE_NULL);
            }
            final ArrayList allEnums = (ArrayList)enumsByClass.get(enumClass);
            if (allEnums == null) {
                return null;
            }
            Map searchTable = (Map)stringSearchByClass.get(enumClass);
            //If not found - add safely to list
            if (searchTable == null) {
                synchronized(stringSearchByClass) {
                    if (stringSearchByClass.containsKey(enumClass)) {
                        //probably somebody has added just already in another thread
                        searchTable = (Map)stringSearchByClass.get(enumClass);
				    } else {
                        // Add new
                        //  NOTE 1: In case of memory shortage use
                        //  search_table = Collections.synchronizedMap(new
                        // WeakHashMap(all_enums.size()));
                        //  and uncomment NOTE 2
                        searchTable = new Hashtable(allEnums.size());
                        stringSearchByClass.put(enumClass, searchTable);
                    }
                }
            }
            // In case if our Map was able to answer - return value ASAP
            // without using additional synchronization
            final Enum ret = (Enum)searchTable.get(stringValue);
            if (ret != null) {
                return ret;
            }
            // If not - verify if our cache is up to date and try again
            // Worth case is that we will search twice for an item not available :o(
            synchronized(searchTable) {
                if (searchTable.size() != allEnums.size()) {
                    for (int i = 0; i < allEnums.size(); i++) {
                        final Enum elem = (Enum)allEnums.get(i);
                        final String key = elem.toString();
                        // Preffered way to handle null values
                        // ignore them as nobody will be able
                        // to search for them
                        if (key != null) {
                            searchTable.put(key, elem);
                        }
                        // NOTE 2: See NOTE 1
                        // if(stringValue.equals(key)) ret = elem;
                        // and at the end of method simply return it
                    }
                }
            }
            return (Enum)searchTable.get(stringValue);
    } // end getEnumByStringValue

    /**
     * <p>Returns a sorted, unmodifiable List of all enumeration value
     * instances of the given Enum subclass. Ordering is
     * determined by the compareTo() method; the default implementation here
     * bases the ordering on ordinal value. </p>
     * <p>This method use <code>java.util.Collections.sort()</code> to sort
     * value on each invocation as result it will run in <i>O(n log n)</i>
     * </p>
     * @param enumClass Class of Enum to get List
     * @return an sorted, unmodifiable List of all enumeration value instances
     * of the given Enum subclass
     * @throws IllegalArgumentException if enumClass is null or does not
     * specify a subclass of Enum
     */
    public static List getEnumList(Class enumClass)
        throws IllegalArgumentException {
            validateEnumClass(enumClass);
            final ArrayList allEnums = (ArrayList)enumsByClass.get(enumClass);
            if (allEnums == null) {
                return Collections.unmodifiableList(new ArrayList());
            }
            ArrayList retList = new ArrayList(allEnums);
            Collections.sort(retList);
            return Collections.unmodifiableList(retList);
    } // end getEnumList

    /**
     * <p> Defines the relative order of this enumeration value
     * and another one of the same type.</p>
     * <p>This implementation defines an ordering that is
     * determined by ordinal values, and this method returns a negative
     * value, positive value or zero as this enumeration value's ordinal is less
     * than, greater than, or equal to the other's ordinal.</p>
     * <p> This method only defines an ordering between two enumeration values
     * that are instances of the same subclass of Enum.
     * Attempts to compare different classes, or non-Enum values, will result
     * in a ClassCastException. </p>
     * <p> Subclasses may override this method if desired. </p>
     * @param o enumeration Object to compare to
     * @return a negative value, positive value or zero as this enumeration
     * value's ordinal is less than, greater than,
     * or equal to the given enumeration value's ordinal
     * @throws ClassCastException if the given object is not an instance of
     * the same class as this object
     */
    public int compareTo(Object o) throws ClassCastException {
        if (!this.getClass().isInstance(o)) {
            throw new ClassCastException(ERR_COMPARE_DIFFERENT);
        }
        final Enum eObj = (Enum)o;
        if (this.getOrdinal() < eObj.getOrdinal()) {
            return -1;
        }
        if (this.getOrdinal() > eObj.getOrdinal()) {
            return 1;
        }
        return 0;
    } // end compareTo

    /**
     * <p> This method is defined so that Enum subclasses will work properly
     * with Java's serialization/deserialization mechanism.</p>
     * <p> Without it, deserializing a serialized instance of an Enum subclass
     * would create a new instance separate from the enumerated set of values,
     * which is undesirable. This method ensures that the serialization
     * mechanism actually returns one of the existing instances, instead of
     * some new object. </p>
     * <p> After deserialization, this object has the same class and ordinal
     * value as the original serialized object. This is enough information to
     * look up the currently-existing instance that represents the same
     * enumeration value. So, this method simply looks up and returns that
     * existing instance using getEnumByOrdinal(). </p>
     * <p>This method cannot be private or else it will not be available to the
     * deserialization mechanism when deserializing subclasses. </p>
     * @return an existing enumeration value corresponding to a serialized
     * enumeration value
     * @see #getEnumByOrdinal(int,Class)
     */
    protected final Object readResolve() {
        try {
            return Enum.getEnumByOrdinal(this.getOrdinal(), this.getClass());
        } catch (IllegalArgumentException iae) {
            return null;
        }
    } // end readResolve

    /**
     * <p> This method check if <code>enumClass</code> param value provided
     * is valid. This mean that it can not be <code>null</code> and must
     * specify a subclass of Enum</p>
     * <p>Created as separate to accomlish decoupling</p>
     * @param enumClass Class object to check if valid param for methods
     * @throws IllegalArgumentException if enumClass is null or does not
     * specify a subclass of Enum
     * @see #getEnumByOrdinal(int,Class)
     * @see #getEnumList(Class enumClass)
     * @see #getEnumByStringValue(String stringValue, Class enumClass)
     */
    private static final void validateEnumClass(Class enumClass)
        throws IllegalArgumentException {
            if (enumClass == null) {
                throw new IllegalArgumentException(ERR_ENUM_CLASS_NULL);
            }
            if (!(Enum.class.isAssignableFrom(enumClass))) {
                throw new IllegalArgumentException(ERR_ENUM_SUBCLASS);
            }
    } // end validateEnumClass
} // end Enum
