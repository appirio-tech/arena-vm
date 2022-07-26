/*
* Copyright (C) - 2014 TopCoder Inc., All Rights Reserved.
*/

/**
 * <p>
 * Changes in version 1.1 (Return Peak Memory Usage for Marathon Match Cpp v1.0):
 * <ol>
 *      <li>Add {@link #PEAK_MEMORY_USED} field.</li>
 *      <li>Add {@link #writePeakMemoryUsed(FILE * bw, int peakMemoryUsed)} method.</li>
 *      <li>Add {@link #forward(FILE * in, FILE * out)} method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (Module Assembly - Return Peak Memory Usage for Marathon Match - DotNet):
 * <ol>
 *      <li>Update {@link #writePeakMemoryUsed(FILE *, long long)} method to write
 *      long long value of the peak memory used.</li>
 *      <li>Update {@link #forward(FILE *, FILE *)} method changing forward value for
 *      the peak memory to 8 (it is long long now), instead 4.</li>
 * </ol>
 * </p>
 *
 * @author TCSASSEMBLER, dexy
 * @version 1.2
 */
#include <cstdio>
#include <string>
#include <vector>
#include <stdlib.h>
using namespace std;

static const int INT_TYPE = 1;
static const int LONG_TYPE = 2;
static const int DOUBLE_TYPE = 3;
static const int STRING_TYPE = 4;
static const int VEC_INT_TYPE = 5;
static const int VEC_LONG_TYPE = 6;
static const int VEC_DOUBLE_TYPE = 7;
static const int VEC_STRING_TYPE = 8;

static const int EXCEPTION = 250;
static const int TIMEOUT = 251;
static const int ABORT = 252;
static const int METHOD_START = 253;
static const int TIME = 254;
static const int TERMINATE = 255;
/**
 * Add the peak memory data type.
 * @since 1.1
 */
static const int PEAK_MEMORY_USED = 245;

void flush(FILE* bw) {
    fflush(bw);
}
//assumes little endianness
//three primitive helper functions
//don't write type
inline void writeInt(FILE * bw, int a){
    char * ch = (char*)&a;
    putc(ch[3], bw);
    putc(ch[2], bw);
    putc(ch[1], bw);
    putc(ch[0], bw);
}
inline void writeLongLong(FILE * bw, long long a){
    char * ch = (char*)&a;
    putc(ch[7], bw);
    putc(ch[6], bw);
    putc(ch[5], bw);
    putc(ch[4], bw);
    putc(ch[3], bw);
    putc(ch[2], bw);
    putc(ch[1], bw);
    putc(ch[0], bw);
}
inline void writeDouble(FILE * bw, double a){
    char * ch = (char*)&a;
    putc(ch[7], bw);
    putc(ch[6], bw);
    putc(ch[5], bw);
    putc(ch[4], bw);
    putc(ch[3], bw);
    putc(ch[2], bw);
    putc(ch[1], bw);
    putc(ch[0], bw);
}

inline void writeString(FILE* bw, string* s){
    writeInt(bw,s->size());
    for(string::iterator it = s->begin(); it!=s->end(); it++){
        putc(*it,bw);
    }
}

//meta
void writeMethod(FILE* bw, int a) {
    putc(METHOD_START,bw);
    writeInt(bw,a);
}

void writeTime(FILE* bw, int time) {
    putc(TIME,bw);
    writeInt(bw,time);
}

/**
 * Writes the peak memory used value to app.
 * @param bw the file handle.
 * @param peakMemoryUsed the value of the peak memory used (in KB), -1 if not available.
 * @since 1.1
 */
void writePeakMemoryUsed(FILE * bw, long long peakMemoryUsed) {
    putc(PEAK_MEMORY_USED, bw);
    writeLongLong(bw, peakMemoryUsed);
}

void writeException(FILE* bw, string *a) {
    putc(EXCEPTION,bw);
    writeString(bw,a);
}

//writeArg functions now
inline void writeArg(FILE* bw, double a) {
    putc(DOUBLE_TYPE, bw);
    writeDouble(bw,a);
}
inline void writeArg(FILE* bw, long long a) {
    putc(LONG_TYPE, bw);
    writeLongLong(bw,a);
}
inline void writeArg(FILE* bw, int a) {
    putc(DOUBLE_TYPE, bw);
    writeInt(bw,a);
}

