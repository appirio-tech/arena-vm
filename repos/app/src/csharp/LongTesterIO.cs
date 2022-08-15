/*
 * Copyright (C) -2014 TopCoder Inc., All Rights Reserved.
 */

using System;
using System.IO;
using System.Text;
/**
 * I/O utility used for the communication during marathon testing of .NET solutions.
 *
 * <p>
 * Changes in version 1.1 (Module Assembly - Return Peak Memory Usage for Marathon Match - DotNet):
 * <ol>
 *      <li>Add {@link #PEAK_MEMORY_USED} constant for the peak memory data type.</li>
 *      <li>Add {@link #WritePeakMemoryUsed(BinaryWriter, long)} method to support writing
 *      of the peak memory data type.</li>
 * </ol>
 * </p>
 *
 * @author dexy
 * @version 1.1
 */
public class LongTesterIO{
    static Object resultObject;
    public const int INT_TYPE = 1;
    public const int LONG_TYPE = 2;
    public const int DOUBLE_TYPE = 3;
    public const int STRING_TYPE = 4;
    public const int VEC_INT_TYPE = 5;
    public const int VEC_LONG_TYPE = 6;
    public const int VEC_DOUBLE_TYPE = 7;
    public const int VEC_STRING_TYPE = 8;

    public const int EXCEPTION = 250;
    public const int TIMEOUT = 251;
    public const int ABORT = 252;
    public const int METHOD_START = 253;
    public const int TIME = 254;
    public const int TERMINATE = 255;
    
    /**
     * The peak memory data type.
     * @since 1.1
     */
    public const int PEAK_MEMORY_USED = 245;

    public static void WriteInt(BinaryWriter bw, int a) {
        bw.Write((byte)(a>>24));
        bw.Write((byte)(a>>16));
        bw.Write((byte)(a>>8));
        bw.Write((byte)(a&0xff));
    }
    public static void WriteLong(BinaryWriter bw, long a) {
        bw.Write((byte)(a>>56));
        bw.Write((byte)((a>>48)));
        bw.Write((byte)((a>>40)));
        bw.Write((byte)((a>>32)));
        bw.Write((byte)((a>>24)));
        bw.Write((byte)((a>>16)));
        bw.Write((byte)((a>>8)));
        bw.Write((byte)(a&0xff));
    }
    public static void WriteDouble(BinaryWriter bw, double a) {
        WriteLong(bw, BitConverter.DoubleToInt64Bits(a));
    }
    public static void WriteString(BinaryWriter bw, String a) {
        WriteInt(bw,a.Length);
System.Text.ASCIIEncoding enc = new System.Text.ASCIIEncoding();
byte[] b = enc.GetBytes(a);

        bw.Write(b);
    }

    public static void setTimeout(BinaryWriter bw, int timeout)  {
        bw.Write((byte)TIMEOUT);
        WriteInt(bw, timeout);
    }
    
    public static void startMethod(BinaryWriter bw, int method)  {
        bw.Write((byte)METHOD_START);
        WriteInt(bw, method);
    }
    
    //sends terminate command to close client while loop
    public static void terminate(BinaryWriter bw)  {
        bw.Write((byte)TERMINATE);
        Flush(bw);
        //socket.close();
    }
    
    public static void Flush(BinaryWriter bw)  {
        bw.Flush();
    }
    
    public static void WriteTime(BinaryWriter bw, int time)  {
        bw.Write((byte)TIME);
        WriteInt(bw,time);
    }

    /**
     * Writes the peak memory used (in KB) to the writer.
     *
     * @param bw binary writer where memory will be written
     * @param peakMemoryUsed the peak memory used (in KB).
     * @since 1.1
     *
     */
    public static void WritePeakMemoryUsed(BinaryWriter bw, long peakMemoryUsed) {
        bw.Write((byte) PEAK_MEMORY_USED);
        WriteLong(bw, peakMemoryUsed);
    }
    
    public static void WriteThrowable(BinaryWriter bw, Exception t)  {
        bw.Write((byte)EXCEPTION);
        WriteString(bw,t.Message+"\n"+t.StackTrace);
    }
    
    //for things like timeouts in plain text
    public static void WriteThrowable(BinaryWriter bw, String s)  {
        bw.Write((byte)EXCEPTION);
        WriteString(bw,s);
    }
    
