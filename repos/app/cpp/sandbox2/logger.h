#ifndef _logger_H
#define	_logger_H

#include <string>
#include <iostream>
#include <fstream>
#include <unistd.h>
using namespace std;

class Logger {
    private:
        ofstream* stream;
        string filename;
    public:
        Logger(string file);
        ~Logger();
        void close();
        void reinit();
        void flush();
        void log(string s);
        void log(const char* s);
        friend Logger& operator<<(Logger& l, string s);
        friend Logger& operator<<(Logger& l, const char* s);
        friend Logger& operator<<(Logger& l, int s);
};

Logger& operator<<(Logger& l, string s);
Logger& operator<<(Logger& l, const char* s);
Logger& operator<<(Logger& l, int s);

#endif

