/* Pre-Processor for TopCoder submissions. (C# Version)
 * Used to determine the percentage of code that a submission uses.
 * If the percentage of unused code is more than 30% then a warning message is displayed.
 * main method demonstrates how this class should be used.
 *
 * Version: 1.3
 * Author: Dmitry Kamenetsky
 *
 * Known problems:
 *	- there might be problems with operator methods
 *	- everything inside the Comparator class is counted
 *  - no discrimination is made between methods with the same name. This means that if
 * 		one method is seen then the other method is also counted as seen.
 *	- white space characters are not counted - even if they are inside Strings and characters.
 *	- do not use the following inside Strings and characters:
 *		'{', '}', "\n", "\t" and 'space' (see above problem)
 */

package com.topcoder.client.contestApplet.unusedCodeProcessor;

import java.util.StringTokenizer;
import java.util.Vector;

public class CSharpProcessor extends UCRProcessor {
    
    private String className;
    private String methodName;
    private String originalCode;
    private String code;
    private String[][] methods;
    private int methodsUsed;
    private String[][] classes;
    private int classesUsed;
    
    //check the validity of the code.
    //A warning message is returned if there is too much unused code.
    public String checkCode() throws RuntimeException {
        code = new CommentStripper().stripComments(originalCode);  //strip comments from the original code
        getClassesAndMethods();
        iterateThroughMethods();
        printClasses();
        printMethods();
        
        //count used code;
        int usedCode=countClassNamesAndPublicVariables()+countCodeInMethods()+countImports();
        int totalLength=countTotalInOriginalCode()-countAutoCodeInOriginalCode();
        double usedPercentage=usedCode*1.0/totalLength;
        if(DEBUG)
            System.out.println("Used Code: "+usedCode+" / "+totalLength+" = " +usedPercentage);
        
        if((totalLength - usedCode) > CODE_LIMIT) {
            if (usedPercentage< (1 - CODE_PERCENT_LIMIT)) return INVALID_MESSAGE;
        }
        return "";
    }
    
    //create a preProcessor instance
    public void initialize(String className, String methodName, String originalCode) {
        this.className=className;
        this.methodName=methodName;
        this.originalCode=originalCode;
        
        //ASSUME that there can be at most 100 methods
        methods=new String[100][5];		//name, class, start, finish, seen
        methodsUsed=0;
        //ASSUME that there can be at most 20 classes
        classes=new String[20][5];		//name, start, finish, seen, isComparator
        classesUsed=0;
    }
    
    
    //records all classes and methods of the program
    public void getClassesAndMethods() throws RuntimeException {
        int count=0;
        int lastCount=-1;
        Vector currentName=new Vector();
        Vector currentClass=new Vector();
        Vector currentIsComparator=new Vector();
        Vector currentStart=new Vector();
        Vector currentLocation=new Vector();
        currentLocation.add("nothing");
        
        
        for (int i=0; i<code.length(); i++) {
            if (code.charAt(i)=='{') {
                count++;
                boolean ok=(code.lastIndexOf("=",i) < Math.max(code.lastIndexOf("(",i),Math.max(code.lastIndexOf("class ",i),code.lastIndexOf("struct ",i))));
                
                if (ok && !currentLocation.elementAt(currentLocation.size()-1).equals("inMethod")) {
                    boolean isClass=(currentLocation.elementAt(currentLocation.size()-1).equals("nothing") || code.lastIndexOf("class ",i)>code.lastIndexOf("(",i) || code.lastIndexOf("struct ",i)>code.lastIndexOf("(",i));
                    
                    if (isClass)		//start of a class
                    {
                        int classStart2=-1;
                        if (code.lastIndexOf("class ",i)>code.lastIndexOf("struct ",i))		//its a class
                        {
                            classStart2=code.lastIndexOf("class ",i);
                        } else		//its a struct
                        {
                            classStart2=code.lastIndexOf("struct ",i);
                        }
                        
                        int classStart=code.lastIndexOf("\n",classStart2)+1;
                        if (classStart<0) classStart=0;
                        
                        StringTokenizer classWords=new StringTokenizer(code.substring(classStart2,i)," \n:");
                        classWords.nextToken();
                        String thisName=""+classWords.nextToken();
                        String isComparator="";
                        while(classWords.hasMoreTokens()) isComparator=""+classWords.nextToken();
                        
                        //					System.out.println("start of "+code.substring(classNameStart,classNameEnd)+" "+classNameStart+" "+classNameEnd);
                        currentClass.add(thisName);
                        currentName.add(thisName);
                        currentStart.add(""+classStart);
                        currentLocation.add("inClass");
                        currentIsComparator.add(isComparator);
                        lastCount++;
                    } else			//start of a method
                    {
                        int leftBracket=code.lastIndexOf("(",i);
                        
                        int methodStart=leftBracket-1;
                        while(code.charAt(methodStart)==' ') methodStart--;
                        methodStart=code.lastIndexOf(" ",methodStart)+1;
                        
                        if (methodStart<0) methodStart=0;

                        String[] methodWords=split(code.substring(methodStart,leftBracket)," ");
                        currentName.add(methodWords[methodWords.length-1]);
                        currentStart.add(""+methodStart);
                        currentLocation.add("inMethod");
                        lastCount++;
                    }
                }
            }
            
            if (code.charAt(i)=='}') {
                count--;
                
                if (count==lastCount)		//end of something
                {
                    if (currentLocation.elementAt(currentLocation.size()-1).equals("inClass"))		//end of class
                    {
                        addClass(""+currentName.elementAt(currentName.size()-1),""+currentStart.elementAt(currentStart.size()-1),""+i,""+currentIsComparator.elementAt(currentIsComparator.size()-1));
                        currentName.removeElementAt(currentName.size()-1);
                        currentStart.removeElementAt(currentStart.size()-1);
                        currentClass.removeElementAt(currentClass.size()-1);
                        currentLocation.removeElementAt(currentLocation.size()-1);
                        currentIsComparator.removeElementAt(currentIsComparator.size()-1);
                        lastCount--;
                    } else if (currentLocation.elementAt(currentLocation.size()-1).equals("inMethod"))		//end of method
                    {
                        addMethod(""+currentName.elementAt(currentName.size()-1),""+currentClass.elementAt(currentClass.size()-1),""+currentStart.elementAt(currentStart.size()-1),""+i);
                        currentName.removeElementAt(currentName.size()-1);
                        currentStart.removeElementAt(currentStart.size()-1);
                        currentLocation.removeElementAt(currentLocation.size()-1);
                        lastCount--;
                    }
                }
            }
            
        }
    }
    
