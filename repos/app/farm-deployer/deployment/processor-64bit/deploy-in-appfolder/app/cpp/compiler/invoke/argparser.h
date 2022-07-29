/**
 * This file contains argument parsing code for each argument
 * that will be passed to the user process, arrayparse is called.
 * new parsing function can be added for classes.  A function for
 * the class has to be made, i.e.
 * 
 *     void myclassparse(char * str,void * memptr);
 *
 * Where str is the pointer to the string version of the class.
 * So, if you could imagine a fromString() function, these are those
 * fromString() functions.  memptr is the place that the actual argument
 * will be written after it has been parsed.  Memory is returned by
 * arrayparse, so it should be free'd after it is done being used.
 * in addition a small modification to arrayparse will have to be made to
 * support new types.
 *
 * @authors Sean Stanek and Jason Stanek
 * @version 1.0
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <vector.h>
#include <tcclasses/String.h>
#include <tcclasses/Integer.h>
#include <tcclasses/ArrayList.h>
#include <string>

void charparse(char * str, void * memptr){
  *(char*)memptr = str[0];
  return;
}

void shortparse(char * str, void * memptr){
  *(short*)memptr = atoi(str);
  return;
}

void intparse(char * str, void * memptr){
  *(int*)memptr = atoi(str);
  return;
}

void floatparse(char * str, void * memptr){
  float f=0;
  sscanf(str,"%f",&f);
  *(float*)memptr = f;
  return;
}

void doubleparse(char * str, void * memptr){
  double d=0;
  sscanf(str,"%lf",&d);
  *(double*)memptr = d;
  return;
}

void longparse(char * str, void * memptr){
  long long l=0;
  sscanf(str,"%lld",&l);
  *(long long*)memptr = l;
  return;
}

void stringparse(char * str, void * memptr){
  sscanf(str,"%s",memptr);
  return;
}

  /*
  ** this takes a string, and advances to the next delimeter, copying
  ** over all characters up to the delimeter into token, and then
  ** returning the pointer to where it left off (on the delimiter).
  ** also uses '\' to designate literal characters (won't be counted
  ** as a delimiter).
  */

char * advance(char * src, char * token, char * delim) {
  int i;
  while(*src && strchr(delim,*src) == 0) {
    if(*src == '\\') src++;
    *token++ = *src++;
  }
  *token = '\0';
  return src;
}


  /*
  ** this arrayparse function takes a string such as "Integer,5" or
  ** "ArrayList,{Integer,5,String,asdf}" and returns a base pointer to it
  */

