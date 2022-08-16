#include "SecurityChecker.h"


class SRMSecurityChecker : public SecurityChecker {
    public:
        
        virtual ~SRMSecurityChecker() {
            config->log("Destroy\n");
        }
        
        virtual void Setup(Configuration* c) {
            config = c;
        }
        
        bool SyscallCheck(struct pstate_t *ps, pid_t childpid) {          
            return false;
        }
};



extern "C" SecurityChecker* create() {
    return new SRMSecurityChecker;
}

extern "C" void destroy(SecurityChecker* i) {
    delete i;
}
