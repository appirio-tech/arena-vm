#include "logger.h"
#include "mutex.h"
#include <sstream>

//note, this needs to be redone if we need multiple loggers
Mutex* mutex = NULL;

Logger::Logger(string f) {
    filename = f;
    stream = new ofstream();
    stream->open(f.c_str());

    stringstream s;
    s << "logger." << getpid();

    mutex = new Mutex(s.str().c_str(), NULL);
    mutex->setLogger(this);
}

void Logger::log(string s) {
    log(s.c_str());
}

void Logger::log(const char* s) {
    mutex->lock();
    (*stream) << s << endl;
    mutex->unlock();
}

void Logger::flush() {
    mutex->lock();
    stream->flush();
    mutex->unlock();
}

void Logger::close() {
    if(stream) {
        (*stream) << "Shutting down" << endl;
        if(stream->is_open())
            stream->close();
        delete stream;
        stream = NULL;
    }
}

void Logger::reinit() {
    close();
    stream = new ofstream();
    stream->open(filename.c_str());
}

Logger::~Logger() {
    close();
    if(mutex) {
        delete mutex;
        mutex = NULL;
    }
}

Logger& operator<<(Logger& l, string s) {
    return operator<<(l, s.c_str());
}

Logger& operator<<(Logger& l, const char* s) {
    mutex->lock();
    (*l.stream) << s;
    mutex->unlock();
    return l;
}
Logger& operator<<(Logger& l, int s) {
    mutex->lock();
    (*l.stream) << s;
    mutex->unlock();
    return l;
}
