/*
 * SecurityCheck.java
 *
 * Created on June 21, 2006, 9:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.services.compiler.util.MSILParser.security;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * Changes in version 1.0 (TopCoder Competition Engine - Enable New DotNET Features v1.0):
 * <ol>
 * <li>Updated {@link SecurityCheck#allowedNamespaces} field to support new LINQ Expression feature.</li>
 * <li>Updated {@link SecurityCheck#allowedTypes} field to support the new dotNet3.5 and dotNet4.0 features</li>
 * </ol>
 * </p>
 * @author rfairfax
 */
public class SecurityCheck {
    private Set userTypes = null;
    private List allowedNamespacesList = null;


    /** Creates a new instance of SecurityCheck */
    public SecurityCheck(String[] userTypes, boolean threadingAllowed) {
        this(new HashSet(Arrays.asList(userTypes)), threadingAllowed);
    }

    public SecurityCheck(Set userTypes, boolean threadingAllowed) {
        this.userTypes = userTypes;
        allowedNamespacesList = Arrays.asList(allowedNamespaces);
        if(threadingAllowed) {
            allowedNamespacesList.add("System.Threading");
        }
    }

    public String checkTypes(String[] types) {
        String ret = "";


        for(int i = 0; i < types.length; i++) {
            String type = types[i];
            //is it a user type
            //System.out.println("TYPE IS: " + type);
            if(userTypes.contains(type)) {
                //System.out.println("USER TYPE: " + types[i]);
            } else if(allowedTypesList.contains(type)) {
                //is it explicitly allowed?
                //System.out.println("ALLOWING: " + types[i]);
            } else if(checkAllowedNamespaces(type)) {
                //is it in an allowed namespace
                //System.out.println("ALLOWED NAMESPACE: " + types[i]);
            } else if(isConditionalType(type)) {
                //conditionally allowed
                //System.out.println("COND: " + types[i]);
            } else {
                //bad
                ret += "Prohibited Class: " + types[i] + "\n";
            }
        }
        return ret;
    }

    public String checkMethods(String[] methods) {
        String ret = "";
        for(int i = 0; i < methods.length; i++) {
            String type = methods[i].split("::")[0];
            String method = methods[i].split("::")[1];
            //is it a user type
            //System.out.println("TYPE IS: " + type);
            if(userTypes.contains(type)) {
                //System.out.println("USER TYPE: " + types[i]);
            } else if(allowedTypesList.contains(type)) {
                //is it explicitly allowed?
                //System.out.println("ALLOWING: " + types[i]);
            } else if(checkAllowedNamespaces(type)) {
                //is it in an allowed namespace
                //System.out.println("ALLOWED NAMESPACE: " + types[i]);
            } else if(isConditionalType(type)) {
                //conditionally allowed
                if(!checkConditionalMethod(type, method))
                    ret += "Prohibited Method: " + methods[i] + "\n";
            } else {
                //bad
                ret += "Prohibited Method: " + methods[i] + "\n";
            }
        }
        return ret;
    }

    private boolean checkConditionalMethod(String type, String method) {
        return allowedMembersList.contains(type + "::" + method);
    }

    private boolean isConditionalType(String s) {
        return condTypesList.contains(s);
    }

    private boolean checkAllowedNamespaces(String s) {
        for(int i = 0; i < allowedNamespacesList.size(); i++) {
            if(s.startsWith( ((String)allowedNamespacesList.get(i)) + "."))
                return true;
        }
        return false;
    }

    private static final String[] allowedNamespaces={
            "System.Collections",
            "System.Globalization",
            "System.Text",
            
            //MyCrap context items
            "Microsoft.VisualBasic.MyServices.Internal",
            
            //support extension expression
            "System.Linq.Expressions"
        };

