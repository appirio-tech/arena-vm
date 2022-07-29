namespace TopCoder.Server.Compiler {

    using NUnit.Framework;

    public sealed class CSSecurityDataTest: TestCase {

        public CSSecurityDataTest(string name): base(name) {
        }

        public void TestContains() {
            AssertNull(CSSecurityData.Contains("Type", "iTypes"));
        }

        public void TestIsIdentifierChar() {
            Assert(CSSecurityData.IsIdentifierChar('i'));
        }

        public void TestContainsLeft() {
            AssertNotNull(CSSecurityData.Contains("Type", "Type"));
        }

        public void TestContainsRight() {
            AssertNull(CSSecurityData.Contains("Type", "Types"));
        }

        public void TestContainsDouble() {
            AssertNotNull(CSSecurityData.Contains("Type", "iTypes Type"));
        }

        public void TestUsing() {
            AssertNotNull(CSSecurityData.Contains("System.Diagnostics", "using System.Diagnostics;"));
        }

    }

}
