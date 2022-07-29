namespace TopCoder.Server.Compiler {

    using System;
    using System.Globalization;
    using System.Text;

    sealed class CSSecurityCheck {

        CSSecurityData securityData=new CSSecurityData();
        CSSecurityDataWithThreads threadSecurityData = new CSSecurityDataWithThreads();

        internal CSSecurityCheck() {
        }

        internal static string RemoveWhitespace(string programText) {
            StringBuilder builder=new StringBuilder();
            bool whiteSpace = false;
            bool dot = false;
            for (int i=0; i<programText.Length; i++) {
                char ch=programText[i];
                if (Char.IsWhiteSpace(ch)) {
                    whiteSpace = true;
                } else if (ch == '.') {
                    dot = true;
                    builder.Append(ch);
                    whiteSpace = false;
                } else {
                    if (whiteSpace && !dot) {
                        builder.Append(' ');
                    }
                    builder.Append(ch);
                    whiteSpace = false;
                    dot = false;
                }
            }
            return builder.ToString();
        }

        static string RemoveComments(string programText) {
            StringBuilder builder=new StringBuilder();
            int state=0;
            for (int i=0; i<programText.Length; i++) {
                char ch=programText[i];
                switch (state) {
                case 0:
                    switch (ch) {
                    case '/':
                        state=1;
                        break;
                    case '"':
                        state=3;
                        break;
                    case '@':
                        state=5;
                        break;
                    default:
                        builder.Append(ch);
                        break;
                    }
                    break;
                case 1:
                    switch (ch) {
                    case '/':
                        state=2;
                        break;
                    case '*':
                        state=7;
                        break;
                    default:
                        builder.Append('/');
                        builder.Append(ch);
                        state=0;
                        break;
                    }
                    break;
                case 2:
                    switch (ch) {
                    case '\u000D':
                    case '\u000A':
                    case '\u2028':
                    case '\u2029':
                        builder.Append(ch);
                        state=0;
                        break;
                    }
                    break;
                case 3:
                    switch (ch) {
                    case '"':
                        state=0;
                        break;
                    case '\\':
                        state=4;
                        break;
                    }
                    break;
                case 4:
                    state=3;
                    break;
                case 5:
                    switch (ch) {
                    case '"':
                        state=6;
                        break;
                    default:
                        builder.Append('@');
                        builder.Append(ch);
                        state=0;
                        break;
                    }
                    break;
                case 6:
                    switch (ch) {
                    case '"':
                        int k=i+1;
                        if (k<programText.Length && programText[k]=='"') {
                            i++;
                        } else {
                            state=0;
                        }
                        break;
                    }
                    break;
                case 7:
                    switch (ch) {
                    case '*':
                        state=8;
                        break;
                    }
                    break;
                case 8:
                    switch (ch) {
                    case '/':
                        state=0;
                        break;
                    default:
                        state=7;
                        break;
                    }
                    break;
                }

            }
            return builder.ToString();
        }

        internal static string ConvertUnicode(string s) {
            int ind=0;
            for (;;) {
                ind=s.IndexOf(@"\u00",ind);
                if (ind<0) {
                    break;
                }
                char ch1=s[ind+4];
                char ch2=s[ind+5];
                if ('0'<=ch1 && ch1<='7' && Uri.IsHexDigit(ch2)) {
                    s=s.Remove(ind,6);
                    s=s.Insert(ind,""+(char) int.Parse(""+ch1+ch2,NumberStyles.HexNumber));
                }
                ind++;
            }
            return s;
        }

        static bool useThreading = false;

        internal string Check(string programText) {
            programText=ConvertUnicode(programText);
            programText=RemoveComments(programText);
            programText=RemoveWhitespace(programText);
            if(useThreading)
                return threadSecurityData.Check(programText);
            return securityData.Check(programText);
        }
        public static void Main(){
            string t = Console.In.ReadLine();
            if(t == "true")
                useThreading = true;

            string s = Console.In.ReadToEnd();
            Console.WriteLine(new CSSecurityCheck().Check(s));
        }
    }

}
