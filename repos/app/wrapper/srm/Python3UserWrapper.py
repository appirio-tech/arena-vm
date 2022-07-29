# Copyright (C) - 2022 TopCoder Inc., All Rights Reserved.

"""
Wrapper for Python3 solution.

@author liuliquan
@version 1.0
"""

#import <CLASS_NAME>
import os
import sys

class myStdout:
	def close(self):
		sys.__stdout__.close()
	def flush(self):
		sys.__stdout__.flush()
	def fileno(self):
		return sys.__stdout__.fileno()
	def isatty(self):
		return sys.__stdout__.isatty()
	def next(self):
		return sys.__stdout__.next()
	def read(self,size=None):
		return sys.__stdout__.read(size)
	def readline(self,size=None):
		return sys.__stdout__.readline(size)
	def readlines(self,size=None):
		return sys.__stdout__.readlines(size)
	def xreadlines(self):
		return sys.__stdout__.xreadlines()
	def seek(self, offset, whence=None):
		sys.__stdout__.seek(offset, whence)
	def tell(self):
		return sys.__stdout__.tell()
	def truncate(self, size=None):
		return sys.__stdout__.truncate(size)
	def write(self,str):
		try:
			sys.__stdout__.write(str)
		except IOError:
			pass
	def writelines(self,str):
		try:
			sys.__stdout__.writelines(str)
		except IOError:
			pass
	
sys.stdout = myStdout()

fd_in = 10
fd_out = 3

def readInt(fd):
	ret = readString(fd)
	return int(ret)

def readBool(fd):
	ret = readString(fd)
	return bool(ret)

def readDouble(fd):
	ret = readString(fd)
	return float(ret)

def readLong(fd):
	ret = readString(fd)
	return long(ret)

def readChar(fd):
	return readString(fd)

def readString(fd):
	ret = ""
	while 1:
		s = os.read(fd, 1).decode('utf-8')
		if(s == "\n"):
			break
		ret += s
	return ret
	
def readStringArray(fd):
	ret = []
	size = readInt(fd)
	for i in range(size):
		ret.append(readString(fd))
	
	return tuple(ret)

def readIntArray(fd):
	ret = []
	size = readInt(fd)
	for i in range(size):
		ret.append(readInt(fd))
	
	return tuple(ret)

def readLongArray(fd):
	ret = []
	size = readInt(fd)
	for i in range(size):
		ret.append(readLong(fd))
	
	return tuple(ret)
	
def readFloatArray(fd):
	ret = []
	size = readInt(fd)
	for i in range(size):
		ret.append(readLong(fd))
	
	return tuple(ret)
	
def readBoolArray(fd):
	ret = []
	size = readInt(fd)
	for i in range(size):
		ret.append(readBool(fd))
	
	return tuple(ret)

def writeInt(fd, ret):
	writeResults(fd, int(ret))

def writeBool(fd, ret):
	writeResults(fd, bool(ret))

def writeDouble(fd, ret):
	writeResults(fd, float(ret))

def writeLong(fd, ret):
	writeResults(fd, long(ret))

def writeChar(fd, ret):
	writeResults(fd, ret)

def writeString(fd, ret):
	writeResults(fd, ret)
	
def writeStringArray(fd, ret):
	r = []
	a = tuple(ret)
	for i in range(len(a)):
		r.append(a[i])
	
	writeResults(fd, tuple(r))

def writeIntArray(fd, ret):
	r = []
	a = tuple(ret)
	for i in range(len(a)):
		r.append(int(a[i]))
	
	writeResults(fd, tuple(r))

def writeLongArray(fd, ret):
	r = []
	a = tuple(ret)
	for i in range(len(a)):
		r.append(long(a[i]))
	
	writeResults(fd, tuple(r))
		
def writeDoubleArray(fd, ret):
	r = []
	a = tuple(ret)
	for i in range(len(a)):
		r.append(float(a[i]))
	
	writeResults(fd, tuple(r))
	
def writeBoolArray(fd, ret):
	r = []
	a = tuple(ret)
	for i in range(len(a)):
		r.append(bool(a[i]))
	
	writeResults(fd, tuple(r))


def writeResults(fd, v):
	if(isinstance(v, tuple) or isinstance(v,list)):
		os.write(fd, str.encode(str(len(v)) + "\n"))
		for a in v:
			os.write(fd, str.encode(str(a) + "\n"))
		return
	os.write(fd, str.encode(str(v) + "\n"))

<ARGS>
<ARG_NAME> = <ARG_METHOD_NAME>(fd_in)
</ARGS>

mod = __import__('<CLASS_NAME>')

sol = mod.<CLASS_NAME>()

sys.setrecursionlimit(10**7)

val = sol.<METHOD_NAME>(<PARAMS>)

<WRITE_METHOD_NAME>(fd_out, val)
