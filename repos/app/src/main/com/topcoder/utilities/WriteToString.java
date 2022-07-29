package com.topcoder.utilities;

import java.io.*;
import java.util.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Generates and replaces toString methods for classes from the given path.  Be warned that it will break if your class
 * contains "public String toString" anywhere other than a toString method (which will be replaced)
 * If you want to save your old toString method, it wouldn't be hard to make it a comment or somthing
 * look at the very end where the old toString gets removed.  I didn't need it, so I didn't do it.
 *
 * @author Lars Backstrom
 */
public class WriteToString {

    public static void main(String args[]) {
        try {
            File f = new File(args[0]);
            writeToString(f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void writeToString(File f) {
        try {
            if (f.isDirectory()) {
                File[] files = f.listFiles(new FileFilter() {
                    public boolean accept(File file) {
                        return file.getName().endsWith(".java") || file.isDirectory();
                    }
                });
                for (int i = 0; i < files.length; i++) {
                    writeToString(files[i]);
                }
            } else {
                //doesn't support files over 100K
                byte[] b = new byte[100000];
                FileInputStream fis = new FileInputStream(f);
                int read = fis.read(b);
                fis.close();
                parseAndWrite(b, read, f);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    int PRIMITIVE = 1;
    int OBJECT = 2;
    int PRIMITIVE_ARRAY = 3;
    int OBJECT_ARRAY = 4;
    String strubg;
    Integer[] is;
    int ns[];

    static void parseAndWrite(byte[] b, int read, File f) {
        try {
            String path = f.getAbsolutePath();
            path = path.substring(path.indexOf("com"), path.length() - 5);
            path = path.replace('/', '.');
            path = path.replace('\\', '.');
            System.out.println(path);
            Class c = Class.forName(path);
            Field[] fields = c.getDeclaredFields();
            StringBuffer buf = new StringBuffer(1000);
            buf.append("    public String toString(){\n");
            buf.append("        StringBuffer ret = new StringBuffer(1000);\n");
            buf.append("        ret.append(\"(");
            buf.append(path);
            buf.append(") [\");\n");
            for (int i = 0; i < fields.length; i++) {
                if (Modifier.isFinal(fields[i].getModifiers())) continue;
                if (Modifier.isStatic(fields[i].getModifiers())) continue;
                String name = fields[i].getName();
                Class type = fields[i].getType();
                buf.append("        ret.append(\"" + name + " = \");\n");
                if (type.isArray()) {
                    Class componentType = type.getComponentType();
                    buf.append("        if(");
                    buf.append(name);
                    buf.append(" == null){\n");
                    buf.append("            ret.append(\"null\");\n");
                    buf.append("        } else {\n");
                    buf.append("            ret.append(\"{\");\n");
                    buf.append("            for(int i = 0; i<");
                    buf.append(name);
                    buf.append(".length;i++){\n");
                    buf.append("                ret.append(");
                    buf.append(name);
                    buf.append("[i]");
                    if (!componentType.isPrimitive()) {
                        buf.append(".toString()+\",\"");
                    }
                    buf.append(");\n");
                    buf.append("            }\n");
                    buf.append("            ret.append(\"}\");\n");
                    buf.append("        }\n");
                } else if (type.isPrimitive()) {
                    buf.append("        ret.append(");
                    buf.append(name);
                    buf.append(");\n");
                } else {
                    buf.append("        if(");
                    buf.append(name);
                    buf.append(" == null){\n");
                    buf.append("            ret.append(\"null\");\n");
                    buf.append("        } else {\n");
                    buf.append("            ret.append(");
                    buf.append(name);
                    buf.append(".toString());\n");
                    buf.append("        }\n");
                }
                buf.append("        ret.append(\", \");\n");
            }
            buf.append("        ret.append(\"]\");\n");
            buf.append("        return ret.toString();\n");
            buf.append("    }\n");
            String out = new String(b, 0, read);
            int startIndex = out.lastIndexOf("public String toString");
            int lastIndex = -1;
            if (startIndex != -1) {
                int depth = -1;
                for (int i = startIndex; true; i++) {
                    if (b[i] == '{') {
                        if (depth == -1)
                            depth += 2;
                        else
                            depth++;
                    }
                    if (b[i] == '}') depth--;
                    if (depth == 0) {
                        lastIndex = i;
                        break;
                    }
                }
            } else {
                startIndex = out.lastIndexOf('}');
                lastIndex = out.lastIndexOf('\n', startIndex);
            }
            out = out.substring(0, out.lastIndexOf('\n', startIndex) + 1) + buf.toString() + out.substring(out.indexOf('\n', lastIndex) + 1);
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(out.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