void writeArg(FILE* bw, string *a) {
    putc(STRING_TYPE,bw);
    writeString(bw,a);
}
void writeArg(FILE* bw, vector<int> *a) {
    putc(VEC_INT_TYPE,bw);
    writeInt(bw,a->size());
    for(vector<int>::iterator it = a->begin(); it!=a->end(); it++){
        writeInt(bw,*it);
    }
}
void writeArg(FILE* bw, vector<long long> *a) {
    putc(VEC_INT_TYPE,bw);
    writeInt(bw,a->size());
    for(vector<long long>::iterator it = a->begin(); it!=a->end(); it++){
        writeLongLong(bw,*it);
    }
}
void writeArg(FILE* bw, vector<double> *a) {
    putc(VEC_INT_TYPE,bw);
    writeInt(bw,a->size());
    for(vector<double>::iterator it = a->begin(); it!=a->end(); it++){
        writeDouble(bw,*it);
    }
}
void writeArg(FILE* bw, vector<string> *a) {
    putc(VEC_INT_TYPE,bw);
    writeInt(bw,a->size());
    for(vector<string>::iterator it = a->begin(); it!=a->end(); it++){
        writeString(bw,&(*it));
    }
}
//helper functions
inline int getInt2(FILE* br) {
    int v;
    char * c = (char*)&v;
    c[3] = getc(br);
    c[2] = getc(br);
    c[1] = getc(br);
    c[0] = getc(br);
    return v;
}
inline long long getLongLong2(FILE* br) {
    long long v;
    char * c = (char*)&v;
    c[7] = getc(br);
    c[6] = getc(br);
    c[5] = getc(br);
    c[4] = getc(br);
    c[3] = getc(br);
    c[2] = getc(br);
    c[1] = getc(br);
    c[0] = getc(br);
    return v;
}
inline double getDouble2(FILE* br) {
    double v;
    char * c = (char*)&v;
    c[7] = getc(br);
    c[6] = getc(br);
    c[5] = getc(br);
    c[4] = getc(br);
    c[3] = getc(br);
    c[2] = getc(br);
    c[1] = getc(br);
    c[0] = getc(br);
    return v;
}
inline string getString2(FILE* br) {
    int size = getInt2(br);
    string ret;
    ret.reserve(size);
    for(int i = 0; i<size; i++){
        ret.push_back(getc(br));
    }
//printf("%d %d %s\n",size,ret.size(),ret.c_str());
    return ret;
}
int getInt(FILE* br) {
    getc(br);//consume type
    return getInt2(br);
}
long long getLongLong(FILE* br) {
    getc(br);//consume type
    return getLongLong2(br);
}
double getDouble(FILE* br) {
//printf("HERE\n");fflush(stdout);
    getc(br);//consume type
double d = getDouble2(br);
//printf("%lf\n",d);fflush(stdout);
    return d;
}
string getString(FILE* f){
    getc(f);//consume type
    return getString2(f);
}
vector<long long> getLongLongArray(FILE* br) {
    getc(br);//consume type
    int size = getInt2(br);
    vector<long long> ret(size);
    for(int i = 0; i<size; i++)
        ret[i] = getLongLong2(br);
    return ret;
}
vector<double> getDoubleArray(FILE* br) {
    getc(br);//consume type
    int size = getInt2(br);
    vector<double> ret(size);
    for(int i = 0; i<size; i++)
        ret[i] = getDouble2(br);
    return ret;
}
vector<string> getStringArray(FILE* br) {
    getc(br);//consume type
    int size = getInt2(br);
    vector<string> ret(size);
    for(int i = 0; i<size; i++)
        ret[i] = getString2(br);
    return ret;
}
const int BUF_SIZE = (1<<20);
char buf[BUF_SIZE];
void forward(FILE * in, FILE * out, int n){
    int i;
    for(i = 0; i<(n>>20); i++){
        fread(buf,1,(1<<20),in);
        fwrite(buf,1,(1<<20),out);
    }
    if(n&0xfffff){
        fread(buf,1,n&0xfffff,in);
        fwrite(buf,1,n&0xfffff,out);
    }
}
/*
inline int getInt2(FILE* br) {
    int v;
    char * c = (char*)&v;
    c[3] = getc(br);
    c[2] = getc(br);
    c[1] = getc(br);
    c[0] = getc(br);
    return v;
}
*/
vector<int> getIntArray(FILE* br) {
    getc(br);//consume type
    int size = getInt2(br);
    //printf("size = %d\n",size);

    vector<int> ret(size);
    int pos = 0;
    while (size >= (1<<18)) {
       fread(buf,1,(1<<20),br);
       int buf_pos = 0;
       while (buf_pos < (1<<20)) {
          int v;
          char * c = (char*)&v;
          c[3] = buf[buf_pos++];
          c[2] = buf[buf_pos++];
          c[1] = buf[buf_pos++];
          c[0] = buf[buf_pos++];
          ret[pos++] = v;
       }
       size -= (1<<18);
    }

    if (size>0) {
       fread(buf,1,4*size,br);
       int buf_pos = 0;
       while (buf_pos < 4*size) {
          int v;
          char * c = (char*)&v;
          c[3] = buf[buf_pos++];
          c[2] = buf[buf_pos++];
          c[1] = buf[buf_pos++];
          c[0] = buf[buf_pos++];
          ret[pos++] = v;
       }
    }

    /*for(int i = 0; i<size; i++)
        ret[i] = getInt2(br);*/

    return ret;
}
/**
 * Forward data from input to output.
 * @param in the input handle.
 * @param out the output handle.
 */
void forward(FILE * in, FILE * out){
    int ch = getc(in);
    int sz, sz2, i;
    putc(ch,out);
    switch(ch){
        case METHOD_START :
        case TIMEOUT :
        case INT_TYPE :
            forward(in,out,4);
            break;
        case LONG_TYPE :
        case TIME :
        case PEAK_MEMORY_USED :
            forward(in,out,8);
            break;
        case DOUBLE_TYPE :
            forward(in,out,8);
            break;
        case STRING_TYPE :
        case EXCEPTION :
            sz = getInt2(in);
            writeInt(out,sz);
            forward(in,out,sz);
            break;
        case VEC_INT_TYPE :
            sz = getInt2(in);
            writeInt(out,sz);
            forward(in,out,sz*4);
            break;
        case VEC_LONG_TYPE :
        case VEC_DOUBLE_TYPE :
            sz = getInt2(in);
            writeInt(out,sz);
            forward(in,out,sz*8);
            break;
        case VEC_STRING_TYPE :
            sz = getInt2(in);
            writeInt(out,sz);
            for(i = 0; i<sz; i++){
                sz2 = getInt2(in);
                writeInt(out,sz2);
                forward(in,out,sz2);
            }
            break;
        case TERMINATE:
            break;
        default: 
            exit(111);
    }
}


