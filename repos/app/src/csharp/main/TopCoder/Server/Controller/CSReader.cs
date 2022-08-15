namespace TopCoder.Server.Controller {

    using System;
    using System.Collections;
    using System.IO;
    using System.Text;

    using TopCoder.Server.Common;
    using TopCoder.Io.Serialization.BasicType;

    sealed class CSReader: ICSReader {

        readonly IBasicTypeReader reader;
        
        internal void ResetMemoryUsageCounter()
        {
            reader.ResetMemoryUsageCounter();
        }
        
        internal ulong MemoryUsageLimit
        {
            get
            {
                return reader.MemoryUsageLimit;
            }
            set
            {
                reader.MemoryUsageLimit = value;
            }
        }

        internal CSReader(IBasicTypeReader reader) {
            this.reader=reader;
        }

        public bool ReadBool() {
            return reader.ReadBoolean();
        }

        public byte ReadByte() {
            return reader.ReadByte();
        }

        public int ReadInt() {
            return reader.ReadInt();
        }

        int ReadShort() {
            return reader.ReadShort();
        }

        long ReadLong() {
            return reader.ReadLong();
        }

        double ReadDouble() {
            return reader.ReadDouble();
        }

        public Hashtable ReadHashtable() {
            if(IsNull(ObjectType.Hashtable)) {
                return null;
            }
            return ReadJustHashtable();
        }

        Hashtable ReadJustHashtable() {
            int size = ReadInt();
            Hashtable hashtable = new Hashtable(size);
            for(int i = 0; i < size; i++) {
                object key = ReadObject();
                object value = ReadObject();
                hashtable.Add(key, value);
            }
            return hashtable;
        }

        bool IsNull(ObjectType expected) {
            ObjectType b=(ObjectType) ReadByte();
            if (b==ObjectType.Null) {
                return true;
            }
            if (b!=expected) {
                throw new ApplicationException("unexpected, b="+b+", expected="+expected);
            }
            return false;
        }

        public byte[] ReadByteArray() {
            if (IsNull(ObjectType.ByteArray)) {
                return null;
            }
            return reader.ReadByteArray();
        }

        object[] ReadJustObjectArray() {
            int size=ReadInt();
            object[] r=new object[size];
            for (int i=0; i<size; i++) {
                r[i]=ReadObject();
            }
            return r;
        }

        public object[] ReadObjectArray() {
            if (IsNull(ObjectType.ObjectArray)) {
                return null;
            }
            return ReadJustObjectArray();
        }

        public object[][] ReadObjectArrayArray() {
            if (IsNull(ObjectType.ObjectArrayArray)) {
                return null;
            }
            int size=ReadInt();
            object[][] r=new object[size][];
            for (int i=0; i<size; i++) {
                r[i]=ReadJustObjectArray();
            }
            return r;
        }

        public string ReadString() {
            if (IsNull(ObjectType.String)) {
                return null;
            }
            return reader.ReadString();
        }

        internal object ReadObject() {
            ObjectType type=(ObjectType) reader.ReadByte();
            switch (type) {
            case ObjectType.Null:
                return null;
            case ObjectType.Char:
                return reader.ReadChar();
            case ObjectType.Boolean:
                return ReadBool();
            case ObjectType.Integer:
                return ReadInt();
            case ObjectType.Long:
                return ReadLong();
            case ObjectType.Double:
                return ReadDouble();
            case ObjectType.String:
                return reader.ReadString();
            case ObjectType.IntArray:
                return reader.ReadIntArray();
            case ObjectType.DoubleArray:
                return reader.ReadDoubleArray();
            case ObjectType.StringArray:
                return reader.ReadStringArray();
            case ObjectType.Hashtable:
                return ReadJustHashtable();
            case ObjectType.ByteArray:
                return reader.ReadByteArray();
            case ObjectType.ObjectArray:
                return ReadJustObjectArray();
            case ObjectType.LongArray:
                return reader.ReadLongArray();
            case ObjectType.CompileRequest:
                CompileRequest compileRequest=new CompileRequest();
                compileRequest.CustomReadObject(this);
                return compileRequest;
            case ObjectType.TestRequest:
                TestRequest testRequest=new TestRequest();
                testRequest.CustomReadObject(this);
                return testRequest;
            case ObjectType.SystemTestRequest:
                SystemTestRequest systemTestRequest=new SystemTestRequest();
                systemTestRequest.CustomReadObject(this);
                return systemTestRequest;
            case ObjectType.PracticeSystemTestRequest:
                PracticeSystemTestRequest pstRequest=new PracticeSystemTestRequest();
                pstRequest.CustomReadObject(this);
                return pstRequest;
            case ObjectType.RestartServiceRequest:
                RestartServiceRequest restartServiceRequest=new RestartServiceRequest();
                restartServiceRequest.CustomReadObject(this);
                return restartServiceRequest;

            default:
                throw new ApplicationException("ReadObject, not implemented: "+type);
            }
        }
        
        internal void Close() {
            reader.Close();
        }
    }
    
}
