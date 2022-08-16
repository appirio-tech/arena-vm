package com.topcoder.utilities;

import java.util.*;

import com.topcoder.server.services.TestService;
import com.topcoder.services.util.*;

public class GetTests {

    public static void main(String as[]) {
        ArrayList testCases = TestService.retrieveTestCases(Integer.parseInt(as[0]));
//		System.out.println(testCases);
        ArrayList ids = (ArrayList) testCases.get(0);
        ArrayList argVals = (ArrayList) testCases.get(1);
        ArrayList expectedResults = (ArrayList) testCases.get(2);
        for (int i = 0; (i < ids.size()); i++) {
            ArrayList args = (ArrayList) argVals.get(i);
            for (int j = 0; j < args.size(); j++) {
                Object o = args.get(j);
                if (o instanceof int[]) {
                    int[] n = (int[]) o;
                    System.out.print("{");
                    for (int k = 0; k < n.length; k++) {
                        if (k == 0)
                            System.out.print(n[k]);
                        else
                            System.out.print("," + n[k]);
                    }
                    System.out.print("}");
                    System.out.println();
                } else if (o instanceof String[]) {
                    String[] n = (String[]) o;
                    System.out.print("{");
                    for (int k = 0; k < n.length; k++) {
                        if (k == 0)
                            System.out.print("\"" + n[k] + "\"");
                        else
                            System.out.print(",\"" + n[k] + "\"");
                    }
                    System.out.print("}");
                    System.out.println();
                } else if (o instanceof char[]) {
                    String[] n = (String[]) o;
                    for (int k = 0; k < n.length; k++) {
                        System.out.print("," + n[k]);
                    }
                    System.out.println();
                } else if (o instanceof String) {
                    System.out.println("\"" + o + "\"");
                } else {
                    System.out.println(o);
                }
            }
            Object expectedResult = expectedResults.get(i);
//			System.out.println("**");
//			System.out.println(expectedResult);
//			System.out.println("**********************");
        }
    }
}