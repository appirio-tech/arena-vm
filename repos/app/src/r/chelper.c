/*
 * Copyright (C) 2013 TopCoder Inc., All Rights Reserved.
 */
#include "stdio.h"
#include "stdlib.h"

/**
 * the default file input handler read from sock dup.
 */
FILE *in = 0;
/**
 * the default file out handler write to sock dup.
 */
FILE *out = 0;

/**
 * get the file output handle with the specific file description.
 * @param handle the file description.
 */
FILE* getOut(int *handle) {
    if(!out)
        out = fdopen(*handle,"a");
    return out;
}
/**
 * get the file intput handle with the specific file description.
 * @param handle the file description.
 */
FILE* getIn(int *handle) {
    if(!in)
        in = fdopen(*handle,"r");
    return in;
}
/**
 * flush the file output handle with the specific file description.
 * @param handle the file description.
 */
void flush(int* handle) {
    fflush(getOut(handle));
}
/**
 * write to server the single int.
 * @param handle the file description.
 */
void writeSingleInt(int *handle,int* value){
    FILE* bw = getOut(handle);
    putc(*value, bw);
}

/**
 * write to server the int value.
 * @param handle the file description.
 * @param a the int value.
 */
void writeInt(int *handle, int *a){
    char * ch = (char*)a;
    FILE* bw = getOut(handle);
    putc(ch[3], bw);
    putc(ch[2], bw);
    putc(ch[1], bw);
    putc(ch[0], bw);
}
/**
 * write to server the long value.
 * @param handle the file description.
 * @param a the long value.
 */
void writeLongLong(int *handle, long long *a){
    char * ch = (char*)a;
    FILE* bw = getOut(handle);
    putc(ch[7], bw);
    putc(ch[6], bw);
    putc(ch[5], bw);
    putc(ch[4], bw);
    putc(ch[3], bw);
    putc(ch[2], bw);
    putc(ch[1], bw);
    putc(ch[0], bw);
}
/**
 * write to server the double value.
 * @param handle the file description.
 * @param a the double value.
 */
void writeDouble(int *handle, double *a){
    char * ch = (char*)a;
    FILE* bw = getOut(handle);
    putc(ch[7], bw);
    putc(ch[6], bw);
    putc(ch[5], bw);
    putc(ch[4], bw);
    putc(ch[3], bw);
    putc(ch[2], bw);
    putc(ch[1], bw);
    putc(ch[0], bw);
}

/**
 * get the int value from server.
 * @param handle the file description.
 * @param result the result of int value.
 */
void getInt2(int* handle, int* result) {
    int v;
    char * c = (char*)&v;
    FILE* br = getIn(handle);
    c[3] = getc(br);
    c[2] = getc(br);
    c[1] = getc(br);
    c[0] = getc(br);
    *result = v;
}
/**
 * get the long value from server.
 * @param handle the file description.
 * @param result the result of long value.
 */
void getLongLong2(int* handle, long long* result) {
    long long v;
    char * c = (char*)&v;
    FILE* br = getIn(handle);
    c[7] = getc(br);
    c[6] = getc(br);
    c[5] = getc(br);
    c[4] = getc(br);
    c[3] = getc(br);
    c[2] = getc(br);
    c[1] = getc(br);
    c[0] = getc(br);
    *result = v;
}

/**
 * get the double value from server.
 * @param handle the file description.
 * @param result the result of double value.
 */
void getDouble2(int* handle, double* result) {
    double v;
    char * c = (char*)&v;
    FILE* br = getIn(handle);
    c[7] = getc(br);
    c[6] = getc(br);
    c[5] = getc(br);
    c[4] = getc(br);
    c[3] = getc(br);
    c[2] = getc(br);
    c[1] = getc(br);
    c[0] = getc(br);
    *result = v;
}
/**
 * get the int value from server,typically this is just a char.
 * @param handle the file description.
 * @param result the result of int value.
 */
void getSingleInt(int* handle, int* result) {
    FILE* br = getIn(handle);
    *result = getc(br);
}