void * arrayparse(char * str) {
  char token[4096];
  char buf[4096];
  char newtype[256];
  int count = 0;
  int bracecount = 0;
  int p,i;
  void * memptr = 0, * tempptr = 0;
  int typesize = sizeof(void*);    // initialized in case of multidimensional array pointers
  void (*convfunc)(char * str, void * memptr);

  if(*str == '}') {  return (void*)NULL;  }

    //
    // There are several different types of parameters we need to parse:
    //   int,5
    //   Integer,5
    //   int*,{int,1,int,2,int,3,int,4,int,5}
    //   int**,{int*,{int,1,int,2},int*,{int,3,int,4}}
    //   String,asdf
    //   ArrayList,{Integer,5,String,asdf}
    //   ArrayList*,{{Integer,1,Integer,2},{Integer,3,Integer,4}}
    //   String[],{String,a,String,afasf}
    //   int[],{int,1,int,3}
    //   double[],{double,2.34,double,7.332}
    //

  str = advance(str,token,",");           // get argument type (int, String, etc.)
  str++;                                  // skip over comma

  if(strchr(token,'*')) {

    str++;                                // must be a '{' if any array type

    typesize = sizeof(void*);

      // if it's a 1D array, we store the actual values instead of pointers to them
      // classes are stored as pointers anyway, so they shouldn't have to be listed here

    if(strcmp(token,"char*") == 0)      typesize = sizeof(char);
    if(strcmp(token,"short*") == 0)     typesize = sizeof(short);
    if(strcmp(token,"int*") == 0)       typesize = sizeof(int);
    if(strcmp(token,"float*") == 0)     typesize = sizeof(float);
    if(strcmp(token,"double*") == 0)    typesize = sizeof(double);
    if(strcmp(token,"Integer*") == 0)   typesize = sizeof(Integer);
    if(strcmp(token,"String*") == 0)    typesize = sizeof(String);
    if(strcmp(token,"ArrayList*") == 0) typesize = sizeof(ArrayList);

    strcpy(newtype,token);

      //
      // there's a few possibilities we want to make sure work here for recursion
      //   "int,5,int,6}"
      //   "int*,{int,5,int,6},int*,{int,7,int,8}}"
      // all we have to do is loop and add more elements to some array until
      // we encounter a '}' that doesn't belong to us.
      //

    while(1) {

      memptr = realloc(memptr,(count+1)*typesize);
      tempptr = arrayparse(str);

      if(strcmp(newtype,"char*") == 0) {

        ((char*)memptr)[count++] = *(char*)tempptr;
        delete ((char*)tempptr);

      } else if(strcmp(newtype,"short*") == 0) {

        ((short*)memptr)[count++] = *(short*)tempptr;
        delete ((short*)tempptr);

      } else if(strcmp(newtype,"int*") == 0) {

        ((int*)memptr)[count++] = *(int*)tempptr;
        delete ((int*)tempptr);

      } else if(strcmp(newtype,"float*") == 0) {

        ((float*)memptr)[count++] = *(float*)tempptr;
        delete ((float*)tempptr);

      } else if(strcmp(newtype,"double*") == 0) {

        ((double*)memptr)[count++] = *(double*)tempptr;
        delete ((double*)tempptr);

      } else if(strcmp(newtype,"Integer*") == 0) {

        ((Integer*)memptr)[count++] = *(Integer*)tempptr;
        delete ((Integer*)tempptr);

      } else if(strcmp(newtype,"String*") == 0) {

        //memcpy(&((String*)memptr)[count],(String*)tempptr,sizeof(String));
        ((String*)memptr)[count++] = *(String*)tempptr;
        delete ((String*)tempptr);

      } else if(strcmp(newtype,"ArrayList*") == 0) {

        ((ArrayList*)memptr)[count++] = *(ArrayList*)tempptr;
        delete ((ArrayList*)tempptr);

      } else {

          // some type not defined (any multidimensional array) or classes, store only pointer

        ((unsigned int*)memptr)[count++] = (unsigned int)tempptr;

      }


        // need to advance past whatever internal array stuff inner elements had

      str = advance(str,token,",");
      str++;                           // advance past type element into data element

      if(*str == '{') {                // if had internal array, continue till past it
        bracecount = 1;
        str++;
        while(bracecount) {
          if(*str == '{') bracecount++;
          if(*str == '}') bracecount--;
          if(*str == '\\') str++;
          str++;
        }
      } else {
        str = advance(str,token,",}"); // if it wasn't an array, just skip this element
      }

      if(*str == '}') break;           // if no more elements, break out now
      str++;
    }

    return (void*)memptr;

  } else if(strcmp(token,"ArrayList") == 0) {

    str++;                                // must be a '{' if any array type

      // this is going to look remarkably similar to the array parser, except
      // that here instead of realloc()ing and setting pointers, we just add
      // the returned element to an ArrayList... and at the end, return it.

    ArrayList * ArrayList_r = new ArrayList();

    if(*str != '}') {
      while(1) {

//        count++;
//        memptr = realloc(memptr,count*sizeof(void*));
//        memptr[count-1] = arrayparse(str);

        memptr = arrayparse(str);

          // need to advance past whatever internal array stuff inner elements had

        str = advance(str,token,",");
        str++;                           // advance past type element into data element


          //
          // another place to add classes that can be contained inside of Objects.
          //

        if(strcmp(token,"Integer") == 0) {

          Integer * Integer_tmp = (Integer*)memptr;
          ArrayList_r->add(Integer_tmp);

        } else if(strcmp(token,"String") == 0) {

          String * String_tmp = (String*)memptr;
          ArrayList_r->add(String_tmp);

        } else if(strcmp(token,"ArrayList") == 0) {

          ArrayList * ArrayList_tmp = (ArrayList*)memptr;
          ArrayList_r->add(ArrayList_tmp);

        }


        if(*str == '{') {                // if had internal array, continue till past it
          bracecount = 1;
          str++;
          while(bracecount) {
            if(*str == '{') bracecount++;
            if(*str == '}') bracecount--;
            if(*str == '\\') str++;
            str++;
          }
        } else {
          str = advance(str,token,",}"); // if it wasn't an array, just skip this element
        }

        if(*str == '}') break;           // if no more elements, break out now
        str++;
      }
    }

    return (void*)ArrayList_r;


  } else if(strcmp(token,"int[]") == 0) {

    str++;                                // must be a '{' if any array type
    vector<int> * vector_r = new vector<int>;

    if(*str != '}') {
      while(1)
      {
        str = advance(str,token,",}");
        vector_r->push_back(atoi(token));
        if(*str == '}') break;           // if no more elements, break out now
        str++;                           // advance
      }
    }

    return (void*)vector_r;

  } else if(strcmp(token,"double[]") == 0) {

    str++;                                // must be a '{' if any array type
    vector<double> * vector_r = new vector<double>;

    if(*str != '}') {
      while(1)
      {
        str = advance(str,token,",}");
        
        double d=0;
        sscanf(token,"%lf",&d);                
        vector_r->push_back(d);
        if(*str == '}') break;           // if no more elements, break out now
        str++;                           // advance
      }
    }

    return (void*)vector_r;
  } else if(strcmp(token,"String[]") == 0) {

    str++;                                // must be a '{' if any array type
    //vector<char*> * vector_r = new vector<char*>;
    vector<string> * vector_r = new vector<string>;

    if(*str != '}') {
      while(1)
      {
        str = advance(str,token,",}");
        char* tmp = new char[strlen(token)+1];
        strcpy(tmp,token);
        vector_r->push_back(tmp);

        if(*str == '}') break;           // if no more elements, break out now
        str++;                           // advance
      }
    }
    return (void*)vector_r;

  } else {

      //
      // here is where you may want to put extra or special types and classes.
      //

    if(strcmp(token,"char") == 0) {

      char * char_r = new char;
      charparse(str,(void*)char_r);
      return (void*)char_r;

    } else if(strcmp(token,"short") == 0) {

      short * short_r = new short;
      shortparse(str,(void*)short_r);
      return (void*)short_r;

    } else if(strcmp(token,"int") == 0) {

      int * int_r = new int;
      intparse(str,(void*)int_r);
      return (void*)int_r;

    } else if(strcmp(token,"float") == 0) {

      float * float_r = new float;
      floatparse(str,(void*)float_r);
      return (void*)float_r;

    } else if(strcmp(token,"double") == 0) {

      double * double_r = new double;
      doubleparse(str,(void*)double_r);
      return (void*)double_r;

    } else if(strcmp(token,"String") == 0) {
    
      //char * str_ptr = new char[strlen(str)+1];
      string * str_ptr = new string(str);
      //for(int i=0; i < strlen(str)+1; i++)
      //  str_ptr[0] = '\0';
      //sscanf(str,"%s", str_ptr);
      return (void*)str_ptr;
      
//      return (void*)&str;    // I want this one to work
/*
      char * string_ptr = new char[strlen(str)+1];
      strcpy(string_ptr,str);
      return (void*)string_ptr;

    } else if(strcmp(token,"String") == 0) {

      char ** cptr = &str;

      advance(str,buf,",}");
      String * String_r = new String(buf);
printf("BUF: %s", buf);
//      return (void*)String_r;
      return (void*)String_r->toString();
*/
    } else if(strcmp(token,"Integer") == 0) {

      Integer * Integer_r = new Integer(atoi(str));
      return (void*)Integer_r;

    } 
  }

  return memptr;
  
}
 
  /**
  * Converts a vector to a String.
  *
  * @param Vec           A vector of any type
  * @param varType       The type of the vector
  * @param forInternal   indicates whether we need to include escape characters in the event of a char* vector
  * @return              A null terminated character array version of the ArrayList
  *
  * @author Adam Silberfein
  */
