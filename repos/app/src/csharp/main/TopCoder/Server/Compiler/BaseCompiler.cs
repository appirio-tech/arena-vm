namespace TopCoder.Server.Compiler {

    using System;
    using System.Collections;
    using System.Diagnostics;
    using System.IO;
    using System.Text;

    using TopCoder.Server.Common;
    using TopCoder.Server.Util;

    abstract class BaseCompiler: ICompiler {

        readonly static CSSecurityCheck securityCheck=new CSSecurityCheck();

        abstract protected string GetExt();
        abstract protected Language GetLanguage();
        abstract protected string GetCompilerExeName();
        abstract protected string GetCompilerArguments(string sourceFileName);

        void DeleteFile(string fileName) {
            File.Delete(fileName);
        }

        static string RunProcess(string fileName, string arguments) {
            Process process=new Process();
            ProcessStartInfo startInfo=process.StartInfo;
            startInfo.FileName=fileName;
            startInfo.Arguments=arguments;
            startInfo.UseShellExecute=false;
            startInfo.RedirectStandardOutput=true;
            process.Start();
            string stdout=process.StandardOutput.ReadToEnd();
            process.WaitForExit();
            return stdout;
        }

        string CheckAssembly(string dllFileName, string className, Type returnType, 
                             string methodName, Type[] argTypes) {
            StringBuilder argBuf=new StringBuilder();
            foreach (Type type in argTypes) {
                argBuf.Append(' ');
                argBuf.Append(type.ToString());
            }
            string args=dllFileName+" "+className+" "+returnType+" "+methodName+argBuf;
            return RunProcess("AssemblyChecker",args);
        }

        byte[] ReadBinaryFile(string fileName) {
            FileStream fileStream;
            fileStream=File.OpenRead(fileName);
            int length=(int) fileStream.Length;
            byte[] b=new byte[length];
            int offset=0;
            while (offset<length) {
                offset+=fileStream.Read(b,offset,length);
            }
            fileStream.Close();
            return b;
        }

        string GetSourceFileName(string className, int userID, int contestID, int roundID, 
                                 int problemID, bool isDeleteSucceeded, int requestID) {
            string fileName;
            if (isDeleteSucceeded) {
                fileName=className;
            } else {
                fileName=RandomNameUtils.GetRandomFileName(className,requestID);
            }
            string sourceFileName=GetDir(userID,contestID,roundID,problemID)+fileName+
                "."+GetExt();
            return sourceFileName;
        }

        void CreateInputFiles(string sourceFileName, string programText, Hashtable sourceFiles, 
                Hashtable dllFiles, int userID, int contestID, int roundID, int problemID) {
            string dir=GetDir(userID,contestID,roundID,problemID);
            StreamWriter writer=File.CreateText(sourceFileName);
            writer.Write(programText);
            writer.Close();
            IDictionaryEnumerator enum1 = sourceFiles.GetEnumerator();
            while(enum1.MoveNext()) {
                writer = File.CreateText(dir + (string)enum1.Key);
                writer.Write((string)enum1.Value);
                writer.Close(); 
            }

            enum1 = dllFiles.GetEnumerator();
            while(enum1.MoveNext()) {
                FileStream bytewriter = File.Create(dir + (string)enum1.Key);
                bytewriter.Write((byte[])enum1.Value, 0, ((byte[])enum1.Value).Length);
                bytewriter.Close(); 
            }
        }

        string FilterCompilerOut(string errors) {
            if (errors.Length<=0) {
                return errors;
            }
            StringBuilder buf=new StringBuilder();
            StringReader reader=new StringReader(errors);
            bool first=true;
            for (;;) {
                string s=reader.ReadLine();
                if (s==null) {
                    break;
                }
                string[] langs = {".cs(", ".vb("};
                foreach (string lang in langs) {
                    int ind=s.IndexOf(lang);
                    if (ind>=0) {
                        s=s.Substring(ind+3);
                        break;
                    }
                }
                if (first) {
                    first=false;
                } else {
                    buf.Append("\n");
                }
                buf.Append(s);
            }
            reader.Close();
            return buf.ToString();
        }

        CompileResponse ICompiler.ProcessCompileRequest(CompileRequest request) {
            string className=request.ClassName;
            string programText=request.ProgramText;
            int requestID=request.RequestID;
            int userID=request.UserID;
            int contestID=request.ContestID;
            int roundID=request.RoundID;
            int problemID=request.ProblemID;
            Hashtable sourceFiles = request.SourceFiles;
            Hashtable dllFiles = request.DllFiles;
            bool isDeleteSucceeded=DeleteFiles(userID,contestID,roundID,problemID,className,
                                               sourceFiles, dllFiles);
            string sourceFileName=GetSourceFileName(className,userID,contestID,roundID,
                                                    problemID,isDeleteSucceeded,requestID);
            CreateInputFiles(sourceFileName,programText,sourceFiles, dllFiles,  userID, contestID, roundID, problemID);
            string dllFileName=GetDllFileName(sourceFileName);
            string compilerArgs=GetCompilerArguments(dllFileName)+" "+sourceFileName;
            string dir=GetDir(userID,contestID,roundID,problemID);

            IDictionaryEnumerator enum1;
            enum1 = sourceFiles.GetEnumerator();
            while(enum1.MoveNext()) {
                compilerArgs += " " + dir + (string)enum1.Key; 
            }

            if(dllFiles.Count > 0) {
                compilerArgs += " /r:";
                enum1 = dllFiles.GetEnumerator();
                bool first = true;
                while(enum1.MoveNext()) {
                    if(!first) {
                        compilerArgs += " /r:";
                    } else {
                        first = false;

                    }
                    compilerArgs += dir + (string)enum1.Key;
                }
            } 

            Console.WriteLine("CompilerArgs=" + compilerArgs);
            string errors=RunProcess(GetCompilerExeName(),compilerArgs);
            errors=FilterCompilerOut(errors);
            byte[] dllBytes=null;
            byte[] pdbBytes=null;
            if (File.Exists(dllFileName)) {
                string errorMessage=CheckAssembly(dllFileName,className,request.ReturnType,
                                                  request.MethodName,request.ArgTypes);
                string pdbFileName=GetPdbFileName(sourceFileName);
                if (errorMessage.Length>0) {
                    errors=errorMessage;
                } else {
                    int start=Environment.TickCount;
                    string reason=securityCheck.Check(programText);
                    Log.WriteLine("requestID="+requestID+", security check took "+
                                  (Environment.TickCount-start)+"ms");
                    if (reason!=null) {
                        errors="error: "+reason+" is not allowed in the program text";
                    } else {
                        dllBytes=ReadBinaryFile(dllFileName);
                        pdbBytes=ReadBinaryFile(pdbFileName);
                    }
                }
            }
            return new CompileResponse(request.LanguageID, requestID,dllBytes,pdbBytes,errors);
        }

        string GetDllFileName(string sourceFileName) {
            return GetFileName(sourceFileName,"dll");
        }

        string GetPdbFileName(string sourceFileName) {
            return GetFileName(sourceFileName,"pdb");
        }

        string GetFileName(string sourceFileName, string ext) {
            int ind=sourceFileName.LastIndexOf('.');
            return sourceFileName.Substring(0,ind+1)+ext;
        }

        string GetDir(int userID, int contestID, int roundID, int problemID) {
            return DirectoryUtils.GetDir(GetLanguage(), userID,contestID,roundID,problemID);
        }

        bool DeleteFiles(int userID, int contestID, int roundID, int problemID, 
                         string className, Hashtable sourceFiles, Hashtable dllFiles) {
            string dir=GetDir(userID,contestID,roundID,problemID);
            string sourceFileName=GetSourceFileName(className,userID,contestID,roundID,
                                                    problemID,true,0);
            string dllFileName=GetDllFileName(sourceFileName);
            string pdbFileName=GetPdbFileName(sourceFileName);
            try {
                Console.WriteLine("Trying to delete: " + sourceFileName);
                DeleteFile(sourceFileName);
                Console.WriteLine("Trying to delete: " + dllFileName);
                DeleteFile(dllFileName);
                Console.WriteLine("Trying to delete: " + pdbFileName);
                DeleteFile(pdbFileName);
                IDictionaryEnumerator enum1 = sourceFiles.GetEnumerator();
                while(enum1.MoveNext()) {
                    Console.WriteLine("Trying to delete: " + dir + ((string)enum1.Key));
                    DeleteFile(dir + (string)enum1.Key);
                }

                enum1 = dllFiles.GetEnumerator();
                while(enum1.MoveNext()) {
                    Console.WriteLine("Trying to delete: " + dir + ((string)enum1.Key));
                    DeleteFile(dir + (string)enum1.Key);
                }
                return true;
            } catch (SystemException) {
            }
            return false;
        }

    }

}