    private static final String[] allowedTypes={
        // Classes
        "System.Action`1",
        "System.ArgumentException",
        "System.ArgumentNullException",
        "System.ArgumentOutOfRangeException",
        "System.ArithmeticException",
        "System.Array",
        "System.ArrayTypeMismatchException",
        "System.BitConverter",
        "System.Buffer",
        "System.CharEnumerator",
        "System.Comparison`1",
        "System.Convert",
        "System.Converter`2",
        "System.Delegate",
        "System.DivideByZeroException",
        "System.Enum",
        "System.Exception",
        "System.FormatException",
        "System.GC",
        "System.IndexOutOfRangeException",
        "System.InvalidCastException",
        "System.Math",
        "System.MulticastDelegate",
        "System.NotFiniteNumberException",
        "System.NullReferenceException",
        "System.Object",
        "System.OverflowException",
        "System.Predicate`1",
        "System.Random",
        "System.RankException",
        "System.String",
        "System.StringSplitOptions",
        "System.StringComparer",
        "System.ValueType",
        "System.Drawing.PointConverter",
        "System.Drawing.RectangleConverter",
        "System.Drawing.Region",
        "System.Drawing.SizeConverter",
        "System.Drawing.Drawing2D.GraphicsPath",
        "System.Drawing.Drawing2D.GraphicsPathIterator",
        "System.Drawing.Drawing2D.Matrix",
        "System.Drawing.Drawing2D.PathData",
        "System.Drawing.Drawing2D.RegionData",
        "System.Drawing.Drawing2D.FillMode",

        // Interfaces
        "System.IDisposable",
        "System.ICloneable",
        "System.IComparable",
        "System.IConvertible",
        "System.ICustomFormatter",
        "System.IFormatProvider",
        "System.IFormattable",

        // Generic Versions of interfaces
        "System.IDisposable`1",
        "System.ICloneable`1",
        "System.IComparable`1",
        "System.IConvertible`1",
        "System.ICustomFormatter`1",
        "System.IFormatProvider`1",
        "System.IFormattable`1",
        "System.IEquatable`1",

        // Structures
        "System.Boolean",
        "System.Byte",
        "System.Char",
        "System.DateTime",
        "System.Decimal",
        "System.Double",
        "System.Int16",
        "System.Int32",
        "System.Int64",
        "System.SByte",
        "System.Single",
        "System.TimeSpan",
        "System.UInt16",
        "System.UInt32",
        "System.UInt64",
        "System.Drawing.Point",
        "System.Drawing.PointF",
        "System.Drawing.Rectangle",
        "System.Drawing.RectangleF",
        "System.Drawing.Size",
        "System.Drawing.SizeF",

        // Enumerations
        "System.DayOfWeek",
        "System.Drawing.RotateFlipType",
        "System.Drawing.Drawing2D.CombineMode",
        "System.Drawing.Drawing2D.MatrixOrder",
        "System.Drawing.Drawing2D.PathPointType",

        //new VB
        "Microsoft.VisualBasic.Collection",
        "Microsoft.VisualBasic.ControlChars",
        "Microsoft.VisualBasic.Conversion",
        "Microsoft.VisualBasic.DateAndTime",
        "Microsoft.VisualBasic.Financial",
        "Microsoft.VisualBasic.Information",
        "Microsoft.VisualBasic.Strings",
        "Microsoft.VisualBasic.CompilerServices.BooleanType",
        "Microsoft.VisualBasic.CompilerServices.ByteType",
        "Microsoft.VisualBasic.CompilerServices.CharType",
        "Microsoft.VisualBasic.CompilerServices.CharArrayType",
        "Microsoft.VisualBasic.CompilerServices.DateType",
        "Microsoft.VisualBasic.CompilerServices.DecimalType",
        "Microsoft.VisualBasic.CompilerServices.DoubleType",
        "Microsoft.VisualBasic.CompilerServices.IntegerType",
        "Microsoft.VisualBasic.CompilerServices.LongType",
        "Microsoft.VisualBasic.CompilerServices.ObjectType",
        "Microsoft.VisualBasic.CompilerServices.ShortType",
        "Microsoft.VisualBasic.CompilerServices.SingleType",
        "Microsoft.VisualBasic.CompilerServices.StringType",
        "Microsoft.VisualBasic.CompareMethod",
        "Microsoft.VisualBasic.CompilerServices.Operators",
        "Microsoft.VisualBasic.CompilerServices.FlowControl",
        "Microsoft.VisualBasic.CompilerServices.Conversions",
        "Microsoft.VisualBasic.CompilerServices.Utils",
        "Microsoft.VisualBasic.VBMath",
        "Microsoft.VisualBasic.CompilerServices.StaticLocalInitFlag",
        "Microsoft.VisualBasic.CompilerServices.IncompleteInitialization",
        "Microsoft.VisualBasic.CompilerServices.ProjectData",
        "Microsoft.VisualBasic.FirstDayOfWeek",
        "Microsoft.VisualBasic.FirstWeekOfYear",

        //For Console.Out
        "System.IO.TextWriter",

        //Compiler attributes
        "System.Runtime.CompilerServices.CompilationRelaxationsAttribute",
        "System.Runtime.CompilerServices.RuntimeCompatibilityAttribute",
        "System.Runtime.CompilerServices.CompilerGeneratedAttribute",
        "System.CodeDom.Compiler.GeneratedCodeAttribute",
        "System.ComponentModel.EditorBrowsableAttribute",
        "System.ComponentModel.EditorBrowsableState",
        "System.Diagnostics.DebuggerNonUserCodeAttribute",
        "System.Diagnostics.DebuggerHiddenAttribute",
        "Microsoft.VisualBasic.HideModuleNameAttribute",
        "Microsoft.VisualBasic.CompilerServices.StandardModuleAttribute",
        "Microsoft.VisualBasic.MyGroupCollectionAttribute",
        "System.Runtime.InteropServices.ComVisibleAttribute",
        "System.ComponentModel.Design.HelpKeywordAttribute",
        "System.STAThreadAttribute",
        "System.Reflection.DefaultMemberAttribute",
        "System.ParamArrayAttribute",
        "System.CLSCompliantAttribute",
        "System.MTAThreadAttribute",

        //compiler crap
        "System.RuntimeTypeHandle",
        "System.Runtime.CompilerServices.IsVolatile",

        //magic yield sugar
        "System.Threading.Interlocked",
        "System.NotSupportedException",
        "System.Nullable`1",
        
        //stopwatch
        "System.Diagnostics.Stopwatch",
        
        //support System.Tuple
        "System.Tuple`1",
        "System.Tuple`2",
        "System.Tuple`3",
        "System.Tuple`4",
        "System.Tuple`5",
        "System.Tuple`6",
        "System.Tuple`7",
        "System.Tuple`8",
        
        //support Anonymous Types
        "System.Diagnostics.DebuggerDisplayAttribute",
        "System.Diagnostics.DebuggerBrowsableAttribute",
        "System.Diagnostics.DebuggerBrowsableState",
        "System.Diagnostics.DebuggerBrowsableAttribute",
        "System.Diagnostics.DebuggerBrowsableState",
        
        //support extension methods
        "System.Runtime.CompilerServices.ExtensionAttribute",
        
        //support system func
        "System.Func`1",
        "System.Func`2",
        "System.Func`3",
        "System.Func`4",
        "System.Func`5",
        "System.Func`6",
        "System.Func`7",
        "System.Func`8",
        "System.Func`9",
        "System.Func`10",
        "System.Func`11",
        "System.Func`12",
        "System.Func`13",
        "System.Func`14",
        "System.Func`15",
        "System.Func`16",
        
        //support linq order
        "System.Linq.IOrderedEnumerable`1",
        
        //support for System.Numerics.BigInteger
        "System.Numerics.BigInteger",
        
        //support for System.Numerics.Complex
        "System.Numerics.Complex",
        
        //support LINQ
        "System.Linq.Enumerable",
        
        //support dynamic binding
        "System.Runtime.CompilerServices.CallSite",
        "System.Runtime.CompilerServices.CallSite`1",
        "Microsoft.CSharp.RuntimeBinder.CSharpArgumentInfo",
        "System.Runtime.CompilerServices.CallSiteBinder",
        "Microsoft.CSharp.RuntimeBinder.Binder",
        "Microsoft.CSharp.RuntimeBinder.CSharpBinderFlags",
        "Microsoft.CSharp.RuntimeBinder.CSharpArgumentInfoFlags",
        
        //support expression reflection
        "System.Reflection.MethodInfo",
        
        //support Asynchronous network I/O API 
        "System.IAsyncResult",
        "System.AsyncCallback"
    };

