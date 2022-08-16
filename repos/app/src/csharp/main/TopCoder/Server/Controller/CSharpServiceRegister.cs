namespace TopCoder.Server.Controller {

    using System;
    using System.ComponentModel;
    using System.Configuration.Install;
    using System.ServiceProcess;
    
    [RunInstallerAttribute(true)]
    public sealed class CSharpServiceRegister: Installer {
    
        public CSharpServiceRegister() {
            ServiceProcessInstaller processInstaller=new ServiceProcessInstaller();
            ServiceInstaller serviceInstaller=new ServiceInstaller();
            processInstaller.Account=ServiceAccount.LocalSystem;
            serviceInstaller.ServiceName="CSharpService";
            Installers.Add(serviceInstaller);
            Installers.Add(processInstaller);
        }
    
    }

}
