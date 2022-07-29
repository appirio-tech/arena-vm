namespace TopCoder.Server.Common {

    sealed class CompileResponse: CustomWriteSerializable {

        readonly int languageID;
        readonly int requestID;
        readonly string errors;
        readonly string warnings="";
        readonly byte[] dllBytes;
        readonly byte[] pdbBytes;

        internal CompileResponse(int languageID, int requestID, byte[] dllBytes, byte[] pdbBytes, string errors) {
            this.languageID=languageID;
            this.requestID=requestID;
            this.errors=errors;
            this.dllBytes=dllBytes;
            this.pdbBytes=pdbBytes;
        }

        public void CustomWriteObject(ICSWriter writer) {
            writer.WriteInt(languageID);
            writer.WriteInt(requestID);
            writer.WriteString(errors);
            writer.WriteString(warnings);
            writer.WriteByteArray(dllBytes);
            writer.WriteByteArray(pdbBytes);
        }

        bool isSuccess() {
            return errors.Length<=0;
        }

        public override string ToString() {
            return "CompileResponse requestID="+languageID + " " + requestID+" "+isSuccess()+" "+errors+" "+ warnings;
        }

    }

}
