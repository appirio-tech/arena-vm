namespace TopCoder.Server.Common {

    using System;

    abstract class BaseTestRequest: BaseRequest {

        byte[] dllBytes;
        byte[] pdbBytes;
        ProblemSignature signature;

        public override void CustomReadObject(ICSReader reader) {
            base.CustomReadObject(reader);
            dllBytes=reader.ReadByteArray();
            pdbBytes=reader.ReadByteArray();
            signature=new ProblemSignature();
            signature.CustomReadObject(reader);
            //Console.WriteLine("DllBytes 2: "+DllBytes);
        }

        internal byte[] DllBytes {
            get {
                return dllBytes;
            }
        }

        internal byte[] PdbBytes {
            get {
                return pdbBytes;
            }
        }

        internal ProblemSignature Signature {
            get {
                return signature;
            }
        }

        internal string ClassName {
            get {
                return signature.ClassName;
            }
        }

        internal string MethodName {
            get {
                return signature.MethodName;
            }
        }

        internal Type[] ArgTypes {
            get {
                return signature.ArgTypes;
            }
        }

        public override string ToString() {
            int DllLength=DllBytes==null ? -1 : DllBytes.Length;
            int PdbLength=PdbBytes==null ? -1 : PdbBytes.Length;
            return base.ToString()+" DllLength="+DllLength+" PdbLength="+PdbLength;
        }

    }

}
