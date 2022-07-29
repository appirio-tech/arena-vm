namespace TopCoder.Server.Compiler {

    using NUnit.Framework;

    public sealed class CSSecurityCheckTest: TestCase {

        public CSSecurityCheckTest(string name): base(name) {
        }

        public void TestConvertUnicode() {
            AssertEquals("s",CSSecurityCheck.ConvertUnicode("s"));
            AssertEquals("S",CSSecurityCheck.ConvertUnicode(@"\u0053"));
            AssertEquals("System",CSSecurityCheck.ConvertUnicode(@"\u0053ystem"));
            AssertEquals("using System",CSSecurityCheck.ConvertUnicode(@"using \u0053ystem"));
        }

        public void TestRemoveWhiteSpace() {
            AssertEquals("a b",CSSecurityCheck.RemoveWhitespace("a b"));
            AssertEquals("a.b",CSSecurityCheck.RemoveWhitespace("a. b"));
            AssertEquals("a.b",CSSecurityCheck.RemoveWhitespace("a .b"));
            AssertEquals("a.b",CSSecurityCheck.RemoveWhitespace("a . b"));
        }

    }

}
