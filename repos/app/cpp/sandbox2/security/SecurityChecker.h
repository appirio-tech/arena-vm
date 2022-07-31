/*
 * Copyright (C) - 2022 TopCoder Inc., All Rights Reserved.
 */

#ifndef _SecurityChecker_H
#define	_SecurityChecker_H

#include "../config/configuration.h"
#if __WORDSIZE == 64
    #include "../syscall64.h"
#else
    #include "../syscall.h"
#endif

/**
 * Security checker header file.
 *
 * <p>
 * Changes in version 1.1 (Python3 Support):
 * <ol>
 *     <li> Added {@link #openForRead(struct pstate_t *ps, char *filename)} method to check whether file is open for read.</li>
 * </ol>
 * </p>
 *
 * @author rfairfax, liuliquan
 * @version 1.1
 */
class SecurityChecker {
    protected:
        Configuration *config;
        bool openForRead(struct pstate_t *ps, char *filename);
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