    //record all method details. Throws an exception if array limit is reached
    public void addMethod(String name1, String name2, String start, String end) throws RuntimeException {
        try {
            methods[methodsUsed][0]=name1;
            methods[methodsUsed][1]=name2;
            methods[methodsUsed][2]=start;
            methods[methodsUsed][3]=end;
            if (methods[methodsUsed][0].equals(methodName))
                methods[methodsUsed][4]="seen";
            else
                methods[methodsUsed][4]="not seen";
            
            methodsUsed++;
        } catch(RuntimeException e) {
            System.out.println("methods array limit reached");
            throw e;
        }
    }
    
    //record all class details. Throws an exception if array limit is reached
    public void addClass(String name, String start, String end, String isComparator) throws RuntimeException {
        try {
            classes[classesUsed][0]=name;
            classes[classesUsed][1]=start;
            classes[classesUsed][2]=end;
            if (classes[classesUsed][0].equals(className))
                classes[classesUsed][3]="seen";
            else
                classes[classesUsed][3]="not seen";
            
            if (isComparator.equals("IComparer") || isComparator.equals("IComparable"))		//check if class is a Comparator class
                classes[classesUsed][4]="true";
            else
                classes[classesUsed][4]="false";
            
            classesUsed++;
        } catch(RuntimeException e) {
            System.out.println("classes array limit reached");
            throw e;
        }
    }
    
    //print details about classes
    public void printClasses() {
        if(DEBUG) {
            System.out.println("Printing Classes:");
            for (int i=0; i<classesUsed; i++)
                System.out.println(classes[i][0]+" "+classes[i][1]+" "+classes[i][2]+" "+classes[i][3]+" "+classes[i][4]);
        }
    }
    
    //print details about methods
    public void printMethods() {
        if(DEBUG) {
            System.out.println("Printing Methods:");
            for (int i=0; i<methodsUsed; i++)
                System.out.println(methods[i][0]+" "+methods[i][1]+" "+methods[i][2]+" "+methods[i][3]+" "+methods[i][4]);
        }
    }
    
