#ifndef _cond_H
#define	_cond_H

#include <semaphore.h>
#include <sys/types.h>
#include <pthread.h>
#include "logger.h"
#include "mutex.h"

class Cond {
    private:
        Mutex* mutex;
        Logger* logger;
        pthread_cond_t* cond;
    public:
        Cond(Mutex* mutex, Logger* l);
        void wait();
        void signal();
        ~Cond();
};

#endif
