import java.nio.file.*;

public class TestProblem {
   public int sum(int a, int b) {
     // String.lines() is new in JDK 11
     String str = "line1 \n line2 \n line3";
     System.out.println("Lines count = " + str.lines().count());
     try {
       Files.exists(Paths.get("/etc/passwd"));
       System.out.println("Open /etc/passwd is allowed");
     } catch (Exception e) {
       System.out.println("Open /etc/passwd is dis-allowed: " + e.getMessage());
     }
     return a + b;
   }
}