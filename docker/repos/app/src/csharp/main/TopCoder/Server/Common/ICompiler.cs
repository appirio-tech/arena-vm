namespace TopCoder.Server.Common {

    interface ICompiler {

        CompileResponse ProcessCompileRequest(CompileRequest request);

    }

}
