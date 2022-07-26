namespace TopCoder.Server.Common {

    using System;

    sealed class ProblemSignature: CustomReadSerializable {

        string className;
        string methodName;
        Type returnType;
        Type[] argTypes;

        string name;

        public void CustomReadObject(ICSReader reader) {
            className=reader.ReadString();
            methodName=reader.ReadString();
            returnType=TypeUtils.ToDotNetType(reader.ReadByte());
            byte[] b=reader.ReadByteArray();
            argTypes=new Type[b.Length];
            for (int i=0; i<b.Length; i++) {
                argTypes[i]=TypeUtils.ToDotNetType(b[i]);
            }
            SetName();
        }

        internal string ClassName {
            get {
                return className;
            }
        }

        internal string MethodName {
            get {
                return methodName;
            }
        }

        internal Type ReturnType {
            get {
                return returnType;
            }
        }

        internal Type[] ArgTypes {
            get {
                return argTypes;
            }
        }

        void SetName() {
            string argStr="";
            for (int i=0; i<argTypes.Length; i++) {
                if (i!=0) {
                    argStr+=",";
                }
                argStr+=argTypes[i].Name;
            }
            name="("+argStr+")";
            name=returnType.Name+" "+className+"."+methodName+" "+argStr;
        }

        public override string ToString() {
            return name;
        }

    }

}
