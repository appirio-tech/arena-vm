/*
 * Copyright (C) 2006 - 2014 TopCoder Inc., All Rights Reserved.
 */

/*
 * <p>
 * Changes in version 1.1 (TopCoder Competition Engine - Support Large Memory Limit Settings):
 * <ol>
 *      <li>Add {@link #getInt64(string s)} method to support large memory.</li>
 * </ol>
 * </p>
 * @author rfairfax, dexy, savon_cn
 * @version 1.1
 */
#include "configuration.h"

#include <sstream>
using namespace std;

Configuration::Configuration(Logger* l) {
    logger = l;
}

Configuration::Configuration() {
    logger = NULL;
}

void Configuration::setLogger(Logger* l) {
    logger = l;
}

bool Configuration::Setup(vector< pair<string,string> > opts) {
    (*logger) << "Loading configuration" << "\n";
    
    string config_file = "";
    
    //step 1, find the --config param
    for(int i = 0; i < opts.size(); i++) {
        if(opts[i].first == "config") {
            config_file = opts[i].second;
            break;
        }
    }
    
    if(config_file == "") {
        return false;
    }
    
    (*logger) << "Loading config file: " << config_file << "\n";
    //TODO: load
    ifstream file;
    file.open(config_file.c_str());
    if(!file.is_open())
        return false;
    
    string s;
    while(!file.eof()) {
        s = "";
        char buf[8000];
        file.getline(buf,8000);
        
        s += (buf);
        
        if(s[0] == '#') {
            //(* logger) << "Comment line" << "\n";
        } else {
            string a = s.substr(0, s.find("="));
            string b = s.substr(s.find("=") + 1, s.size());

            set(a, b);
        }
    }
    
    file.close();
    
    //overrides
    for(int i = 0; i < opts.size(); i++) {
       if(keys.find(opts[i].first) != keys.end()) {
           (*logger) << "Overriding " << opts[i].first << "\n";
       }
       set(opts[i].first, opts[i].second);
    }
    
    return true;
}

void Configuration::set(string key, string val) {
    keys[key] = val;
    (*logger) << "Setting " << key << " to " << val << "\n";
}

void Configuration::setInt(string key, int val) {
    stringstream s;
    s << val;
    keys[key] = s.str();
    (*logger) << "Setting " << key << " to " << s.str() << "\n";
}

void Configuration::log(string s) {
    (*logger) << s;
    logger->flush();
}

void Configuration::log(int a) {
    (*logger) << a;
    logger->flush();
}

string Configuration::get(string s) {
    return keys[s];
}

vector<string> Configuration::getVector(string s, string delim) {
    vector<string> ret;
    string r = keys[s];
    
    while(r.find(delim) != r.npos) {
        string v = r.substr(0, r.find(delim));
        r = r.substr(r.find(delim) + 1, r.size());
        ret.push_back(v);
    }    
    ret.push_back(r);
    
    return ret;
}

int Configuration::getInt(string s) {    
    string a = keys[s];
    stringstream ss;
    ss << a;
    int r = 0;
    ss >> r;
    return r;
}

int64 Configuration::getInt64(string s) {
    string a = keys[s];
    stringstream ss;
    ss << a;
    int64 r = 0;
    ss >> r;
    return r;
}