    public static void WriteArg(BinaryWriter bw, double a) {
        bw.Write((byte)DOUBLE_TYPE);
        WriteDouble(bw,a);
    }
    public static void WriteArg(BinaryWriter bw, int a) {
        bw.Write((byte)INT_TYPE);
        WriteInt(bw,a);
    }
    public static void WriteArg(BinaryWriter bw, long a) {
        bw.Write((byte)LONG_TYPE);
        WriteLong(bw,a);
    }
    public static void WriteArg(BinaryWriter bw, String a) {
        bw.Write((byte)STRING_TYPE);
        WriteString(bw,a);
    }
    public static void WriteArg(BinaryWriter bw, int[] a) {
        bw.Write((byte)VEC_INT_TYPE);
        WriteInt(bw,a.Length);
        for(int i = 0; i<a.Length; i++){
            WriteInt(bw,a[i]);
        }
    }
    public static void WriteArg(BinaryWriter bw, long[] a) {
        bw.Write((byte)VEC_LONG_TYPE);
        WriteInt(bw,a.Length);
        for(int i = 0; i<a.Length; i++){
            WriteLong(bw,a[i]);
        }
    }
    public static void WriteArg(BinaryWriter bw, double[] a) {
        bw.Write((byte)VEC_DOUBLE_TYPE);
        WriteInt(bw,a.Length);
        for(int i = 0; i<a.Length; i++){
            WriteDouble(bw,a[i]);
        }
    }
    public static void WriteArg(BinaryWriter bw, String[] a) {
        bw.Write((byte)VEC_STRING_TYPE);
        WriteInt(bw,a.Length);
        for(int i = 0; i<a.Length; i++){
            WriteString(bw,a[i]);
        }
    }
    
    public static int GetInt2(BinaryReader br) {
        int r = br.ReadByte() << 24;
        r |= br.ReadByte()<<16;
        r |= br.ReadByte()<<8;
        r |= br.ReadByte();
        return r;
    }
    //XXX this is sucky, is string buffer faster, less mem?
static System.Text.ASCIIEncoding enc = new System.Text.ASCIIEncoding();
    public static String GetString2(BinaryReader br) {
        int Length = GetInt2(br);
byte[] b = br.ReadBytes(Length);
return enc.GetString(b);
    }
    public static long GetLong2(BinaryReader br) {
        long r = ((long)br.ReadByte()) << 56;
        r |= ((long)br.ReadByte())<<48;
        r |= ((long)br.ReadByte())<<40;
        r |= ((long)br.ReadByte())<<32;
        r |= ((long)br.ReadByte())<<24;
        r |= ((long)br.ReadByte())<<16;
        r |= ((long)br.ReadByte())<<8;
        r |= ((long)br.ReadByte());
        return r;
    }
    public static double GetDouble2(BinaryReader br) {
        return BitConverter.Int64BitsToDouble(GetLong2(br));
    }
    
    public static int GetInt(BinaryReader br) {
        if(br.ReadByte() == EXCEPTION){
            //exceptions.add(GetString2(br));
            return 0;
        }
        return GetInt2(br);
    }
    public static long GetLong(BinaryReader br) {
        if(br.ReadByte() == EXCEPTION){
            //exceptions.add(GetString2(br));
            return 0;
        }
        return GetLong2(br);
    }
    public static double GetDouble(BinaryReader br) {
        if(br.ReadByte() == EXCEPTION){
            //exceptions.add(GetString2(br));
            return 0;
        }
        return GetDouble2(br);
    }

    public static String GetString(BinaryReader br) {
        if(br.ReadByte() == EXCEPTION){
            //exceptions.add(GetString2(br));
            return null;
        }
        return GetString2(br);
    }
    public static int[] GetIntArray(BinaryReader br) {
        if(br.ReadByte() == EXCEPTION){
            //exceptions.add(GetString2(br));
            return null;
        }
        int N = GetInt2(br);
        int[] ret = new int[N];
        for(int i = 0; i<ret.Length; i++){
            ret[i] = GetInt2(br);
        }
        return ret;
    }
    public static long[] GetLongArray(BinaryReader br) {
        if(br.ReadByte() == EXCEPTION){
            //exceptions.add(GetString2(br));
            return null;
        }
        int N = GetInt2(br);
        long[] ret = new long[N];
        for(int i = 0; i<ret.Length; i++){
            ret[i] = GetLong2(br);
        }
        return ret;
    }
    public static double[] GetDoubleArray(BinaryReader br) {
        if(br.ReadByte() == EXCEPTION){
            //exceptions.add(GetString2(br));
            return null;
        }
        int N = GetInt2(br);
        double[] ret = new double[N];
        for(int i = 0; i<ret.Length; i++){
            ret[i] = GetDouble2(br);
        }
        return ret;
    }
    public static String[] GetStringArray(BinaryReader br) {
        if(br.ReadByte() == EXCEPTION){
            //exceptions.add(GetString2(br));
            return null;
        }
        int N = GetInt2(br);
        String[] ret = new String[N];
        for(int i = 0; i<ret.Length; i++){
            ret[i] = GetString2(br);
        }
        return ret;
    }
    public static bool IsNull(Object o) {
        return o == null;
    }
    public static bool IsNull(byte o) {
        return false;
    }
    public static bool IsNull(int o) {
        return false;
    }
    public static bool IsNull(long o) {
        return false;
    }
    public static bool IsNull(float o) {
        return false;
    }
    public static bool IsNull(double o) {
        return false;
    }
    public static bool IsNull(bool o) {
        return false;
    }
    public static bool IsNull(char o) {
        return false;
    }
    
}

