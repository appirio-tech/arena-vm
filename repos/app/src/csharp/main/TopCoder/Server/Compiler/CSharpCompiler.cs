namespace TopCoder.Server.Compiler {

    using TopCoder.Server.Common;

    sealed class CSharpCompiler: BaseCompiler {

        override protected string GetExt() {
            return "cs";
        }

        override protected string GetCompilerExeName() {
            return "csc";
        }

        override protected string GetCompilerArguments(string dllFileName) {
            return "/nologo /target:library /debug /optimize /out:"+dllFileName;
        }

        override protected Language GetLanguage() {
            return Language.CSHARP;
        }

    }

}
