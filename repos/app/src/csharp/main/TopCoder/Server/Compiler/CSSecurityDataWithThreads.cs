namespace TopCoder.Server.Compiler {

    using System;
    using System.Collections;
    using System.Reflection;

    sealed class CSSecurityDataWithThreads {

        // all classes in these namespaces and their nested namespaces are allowed
        readonly string[] allowedNamespaces={
            "System.Collections",
            "System.Globalization",
            "System.Text",
            "System.Threading",
        };

        // namespaces not included here and in allowedNamespaces are prohibited
        readonly string[] condNamespaces={
            "System",
            "System.Drawing",
            "System.Drawing.Drawing2D",
            "Microsoft.VisualBasic",
        };

        readonly IEnumerable[] condNamespacesTypeList;

        readonly string[] prohibitedNamespaces={
            "Microsoft.CSharp",
            "Microsoft.JScript",
            "Microsoft.Vsa",
            "Microsoft.Win32",
            "System.CodeDom",
            "System.ComponentModel",
            "System.Configuration",
            "System.Data",
            "System.Deployment",
            "System.Deployment.Internal",
            "System.Diagnostics",
            "System.DirectoryServices",
            "System.Drawing.Design",
            "System.Drawing.Imaging",
            "System.Drawing.Printing",
            "System.Drawing.Text",
            "System.EnterpriseServices",
            "System.IO",
            "System.Management",
            "System.Media",
            "System.Messaging",
            "System.Net",
            "System.Reflection",
            "System.Resources",
            "System.Runtime",
            "System.Security",
            "System.ServiceProcess",
            "System.Timers",
            "System.Web",
            "System.Windows",
            "System.Xml",
            "typeof",
            "GetType",
            "Assembly",
            "Microsoft.VisualBasic.ApplicationServices",
            "Microsoft.VisualBasic.CompilerServices",
            "Microsoft.VisualBasic.Devices",
            "Microsoft.VisualBasic.FileIO",
            "Microsoft.VisualBasic.Logging",
            "Microsoft.VisualBasic.MyServices",
        };

        readonly string[] allowedTypes={
            // Classes
            "System.ArgumentException",
            "System.ArgumentNullException",
            "System.ArgumentOutOfRangeException",
            "System.ArithmeticException",
            "System.Array",
            "System.ArrayTypeMismatchException",
            "System.BitConverter",
            "System.Buffer",
            "System.CharEnumerator",
            "System.Convert",
            "System.Delegate",
            "System.DivideByZeroException",
            "System.Enum",
            "System.Exception",
            "System.FormatException",
            "System.IndexOutOfRangeException",
            "System.InvalidCastException",
            "System.Math",
            "System.NotFiniteNumberException",
            "System.NullReferenceException",
            "System.Object",
            "System.OverflowException",
            "System.Random",
            "System.RankException",
            "System.String",
            "System.StringSplitOptions",
            "System.StringComparer",
            "System.Drawing.PointConverter",
            "System.Drawing.RectangleConverter",
            "System.Drawing.Region",
            "System.Drawing.SizeConverter",
            "System.Drawing.Drawing2D.GraphicsPath",
            "System.Drawing.Drawing2D.GraphicsPathIterator",
            "System.Drawing.Drawing2D.Matrix",
            "System.Drawing.Drawing2D.PathData",
            "System.Drawing.Drawing2D.RegionData",

            // Interfaces
            "System.ICloneable",
            "System.IComparable",
            "System.IConvertible",
            "System.ICustomFormatter",
            "System.IFormatProvider",
            "System.IFormattable",

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

        };

        readonly string[] condTypes={
            "System.Console",
            "System.Environment",
            "System.Uri",
        };

        readonly string[] condTypesNamespace;
        readonly string[] condTypesSimpleName;
        readonly IEnumerable[] condTypesMembersList;

        readonly string[] allowedMembers={
            // Properties
            "System.Console.Error",
            "System.Console.Out",
            "System.Environment.TickCount",

            // Methods
            "System.Console.Write",
            "System.Uri.FromHex",
            "System.Uri.HexEscape",
            "System.Uri.HexUnescape",
            "System.Uri.IsHexDigit",
            "System.Uri.IsHexEncoding",
        };

        internal CSSecurityDataWithThreads() {
            string location=System.AppDomain.CurrentDomain.BaseDirectory+"RefDlls.dll";
            Assembly refDlls=Assembly.LoadFrom(location);
            IDictionary namespaceMap=new SortedList();
            IDictionary typeMap=new SortedList();
            foreach (AssemblyName assemblyName in refDlls.GetReferencedAssemblies()) {
                Assembly assembly=Assembly.Load(assemblyName);
                foreach (Type type in assembly.GetTypes()) {
                    if (type.IsPublic) {
                        string namespaceName=type.Namespace;
                        namespaceMap[namespaceName]="";
                        if (!NamespaceStartsWith(prohibitedNamespaces,namespaceName) &&
                                !NamespaceStartsWith(allowedNamespaces,namespaceName)) {
                            typeMap[type.FullName]="";
                        }
                    }
                }
            }
            CheckTypes(typeMap,allowedTypes);
            CheckTypes(typeMap,condTypes);
            IEnumerable prohibitedTypes=new ArrayList(typeMap.Keys);
            condNamespacesTypeList=new IEnumerable[condNamespaces.Length];
            for (int i=0; i<condNamespaces.Length; i++) {
                IList list=new ArrayList();
                string namespaceName=condNamespaces[i];
                foreach (string typeName in prohibitedTypes) {
                    string name=GetSimpleName(typeName,namespaceName);
                    if (name!=null) {
                        list.Add(name);
                    }
                }
                condNamespacesTypeList[i]=list;
            }
            foreach (string name in namespaceMap.Keys) {
                bool bad=true;
                foreach (string namespaceName in allowedNamespaces) {
                    if (name==namespaceName || name.StartsWith(namespaceName+".")) {
                        bad=false;
                        break;
                    }
                }
                if (bad) {
                    foreach (string namespaceName in condNamespaces) {
                        if (name==namespaceName) {
                            bad=false;
                            break;
                        }
                    }
                }
                if (bad) {
                    if (!NamespaceStartsWith(prohibitedNamespaces,name)) {
                        throw new ApplicationException("new namespace? "+name);
                    }
                }
            }
            condTypesMembersList=new IEnumerable[condTypes.Length];
            condTypesNamespace=new string[condTypes.Length];
            condTypesSimpleName=new string[condTypes.Length];
            for (int i=0; i<condTypes.Length; i++) {
                IList list=new ArrayList();
                string typeName=condTypes[i];
                bool done=false;
                foreach (string namespaceName in condNamespaces) {
                    string simpleName=GetSimpleName(typeName,namespaceName);
                    if (simpleName!=null) {
                        condTypesNamespace[i]=namespaceName;
                        condTypesSimpleName[i]=simpleName;
                        done=true;
                        break;
                    }
                }
                if (!done) {
                    throw new ApplicationException("unknown type: "+typeName);
                }
                foreach (string memberName in allowedMembers) {
                    if (memberName.StartsWith(typeName)) {
                        list.Add(GetSimpleName(memberName,typeName));
                    }
                }
                condTypesMembersList[i]=list;
            }
        }

        internal static bool IsIdentifierChar(char ch) {
            return char.IsLetterOrDigit(ch);
        }

        internal static string Contains(string name, string text) {
            string result;
            int index = 0;
            for (;;) {
                index = text.IndexOf(name, index);
                if (index < 0) {
                    result = null;
                    break;
                }
                result = name;
                int nextIndex = index + name.Length;
                if (nextIndex < text.Length && IsIdentifierChar(text[nextIndex])) {
                    result = null;
                } else {
                    int prevIndex = index - 1;
                    if (prevIndex >= 0 && IsIdentifierChar(text[prevIndex])) {
                        result = null;
                    }
                }
                index = nextIndex;
                if (result != null) {
                    break;
                }
            }
            return result;
        }

        static string Contains(IEnumerable namespaces, string programText) {
            foreach (string namespaceName in namespaces) {
                string s = Contains(namespaceName, programText);
                if (s != null) {
                    return s;
                }
            }
            return null;
        }

        internal string Check(string programText) {
            string reason=Contains(prohibitedNamespaces, programText);
            if (reason!=null) {
                return reason;
            }
            IDictionary nsMap=new Hashtable();
            for (int i=0; i<condNamespaces.Length; i++) {
                string nsName=condNamespaces[i];
                if (programText.IndexOf(nsName)<0) {
                    continue;
                }
                nsMap[nsName]="";
                reason=Contains(condNamespacesTypeList[i],programText);
                if (reason!=null) {
                    return nsName+"."+reason;
                }
            }
            for (int i=0; i<condTypes.Length; i++) {
                string errorMessage="Certain usage of "+condTypes[i];
                if (nsMap[condTypesNamespace[i]]==null) {
                    continue;
                }
                string simpleName=condTypesSimpleName[i];
                int ind=0;
                for (;;) {
                    ind=programText.IndexOf(simpleName,ind);
                    if (ind<0) {
                        break;
                    }
                    ind+=simpleName.Length;
                    if (ind>=programText.Length) {
                        break;
                    }
                    if (programText[ind]!='.') {
                        return errorMessage;
                    }
                    ind++;
                    bool found=false;
                    string psub=programText.Substring(ind);
                    foreach (string memberName in condTypesMembersList[i]) {
                        if (psub.StartsWith(memberName)) {
                            found=true;
                            ind+=memberName.Length;
                            break;
                        }
                    }
                    if (!found) {
                        return errorMessage;
                    }
                }
            }
            return null;
        }

        bool NamespaceStartsWith(string[] namespaces, string name) {
            foreach (string s in namespaces) {
                if (name.StartsWith(s)) {
                    return true;
                }
            }
            return false;
        }

        void CheckTypes(IDictionary map, string[] types) {
            foreach (string name in types) {
                if (map[name]==null) {
                    throw new ApplicationException("misspelled type? "+name);
                }
                map.Remove(name);
            }
        }

        string GetSimpleName(string typeName, string namespaceName) {
            int ind=typeName.LastIndexOf('.');
            if (typeName.Substring(0,ind)==namespaceName) {
                return typeName.Substring(ind+1);
            }
            return null;
        }

    }

}
