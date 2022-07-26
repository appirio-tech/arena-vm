namespace TopCoder.Server.Common {

    using System.Collections;

    interface ICSReader {

        bool ReadBool();
        byte ReadByte();
        int ReadInt();
        string ReadString();
        byte[] ReadByteArray();
        object[] ReadObjectArray();
        object[][] ReadObjectArrayArray();
        Hashtable ReadHashtable();
    }

}
