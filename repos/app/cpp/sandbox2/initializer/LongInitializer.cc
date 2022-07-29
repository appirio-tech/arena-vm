#include "initializer.h"

#include <cstdio>
#include <sys/time.h>
#include <signal.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/resource.h>
#include <iostream>
#include <sstream>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <netinet/tcp.h>


using namespace std;

#include "../../long_io.cc"
#include "BaseLongInitializer.cc"



class LongInitializer : public BaseLongInitializer {
    public:
        virtual void start_timer(int cputimeoutval, int walltimeoutval) {
			struct itimerval walltimeout, cputimeout;

            /* we only want them to fire once */
            walltimeout.it_interval.tv_sec = 0;
            walltimeout.it_interval.tv_usec = 0;
            cputimeout.it_interval.tv_sec = 0;
            cputimeout.it_interval.tv_usec = 0;
#ifdef DEBUG
             stringstream s;
             s << "Setting timers to : " << cputimeoutval << endl;
             config->log(s.str());
#endif
            /* converting from milliseconds */
            walltimeout.it_value.tv_sec = walltimeoutval/1000;
            walltimeout.it_value.tv_usec = 1000*(walltimeoutval%1000);
            cputimeout.it_value.tv_sec = cputimeoutval/1000;
            cputimeout.it_value.tv_usec = 1000*(cputimeoutval%1000);

            /* exit after this much cpu time has been used; the SIGVTALRM is guaranteed to
               arrive one tick late rather than early, so we can use our exact cpu max here */
            setitimer(ITIMER_VIRTUAL, &cputimeout, 0);

            /* exit after this much wall time, just additional paranoia in case a
               syscall somehow blocks and the parent doesn't take the child with it */
            setitimer(ITIMER_REAL, &walltimeout, 0);

            /* nothing else need be done; the default handler for both signals
               delivered by the timers (SIGVTALRM and SIGALRM) is to exit */
        }
};


extern "C" Initializer* create() {
    return new LongInitializer;
}

extern "C" void destroy(Initializer* i) {
    delete i;
}

