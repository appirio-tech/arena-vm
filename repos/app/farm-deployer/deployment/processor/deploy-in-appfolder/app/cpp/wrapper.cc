// wrapper.cc

#ifdef WRAPPER_THUNK
#line 10000 "end of your submission"
#else
#line __LINE__ "wrapper.cc"
#endif

#if !defined WRAPPER_ENTRY && !defined WRAPPER_THUNK
#error must define at least one of WRAPPER_ENTRY or WRAPPER_THUNK
#endif

// both paths see these settings... only thunk cares about some though

#ifndef CLASS_NAME
#error CLASS_NAME must be defined
#endif

#ifndef METHOD_NAME
#error METHOD_NAME must be defined
#endif

#ifndef RETURN_TYPE
#error RETURN_TYPE must be defined
#endif

#ifndef ARG0_TYPE
#error ARG0_TYPE not defined, the method takes no arguments?!?
#endif

// If the user didn't declare the class correctly (e.g. mistyped the
// name), the presence of a forward declaration makes the error
// message much more legible.
#ifdef WRAPPER_THUNK
class CLASS_NAME;
#endif

// get library headers before i make life after cpp any more interesting

#ifdef WRAPPER_ENTRY

#include <cstdio>
#include <sstream>
#include <iomanip>
extern "C" {
#include <unistd.h>
}
#endif // WRAPPER_ENTRY

// both paths need these headers

#include <string>
#include <vector>

using namespace std;

// both paths need these types

namespace _wrapper {

// custom types

#ifdef __GNUC__
#ifndef int64
typedef long long int64;
#endif
#else
#error missing/unknown int64 support with this compiler
#endif

typedef vector<int> vecint;
typedef vector<char> vecchar;
typedef vector<bool> vecbool;
typedef vector<int64> vecint64;
typedef vector<double> vecdouble;
typedef vector<string> vecstring;

// declare the thunk

// extern here is not necessary in any case...

RETURN_TYPE thunk(
#ifdef ARG0_TYPE
ARG0_TYPE arg0
#endif
#ifdef ARG1_TYPE
, ARG1_TYPE arg1
#endif
#ifdef ARG2_TYPE
, ARG2_TYPE arg2
#endif
#ifdef ARG3_TYPE
, ARG3_TYPE arg3
#endif
#ifdef ARG4_TYPE
, ARG4_TYPE arg4
#endif
#ifdef ARG5_TYPE
, ARG5_TYPE arg5
#endif
#ifdef ARG6_TYPE
, ARG6_TYPE arg6
#endif
#ifdef ARG7_TYPE
, ARG7_TYPE arg7
#endif
#ifdef ARG8_TYPE
, ARG8_TYPE arg8
#endif
#ifdef ARG9_TYPE
, ARG9_TYPE arg9
#endif
)

#ifdef WRAPPER_THUNK

// define the thunk
{


// if there is an error here, it should only be due to the submission
// not matching the problem statement
#line 20000 "Your class or method was improperly declared"

  // construct an instance, call their method, and return the result

  return (new CLASS_NAME) -> METHOD_NAME(
#ifdef ARG0_TYPE
arg0
#endif
#ifdef ARG1_TYPE
, arg1
#endif
#ifdef ARG2_TYPE
, arg2
#endif
#ifdef ARG3_TYPE
, arg3
#endif
#ifdef ARG4_TYPE
, arg4
#endif
#ifdef ARG5_TYPE
, arg5
#endif
#ifdef ARG6_TYPE
, arg6
#endif
#ifdef ARG7_TYPE
, arg7
#endif
#ifdef ARG8_TYPE
, arg8
#endif
#ifdef ARG9_TYPE
, arg9
#endif
  );

  // we do not destroy their object, this memory leak is deliberate
}

#else // not WRAPPER_THUNK

// just prototyping the thunk
;

#endif

#ifdef WRAPPER_ENTRY

// input methods

// these silly hardcoded integers match the sandbox
int in=10, out=3;
int length_limit = 32767;

string readstring() {
  string r; char c;
  while(read(in,&c,1)) { // this is slow, but there shouldn't be much data
    if (c=='\n') break;
    r+=c;
  }
  if (r[r.length()-1]=='\r')
    r.resize(r.length()-1);
  return r;
}

template <typename T>
void readval(T &type)
{
    string s = readstring();

    istringstream iss(s);
    iss >> type;
}

template <>
void readval(string &type)
{
    type = readstring();
}

// in case the first char on the line is whitespace
template <>
void readval(char &type)
{
    type = readstring().c_str()[0];
}

template <>
void readval(signed char &type)
{
    type = readstring().c_str()[0];
}

template <>
void readval(unsigned char &type)
{
    type = readstring().c_str()[0];
}

template <typename T>
void readval(vector<T> &type)
{
    int i, c;

    readval(c);

    type.resize(c);
    for (i = 0; i < c; i++) {
        readval(type[i]);
    }
}

void genbase64encode(ostringstream &oss, unsigned char b[3], int padding) {
    const char* BASE64_CHAR="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    //get next three bytes in unsigned form lined up
    int combined = b[0] & 0xff;
    combined <<= 8;
    combined |= b[1] & 0xff;
    combined <<= 8;
    combined |= b[2] & 0xff;
    
    //break those 24 bits into 4 groups of 6 bits
    int c3 = combined & 0x3f;
    combined >>= 6;
    int c2 = combined & 0x3f;
    combined >>= 6;
    int c1 = combined & 0x3f;
    combined >>= 6;
    int c0 = combined & 0x3f;
    
    oss << BASE64_CHAR[c0] << BASE64_CHAR[c1] << ((padding >= 2) ? '=' : BASE64_CHAR[c2]) << ((padding >= 1) ? '=' : BASE64_CHAR[c3]);
}

// output methods
void genwrite(int o, const string &x) {
    // Check the length of string, if over, terminate with 200 indicating too long string/vector
    if (x.length() > length_limit) exit(200);
    
    // Do base64 encoding here.
    ostringstream oss;
    int len = (x.length() / 3) * 3;
    int leftover = x.length() - len;
    unsigned char bytes[3];
    for (int i = 0; i < len; i += 3) {
        bytes[0] = x[i];
        bytes[1] = x[i+1];
        bytes[2] = x[i+2];
        genbase64encode(oss, bytes, 0);
    }
    if (leftover == 1) {
      bytes[0] = x[len];
      bytes[1] = 0;
      bytes[2] = 0;
      genbase64encode(oss, bytes, 2);
    } else if (leftover == 2) {
      bytes[0] = x[len];
      bytes[1] = x[len+1];
      bytes[2] = 0;
      genbase64encode(oss, bytes, 1);
    }
    oss << endl;
    string s=oss.str();
    write(o,s.data(),s.length());
}

template <typename T> void genwrite(int o, T x) {
  ostringstream oss;
  oss << setprecision(12) << x << endl;
  string s=oss.str();
  write(o,s.data(),s.length());
}

template <typename T> void genwrite(int o, const vector<T> &x) {
  typename vector<T>::const_iterator p;
  // Check the length of vector, if over, terminate with 200 indicating too long string/vector
  if (x.size() > length_limit) exit(200);
  genwrite(o,x.size());
  for(p=x.begin(); p!=x.end(); p++)
    genwrite(o,*p);
}


#endif // WRAPPER_ENTRY

} // close namespace _wrapper

