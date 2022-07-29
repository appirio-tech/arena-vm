namespace TopCoder.Server.Tester {

    using System;
    using System.Diagnostics;
    using System.IO;
    using System.Collections;
    using System.Threading;

    using TopCoder.Server.Common;
    using TopCoder.Server.Util;

    sealed class Tester: ITester {

        readonly static string baseDir=AppDomain.CurrentDomain.BaseDirectory;

        void CreateFile(string fileName, byte[] b) {
            Stream stream=File.Create(fileName);
            stream.Write(b,0,b.Length);
            stream.Close();
        }

        void DeleteFile(string fileName) {
            File.Delete(fileName);
        }

        TestResponse ProcessResults(string s, int requestID, bool isSystemTest) {
            Console.WriteLine("S is: " + this.stderr);
            object[] objArray=SerializationUtils.ReadObject(s);
            int elapsedTime;
            bool hasResult;
            bool isTimeout;
            object result;
            string stdout;
            string stderr;
            if (objArray==null) {
                Log.WriteLine("fatal stack overflow error (?), requestID="+requestID);
                elapsedTime=0;
                hasResult=false;
                isTimeout=false;
                result=null;
                stdout="";
                stderr="Internal error. This is usually a fatal stack overflow error or OutOfMemoryException.";
            } else {
                elapsedTime=(int) objArray[0];
                hasResult=(bool) objArray[1];
                result = objArray[2];
                //Log.WriteLine("result: " + result);
            	if(isSystemTest)
            	{
            		stdout = "";
            		stderr = "";
            	}
            	else
            	{
              	 	stdout=(string) objArray[3];
               	  	stderr=(string) objArray[4];
            	}
                isTimeout = (bool) objArray[5];
            }
            return new TestResponse(requestID,hasResult,result,elapsedTime,stdout,stderr,isTimeout);
        }

        void SendParams(TextWriter writer, string dllFileName, string className, 
                        string methodName, Type[] argTypes, object[] args) {
            object[] objArray={dllFileName,className,methodName,argTypes,args};
            SerializationUtils.WriteObject(writer,objArray);
        }

        bool stdoutDone = false;
        string stdout = "";
        string stderr = "";

        void readStdout()
        {
            stdout = p.StandardOutput.ReadToEnd();
            stdoutDone = true;
        }

        void readStderr()
        {
            stderr = p.StandardError.ReadToEnd();
        }

        Process p;

        TestResponse RunTestProcess(string dllFileName, int requestID, string className,
                                    string methodName, Type[] argTypes, object[] args, bool isSystemTest) {
            p=new Process();
            stdout = "";
            stderr = "";
            ProcessStartInfo startInfo=p.StartInfo;
            startInfo.FileName=baseDir+"TestProcess";
            startInfo.Arguments="";
            startInfo.UseShellExecute=false;
            startInfo.RedirectStandardOutput=true;
            startInfo.RedirectStandardError=true;
            startInfo.RedirectStandardInput=true;
            p.Start();
                                   	
            SendParams(p.StandardInput,dllFileName,className,methodName,argTypes,args);
            Thread stderrThread = new Thread(new ThreadStart(readStderr));
            stderrThread.Start();

            Thread stdoutThread = new Thread(new ThreadStart(readStdout));
            stdoutThread.Start();

            p.WaitForExit();
            while(!stdoutDone) {
                Thread.Sleep(10);
            }
            return ProcessResults(stdout,requestID, isSystemTest);
        }

        void SetFileNames(string fileName, out string dllFileName, out string pdbFileName) {
            dllFileName=fileName+".dll";
            pdbFileName=fileName+".pdb";
        }

        void SetRandomFileNames(string dir, string className, int id,
                                out string dllFileName, out string pdbFileName) {
            string fileName=dir+RandomNameUtils.GetRandomFileName(className,id);
            SetFileNames(fileName,out dllFileName, out pdbFileName);
        }

        TestResponse ITester.ProcessTestRequest(int requestID, Language language, byte[] dllBytes,
                byte[] pdbBytes, string className, string methodName, Type[] argTypes, 
                object[] args, int userID, int contestID, int roundID, int problemID, bool isSystemTest, Hashtable dllFiles) {
            int id=requestID;
            string dir=GetDir(language, userID,contestID,roundID,problemID);
            string dllFileName;
            string pdbFileName;
            SetFileNames(dir+className,out dllFileName,out pdbFileName);
            try {
                DeleteFile(dllFileName);
                DeleteFile(pdbFileName);

                IDictionaryEnumerator enum1 = dllFiles.GetEnumerator();
                while(enum1.MoveNext()) {
                    DeleteFile(dir + (string)enum1.Key);
                }
            } catch (SystemException) {
                SetRandomFileNames(dir,className,requestID,out dllFileName,out pdbFileName);
            }
            for (;;) {
                try {
                    CreateFile(dllFileName,dllBytes);
                    CreateFile(pdbFileName,pdbBytes);

                    try {
                        IDictionaryEnumerator enum1 = dllFiles.GetEnumerator();
                        while(enum1.MoveNext()) {
                            FileStream bytewriter = File.Create(dir + (string)enum1.Key);
                            bytewriter.Write((byte[])enum1.Value, 0, ((byte[])enum1.Value).Length);
                            bytewriter.Close(); 
                        }
                    } catch(Exception) {
                        //files exist, use them
                    }
                      
                    break;
                } catch (SystemException) {
                    SetRandomFileNames(dir,className,requestID,out dllFileName,
                                       out pdbFileName);
                }
            }
            TestResponse response=RunTestProcess(dllFileName,requestID,className,methodName,
                                                 argTypes,args, isSystemTest);
            return response;
        }

        string GetDir(Language language, int userID, int contestID, int roundID, int problemID) {
            return DirectoryUtils.GetDir(language, userID,contestID,roundID,problemID);
        }

    }

}