template <class T> char* toString(vector<T> & Vec, char* vartype, bool
forInternal) 
{ 
  vector<T>::iterator p; 

  int type = 0; 
  if(strcmp(vartype, "int") == 0) 
    type = 1; 
  else if(strcmp(vartype, "double") == 0) 
    type = 2; 
  //else if(strcmp(vartype, "char*") == 0) 
  else if(strcmp(vartype, "string") == 0)
    type = 3; 

  string strRet;
  strRet += "{";
   
  for (p = Vec.begin(); p != Vec.end(); p++) 
  { 
    string temp = toString(*p);
    if(type == 3 && forInternal)  // We need to escape certain characters (',', '{', '}','"') 
      temp = resultString(temp);
          
    if(p != Vec.begin()) 
        strRet += ", ";

    if(type == 3) {
      temp = "\"" + temp + "\"";
    }

    strRet += temp;

  } 
   
  strRet += "}";
  char *ret = new char[strRet.length() + 1];
  strcpy( ret, strRet.c_str() );

  return ret; 
} 


string toString(int i)
{
  char* temp = new char[10];
  sprintf(temp, "%d", i);
  return string(temp);  
}

string toString(double d)
{
  char* temp = new char[25];
  sprintf(temp, "%.2f", d);
  return string(temp);    
}

string toString(string s)
{
  return s;
}

/**
* Takes a string without escaped special characters and puts the escape characters in
**/
char* resultString(char* thestring) { 

  int count = 0; 
  int len = strlen(thestring); 
  int i = 0; 
  for(i=0;i<strlen(thestring);i++){ 
    switch(thestring[i]){ 
      case ',': 
      case '{': 
      case '}':
        count++; 
        break; 
      default: 
        break; 
    } 
  } 

  char* temp = new char[len+count+1]; 
  int cur = 0; 

  for(i=0;i<strlen(thestring);i++) 
  { 
    switch(thestring[i]){ 
      case ',': 
      case '{': 
      case '}':
        temp[i+cur] = '\\'; 
        cur++; 
        break; 
      default: 
        break; 
    } 
    temp[i+cur] = thestring[i]; 
  } 
  temp[i+cur] = '\0'; 
  return temp; 
} 

string resultString(string thestring) 
{ 
  string retVal;
 
  for(int i = 0; i < thestring.length(); i++)
  {
    switch(thestring[i]) 
    {
      case ',':
      case '{':
      case '}':
      case '"':
        retVal = retVal + '\\';
      default:
        retVal = retVal + thestring[i];
    }
  }
  
  return retVal;

}
 