#ifdef WRAPPER_ENTRY

using namespace _wrapper;

// declare main

int main(int argc, char **argv) {

  // assuage gcc
  (void) argc;
  (void) argv;

  if (argc > 1)
  {
    // Read the first argument as the array/string length limit
    std::istringstream iss(argv[1]);
    iss >> length_limit;
  }

  // disable buffering so they get the most of their debug prints after a crash
  setvbuf(stdout, 0, _IONBF, 0);
  setvbuf(stderr, 0, _IONBF, 0);

  // this is an idea, but requires <iostream>... @@@
  // cout << unitbuf;
  // cerr << unitbuf;

  // get the arguments

#ifdef ARG0_TYPE
  ARG0_TYPE arg0;
  readval(arg0);
#endif
#ifdef ARG1_TYPE
  ARG1_TYPE arg1;
  readval(arg1);
#endif
#ifdef ARG2_TYPE
  ARG2_TYPE arg2;
  readval(arg2);
#endif
#ifdef ARG3_TYPE
  ARG3_TYPE arg3;
  readval(arg3);
#endif
#ifdef ARG4_TYPE
  ARG4_TYPE arg4;
  readval(arg4);
#endif
#ifdef ARG5_TYPE
  ARG5_TYPE arg5;
  readval(arg5);
#endif
#ifdef ARG6_TYPE
  ARG6_TYPE arg6;
  readval(arg6);
#endif
#ifdef ARG7_TYPE
  ARG7_TYPE arg7;
  readval(arg7);
#endif
#ifdef ARG8_TYPE
  ARG8_TYPE arg8;
  readval(arg8);
#endif
#ifdef ARG9_TYPE
  ARG9_TYPE arg9;
  readval(arg9);
#endif

  // use the thunk to invoke their method

  genwrite(out,thunk(
#ifdef ARG0_TYPE
arg0
#endif
#ifdef ARG1_TYPE
, arg1
#endif
#ifdef ARG2_TYPE
, arg2
#endif
#ifdef ARG3_TYPE
, arg3
#endif
#ifdef ARG4_TYPE
, arg4
#endif
#ifdef ARG5_TYPE
, arg5
#endif
#ifdef ARG6_TYPE
, arg6
#endif
#ifdef ARG7_TYPE
, arg7
#endif
#ifdef ARG8_TYPE
, arg8
#endif
#ifdef ARG9_TYPE
, arg9
#endif
  ));

  return 0;
}

#endif // WRAPPER_ENTRY
