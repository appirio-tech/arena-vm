package com.topcoder.services.tester.common;

import java.util.HashMap;
import java.util.Map;

import com.topcoder.shared.util.logging.Logger;

public final class TCClassLoader extends ClassLoader {
    private Logger logger = null;
    private final Map localClasses;

    private Class mainClass;

    private ClassLoader pClassLoader;

    //constructor to initialize the local cache
    public TCClassLoader(Map classList) {
        this.localClasses = new HashMap(classList);
        this.pClassLoader = getClass().getClassLoader();
    }
    /*
    public TCClassLoader() {
        localClasses=new HashMap();
    }
    */

    public Class loadClass(String className) {
/*    Class c = null;
    try {
      c = loadClass(className, true);
    } catch (Exception e) { e.printStackTrace(); }

    return c;
*/
//    System.out.println("TCCL.loadClass() called");
//    System.out.println("LOCAL CLASSES: " + localClasses);
//System.out.println("loadClass() -- className: " + className);
        return findClass(className);
    }

    // the first call to TCClassLoader.findClass is for the main class
    // this is to make sure that only that class is marked as main
    private boolean nested = false;

    /**
     * FindClass will attempt to retrieve the bytes of the className from
     * the local class file cache. If the bytes exist, defineClass is called
     * to load the class. If the bytes do not exist, the method returns null.
     */
    public Class findClass(String className) {
        //System.out.println("Inside TCCL.findClass()");
        //System.out.println("localClasses: " + localClasses);
        //System.out.println("className: " + className);
        Class c = null;
        byte[] b = (byte[]) localClasses.get(className);
//System.out.println("after the get");
        if (b != null) {
//System.out.println("B was not null");

            // defineClass may cause other classes to be loaded
            boolean isnested = nested;
            nested = true;

            try {
                c = defineClass(className, b, 0, b.length);
            } catch (java.lang.Error e) {
                error("CLASS: " + className + " not found", e);
            }

            // restore previous nested status
            nested = isnested;

            if ((mainClass == null) && !nested && checkMainClass(className)) {
                mainClass = c;
            }
        } else {
//System.out.println("B was null");
            try {
                return pClassLoader.loadClass(className);
            } catch (Exception e) {
                error(e);
            }
//      return null;
        }
        return c;

    }

    public Class getMainClass() {
        return mainClass;
    }

/*
  public Class loadTheClass(String className) {
    try {
      mainClass = loadClass(className, true);
      return mainClass;
    }
    catch (ClassNotFoundException e) {
      e.printStackTrace();
      return null;
    }
  }

  public Class findClass(HashMap classFiles)
  {
//System.out.println("TCCL:FindClass(HashMap) called...");
    String name = "";
    byte[] b = null;
    Class c = null;
    //Class mainClass = null;

    Set keys = classFiles.keySet();
    Iterator iterator = keys.iterator();
    boolean firstTime = true;
*/
/*
    while(iterator.hasNext()) {
      name = (String)iterator.next();
      b = (byte[])classFiles.get(name);

      c = defineClass(name, b, 0, b.length);
      resolveClass(c);

      if(checkMainClass(name)) {
        mainClass = c;
      }

    }
*/

/*
    ArrayList keyArrList = new ArrayList();
    while(iterator.hasNext()) {
      name = (String)iterator.next();
      keyArrList.add(name);
    }

    Collections.sort(keyArrList);
    //Collections.reverse(keyArrList);


    for(int i=0; i<keyArrList.size(); i++) {
      name = (String)keyArrList.get(i);
      //System.out.println("CLASS NAME: " + name);
      b = (byte[])classFiles.get(name);

      if(findLoadedClass(name) == null)
      {
//System.out.println("TRYING TO DEFINE CLASS: " + name);
        try {
          c = defineClass(name, b, 0, b.length);
        } catch (Exception e) { }
        if(firstTime) {
          resolveClass(c);
          firstTime = false;
        }
      }

      if(checkMainClass(name)) {
        mainClass = c;
      }
    }
*/


/*
    b = (byte[]) classFiles.get("u104366.c1002.r1002.p40.StringDup$F");
    c = defineClass("u104366.c1002.r1002.p40.StringDup$F", b, 0, b.length);
    resolveClass(c);

    b = (byte[]) classFiles.get("u104366.c1002.r1002.p40.StringDup$EE");
    c = defineClass("u104366.c1002.r1002.p40.StringDup$EE", b, 0, b.length);
    resolveClass(c);

    b = (byte[]) classFiles.get("u104366.c1002.r1002.p40.StringDup");
    c = defineClass("u104366.c1002.r1002.p40.StringDup", b, 0, b.length);
    resolveClass(c);
    mainClass = c;
*/
/*
    int classesLeftToLoad = keyArrList.size();
//System.out.println("INITIALLY, CLASSES LEFT TO LOAD EQUALS " + classesLeftToLoad);
    while(classesLeftToLoad > 0) {
//System.out.println("BEGINNING OF WHILE...");
      for(int i=0; i<keyArrList.size(); i++)
      {
//System.out.println("BEGINNING OF FOR WITH I = " + i + "...");
        name = (String)keyArrList.get(i);
        //System.out.println("CLASS NAME: " + name);
        b = (byte[])classFiles.get(name);

        Class tmp = findLoadedClass(name);
        if(tmp == null)
        {
          //System.out.println("TRYING TO DEFINE CLASS: " + name);
          try {
            c = defineClass(name, b, 0, b.length);
            classesLeftToLoad--;
            //System.out.println("CLASSES LEFT TO LOAD NOW EQUALS " + classesLeftToLoad);
//            resolveClass(c);
            if(checkMainClass(name))
              mainClass = c;

          } catch(java.lang.LinkageError e) { e.printStackTrace(); }

          //System.out.println("AFTER CATCH I = " + i);
        }
        else
        {
          if(name.equals("u104366.c1002.r1002.p40.StringDup$F"))
            tmp = defineClass(name, b, 0, b.length);
          //System.out.println("RESOLVING CLASS: " + name);
          resolveClass(tmp);
        }

      }
//System.out.println("AFTER FOR...");
    }

    resolveClass(mainClass);
*/
    //  return mainClass;
    //}

