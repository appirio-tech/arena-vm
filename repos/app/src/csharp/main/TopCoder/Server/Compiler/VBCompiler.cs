namespace TopCoder.Server.Compiler {

    using System;

    using TopCoder.Server.Common;

    sealed class VBCompiler: BaseCompiler {

        override protected string GetExt() {
            return "vb";
        }

        override protected string GetCompilerExeName() {
            return "vbc";
        }

        override protected string GetCompilerArguments(string dllFileName) {
            return "/nologo /t:library /debug  /optimize /out:"+dllFileName;
        }

        override protected Language GetLanguage() {
            return Language.VB;
        }

    }

}
