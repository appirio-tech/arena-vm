namespace TopCoder.Server.Controller {
    
    using System;
    using System.Configuration;
    using System.ServiceProcess;
    
    using TopCoder.Server.Util;
    
    public sealed class CSharpService: ServiceBase {
    
        CTController controller;
        bool started;

        public CSharpService() {
            ServiceName="CSharpService";
        }
    
        protected override void OnStart(string[] args) {
            Log.WriteLine("OnStart starting");
            Log.WriteLine("PATH="+Environment.GetEnvironmentVariable("PATH"));
            AppSettingsReader reader=new AppSettingsReader();
            string address=(string) reader.GetValue("address",typeof(string));
            int numWorkerThreads=(int) reader.GetValue("numWorkerThreads",typeof(int));
            controller=new CTController(address,numWorkerThreads);
            Log.WriteLine("OnStart started");
            started=true;
        }
    
        protected override void OnStop() {
            Log.WriteLine("OnStop stopping");
            if (started) {
                controller.Stop();
            }
            Log.WriteLine("OnStop stopped");
            Log.Close();
        }
    
        public static void Main(string[] args) {
            ServiceBase.Run(new CSharpService());
        }
    
    }

}