    /*
    public Class findClass(byte[] b, String name) {
        //System.out.println("FIND CLASS CALLED: " + name);
        return defineClass(name, b, 0, b.length);
    }
    */


    /*
    public Class findTCClass(String name)
    {
      FileInputStream fi = null;
      Class c = null;
      try
      {
        //System.out.println("findTCClass finding class: " + name);
        String path = name.replace('.', '/');
        fi = new FileInputStream(ApplicationServer.BASE_DIR + "/" + path + ".class");
        byte[] classBytes = new byte[fi.available()];
        fi.read(classBytes);
        fi.close();
        //definePackage(name);
        c = defineClass(name, classBytes, 0, classBytes.length);
      }catch(Exception e){
        e.printStackTrace();
      }
      return c;
    }
    */

    /**
     * Method used to check if a class is an inner class or the main class.
     * Assumes that if the name does not contain a $, then it is the main class.
     *
     * author ademich
     *
     * @param name - String - The class name.
     * @return boolean - true if the name is not of an inner class, flase otherwise.
     **/
    private static boolean checkMainClass(String name) {
        if (name == null) {
            return false;
        }

        if (name.indexOf("$") == -1) {
            return true;
        } else {
            return false;
        }

    }

/*
  public Class loadClass(String name,
                            boolean resolve) throws ClassNotFoundException
    {
    Class thisclass;
    //System.out.println("loadClass: "+name);
    if (!classes.containsKey(name))
        {
        // try original
	try
	  {
          thisclass=findSystemClass(name);
          classes.put(name,thisclass);
          }
        catch (ClassNotFoundException e)
       {
   // not a system class
       try // file with no extension in current dir?
         {
         FileInputStream fis = new FileInputStream(name);
         byte bytecodes[] = new byte[fis.available()];
         fis.read(bytecodes);
         classes.put(name,defineClass(name,bytecodes,0,bytecodes.length));
         }
        catch (IOException e1)
          {
          throw new ClassNotFoundException();
          }
         }
        }
        if (resolve)
          resolveClass((Class)classes.get(name));
        return (Class) classes.get(name);
       }


      }
*/

    private void error(Throwable t) {
        error("", t);
    }

    private void error(Object message, Throwable t) {
        if (logger == null) {
            try {
                logger = Logger.getLogger(TCClassLoader.class);
            } catch (Throwable e) {
                //If we can't get the logger, just write to stderr
                System.err.println(message);
                t.printStackTrace(System.err);
                return;
            }
        }
        logger.error(message, t);
    }
}
