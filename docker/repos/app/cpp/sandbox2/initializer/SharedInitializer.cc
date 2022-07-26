#include "initializer.h"
#include <sys/time.h>
#include <signal.h>
#include <unistd.h>
#include <stdlib.h>
void death_by_signal(int sig) {
  exit(EXIT_FAILURE);
}

class SharedInitializer : public Initializer {
    private:
        Configuration* config;
    
    public:
        
        virtual ~SharedInitializer() {
            config->log("Destroy\n");
        }
        
        virtual void Setup(Configuration* c) {
            config = c;
        }
        
        virtual void init() {
            config->log("Shared Init\n");
            /* just in case all the other timeout mechanisms fail :) */
            {
                int wall_msec_limit = config->getInt(WALL_TIMEOUT);
                struct sigaction sa;
                struct itimerval walltimeout;

                config->log("Signal Setup\n");

                sa.sa_handler = death_by_signal;
                sigemptyset(&sa.sa_mask);
                sa.sa_flags = 0;

                sigaction(SIGALRM, &sa, 0);

                config->log("ALRM set\n");

                walltimeout.it_interval.tv_sec = 0;
                walltimeout.it_interval.tv_usec = 0;
                walltimeout.it_value.tv_sec = wall_msec_limit/1000 + 2;
                walltimeout.it_value.tv_usec = 1000*(wall_msec_limit%1000);

                setitimer(ITIMER_REAL, &walltimeout, 0);
            }
            config->log("Leaving Shared Init\n");

        }
        
        virtual void childInit() {
            config->log("Shared Child Init\n");
        }
        
        virtual void postChildInit() {
            config->log("Shared Post Child Init\n");
        }
        
        virtual void exit(bool early) {
            
        }
        
};



extern "C" Initializer* create() {
    return new SharedInitializer;
}

extern "C" void destroy(Initializer* i) {
    delete i;
}
