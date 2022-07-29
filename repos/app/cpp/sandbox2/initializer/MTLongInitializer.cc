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

class MTLongInitializer;

class MTLongInitializer : public BaseLongInitializer {
    public:
        virtual void start_timer(int cputimeoutval, int walltimeoutval) {
            struct itimerval walltimeout, cputimeout;
#ifdef DEBUG
             stringstream s;
             s << "Setting wall-timer to : " << cputimeoutval << endl;
             config->log(s.str());
#endif
			/* For MT we don't use cpu time, only setting walltimeout using cputime which is the one in use
			   in the base class */
            /* we only want them to fire once */
            walltimeout.it_interval.tv_sec = 0;
            walltimeout.it_interval.tv_usec = 0;

            /* converting from milliseconds */
            walltimeout.it_value.tv_sec = cputimeoutval/1000;
            walltimeout.it_value.tv_usec = 1000*(cputimeoutval%1000);

			/* exit after this much wall time, just additional paranoia in case a
               syscall somehow blocks and the parent doesn't take the child with it */
            setitimer(ITIMER_REAL, &walltimeout, 0);
        }
};

extern "C" Initializer* create() {
    return new MTLongInitializer;
}

extern "C" void destroy(Initializer* i) {
    delete i;
}
