namespace TopCoder.Server.Common {

    using System;
    using System.Collections;

    sealed class CompileRequest: BaseRequest {

        string programText;
        Hashtable sourceFiles;
        Hashtable dllFiles;
        ProblemSignature problemSignature;

        public override void CustomReadObject(ICSReader reader) {
            base.CustomReadObject(reader);
            programText=reader.ReadString();
            sourceFiles = reader.ReadHashtable();
            dllFiles = reader.ReadHashtable();
            problemSignature=new ProblemSignature();
            problemSignature.CustomReadObject(reader);
        }

        internal Hashtable SourceFiles {
            get {
                return sourceFiles;
            }
        }

        internal Hashtable DllFiles {
            get {
                return dllFiles;
            }
        }

        internal string ProgramText {
            get {
                return programText;
            }
        }

        internal ProblemSignature Signature {
            get {
                return problemSignature;
            }
        }

        internal string ClassName {
            get {
                return Signature.ClassName;
            }
        }

        internal string MethodName {
            get {
                return Signature.MethodName;
            }
        }

        internal Type ReturnType {
            get {
                return Signature.ReturnType;
            }
        }

        internal Type[] ArgTypes {
            get {
                return Signature.ArgTypes;
            }
        }

        public override string ToString() {
            return "CompileRequest "+base.ToString()+" Hash="+programText.GetHashCode()+
                " ProblemSignature="+problemSignature+" SourceFiles="+sourceFiles+" DllFiles="+dllFiles;
        }

    }

}
