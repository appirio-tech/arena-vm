#include "cond.h"
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

Cond::Cond(Mutex* m, Logger* l) {
    //setup 
    logger = l;
    mutex = m;
    
    cond = (pthread_cond_t*)malloc(sizeof(pthread_cond_t));

    if((pthread_cond_init(cond, NULL))== -1) {
        fprintf(stderr, "Condition create error\n");
        fprintf(stderr, "Error is: %i\n", errno);
        fflush(stderr);
        exit(1);
    }
}

void Cond::wait() {

    logger->log("Preparing to wait");
    if((pthread_cond_wait(cond, mutex->getMutex())) == -1)
    {
        
            (*logger) << "Error Waiting: " << errno << "\n";
            logger->flush();
            exit(1);
    }
    logger->log("Waited on Condition");
}

void Cond::signal() {
    
    logger->log("Preparing to signal");
    if((pthread_cond_signal(cond)) == -1)
    {
            (*logger) << "Error Signaling: " << errno << "\n";
            logger->flush();
            exit(1);
    }
    logger->log("Signaled");
}

Cond::~Cond() {
    //fprintf(stderr, "Destroying Condition\n");
    pthread_cond_destroy(cond);
    free(cond);
}
