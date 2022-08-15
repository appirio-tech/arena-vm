// 
// File:   initializer.h
// Author: rfairfax
//
// Created on July 6, 2006, 8:31 PM
//

#ifndef _initializer_H
#define	_initializer_H

#include "../config/configuration.h"

class Initializer {
    public:
        Initializer();
        
        virtual ~Initializer();
        
        virtual void Setup(Configuration*);
        
        virtual void init();
        
        virtual void childInit();
        
        virtual void postChildInit();
        
        virtual void exit(bool early);
        
        virtual void signaled(int signal, string name);
        
        virtual bool receiveSignal(int signal, int pid);
        
};

typedef Initializer* create_init_t();
typedef void destroy_init_t(Initializer*);

#endif	/* _initializer_H */

