namespace TopCoder.Server.Serialization {

    using System;
    using System.Globalization;
    using System.Text;

    sealed class WrapUtil {

        static readonly string STRING_PREFIX = "S";
        static readonly string CHAR_PREFIX = "C";

        internal static object Wrap(object obj) {
            object wrappedObject;
            if (obj is object[]) {
                object[] objectArray = (object[]) obj;
                for (int i = 0; i < objectArray.Length; i++) {
                    objectArray[i] = Wrap(objectArray[i]);
                }
                wrappedObject = obj;
            } else if (obj is string) {
                wrappedObject = WrapString(STRING_PREFIX + obj);
            } else if (obj is char) {
                wrappedObject = WrapString(CHAR_PREFIX + obj);
            } else {
                wrappedObject = obj;
            }
            return wrappedObject;
        }

        internal static object Unwrap(object wrappedObject) {
            object obj;
            if (wrappedObject is object[]) {
                object[] objectArray = (object[]) wrappedObject;
                for (int i = 0; i < objectArray.Length; i++) {
                    objectArray[i] = Unwrap(objectArray[i]);
                }
                obj = wrappedObject;
            } else if (wrappedObject is string) {
                string str = UnwrapString((string) wrappedObject);
                if (str.StartsWith(STRING_PREFIX)) {
                    obj = str.Substring(STRING_PREFIX.Length);
                } else if (str.StartsWith(CHAR_PREFIX)) {
                    str = str.Substring(CHAR_PREFIX.Length);
                    if (str.Length != 1) {
                        throw new ApplicationException();
                    }
                    obj = str[0];
                } else {
                    throw new ApplicationException("str: " + str);
                }
            } else {
                obj = wrappedObject;
            }
            return obj;
        }

        static string WrapString(string str) {
            StringBuilder buf = new StringBuilder();
            foreach (char ch in str) {
                string s = ((int) ch).ToString("x");
                while (s.Length < 4) {
                    s = "0" + s;
                }
                if (s.Length != 4) {
                    throw new ApplicationException("s: " + s);
                }
                buf.Append(s);
            }
            return buf.ToString();
        }

        static string UnwrapString(string str) {
            if (str.Length % 4 != 0) {
                throw new ApplicationException("str: " + str);
            }
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < str.Length; i += 4) {
                string s = str.Substring(i, 4);
                char ch = (char) int.Parse(s, NumberStyles.HexNumber);
                buf.Append(ch);
            }
            return buf.ToString();
        }

    }

}
