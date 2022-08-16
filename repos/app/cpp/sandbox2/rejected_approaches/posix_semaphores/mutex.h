#ifndef _mutex_H
#define	_mutex_H

#include <semaphore.h>
#include <sys/types.h>
#include "logger.h"

class Mutex {
    private:
        string name;
        sem_t sem;
        Logger* logger;
    public:
        Mutex(const char* name, Logger* l);
        void lock();
        void unlock();
        void setLogger(Logger* l);
        ~Mutex();
};

#endif
