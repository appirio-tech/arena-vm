#include "mutex.h"
#include <sys/time.h>
#include <iostream>
#include <stdio.h>
#include <ctype.h>
#include <stdlib.h>
#include <errno.h>
#include <unistd.h>
#include <sys/mman.h>
#include <sys/stat.h>
#include <sys/fcntl.h>

Mutex::Mutex(const char* n, Logger* l) {
    //setup semaphores
    logger = l;

    name = n;
    
    mutex = (pthread_mutex_t*)malloc(sizeof(pthread_mutex_t));

    //sem = sem_open(name.c_str(), O_CREAT|O_EXCL, 0666, 1);
    if((pthread_mutex_init(mutex, NULL))== -1) {
        fprintf(stderr, "Mutex create error\n");
        fprintf(stderr, "Error is: %i\n", errno);
        fflush(stderr);
        exit(1);
    }
}

void Mutex::setLogger(Logger* l) {
    logger = l;
}

pthread_mutex_t* Mutex::getMutex() {
    return mutex;
}

void Mutex::lock() {

    if((pthread_mutex_lock(mutex)) == -1)
    {
        
            (*logger) << "Error Locking: " << errno << "\n";
            logger->flush();
            exit(1);
    }


}

void Mutex::unlock() {
    
    if((pthread_mutex_unlock(mutex)) == -1)
    {
            (*logger) << "Error Unlocking: " << errno << "\n";
            logger->flush();
            exit(1);
    }
}

Mutex::~Mutex() {
    //fprintf(stderr, "Destroying %s\n", name.c_str());
    pthread_mutex_destroy(mutex);
    free(mutex);
    //sem_destroy(&sem);
    //sem_close(sem);
    //sem_unlink(name.c_str());
}
