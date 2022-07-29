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

    //sem = sem_open(name.c_str(), O_CREAT|O_EXCL, 0666, 1);
    if((sem_init(&sem, 1, 1))== -1) {
        fprintf(stderr, "Semaphore create error, cannot get key\n");
        fprintf(stderr, "Error is: %i\n", errno);
        fflush(stderr);
        exit(1);
    }
}

void Mutex::setLogger(Logger* l) {
    logger = l;
}

void Mutex::lock() {
    if((sem_wait(&sem)) == -1)
    {
        
            (*logger) << "Error Locking: " << errno << "\n";
            logger->flush();
            exit(1);
    }
}

void Mutex::unlock() {
    if((sem_post(&sem)) == -1)
    {
            (*logger) << "Error Unlocking: " << errno << "\n";
            logger->flush();
            exit(1);
    }
}

Mutex::~Mutex() {
    sem_destroy(&sem);
    //sem_close(sem);
    //sem_unlink(name.c_str());
}
