namespace TopCoder.Server.Serialization {

    using NUnit.Framework;

    public sealed class ObjectConverterTest: TestCase {

        public ObjectConverterTest(string name): base(name) {
        }

        void Test(object obj) {
            byte[] byteArray = ObjectConverter.Serialize(new object[] {obj});
            object[] objects = (object[]) ObjectConverter.Deserialize(byteArray);
            AssertEquals(1, objects.Length);
            AssertEquals(obj, objects[0]);
        }

        public void TestForthAndBack() {
            Test("a");
            Test("");
            Test(null);
            Test(2);
            Test('a');
            Test("a ");
            Test(" ");
            Test(' ');
            Test(" a");
            Test("" + ((char) 160));
            Test((char) 160);
            Test("[" + ((char) 160) + "]");
            Test("" + ((char) 0x41) + ((char) 0xC3) + ((char) 0xBF));
            Test("" + ((char) 0x41C3));
        }

        public void TestEmpty() {
            byte[] byteArray = new byte[0];
            object[] objects = (object[]) ObjectConverter.Deserialize(byteArray);
            AssertNull(objects);
        }

    }

}
