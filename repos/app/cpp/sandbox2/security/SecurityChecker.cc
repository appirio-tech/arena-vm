#include "SecurityChecker.h"
#include <sstream>

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

