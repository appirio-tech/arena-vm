/*
	This is the template for Long Contest MPSQAS Code
	Each "define" sourrounded by brackets gets expanded before compiling.
	The following defines are used (spaces used to prevent problems):
	
	< WRAPPER_CLASS> - Constant for the class name (Wrapper)
        < LONG_IO_CLASS> - Location of the IO Wrapper
	< CLASS_NAME> - Name of problem class
	< METHODS> - A special define, the dynamic methods go here.  The block is repeated until < /METHODS>
	< ARGS> - A special define, the dynamic args for a method go here.  The block is repeated until < /ARGS>

	< ARG_TYPE> - Type for current arg
	< RETURN_TYPE> - Return type for current method
	< METHOD_NAME> - Method name of function
	< METHOD_NUMBER> - Number of method, used for IO
	< PARAMS> - Expends to entire params of function (ex: a0, a1,...)
	< ARG_NAME> - Expends to the variable for the current arg
        < RETURN_POINTER_VAL> - Expands to the proper method pass for the return type
	< ARG_METHOD_NAME> - Gets the IO function to read the arg type
*/

#line 2 "top level"
#include "<EXPOSED_WRAPPER_CLASS>.cc"
int what_time(){
    struct timeval tv;
    gettimeofday(&tv, NULL);
    return timevalToMSec(tv);
}
/*
char * forceswap = malloc(1<<30);
int ffs(){
free(forceswap);
return 1;
}
int xx = ffs();
*/
long long startup = what_time();
#include "<CLASS_NAME>.cc"

int main() {
    startup = what_time() - startup;
    bool initialized = false;

printf("startup = %lld\n",startup);fflush(stdout);
    FILE* out = fdopen(12, "a");
    FILE* in = fdopen(11, "r");

    if(out == NULL)
        fprintf(stderr, "OUT IS NULL\n");
    if(in == NULL)
        fprintf(stderr, "IN IS NULL\n");
CPPStopwatch watch;

    <CLASS_NAME>* sol;
    
    <EXPOSED_WRAPPER_CLASS>::initialize(&watch);

    int timercalls = 0;
//while(1){ int command = getc(in); printf("Command = %d\n",command);fflush(stdout); if(command == -1)exit(1); }
    while(true) {
        int command = getc(in);
//printf("command = %d\n",command);fflush(stdout);
        if(command == TERMINATE) {
            return 0;
        } else if(command == METHOD_START) {
            int method = getInt2(in);
//printf("method = %d\n",method);fflush(stdout);
            
            switch(method) {
            <METHODS>
                case <METHOD_NUMBER>:
                {
                    <ARGS>
                    <ARG_TYPE> <ARG_NAME> = <ARG_METHOD_NAME>(in);
                    </ARGS>

                    //populate up the "start" signal
                    raise(SIGPROF);

                    watch.reset();
                    struct timeval tv;
                    gettimeofday(&tv, NULL);
		    timercalls++;

                    long long time = timevalToMSec(tv);

                    if(!initialized) {
                        sol = new <CLASS_NAME>();
                        initialized = true;
                    }

                    <RETURN_TYPE> val = sol-><METHOD_NAME>(<PARAMS>);
//printf("%d\n",val.size());fflush(stdout);
//cudaThreadSynchronize();
                    //populate up the "we're done" signal

                    gettimeofday(&tv, NULL);
		    timercalls++;

                    time = timevalToMSec(tv) - time;
                    if(startup > 0){
                        time += startup;
                        startup = 0;
                    }
//printf("time = %d\n",time);fflush(stdout);
                    if((time - watch.getTime()) >= 0)
	                    time -= watch.getTime();
		    if(time > 0 && timercalls >= 50) {
			time -= 1;
			timercalls -= 50;
		    }
		    raise(SIGPROF);

		    writeTime(out, time);
                    writeArg(out, 1);
                    writeArg(out, <RETURN_POINTER_VAL>val);
                    flush(out);

                    break;
            }
            </METHODS>
            }
        }    
    }

    return 0;
}
