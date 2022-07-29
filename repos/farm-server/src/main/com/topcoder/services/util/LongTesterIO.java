/*
* Copyright (C) - 2014 TopCoder Inc., All Rights Reserved.
*/
package com.topcoder.services.util;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * <p>
 * Changes in version 1.1 (Return Peak Memory Usage for Marathon Match Java v1.0):
 * <ol>
 *      <li>Add {@link #PEAK_MEMORY_USED} field.</li>
 *      <li>Add {@link #writePeakMemoryUsed(BufferedOutputStream bw, long peakMemoryUsed)} method.</li>
 * </ol>
 * </p>
 * @author TCSASSEMBLER
 * @version 1.1
 *
 */
public class LongTesterIO{
    static Object resultObject;
    public static final int INT_TYPE = 1;
    public static final int LONG_TYPE = 2;
    public static final int DOUBLE_TYPE = 3;
    public static final int STRING_TYPE = 4;
    public static final int VEC_INT_TYPE = 5;
    public static final int VEC_LONG_TYPE = 6;
    public static final int VEC_DOUBLE_TYPE = 7;
    public static final int VEC_STRING_TYPE = 8;

    public static final int EXCEPTION = 250;
    public static final int TIMEOUT = 251;
    public static final int ABORT = 252;
    public static final int METHOD_START = 253;
    public static final int TIME = 254;
    public static final int TERMINATE = 255;

    /**
     * The Peak Memory Used Type.
     * @since 1.1
     */
    public static final int PEAK_MEMORY_USED = 245;
    
    public static void writeInt(BufferedOutputStream bw, int a) throws IOException{
        bw.write(a>>>24);
        bw.write((a>>>16)&0xff);
        bw.write((a>>>8)&0xff);
        bw.write(a&0xff);
    }
    public static void writeLong(BufferedOutputStream bw, long a) throws IOException{
        bw.write((int)(a>>>56));
        bw.write((int)((a>>>48)&0xff));
        bw.write((int)((a>>>40)&0xff));
        bw.write((int)((a>>>32)&0xff));
        bw.write((int)((a>>>24)&0xff));
        bw.write((int)((a>>>16)&0xff));
        bw.write((int)((a>>>8)&0xff));
        bw.write((int)(a&0xff));
    }
    public static void writeDouble(BufferedOutputStream bw, double a) throws IOException{
        writeLong(bw,Double.doubleToRawLongBits(a));//XXX is Raw faster?
    }
    public static void writeString(BufferedOutputStream bw, String a) throws IOException{
        writeInt(bw,a.length());
        bw.write(a.getBytes());
    }

    public static void setTimeout(BufferedOutputStream bw, int timeout) throws IOException {
        bw.write(TIMEOUT);
        writeInt(bw, timeout);
    }
    
    public static void startMethod(BufferedOutputStream bw, int method) throws IOException {
        bw.write(METHOD_START);
        writeInt(bw, method);
    }
    
    //sends terminate command to close client while loop
    public static void terminate(BufferedOutputStream bw) throws IOException {
        bw.write(TERMINATE);
        flush(bw);
        //socket.close();
    }
    
    public static void shutdownSocket() throws IOException {
        safeClose(client);
        safeClose(readClient);
        safeClose(socket);
        safeClose(readSocket);
    }

    private static void safeClose(Socket s) throws IOException {
        if (s != null) {
            s.close();
        }
    }
    
    private static void safeClose(ServerSocket s) throws IOException {
        if (s != null) {
            s.close();
        }
    }
    public static void flush(BufferedOutputStream bw) throws IOException {
        bw.flush();
    }
    
    public static void writeTime(BufferedOutputStream bw, int time) throws IOException {
        bw.write(TIME);
        writeInt(bw,time);
    }
    /**
     * Write peak memory used.
     * @param bw the output stream.
     * @param peakMemoryUsed the peak memory used value.
     * @throws IOException if any io error occur.
     * @since 1.1
     */
    public static void writePeakMemoryUsed(BufferedOutputStream bw, long peakMemoryUsed) throws IOException {
        bw.write(PEAK_MEMORY_USED);
        writeLong(bw,peakMemoryUsed);
    }
    
    public static void writeThrowable(BufferedOutputStream bw, Throwable t) throws IOException {
        bw.write(EXCEPTION);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        t.printStackTrace(ps);
        String s = new String(baos.toByteArray());
        System.err.println("Writing exception " + s);
        //System.err.println("java.home=" + System.getProperty("java.home"));
        writeString(bw,s);
    }
    
    //for things like timeouts in plain text
    public static void writeThrowable(BufferedOutputStream bw, String s) throws IOException {
        bw.write(EXCEPTION);
        writeString(bw,s);
    }
    
    public static void writeArg(BufferedOutputStream bw, double a) throws IOException{
        bw.write(DOUBLE_TYPE);
        writeDouble(bw,a);
    }
    public static void writeArg(BufferedOutputStream bw, int a) throws IOException{
        bw.write(INT_TYPE);
        writeInt(bw,a);
    }
    public static void writeArg(BufferedOutputStream bw, long a) throws IOException{
        bw.write(LONG_TYPE);
        writeLong(bw,a);
    }
    public static void writeArg(BufferedOutputStream bw, String a) throws IOException{
        bw.write(STRING_TYPE);
        writeString(bw,a);
    }
    public static void writeArg(BufferedOutputStream bw, int[] a) throws IOException{
        bw.write(VEC_INT_TYPE);
        writeInt(bw,a.length);
        for(int i = 0; i<a.length; i++){
            writeInt(bw,a[i]);
        }
    }
    public static void writeArg(BufferedOutputStream bw, long[] a) throws IOException{
        bw.write(VEC_LONG_TYPE);
        writeInt(bw,a.length);
        for(int i = 0; i<a.length; i++){
            writeLong(bw,a[i]);
        }
    }
    public static void writeArg(BufferedOutputStream bw, double[] a) throws IOException{
        bw.write(VEC_DOUBLE_TYPE);
        writeInt(bw,a.length);
        for(int i = 0; i<a.length; i++){
            writeDouble(bw,a[i]);
        }
    }
    public static void writeArg(BufferedOutputStream bw, String[] a) throws IOException{
        bw.write(VEC_STRING_TYPE);
        writeInt(bw,a.length);
        for(int i = 0; i<a.length; i++){
            writeString(bw,a[i]);
        }
    }
    
    public static void initialize(int port) throws IOException {
        exceptions = new ArrayList();
        resultObject = null;
        time = 0;
        
        //bind the socket
        socket = new ServerSocket(port,10, InetAddress.getByName(null));
        socket.setSoTimeout(30000);
        
        readSocket = new ServerSocket(port+1,10, InetAddress.getByName(null));
        readSocket.setSoTimeout(30000);
    }
    
    static Socket client;
    static Socket readClient;
    
    public static Socket getSocket() throws IOException {
        client = socket.accept();
        client.setTcpNoDelay(true);
        return client;
    }
    
    public static Socket getReadSocket() throws IOException {
        readClient = readSocket.accept();
        readClient.setTcpNoDelay(true);
        return readClient;
    }
    
    private static ServerSocket socket = null;
    
    private static ServerSocket readSocket = null;
    
    private static List exceptions = new ArrayList();
    private static long time = 0;
    
    public static List getExceptions() {
        return exceptions;
    }
    
    public static void setTime(long t) {
        time = t;
    }
    
    public static void addTime(long t) {
        time += t;
    }
    
    public static void addFatalError(String s) {
        exceptions.add(s);
    }
    
    public static void setResultObject(Object o) {
        resultObject = o;
    }
    
    public static Object getResultObject() {
        return resultObject;
    }
    
    public static long getTime() {
        return time;
    }
    public static int getInt2(BufferedInputStream br) throws IOException{
        int r = br.read() << 24;
        r |= br.read()<<16;
        r |= br.read()<<8;
        r |= br.read();
        return r;
    }
    //XXX this is sucky, is string buffer faster, less mem?
    public static String getString2(BufferedInputStream br) throws IOException{
        int length = getInt2(br);
        byte[] b = new byte[length];
        for(int i = 0; i<length; i++){
            b[i] = (byte)br.read();
        }
        return new String(b);
    }
    public static long getLong2(BufferedInputStream br) throws IOException{
        long r = ((long)br.read()) << 56;
        r |= ((long)br.read())<<48;
        r |= ((long)br.read())<<40;
        r |= ((long)br.read())<<32;
        r |= ((long)br.read())<<24;
        r |= ((long)br.read())<<16;
        r |= ((long)br.read())<<8;
        r |= ((long)br.read());
        return r;
    }
    public static double getDouble2(BufferedInputStream br) throws IOException{
        return Double.longBitsToDouble(getLong2(br));
    }
    
    public static int getInt(BufferedInputStream br) throws IOException{
        if(br.read() == EXCEPTION){
            exceptions.add(getString2(br));
            return 0;
        }
        return getInt2(br);
    }
    public static long getLong(BufferedInputStream br) throws IOException{
        if(br.read() == EXCEPTION){
            exceptions.add(getString2(br));
            return 0;
        }
        return getLong2(br);
    }
    public static double getDouble(BufferedInputStream br) throws IOException{
        if(br.read() == EXCEPTION){
            exceptions.add(getString2(br));
            return 0;
        }
        return getDouble2(br);
    }

    public static String getString(BufferedInputStream br) throws IOException{
        if(br.read() == EXCEPTION){
            exceptions.add(getString2(br));
            return null;
        }
        return getString2(br);
    }
    public static int[] getIntArray(BufferedInputStream br) throws IOException{
        if(br.read() == EXCEPTION){
            exceptions.add(getString2(br));
            return null;
        }
        int N = getInt2(br);
        int[] ret = new int[N];
        for(int i = 0; i<ret.length; i++){
            ret[i] = getInt2(br);
        }
        return ret;
    }
    public static long[] getLongArray(BufferedInputStream br) throws IOException{
        if(br.read() == EXCEPTION){
            exceptions.add(getString2(br));
            return null;
        }
        int N = getInt2(br);
        long[] ret = new long[N];
        for(int i = 0; i<ret.length; i++){
            ret[i] = getLong2(br);
        }
        return ret;
    }
    public static double[] getDoubleArray(BufferedInputStream br) throws IOException{
        if(br.read() == EXCEPTION){
            exceptions.add(getString2(br));
            return null;
        }
        int N = getInt2(br);
        double[] ret = new double[N];
        for(int i = 0; i<ret.length; i++){
            ret[i] = getDouble2(br);
        }
        return ret;
    }
    public static String[] getStringArray(BufferedInputStream br) throws IOException{
        if(br.read() == EXCEPTION){
            exceptions.add(getString2(br));
            return null;
        }
        int N = getInt2(br);
        String[] ret = new String[N];
        for(int i = 0; i<ret.length; i++){
            ret[i] = getString2(br);
        }
        return ret;
    }
    
    public static boolean isNull(Object o) {
        return o == null;
    }
    public static boolean isNull(byte o) {
        return false;
    }
    public static boolean isNull(int o) {
        return false;
    }
    public static boolean isNull(long o) {
        return false;
    }
    public static boolean isNull(float o) {
        return false;
    }
    public static boolean isNull(double o) {
        return false;
    }
    public static boolean isNull(boolean o) {
        return false;
    }
    public static boolean isNull(char o) {
        return false;
    }
    public static void main(String[] args) throws Exception{
        initialize(8000);
        Socket s = getSocket();
        BufferedOutputStream bos = new BufferedOutputStream(s.getOutputStream());
        BufferedInputStream bis = new BufferedInputStream(s.getInputStream());
        System.out.println("got streams");
        startMethod(bos,1);
        bos.flush();
        System.out.println("wrote method");
        int[] wtf = new int[1000];
        wtf[0] = 0x0A0A0A0A;
        writeArg(bos, wtf);
        //bos.write(255);
        bos.flush();
        System.out.println("wrote int[]");
        System.out.println(getInt(bis));
    }
}
