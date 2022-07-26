/* Pre-Processor for TopCoder submissions. (C++ Version)
 * Used to determine the percentage of code that a submission uses.
 * If the percentage of unused code is more than 30% then a warning message is displayed.
 * main method demonstrates how this class should be used.
 *
 * Version: 1.3
 * Author: Dmitry Kamenetsky
 *
 * Known problems:
 *  - no discrimination is made between methods with the same name. This means that if
 * 		one method is seen then the other method is also counted as seen.
 *	- white space characters are not counted - even if they are inside Strings and characters.
 *	- do not use the following inside Strings and characters:
 *		'{', '}', "\n", "\t" and 'space' (see above problem)
 *  - methods that have the same name as their class are constructors. All constructors of a seen
 *		class are also seen.
 *	- all operator methods are seen
 *	- #ifdef and #endif are not properly handled
 */

package com.topcoder.client.contestApplet.unusedCodeProcessor;

import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CPPProcessor extends UCRProcessor {
    private String className;
    private String methodName;
    private String originalCode;
    private String code;
    private String[][] methods;
    private int methodsUsed;
    private String[][] classes;
    private int classesUsed;
    private String[][] defines;
    private int definesUsed;
    private static final Pattern TYPEDEF_PATTERN = Pattern.compile("^typedef\\s+(\\w.*\\W)(\\w+)\\s*;$");
    
    //check the validity of the code.
    //A warning message is returned if there is too much unused code.
    public String checkCode() throws RuntimeException {
        code = new CommentStripper().stripComments(originalCode).replaceAll("[\t ]+", " ").replaceAll("\r\n?", "\n");  //strip comments from the original code
        getClassesAndMethods();
        iterateThroughMethods();
        getDefines();
        iterateThroughDefines();
        
        printClasses();
        printMethods();
        printDefines();
        
        //count used code;
        int usedCode=countClassNamesAndPublicVariables()+countCodeInMethods()+countImports()+countDefines()+countNameSpace()+countLine()+countGlobalVariables();
        int totalLength=countTotalInOriginalCode()-countAutoCodeInOriginalCode();
        double usedPercentage=usedCode*1.0/totalLength;
        if(DEBUG) {
            System.out.println("Used Code: "+usedCode+" / "+totalLength+" = " +usedPercentage);
        }
        
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
        //ASSUME that there can be at most 1000 defines
        defines=new String[1000][5];			//from, to, start, finish, seen
        definesUsed=0;
    }
    
    public void getDefines() throws RuntimeException {
        int cur=0;
        
        while(true) {
            int defineStart=code.indexOf("#define ",cur);
            int typedefStart=code.indexOf("typedef",cur);
            if (defineStart<0 && typedefStart<0) break;
            
            if (defineStart<0 || (typedefStart>=0 && typedefStart<defineStart))	//its a typedef
            {
                int nextColon=code.indexOf(";",typedefStart);
                if (nextColon<0) break;
                
                Matcher matcher = TYPEDEF_PATTERN.matcher(code.substring(typedefStart, nextColon + 1));
                //int temp=code.indexOf(" ",typedefStart);
                //int temp2=code.lastIndexOf(" ",nextColon);
                //String to=code.substring(temp+1,temp2+1).trim();
                //String from=code.substring(temp2+1,nextColon);
                if (matcher.matches()) {
                    int temp2 = matcher.start();
                    String to = matcher.group(1).trim();
                    String from = matcher.group(2);
  
                    try {
                        defines[definesUsed][0]=from;
                        defines[definesUsed][1]=to;
                        defines[definesUsed][2]=""+typedefStart;
                        defines[definesUsed][3]=""+nextColon;
                        defines[definesUsed][4]="not seen";
                        definesUsed++;
                    } catch(RuntimeException e) {
                        System.out.println("defines array limit reached");
                        throw e;
                    }
                }

                cur=nextColon+1;
            } else		//its a #define
            {
                int nextEnter=code.indexOf("\n",defineStart);
                if (nextEnter<0) break;
                
                StringTokenizer token=new StringTokenizer(code.substring(defineStart,nextEnter)," ");
                token.nextToken();
                String from=""+token.nextToken();
                String to = "";

                if (defineStart+("#define " + from + " ").length() < nextEnter) {
                    to=code.substring(defineStart+("#define "+from+" ").length(),nextEnter);
                }

                //deal with ( case
                int temp=from.indexOf("(");
                if (temp>=0) from=from.substring(0,temp);
                
                
                try {
                    defines[definesUsed][0]=from;
                    defines[definesUsed][1]=to;
                    defines[definesUsed][2]=""+defineStart;
                    defines[definesUsed][3]=""+nextEnter;
                    defines[definesUsed][4]="not seen";
                    definesUsed++;
                } catch(RuntimeException e) {
                    System.out.println("defines array limit reached");
                    throw e;
                }
                
                cur=nextEnter+1;
            }
        }
    }
    
    
    //records all classes and methods of the program
    public void getClassesAndMethods() throws RuntimeException {
        int count=0;
        int lastCount=-1;
        Vector currentName=new Vector();
        Vector currentClass=new Vector();
        currentClass.add("public");
        Vector methodClass=new Vector();
        Vector currentIsComparator=new Vector();
        currentIsComparator.add("");
        Vector currentStart=new Vector();
        Vector currentLocation=new Vector();
        currentLocation.add("nothing");
        addClass("public",""+0,""+(code.length()-1),"");
        
        
        for (int i=0; i<code.length(); i++) {
            if (code.charAt(i)=='{') {
                count++;
                boolean ok=(code.lastIndexOf("=",i) < Math.max(code.lastIndexOf(")",i),Math.max(code.lastIndexOf("class ",i),code.lastIndexOf("struct ",i))));
                
                if (ok && !currentLocation.elementAt(currentLocation.size()-1).equals("inMethod")) {
                    boolean isClass=(code.lastIndexOf("class ",i)>code.lastIndexOf("(",i) || code.lastIndexOf("struct ",i)>code.lastIndexOf("(",i));
                    
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
                        
                        StringTokenizer classWords=new StringTokenizer(code.substring(classStart2,i)," ");
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
                        int methodStart=code.lastIndexOf("\n",leftBracket)+1;
//						int methodStart=code.lastIndexOf(" ",leftBracket)+1;
                        if (methodStart<0) methodStart=0;
                        
                        String[] methodWords=split(code.substring(methodStart,leftBracket)," ");
                        String nameToAdd="";
                        if (methodWords[methodWords.length-1].indexOf("::")>=0) {
                            StringTokenizer token=new StringTokenizer(methodWords[methodWords.length-1],":");
                            methodClass.add(""+token.nextToken());
                            nameToAdd=""+token.nextToken();
                            
                        } else {
                            methodClass.add(""+currentClass.elementAt(currentClass.size()-1));
                            nameToAdd=methodWords[methodWords.length-1];
                        }
                        
//						System.out.println(nameToAdd);
                        if (nameToAdd.charAt(0)=='&') nameToAdd=nameToAdd.substring(1);
                        
                        currentName.add(nameToAdd);
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
//						System.out.println("adding class "+			currentName.elementAt(currentName.size()-1)+"A");
                        int i2=i;
                        if (i+1<code.length() && code.charAt(i+1)==';') i2=i+1;
                        
                        addClass(""+currentName.elementAt(currentName.size()-1),""+currentStart.elementAt(currentStart.size()-1),""+i2,""+currentIsComparator.elementAt(currentIsComparator.size()-1));
                        currentName.removeElementAt(currentName.size()-1);
                        currentStart.removeElementAt(currentStart.size()-1);
                        currentClass.removeElementAt(currentClass.size()-1);
                        currentLocation.removeElementAt(currentLocation.size()-1);
                        currentIsComparator.removeElementAt(currentIsComparator.size()-1);
                        lastCount--;
                    } else if (currentLocation.elementAt(currentLocation.size()-1).equals("inMethod"))		//end of method
                    {
//						System.out.println("adding method "+			currentName.elementAt(currentName.size()-1)+"A");
                        addMethod(""+currentName.elementAt(currentName.size()-1),""+methodClass.elementAt(methodClass.size()-1),""+currentStart.elementAt(currentStart.size()-1),""+i);
                        currentName.removeElementAt(currentName.size()-1);
                        methodClass.removeElementAt(methodClass.size()-1);
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
            if (classes[classesUsed][0].equals(className) || classes[classesUsed][0].equals("public"))
                classes[classesUsed][3]="seen";
            else
                classes[classesUsed][3]="not seen";
            
            if (isComparator.equals("Comparator") || isComparator.equals("Comparable"))		//check if class is a Comparator class
                classes[classesUsed][4]="true";
            else
                classes[classesUsed][4]="false";
            
            classesUsed++;
        } catch(RuntimeException e) {
            System.out.println("classes array limit reached");
            throw e;
        }
    }
    
    //print details about defines
    public void printDefines() {
        if(DEBUG) {
            System.out.println("Printing Defines:");
            for (int i=0; i<definesUsed; i++)
                System.out.println(defines[i][0]+"|"+defines[i][1]+"|"+defines[i][2]+"|"+defines[i][3]+"|"+defines[i][4]);
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
    
    
    public void iterateThroughDefines() {
        while(true) {
            boolean finished=true;
            
            //find in methods
            for (int i=0; i<definesUsed; i++) {
                if (defines[i][4].equals("not seen")) {
                    for (int k=0; k<methodsUsed; k++) {
                        if (methods[k][4].equals("seen")) {
                            int cur=Integer.parseInt(methods[k][2]);
                            int temp=code.indexOf(defines[i][0],cur);		//look for FROM
                            if (temp<0) break;
                            
                            if (temp<=Integer.parseInt(methods[k][3])) {
                                defines[i][4]="seen";
                                finished=false;
                                break;
                            }
                        }
                    }
                }
            }
            
            
            
            //find in other defines
            for (int i=0; i<definesUsed; i++) {
                if (defines[i][4].equals("not seen")) {
                    for (int k=0; k<definesUsed; k++) {
                        if (defines[k][4].equals("seen")) {
                            int temp=defines[k][1].indexOf(defines[i][0]);		//find FROMnew in TOold
                            if (temp>=0) {
                                defines[i][4]="seen";
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
    
    
    //This method iteratively finds used methods and classes
    public void iterateThroughMethods() {
        while(true) {
            boolean finished=true;
            
////////////////Find new methods//////////////////////
            for (int i=0; i<methodsUsed; i++) {
                if (methods[i][4].equals("not seen")) {
                                /*	boolean found2=false;
                                        for (int m=0; m<classesUsed; m++)
                                                if (classes[m][0].equals(methods[i][1]) && classes[m][3].equals("seen"))
                                                {
                                                        found2=true;
                                                        break;
                                                }*/
                    
                    boolean found2=true;
                    
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
                                int classStart=code.indexOf(classes[i][0]+" ",cur);
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
            
/////////////////Find Constructor and Operator Methods///////////////////
            for (int i=0; i<classesUsed; i++)
                if (classes[i][3].equals("seen"))
                    for (int k=0; k<methodsUsed; k++)
                        if (!methods[k][4].equals("seen") && methods[k][1].equals(classes[i][0]) && (methods[k][0].equals(methods[k][1]) || methods[k][0].indexOf("operator")==0)) {
                methods[k][4]="seen";
                finished=false;
                        }
            
            
            if (finished) break;
        }
    }
    
    
    
    public int countLine() {
        int lineStart=code.indexOf("#line ");
        if (lineStart<0) return 0;
        int nextEnter=code.indexOf("\n",lineStart);
        int count=0;
        
        return generalCount("code",lineStart,nextEnter);
    }
    
    public int countDefines() {
        int count=0;
        
        for (int i=0; i<definesUsed; i++)
            if (defines[i][4].equals("seen"))
                count+=generalCount("code",Integer.parseInt(defines[i][2]),Integer.parseInt(defines[i][3]));
        
        return count;
    }
    
    
    
    //counts the amount of auto-generated code
    public int countAutoCodeInOriginalCode() {
        String[] auto={"// Powered by FileEdit","// Powered by CodeProcessor","// Powered by PopsEdit","// Powered by [KawigiEdit]","// Powered by TZTester","// Powered by TomekAI"};
        int rightBrace=originalCode.lastIndexOf("}");
        
        int count=0;
        for (int i=0; i<auto.length; i++) {
            int autoIndex=originalCode.lastIndexOf(auto[i]);
            if (autoIndex>rightBrace) {
                int nextEnter=originalCode.indexOf("\n",autoIndex);
                if (nextEnter<0) nextEnter=originalCode.length()-1;
                
                count+=generalCount("originalCode",autoIndex,nextEnter);
            }
        }
        
        return count;
    }
    
    //counts the amount of imported code
    public int countImports() {
        int cur=0;
        int count=0;
        
        while(true) {
            int importStart=code.indexOf("#include ",cur);
            int nextEnter=code.indexOf("\n",importStart);
            if (importStart<0 || importStart>nextEnter) break;
            
            count+=generalCount("code",importStart,nextEnter);
            cur=nextEnter+1;
            
        }
        
        return count;
    }
    
    
    public int countNameSpace() {
        int count=0;
        int nameSpaceStart=code.indexOf("using namespace",0);
        if (nameSpaceStart>=0) {
            int nextEnter=code.indexOf(";",nameSpaceStart);
            count+=generalCount("code",nameSpaceStart,nextEnter);
        }
        
        return count;
    }
    
    
    //counts the length of class names of seen classes
    public int countClassNamesAndPublicVariables() {
        int count=0;
        
        for (int i=0; i<classesUsed; i++) {
            if (!classes[i][0].equals("public") && classes[i][3].equals("seen")) {
                int classTotal=generalCount("code",Integer.parseInt(classes[i][1]),Integer.parseInt(classes[i][2]));
                
                int innerClasses=0;
                for (int m=0; m<classesUsed; m++)
                    if (m!=i && Integer.parseInt(classes[m][1])>Integer.parseInt(classes[i][1]) && Integer.parseInt(classes[m][2])<Integer.parseInt(classes[i][2]))
                        innerClasses+=generalCount("code",Integer.parseInt(classes[m][1]),Integer.parseInt(classes[m][2]));
                
                int innerMethods=0;
                for (int m=0; m<methodsUsed; m++)
                    if (methods[m][1].equals(classes[i][0]) && Integer.parseInt(methods[m][2])>Integer.parseInt(classes[i][1]) && Integer.parseInt(methods[m][3])<Integer.parseInt(classes[i][2]))
                        innerMethods+=generalCount("code",Integer.parseInt(methods[m][2]),Integer.parseInt(methods[m][3]));
                
                count+=(classTotal-innerClasses-innerMethods);
            }
        }
        
        return count;
    }
    
    public int countGlobalVariables() {
        int count=0;
        int cur=0;
        
        while(true) {
            int colon=code.indexOf(";",cur);
            if (colon<0) break;
            
            boolean found=false;
            //check if colon is inside class, method, or define
            //first check all classes
            for (int i=0; i<classesUsed; i++) {
                if (!classes[i][0].equals("public") && colon>=Integer.parseInt(classes[i][1]) && colon<=Integer.parseInt(classes[i][2])) {
                    //System.out.println(found in class
                    found=true;
                    cur=Integer.parseInt(classes[i][2])+1;		//go to end of method
                    break;
                }
            }
            
            //if not found check methods
            if (!found) {
                for (int i=0; i<methodsUsed; i++) {
                    if (colon>=Integer.parseInt(methods[i][2]) && colon<=Integer.parseInt(methods[i][3])) {
                        found=true;
                        cur=Integer.parseInt(methods[i][3])+1;		//go to end of method
                        break;
                    }
                }
            }
            
            //if not found check defines
            if (!found) {
                for (int i=0; i<definesUsed; i++) {
                    if (colon>=Integer.parseInt(defines[i][2]) && colon<=Integer.parseInt(defines[i][3])) {
                        found=true;
                        cur=Integer.parseInt(defines[i][3])+1;		//go to end of define
                        break;
                    }
                }
            }
            
            
            //if still not found then its a global variable
            if (!found) {
                int variableStart=code.lastIndexOf("\n",colon)+1;
                if (variableStart<0) variableStart=0;
                
                if (!code.substring(variableStart,colon).equals("using namespace std")) {
//					System.out.println("global variable|"+code.substring(variableStart,colon)+" "+colon);
                    count+=generalCount("code",variableStart,colon);
                }
                cur=colon+1;
            }
        }
        
        return count;
    }
    
    //counts all the code inside methods that have been seen
    public int countCodeInMethods() {
        int count=0;
        
        for (int i=0; i<methodsUsed; i++)
            if (methods[i][4].equals("seen"))
                count+=generalCount("code",Integer.parseInt(methods[i][2]),Integer.parseInt(methods[i][3]));
        
        return count;
    }
    
    //count all non-space characters in the submission
    public int countTotalInOriginalCode() {
        return generalCount("originalCode",0,originalCode.length()-1);
    }
    
    public int generalCount(String text, int start, int finish) {
        int count=0;
        for (int k=start; k<=finish; k++) {
            if (text.equals("code") && code.charAt(k)!=' ' && code.charAt(k)!='\t' && code.charAt(k)!='\n' && code.charAt(k)!='\r')
                count++;
            else if (text.equals("originalCode") && originalCode.charAt(k)!=' ' && originalCode.charAt(k)!='\t' && originalCode.charAt(k)!='\n' && originalCode.charAt(k)!='\r')
                count++;
        }
        
        return count;
    }
    
}
