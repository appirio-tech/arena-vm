namespace TopCoder.Server.Common {

    using System;
    using System.Text;

    sealed class RandomNameUtils {

        static readonly Random random=new Random();

        RandomNameUtils() {
        }

        internal static string GetRandomFileName(string className, int id) {
            const int RANDOM_FILENAME_MAX=10;
            StringBuilder buf=new StringBuilder(RANDOM_FILENAME_MAX+className.Length+8);
            buf.Append(className);
            buf.Append('-');
            buf.Append(id);
            buf.Append('-');
            for (int i=0; i<RANDOM_FILENAME_MAX; i++) {
                buf.Append(GetRandomChar());
            }
            return buf.ToString();
        }

        static char GetRandomChar() {
            return (char) ('a'+random.Next(26));
        }

    }

}
