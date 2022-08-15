namespace TopCoder.Server.Controller {

    using System;
    using System.IO;

    using TopCoder.Server.Common;
    using TopCoder.Server.Util;
    using TopCoder.Io.Serialization.BasicType;

    sealed class CSWriter: ICSWriter {

        readonly IBasicTypeWriter writer;

        internal CSWriter(IBasicTypeWriter writer) {
            this.writer=writer;
        }
        
        public void Flush()
        {
            writer.Flush();
        }

        void WriteType(ObjectType type) {
            writer.WriteByte((byte) type);
        }

        public void WriteByte(byte b) {
            writer.WriteByte(b);
        }

        void WriteNull() {
            WriteType(ObjectType.Null);
        }

        public void WriteShort(short v) {
            writer.WriteShort(v);
        }

        public void WriteInt(int v) {
            writer.WriteInt(v);
        }

        void WriteLong(long v) {
            writer.WriteLong(v);
        }

        public void WriteBoolean(bool v) {
            writer.WriteBoolean(v);
        }

        void WriteDouble(double v) {
            writer.WriteDouble(v);
        }
        
        void WriteChar(char v)
        {
            writer.WriteChar(v);
        }

        public void WriteString(string s) {
            if (s==null) {
                WriteNull();
                return;
            }
            WriteType(ObjectType.String);
            writer.WriteString(s, null);
        }

        public void WriteByteArray(byte[] byteArray) {
            if (byteArray==null) {
                WriteNull();
                return;
            }
            WriteType(ObjectType.ByteArray);
            writer.WriteByteArray(byteArray);
        }

        void WriteJustDoubleArray(double[] doubleArray) {
            WriteType(ObjectType.DoubleArray);
            writer.WriteDoubleArray(doubleArray);
        }

        void WriteJustIntArray(int[] intArray) {
            WriteType(ObjectType.IntArray);
            writer.WriteIntArray(intArray);
        }

        void WriteJustStringArray(string[] stringArray) {
            WriteType(ObjectType.StringArray);
            writer.WriteStringArray(stringArray, null);
        }

        void WriteJustLongArray(long[] longArray) {
            WriteType(ObjectType.LongArray);
            writer.WriteLongArray(longArray);
        }

        void WriteJustObjectArray(object[] array)
        {
            int size = array.Length;
            WriteType(ObjectType.ObjectArray);
            WriteInt(size);
            for (int i = 0; i < size; i++)
            {
                WriteObject(array[i]);
            }
        }

        void CustomWriteObject(object obj) {
            ((CustomWriteSerializable) obj).CustomWriteObject(this);
        }

        public void WriteObject(object obj) {
            if (obj==null) {
                WriteNull();
            } else if (obj is CompileResponse) {
                WriteType(ObjectType.CompileResponse);
                CustomWriteObject(obj);
            } else if (obj is TestResponse) {
                WriteType(ObjectType.TestResponse);
                CustomWriteObject(obj);
            } else if (obj is SystemTestResponse) {
                WriteType(ObjectType.SystemTestResponse);
                CustomWriteObject(obj);
            } else if (obj is PracticeSystemTestResponse) {
                WriteType(ObjectType.PracticeSystemTestResponse);
                CustomWriteObject(obj);
            } else if (obj is int) {
                WriteType(ObjectType.Integer);
                WriteInt((int) obj);
            } else if (obj is string) {
                WriteString((string) obj);
            } else if (obj is bool) {
                WriteType(ObjectType.Boolean);
                WriteBoolean((bool) obj);
            } else if (obj is long) {
                WriteType(ObjectType.Long);
                WriteLong((long) obj);
            } else if (obj is char) {
                WriteType(ObjectType.Char);
                WriteChar((char) obj);
            } else if (obj is int[]) {
                WriteJustIntArray((int[]) obj);
            } else if (obj is double[]) {
                WriteJustDoubleArray((double[]) obj);
            } else if (obj is string[]) {
                WriteJustStringArray((string[]) obj);
            } else if (obj is long[]) {
                WriteJustLongArray((long[]) obj);
            } else if (obj is double) {
                WriteType(ObjectType.Double);
                WriteDouble((double) obj);
            } else if (obj is object[]) {
                WriteJustObjectArray((object[])obj);
            } else {
                throw new ApplicationException("not implemented: " + obj.GetType());
            }
        }

    }

}
