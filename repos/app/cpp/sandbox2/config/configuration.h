/*
 * Copyright (C) 2006 - 2014 TopCoder Inc., All Rights Reserved.
 */

/**
 * <p>The configuration for sandbox.</p>
 *
 * <p>
 * Changes in version 1.1 (PoC Assembly - Return Peak Memory Usage for Executing SRM Solution):
 * <ol>
 *     <li> Add {@link #MAXMEMUSED} field to support tracking of memory usage.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.2 (TopCoder Competition Engine - Support Large Memory Limit Settings):
 * <ol>
 *      <li>Add {@link #getInt64(string s)} method to support large memory.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes in version 1.3 (TopCoder Competition Engine - Stack Size Configuration For SRM Problems v1.0):
 * <ol>
 *      <li>Added {@link #STACK_LIMIT} constant for stack limit.</li>
 * </ol>
 * </p>
 *
 * @author rfairfax, dexy, savon_cn, Selena
 * @version 1.3
 */

#ifndef _configuration_H
#define	_configuration_H

#include "../logger.h"

#include <vector>
#include <string>
#include <utility>
#include <iostream>
#include <fstream>
#include <map>

using namespace std;

#ifdef __GNUC__
#ifndef int64
typedef long long int64;
#endif
#else
#error missing/unknown int64 support with this compiler
#endif

const string CHILD_PATH = "childpath";
const string CHILD_ARGS = "childargs";

const string INIT_MODULES = "initmodules";
const string SECURITY_MODULES = "securitymodules";

const string WALL_TIMEOUT = "maxwall";
const string CPU_TIMEOUT = "maxcpu";
const string MEMORY_LIMIT = "maxmem";

/*
 * Stack size limit argument name.
 *
 * @since 1.3
*/
const string STACK_LIMIT = "maxstack";

const string RESULTS_DIR = "resultsdir";
const string BASE_DIR = "basedir";

const string CHILD_PID = "childpid";
const string OUTPUT_BYTE_LIMIT = "maxwrite";
const string MAX_THREADS = "maxthreads";

const string CHILD_EXIT = "child_exit";
const string USED_CPU = "used_cpu";
/**
 * maximum memory used in KB
 * @since 1.1
 */
const string MAXMEMUSED = "maxMemUsed";

const string APPROVED_PATH = "approvedpath";
const string APPROVED_FILES = "approvedfiles";

const string PORT = "port";

class Configuration {
private:
    Logger* logger;
    map<string, string> keys;

public:
    Configuration();

    Configuration(Logger*);

    void setLogger(Logger*);

    bool Setup(vector<pair<string, string> >);

    void set(string, string);
    void setInt(string, int);

    void log(string);
    void log(int);

    string get(string);

    vector<string> getVector(string, string);

    int getInt(string);

    int64 getInt64(string);
};

#endif	/* _configuration_H */