    //This method iteratively finds used methods and classes
    public void iterateThroughMethods() {
        while(true) {
            boolean finished=true;
            
////////////////Find new methods//////////////////////
            for (int i=0; i<methodsUsed; i++) {
                if (methods[i][4].equals("not seen")) {
                    boolean found2=false;
                    for (int m=0; m<classesUsed; m++)
                        if (classes[m][0].equals(methods[i][1]) && classes[m][3].equals("seen")) {
                        found2=true;
                        break;
                        }
                    
                    if (found2) {
                        
                        boolean found=false;
                        
                        //check if this method is used by other methods that are already seen
                        for (int k=0; k<methodsUsed; k++) {
                            if (methods[k][4].equals("seen")) {
                                int cur=Integer.parseInt(methods[k][2]);
                                while(cur<=Integer.parseInt(methods[k][3])) {
                                    int methodStart=code.indexOf(methods[i][0]+"(",cur);
                                    if (methodStart<0) break;
                                    cur=methodStart+1;
                                    
                                    
                                    //method is used here
                                    if (methodStart<=Integer.parseInt(methods[k][3])) {
                                        methods[i][4]="seen";
                                        found=true;
                                        break;
                                    }
                                }
                                
                                if (found) {
                                    finished=false;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            
/////////////////Find new classes///////////////////
            for (int i=0; i<classesUsed; i++) {
                if (classes[i][3].equals("not seen")) {
                    boolean found=false;
                    
                    //check if this class is used by other methods that are already seen
                    for (int k=0; k<methodsUsed; k++) {
                        if (methods[k][4].equals("seen")) {
                            int cur=Integer.parseInt(methods[k][2]);
                            while(cur<=Integer.parseInt(methods[k][3])) {
                                int classStart=code.indexOf("new "+classes[i][0],cur);
                                if (classStart<0) break;
                                cur=classStart+1;
                                
                                
                                //class is used here
                                if (classStart<=Integer.parseInt(methods[k][3])) {
                                    classes[i][3]="seen";
                                    found=true;
                                    
                                    //if this is a comparator class then make all its methods seen
                                    if (classes[i][4].equals("true"))
                                        for (int m=0; m<methodsUsed; m++)
                                            if (methods[m][1].equals(classes[i][0])) methods[m][4]="seen";
                                    
                                    break;
                                }
                            }
                            
                            if (found) {
                                finished=false;
                                break;
                            }
                        }
                    }
                }
            }
            
            if (finished) break;
        }
    }
    
    //counts the amount of auto-generated code
    public int countAutoCodeInOriginalCode() {
        String[] auto={"// Powered by FileEdit","// Powered by CodeProcessor","// Powered by PopsEdit","// Powered by [KawigiEdit]","//Powered by [KawigiEdit]","// Powered by TCGen for C#"};
        int rightBrace=originalCode.lastIndexOf("}");
        
        int count=0;
        for (int i=0; i<auto.length; i++) {
            int autoIndex=originalCode.lastIndexOf(auto[i]);
            if (autoIndex>rightBrace)
                for (int k=0; k<auto[i].length(); k++)
                    if (auto[i].charAt(k)!=' ' && auto[i].charAt(k)!='\t' && auto[i].charAt(k)!='\n' && auto[i].charAt(k)!='\r')
                        count++;
            
        }
        
        return count;
    }
    
    //counts the amount of imported code
    public int countImports() {
        int cur=0;
        int count=0;
        
        while(true) {
            int importStart=code.indexOf("using",cur);
            int nextEnter=code.indexOf(";",cur);
            if (importStart<0 || importStart>nextEnter) break;
            
            cur=nextEnter+1;
            for (int k=importStart; k<=nextEnter; k++)
                if (code.charAt(k)!=' ' && code.charAt(k)!='\t' && code.charAt(k)!='\n' && code.charAt(k)!='\r')
                    count++;
        }
        
        return count;
    }
    
    
    //counts the length of class names of seen classes
    public int countClassNamesAndPublicVariables() {
        int count=0;
        
        for (int i=0; i<classesUsed; i++) {
            if (classes[i][3].equals("seen")) {
                int classTotal=0;
                for (int k=Integer.parseInt(classes[i][1]); k<=Integer.parseInt(classes[i][2]); k++)
                    if (code.charAt(k)!=' ' && code.charAt(k)!='\t' && code.charAt(k)!='\n' && code.charAt(k)!='\r')
                        classTotal++;
                
                int innerClasses=0;
                for (int m=0; m<classesUsed; m++)
                    if (m!=i && Integer.parseInt(classes[m][1])>Integer.parseInt(classes[i][1]) && Integer.parseInt(classes[m][2])<Integer.parseInt(classes[i][2]))
                        for (int k=Integer.parseInt(classes[m][1]); k<=Integer.parseInt(classes[m][2]); k++)
                            if (code.charAt(k)!=' ' && code.charAt(k)!='\t' && code.charAt(k)!='\n' && code.charAt(k)!='\r')
                                innerClasses++;
                
                int innerMethods=0;
                for (int m=0; m<methodsUsed; m++)
                    if (methods[m][1].equals(classes[i][0]) && Integer.parseInt(methods[m][2])>Integer.parseInt(classes[i][1]) && Integer.parseInt(methods[m][3])<Integer.parseInt(classes[i][2]))
                        for (int k=Integer.parseInt(methods[m][2]); k<=Integer.parseInt(methods[m][3]); k++)
                            if (code.charAt(k)!=' ' && code.charAt(k)!='\t' && code.charAt(k)!='\n' && code.charAt(k)!='\r')
                                innerMethods++;
                
                count+=(classTotal-innerClasses-innerMethods);
            }
        }
        
        return count;
    }
    
    //counts all the code inside methods that have been seen
    public int countCodeInMethods() {
        int count=0;
        
        for (int i=0; i<methodsUsed; i++)
            if (methods[i][4].equals("seen"))
                for (int k=Integer.parseInt(methods[i][2]); k<=Integer.parseInt(methods[i][3]); k++)
                    if (code.charAt(k)!=' ' && code.charAt(k)!='\t' && code.charAt(k)!='\n' && code.charAt(k)!='\r')
                        count++;
        
        
        return count;
    }
    
    //count all non-space characters in the submission
    public int countTotalInOriginalCode() {
        int count=0;
        
        for (int i=0; i<originalCode.length(); i++)
            if (originalCode.charAt(i)!=' ' && originalCode.charAt(i)!='\t' && originalCode.charAt(i)!='\n' && originalCode.charAt(i)!='\r')
                count++;
        
        return count;
    }
}


