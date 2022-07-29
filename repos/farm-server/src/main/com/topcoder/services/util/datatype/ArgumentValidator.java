package com.topcoder.services.util.datatype;

/**
 * ArgumentValidator.java
 *
 * Created on June 19, 2001
 */

import java.util.ArrayList;

import com.topcoder.netCommon.contest.Matrix2D;
import com.topcoder.shared.problem.DataType;

/**
 * The purpose of this class is to be able to validate whether
 * a user entered argument is a valid argument.  For instance,
 * "hello" is not an Integer.
 *
 * @author Jason Stanek
 * @version 1.0
 */

final class ArgumentValidator {

    private ArgumentValidator() {
    }

    /**
     * Validate is used to take the String/ArrayList arguments and convert them into
     * the correct type.  If the string is not the correct type, then an exception
     * is thrown.
     *
     * @param argTypes        Strings containing the argument types
     * @param args            The arguments in either ArrayLists/Strings
     *
     * @return                 ArrayList contains validated arguments in their appropriate form
     * @exception              Exception If an argument was not able to be parsed into the argType
     */
    static Object[] validate(DataType[] argTypes, Object[] args) throws InvalidArgumentTypeException {
        Object[] validatedArgs = new Object[args.length];
        for (int i = 0; i < argTypes.length; i++) {
            //System.out.println(argTypes.get(i).toString() + " : " + args.get(i).toString());
            try {
                validatedArgs[i] = validate(argTypes[i].getDescription(), args[i]);
            } catch (Exception e) {
                //e.printStackTrace();
                throw new InvalidArgumentTypeException("Argument #" + (i + 1) + " must be an " + e.getMessage());
            }
        }
        return validatedArgs;
    }

    /**
     * Validate is used to validate one argument
     *
     * @param argType         Argument type... i.e. int,int*,String,ArrayList,etc...
     * @param arg             Argument the argument to be parsed into the Argument type
     *
     * @return                 Object that was created of type argType by parsing arg
     * @exception              Exception If the argument was not able to be parsed into an argType
     */
    private static Object validate(String argType, Object arg) throws Exception {
        if (argType.indexOf("*") != -1) {
            ArrayList validatedArgs = new ArrayList();
            for (int i = 0; i < ((ArrayList) arg).size(); i++) {
                validatedArgs.add(validate(argType.substring(0, argType.length() - 1), ((ArrayList) arg).get(i)));
            }
            return validatedArgs;
        } else {
            if (argType.compareTo("char") == 0) {
                if (((String) arg).length() == 1) {
                    return new Character(((String) arg).charAt(0));
                } else {
                    throw new Exception("Character");
                }
            } else if (argType.compareTo("short") == 0) {
                Short s;
                try {
                    s = new Short((String) arg);
                } catch (Exception e) {
                    throw new Exception("Short");
                }
                return (s);
            } else if (argType.compareTo("int") == 0 ||
                    argType.compareTo("Integer") == 0) {
                Integer i;
                try {
                    i = new Integer((String) arg);
                } catch (Exception e) {
                    throw new Exception("Integer");
                }
                return (i);
            } else if (argType.compareTo("long") == 0) {
                Long l;
                try {
                    l = new Long((String) arg);
                } catch (Exception e) {
                    throw new Exception("Long");
                }
                return (l);
            } else if (argType.compareTo("float") == 0) {
                Float f;
                try {
                    f = new Float((String) arg);
                } catch (Exception e) {
                    throw new Exception("Float");
                }
                return (f);
            } else if (argType.compareTo("double") == 0) {
                Double d;
                try {
                    d = new Double((String) arg);
                } catch (Exception e) {
                    throw new Exception("Double");
                }
                return (d);
            } else if (argType.compareTo("ArrayList") == 0) {
                ArrayList t;
                try {
                    t = new ArrayList((ArrayList) arg);
                } catch (Exception e) {
                    throw new Exception("ArrayList");
                }
                return (t);
            } else if (argType.compareTo("String") == 0) {
                String t;
                try {
                    t = new String((String) arg);
                } catch (Exception e) {
                    throw new Exception("String");
                }
                return (t);
            } else if (argType.compareTo("boolean") == 0) {
                Boolean t;
                try {
                    t = new Boolean((String) arg);
                } catch (Exception e) {
                    throw new Exception("bool");
                }
                return (t);
            } else if (argType.compareTo("int[]") == 0) {
                int[] t;
                try {
                    ArrayList x = new ArrayList((ArrayList) arg);
                    t = new int[x.size()];
                    for (int i = 0; i < x.size(); i++)
                        t[i] = Integer.parseInt((String) x.get(i));

                } catch (Exception e) {
                    throw new Exception("int[]");
                }
                return (t);
            } else if (argType.compareTo("long[]") == 0) {
                long[] t;
                try {
                    ArrayList x = new ArrayList((ArrayList) arg);
                    t = new long[x.size()];
                    for (int i = 0; i < x.size(); ++i) {
                        t[i] = Long.parseLong((String) x.get(i));
                    }
                } catch (Exception e) {
                    throw new Exception("long[]");
                }
                return (t);
            } else if (argType.compareTo("double[]") == 0) {
                double[] t;
                try {
                    ArrayList x = new ArrayList((ArrayList) arg);
                    t = new double[x.size()];
                    for (int i = 0; i < x.size(); i++)
                        t[i] = Double.parseDouble((String) x.get(i));
                } catch (Exception e) {
                    throw new Exception("double[]");
                }
                return (t);
            } else if (argType.compareTo("String[]") == 0) {
                String[] t;
                try {
                    ArrayList x = new ArrayList((ArrayList) arg);
                    t = new String[x.size()];
                    for (int i = 0; i < x.size(); i++)
                        t[i] = (String) x.get(i);
                } catch (Exception e) {
                    throw new Exception("String[]");
                }
                return (t);
            } else if (argType.compareTo("Matrix2D") == 0) {
                Matrix2D mat;
                try {
                    mat = (Matrix2D) arg;
                } catch (Exception e) {
                    throw new Exception("Matrix2D");
                }
                return (mat);
            } else {
                System.out.println("ARGUMENT NOT OF RECOGNIZED TYPE");
            }
        }
        return null;
    }

}
