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

#include "<LONG_IO_CLASS>"
#include <signal.h>
#include <errno.h>
#include <sys/time.h>
#include <iostream>
#include <stdio.h>
#include <ctype.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/sem.h>
#ifdef _TC_THREADING_
#include <pthread.h>
#endif
union semun {
				int val;						// value for SETVAL
				struct semid_ds *buf;			// buffer for IPC_STAT, IPC_SET
				unsigned short int *array;		// aray for GETALL, SETALL
				struct seminfo *__buf;			// buffer for IPC_INFO
			};

using namespace std;


long long timevalToMSec(struct timeval tv) {
    long long ret = 0;
    ret += (tv.tv_sec * 1000);
    ret += (tv.tv_usec / 1000);

    return ret;
}

long long fullResolutionTimeval(struct timeval tv) {
    long long ret = 0;
    ret += ((long long)tv.tv_sec * 1000000LL);
    ret += (tv.tv_usec);

    return ret;
}


class CPPStopwatch {
    private:
        long long time;
        long long startTime;
    public:
        CPPStopwatch() {
            time = 0;
            startTime = 0;
        }
        
        void reset() {
            time = 0;
            startTime = 0;
        }
        
        void start() {
            struct timeval tv;
            gettimeofday(&tv, NULL);
            startTime = fullResolutionTimeval(tv);
        }
        
        void stop() {
            struct timeval tv;
            gettimeofday(&tv, NULL);
            long long end = fullResolutionTimeval(tv);
            time += (end - startTime);
            startTime = 0;
        }
        
        long long getTime() {
            return time / 1000LL;
        }
};

#ifdef _TC_THREADING_
class WrapperMutex {
	private:
		pthread_mutex_t m;

    public:
        WrapperMutex() {
			if (pthread_mutex_init(&m, NULL) == -1) {
				fprintf(stderr, "Failed to create mutex: %d", errno);
				exit(1);
			}
		 }

		 void lock() {
             if(pthread_mutex_lock( &m ) == -1) {
				 fprintf(stderr, "Lock failed %d\n", errno);
				 exit(1);
			 }
		 }

		 void unlock() {
			 if(pthread_mutex_unlock( &m ) == -1) {
			 	fprintf(stderr, "Unlock failed %d\n", errno);
			 	exit(1);
			 }
        }

		~WrapperMutex() {
			pthread_mutex_destroy(&m);
		}


};
#endif

class <EXPOSED_WRAPPER_CLASS> {
    private:
        static FILE* out;
        static FILE* in;
        static CPPStopwatch* watch;
        
        static key_t key;
        static int sid;
#ifdef _TC_THREADING_
        static WrapperMutex *mutex;
#endif
    
    public:
        static void initialize(CPPStopwatch* sw) {
            watch = sw;
            out = fdopen(13, "a");
            in = fdopen(13, "r");
#ifdef _TC_THREADING_
            mutex = new WrapperMutex();
#endif
        }


        
        
        <EXPOSED_METHODS>
	static <RETURN_TYPE> <METHOD_NAME>(<PARAMS>) {
#ifdef _TC_THREADING_
            mutex->lock();
#endif
            watch->start();
            raise(SIGXFSZ);
            
            writeMethod(out, <METHOD_NUMBER>);

            <WRITE_ARGS>
            writeArg(out, <RETURN_POINTER_VAL><ARG_NAME>);
            </WRITE_ARGS>
            flush(out);

            <RETURN_TYPE> val = <RETURN_METHOD_NAME>(in);

            raise(SIGXFSZ);
            watch->stop();
#ifdef _TC_THREADING_
            mutex->unlock();
#endif
            return val;
	}
	</EXPOSED_METHODS>
};

FILE* <EXPOSED_WRAPPER_CLASS>::out = NULL;
FILE* <EXPOSED_WRAPPER_CLASS>::in = NULL;
key_t <EXPOSED_WRAPPER_CLASS>::key = 0;
int <EXPOSED_WRAPPER_CLASS>::sid = 0;
#ifdef _TC_THREADING_
WrapperMutex* <EXPOSED_WRAPPER_CLASS>::mutex = NULL;
#endif
CPPStopwatch* <EXPOSED_WRAPPER_CLASS>::watch = NULL;
