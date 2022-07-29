namespace TopCoder.Server.Util {

    using System;
    using System.IO;

    sealed class Log {

        static TextWriter outWriter;
        static TextWriter errWriter;
    
        static Log() {
            string baseDir=AppDomain.CurrentDomain.BaseDirectory;
            outWriter=new StreamWriter(new FileStream(baseDir+ System.Diagnostics.Process.GetCurrentProcess().Id +"out.log", FileMode.Append));
            errWriter=new StreamWriter(new FileStream(baseDir+ System.Diagnostics.Process.GetCurrentProcess().Id+"err.log", FileMode.Append));
            Console.SetOut(outWriter);
            Console.SetError(errWriter);
        }

        Log() {
        }

        internal static void WriteLine(string msg) {
            DateTime time=DateTime.Now;
            Console.WriteLine(time.ToString("MM/dd/yy HH:mm:ss,fff")+": "+msg);
            outWriter.Flush();
        }

        internal static void Close() {
            errWriter.Close();
            outWriter.Close();
        }

    }

}
