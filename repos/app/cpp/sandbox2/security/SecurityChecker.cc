/*
 * Copyright (C) - 2022 TopCoder Inc., All Rights Reserved.
 */

/**
 * Security checker.
 *
 * <p>
 * Changes in version 1.1 (Python3 Support):
 * <ol>
 *     <li> Added {@link #openForRead(struct pstate_t *ps, char *filename)} method to check whether file is open for read.</li>
 * </ol>
 * </p>
 *
 * @author liuliquan
 * @version 1.1
 */

#include "SecurityChecker.h"
#include <fcntl.h>
#include <sstream>
#include <cstring>
using namespace std;

SecurityChecker::SecurityChecker() {
    
}

SecurityChecker::~SecurityChecker() {
    
}

void SecurityChecker::Setup(Configuration* c) {
    
}

bool SecurityChecker::SyscallCheck(struct pstate_t *ps, pid_t childpid) {
    return false;
}

bool SecurityChecker::openForRead(struct pstate_t *ps, char *filename) {
    bool openat = ps->scno == NR_openat;
    #if __WORDSIZE == 64
        long _ecx = openat ? ps->rsv0[1] : ps->rsv0[2];
    #else
        int _ecx = openat ? ps->arg[2] : ps->arg[1];
    #endif

    // The allowed open flags are: O_RDONLY|O_CLOEXEC|O_DIRECTORY|O_NONBLOCK|O_LARGEFILE
    bool hasOtherFlag = _ecx & ~(O_RDONLY|O_CLOEXEC|O_DIRECTORY|O_NONBLOCK|O_LARGEFILE);
    if (hasOtherFlag) {
        if ((strstr(filename, "/dev/null") == filename)) {
            return true;
        }

        stringstream s;
        s << "Invalid open mode of " << filename << ": " << _ecx << endl;
        config->log(s.str());
        return false;
    }
    return true;
}

void SecurityChecker::dumpRegs(const char* text, pstate_t* ps) {
    stringstream s;
    s << text << ": " << endl;
    s << hex << ps->arg[0];
    s << ",";
    s << hex << ps->arg[1];
    s << ",";
    s <<  hex << ps->arg[2];
    s << ",";
    s <<  hex << ps->arg[3];
    s << ",";
    s <<  hex << ps->arg[4];
    s << ",";
    s <<  hex << ps->arg[5];
    s << ",";
    s <<  hex << ps->arg[6];
    config->log(s.str());
}

