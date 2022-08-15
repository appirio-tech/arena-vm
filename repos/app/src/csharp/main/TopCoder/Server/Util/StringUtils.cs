namespace TopCoder.Server.Util {

    using System;
    using System.Text;

    sealed class StringUtils {

        StringUtils() {
        }

        internal static string ToString(object obj) {
            if (obj==null) {
                return "null";
            }
            if (obj is string) {
                string s=obj.ToString();
                StringBuilder buf=new StringBuilder(s.Length+2);
                buf.Append('"');
                buf.Append(s);
                buf.Append('"');
                return buf.ToString();
            }
            if (obj is char) {
                string s=obj.ToString();
                StringBuilder buf=new StringBuilder(s.Length+2);
                buf.Append("'");
                buf.Append(s);
                buf.Append("'");
                return buf.ToString();
            }
            if (obj is Array) {
                StringBuilder buf=new StringBuilder("[");
                bool first=true;
                foreach (object o in (Array) obj) {
                    if (first) {
                        first=false;
                    } else {
                        buf.Append(',');
                    }
                    buf.Append(ToString(o));
                }
                buf.Append(']');
                return buf.ToString();
            }
            return obj.ToString();
        }

    }

}
