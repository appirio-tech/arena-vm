import java.io.FileInputStream;
import java.io.PrintWriter;


public class VerifyConfigurationGenerator {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        FileInputStream is = new FileInputStream(args[0]);
        PrintWriter pw = new PrintWriter("Verify.properties");
        
        int now = 0, prev = 0, pos = 0;
        pw.println("# This property file defines the template of a dynamically generated class byte code.");
        pw.println("# The class name should be com.topcoder.temporary.Verify. It should contain a static public method verify.");
        pw.println("# This method returns an integer, which is calculated from a dynamically generated formula. This number is sent back.");
        pw.println("# This class is sent to the client for verification. In case the returned integer is 0, the verification fails.");
        pw.println("# The formula is created as ((..(N1 op1 N2) op2 N3) op3 N4) op4 N5) op5 N6)....) op_n N_n+1)");
        pw.println();
        pw.println("# The compiled class template file in resource is given in 'class'.");
        pw.println("# You can use a Java compiler to compile a Java source code to create the class template.");
        pw.println("class=/" + args[0]);
        pw.println();
        pw.println("# The number of operations in the formula.");
        pw.println("count=5");
        pw.println();
        pw.println("# The offset where the numbers should be written to is given as 'number_n'.");
        pw.println("# The offset where the operation instructions should be written to is given as 'operator_n'.");
        pw.println("# The numbers should always be 1 more than the operators.");
        pw.println("# The offsets can be obtained by setting some specific values in the template source code (e.g. 0x1111, 0x2222, etc).");
        pw.println("# For numbers, the bytes at offset and offset+1 will be replaced by a signed short.");
        pw.println("# For operation instructions, only the byte at offset will be replaced by proper operation (iadd, isub, imul, idiv & irem).");
        
        do {
            prev = now;
            now = is.read();
            if (now == -1) {break;}
            int test = (prev << 8) | now;
            
            if (test == 0x2222) {
                pw.println("number_1=" + (pos - 1));
            }
            
            if (test == 0x3333) {
                pw.println("number_2=" + (pos - 1));
            }
            
            if (test == 0x4444) {
                pw.println("number_3=" + (pos - 1));
            }
            
            if (test == 0x5555) {
                pw.println("number_4=" + (pos - 1));
            }
            
            if (test == 0x6666) {
                pw.println("number_5=" + (pos - 1));
            }
            
            if (test == 0x7777) {
                pw.println("number_6=" + (pos - 1));
            }
            
            if (test == 0x1b60) {
                pw.println("operator_1=" + pos);
            }
            
            if (test == 0x1c64) {
                pw.println("operator_2=" + pos);
            }
            
            if (test == 0x1d68) {
                pw.println("operator_3=" + pos);
            }
            
            if (test == 0x6c15) {
                pw.println("operator_4=" + (pos - 1));
            }
            
            if (test == 0x70ac) {
                pw.println("operator_5=" + (pos - 1));
            }
            ++pos;
        } while (true);

	pw.close();
	is.close();
    }
}
