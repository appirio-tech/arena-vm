// 
// File:   SecurityChecker.h
// Author: rfairfax
//
// Created on July 11, 2006, 5:54 PM
//

#ifndef _SecurityChecker_H
#define	_SecurityChecker_H

#include "../config/configuration.h"
#if __WORDSIZE == 64
    #include "../syscall64.h"
#else
    #include "../syscall.h"
#endif


class SecurityChecker {
    protected:
        Configuration *config;
    public:
        SecurityChecker();
        
        virtual ~SecurityChecker();
        
        virtual void Setup(Configuration*);
        
        virtual bool SyscallCheck(struct pstate_t *ps, pid_t childpid);
        
        void dumpRegs(const char* text, pstate_t* ps); 
};

typedef SecurityChecker* create_security_t();
typedef void destroy_security_t(SecurityChecker*);

#endif	/* _SecurityChecker_H */