    private static final String[] condTypes={
        "System.Console",
        "System.Environment",
        "System.Uri",

        //needed by compilers
        "System.Runtime.CompilerServices.RuntimeHelpers",
        "System.RuntimeFieldHandle",

        //VB MyCrap, let the compiler make stuff then don't let them use it
        "Microsoft.VisualBasic.ApplicationServices.ApplicationBase",
        "Microsoft.VisualBasic.Devices.Computer",
        "Microsoft.VisualBasic.ApplicationServices.User",

        //VB requires a lot of crap to make the Cxxx functions work.
        "System.Activator",
        "System.Type",

        //Late Binding, used by VB to cheat type safety
        "Microsoft.VisualBasic.CompilerServices.LateBinding",
        "Microsoft.VisualBasic.CompilerServices.NewLateBinding",

        //IIf
        "Microsoft.VisualBasic.Interaction",

        //IsNumeric
        "Microsoft.VisualBasic.CompilerServices.Versioned",

        //used by VB for static locals
        "System.Threading.Monitor",

        //Not sure about these
        "System.IAsyncResult",
        "System.AsyncCallback",
        "System.IAsyncResult",
        "System.Diagnostics.DebuggerStepThroughAttribute"
    };

    private static final String[] allowedMembers={
        // Properties
        "System.Console::get_Error",
        "System.Console::get_Out",
        "System.Environment::get_TickCount",

        // Methods
        "System.Console::Write",
        "System.Console::WriteLine",
        "System.Uri::FromHex",
        "System.Uri::HexEscape",
        "System.Uri::HexUnescape",
        "System.Uri::IsHexDigit",
        "System.Uri::IsHexEncoding",

        "System.Runtime.CompilerServices.RuntimeHelpers::InitializeArray",
        "System.Runtime.CompilerServices.RuntimeHelpers::GetObjectValue",

        //compiler magic
        "System.Type::GetType",
        "System.Type::GetTypeFromHandle",
        "System.Type::IsInstanceOfType",
        "System.Type::GetElementType",
        "System.Type::get_IsArray",
        "System.Activator::CreateInstance",

        //VB magic
        "Microsoft.VisualBasic.CompilerServices.LateBinding::LateGet",
        "Microsoft.VisualBasic.CompilerServices.LateBinding::LateIndexGet",
        "Microsoft.VisualBasic.CompilerServices.LateBinding::LateIndexSet",
        "Microsoft.VisualBasic.CompilerServices.LateBinding::LateSetComplex",
        "Microsoft.VisualBasic.CompilerServices.NewLateBinding::LateGet",
        "Microsoft.VisualBasic.CompilerServices.NewLateBinding::LateIndexGet",
        "Microsoft.VisualBasic.CompilerServices.NewLateBinding::LateIndexSet",
        "Microsoft.VisualBasic.CompilerServices.NewLateBinding::LateSetComplex",
        "Microsoft.VisualBasic.ApplicationServices.ApplicationBase::.ctor",
        "Microsoft.VisualBasic.Devices.Computer::.ctor",
        "Microsoft.VisualBasic.Interaction::IIf",
        "Microsoft.VisualBasic.CompilerServices.Versioned::IsNumeric",

        //statics
        "System.Threading.Monitor::Exit",
        "System.Threading.Monitor::Enter",
    };

    private static final Set allowedTypesList = new HashSet(Arrays.asList(allowedTypes));
    private static final Set condTypesList = new HashSet(Arrays.asList(condTypes));
    private static final Set allowedMembersList = new HashSet(Arrays.asList(allowedMembers));
}
