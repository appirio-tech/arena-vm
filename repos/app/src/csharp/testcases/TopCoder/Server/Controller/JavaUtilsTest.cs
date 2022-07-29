namespace TopCoder.Server.Controller {

    using NUnit.Framework;

    public sealed class JavaUtilsTest: TestCase {

        public JavaUtilsTest(string name): base(name) {
        }

        void TestConvertInt32(int a, int b) {
            AssertEquals(a,JavaUtils.ConvertInt32(b));
            AssertEquals(b,JavaUtils.ConvertInt32(a));
        }

        public void TestConvertInt32() {
            TestConvertInt32(0,0);
            TestConvertInt32(1,0x01000000);
            TestConvertInt32(0x01020304,0x04030201);
            TestConvertInt32(-1,-1);
        }

    }

}
