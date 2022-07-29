namespace TopCoder.Server.Common {

    interface ICSWriter {

        void WriteByte(byte v);
        void WriteShort(short v);
        void WriteInt(int v);
        void WriteBoolean(bool v);
        void WriteString(string s);
        void WriteObject(object obj);
        void WriteByteArray(byte[] b);

    }

}
