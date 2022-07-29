namespace TopCoder.Server.Compiler {

    using System;
    using System.Reflection;

    sealed class AssemblyChecker {

        AssemblyChecker() {
        }

        public static void Main(string[] argv) {
            string dllFileName=argv[0];
            Assembly assembly=Assembly.LoadFrom(dllFileName);
            string className=argv[1];
            Type type=assembly.GetType(className);
            if (type==null || type.IsNotPublic) {
                Console.Write("error: Cannot find public class "+className);
                return;
            }
            ConstructorInfo constructor=type.GetConstructor(Type.EmptyTypes);
            if (constructor==null) {
                Console.Write("error: Cannot find the public no-arg constructor");
                return;
            }
            string methodName=argv[3];
            Type[] argTypes=new Type[argv.Length-4];
            for (int i=0; i<argTypes.Length; i++) {
                argTypes[i]=Type.GetType(argv[i+4]);
            }
            MethodInfo method=type.GetMethod(methodName,argTypes);
            if (method==null || !method.IsPublic) {
                Console.Write("error: Cannot find the required method");
                return;
            }
            Type returnType=Type.GetType(argv[2]);
            if (!method.ReturnType.Equals(returnType)) {
                Console.Write("error: Wrong return type");
                return;
            }
        }

    }

}
