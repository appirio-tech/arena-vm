namespace TopCoder.Server.Controller {

    using System;

    sealed class CTMain {

        static CTController controller;

        public static void Main(string[] arg) {
            if (arg.Length!=1) {
                Console.WriteLine("Usage: DotNetCompilerTester server_host:port");
                return;
            }
            string address=arg[0];
            //int numWorkerThreads=int.Parse(arg[1]);
            AppDomain.CurrentDomain.ProcessExit+=new EventHandler(ShutdownHook);
            controller=new CTController(address,1);
        }

        static void ShutdownHook(object sender, EventArgs e) {
            controller.Stop();
        }

    }

}
