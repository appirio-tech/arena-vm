using System;
using System.Globalization;
using System.Numerics;

public struct HexValue
{
   public int Sign;
   public string Value;
}

public class TestProblem
{
    public int sum(int a, int b) {
        uint positiveNumber = 4039543321;
        int negativeNumber = -255423975;

        // Convert the numbers to hex strings.
        HexValue hexValue1, hexValue2;
        hexValue1.Value = positiveNumber.ToString("X");
        hexValue1.Sign = Math.Sign(positiveNumber);

        hexValue2.Value = Convert.ToString(negativeNumber, 16);
        hexValue2.Sign = Math.Sign(negativeNumber);

        // Round-trip the hexadecimal values to BigInteger values.
        string hexString;
        BigInteger positiveBigInt, negativeBigInt;

        hexString = (hexValue1.Sign == 1 ? "0" : "") + hexValue1.Value;
        positiveBigInt = BigInteger.Parse(hexString, NumberStyles.HexNumber);
        Console.WriteLine("Converted {0} to {1} and back to {2}.",
                          positiveNumber, hexValue1.Value, positiveBigInt);

        hexString = (hexValue2.Sign == 1 ? "0" : "") + hexValue2.Value;
        negativeBigInt = BigInteger.Parse(hexString, NumberStyles.HexNumber);
        Console.WriteLine("Converted {0} to {1} and back to {2}.",
                          negativeNumber, hexValue2.Value, negativeBigInt);

        return a + b;
    }
}