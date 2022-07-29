package com.topcoder.services.tester.invoke;

import com.topcoder.shared.problem.DataType;

public final class CType {

    private static final byte INT = 1;
    private static final byte STRING = 2;
    private static final byte CHAR = 3;
    static final byte INT_ARRAY = 4;
    private static final byte DOUBLE = 5;
    static final byte STRING_ARRAY = 6;
    static final byte LONG = 7;
    private static final byte BOOLEAN = 8;
    static final byte DOUBLE_ARRAY = 9;
    private static final byte LONG_ARRAY = 10;

    private CType() {
    }

    static boolean isSameType(Object obj, byte type) {
        switch (type) {
        case INT:
            return obj instanceof Integer;
        case STRING:
            return obj instanceof String;
        case CHAR:
            return obj instanceof Character;
        case INT_ARRAY:
            return obj instanceof int[];
        case DOUBLE:
            return obj instanceof Double;
        case STRING_ARRAY:
            return obj instanceof String[];
        case LONG:
            return obj instanceof Long;
        case LONG_ARRAY:
            return obj instanceof long[];
        case BOOLEAN:
            return obj instanceof Boolean;
        case DOUBLE_ARRAY:
            return obj instanceof double[];
        default:
            throw new RuntimeException("unknown type: " + type);
        }
    }

    public static byte convert(String type) {
        if (type.equals("int")) {
            return INT;
        }
        if (type.equals("String")) {
            return STRING;
        }
        if (type.equals("String[]")) {
            return STRING_ARRAY;
        }
        if (type.equals("int[]")) {
            return INT_ARRAY;
        }
        if (type.equals("char")) {
            return CHAR;
        }
        if (type.equals("double")) {
            return DOUBLE;
        }
        if (type.equals("long")) {
            return LONG;
        }
        if (type.equals("long[]")) {
            return LONG_ARRAY;
        }
        if (type.equals("boolean")) {
            return BOOLEAN;
        }
        if (type.equals("double[]")) {
            return DOUBLE_ARRAY;
        }
        throw new RuntimeException("not known type: " + type);
    }

    static byte[] convert(String[] type) {
        int n = type.length;
        byte[] b = new byte[n];
        for (int i = 0; i < n; i++) {
            b[i] = convert(type[i]);
        }
        return b;
    }

    public static byte[] convert(DataType[] types) {
        int n = types.length;
        byte[] b = new byte[n];
        for (int i = 0; i < n; i++) {
            b[i] = convert(types[i].getDescription());
        }
        return b;
    }

    // TEMP
    static String getTypeName(Object object) {
        String typeName;
        if (object instanceof Integer) {
            typeName = "int";
        } else if (object instanceof String) {
            typeName = "String";
        } else if (object instanceof Character) {
            typeName = "char";
        } else if (object instanceof Double) {
            typeName = "double";
        } else if (object instanceof Long) {
            typeName = "long";
        } else if (object instanceof Boolean) {
            typeName = "boolean";
        } else if (object instanceof int[]) {
            typeName = "int[]";
        } else if (object instanceof long[]) {
            typeName = "long[]";
        } else if (object instanceof String[]) {
            typeName = "String[]";
        } else {
            throw new RuntimeException("not known type: " + object.getClass() + " " + object);
        }
        return typeName;
    }

}
