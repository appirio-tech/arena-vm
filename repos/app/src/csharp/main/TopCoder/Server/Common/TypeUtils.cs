namespace TopCoder.Server.Common {

    using System;

    sealed class TypeUtils {

        static Type CHAR_TYPE=typeof(char);
        static Type STRING_TYPE=typeof(string);
        static Type INT_TYPE=typeof(int);
        static Type INT_ARRAY_TYPE=typeof(int[]);
        static Type DOUBLE_ARRAY_TYPE=typeof(double[]);
        static Type DOUBLE_TYPE=typeof(double);
        static Type STRING_ARRAY_TYPE=typeof(string[]);
        static Type LONG_TYPE=typeof(long);
        static Type BOOLEAN_TYPE=typeof(bool);
        static Type LONG_ARRAY_TYPE=typeof(long[]);

        TypeUtils() {
        }

        internal static Type[] ToDotNetTypes(byte[] b) {
            Type[] types = new Type[b.Length];
            for (int i = 0; i < b.Length; i++)
            {
                types[i] = ToDotNetType(b[i]);
            }
            return types;

        }

        internal static Type ToDotNetType(byte b) {
            CType type=(CType) b;
            switch (type) {
            case CType.Char:
                return CHAR_TYPE;
            case CType.String:
                return STRING_TYPE;
            case CType.StringArray:
                return STRING_ARRAY_TYPE;
            case CType.Int:
                return INT_TYPE;
            case CType.DoubleArray:
                return DOUBLE_ARRAY_TYPE;
            case CType.IntArray:
                return INT_ARRAY_TYPE;
            case CType.Double:
                return DOUBLE_TYPE;
            case CType.Long:
                return LONG_TYPE;
            case CType.Boolean:
                return BOOLEAN_TYPE;
            case CType.LongArray:
                return LONG_ARRAY_TYPE;
            default:
                throw new ApplicationException("unknown type: "+type);
            }
        }

    }

}
