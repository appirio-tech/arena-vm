namespace TopCoder.Server.Tester {

    using System;
    using System.IO;
    using System.Runtime.Serialization;
    using System.Runtime.Serialization.Formatters.Soap;
    using System.Text;
    using System.Threading;

    using TopCoder.Server.Serialization;

    sealed class SerializationUtils {

        SerializationUtils() {
        }

        static readonly Encoding encoding = Encoding.Default;

        internal static void WriteObject(TextWriter writer, object[] objArray) {
            byte[] byteArray = ObjectConverter.Serialize(objArray);
            string str = encoding.GetString(byteArray);
            writer.Write(str);
            writer.Close();
        }

        internal static object[] ReadObject(string str) {
            byte[] byteArray = encoding.GetBytes(str);
            object[] objArray = (object[]) ObjectConverter.Deserialize(byteArray);
            return objArray;
        }

    }

}
