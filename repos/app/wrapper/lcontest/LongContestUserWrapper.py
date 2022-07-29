# Copyright (C) - 2014 TopCoder Inc., All Rights Reserved.

"""
Wrapper for Python MM solution.

<p>
Changes in version 1.1 (TopCoder Competition Engine - Stack Size Configuration For MM Problems v1.0):
<ol>
    <li>Added sys.setrecursionlimit(10**7) in order to avoid recursion limit problems in user code.</li>
</ol>
</p>

@author Selena
@version 1.1
"""

import re
import struct 
import os
import sys
import time
import <EXPOSED_WRAPPER_CLASS>

sys.setrecursionlimit(10**7)

class myStdout:
	def __init__(self):
		self.readAll = False
		
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
		if(self.readAll):
			return
		try:
			sys.__stdout__.write(str)
		except IOError:
			self.readAll = True
	def writelines(self,str):
		if(self.readAll):
			return
		try:
			sys.__stdout__.writelines(str)
		except IOError:
			self.readAll = True
	
#sys.stdout = myStdout()

SIG_PROF = 27
INT_TYPE = 1
LONG_TYPE = 2
DOUBLE_TYPE = 3
STRING_TYPE = 4
VEC_INT_TYPE = 5
VEC_LONG_TYPE = 6
VEC_DOUBLE_TYPE = 7
VEC_STRING_TYPE = 8

EXCEPTION = 250
TIMEOUT = 251
ABORT = 252
METHOD_START = 253
TIME = 254
TERMINATE = 255

#library code

def flush(fd):
	try: os.fsync(fd)
	except OSError, e:
		pass

def writeInt2(fd, a):
	os.write(fd,chr((a>>24)&255))
	os.write(fd,chr((a>>16)&255))
	os.write(fd,chr((a>>8)&255))
	os.write(fd,chr(a&255))

def writeInt(fd, a):
	os.write(fd,chr(INT_TYPE))
	writeInt2(fd,a)

def startMethod(fd, a):
	os.write(fd,chr(METHOD_START))
	writeInt2(fd,a)

def writeTime(fd, a):
	os.write(fd,chr(TIME))
	writeInt2(fd,a)

def writeLong(fd, a):
	os.write(fd,chr((a>>56)&255))
	os.write(fd,chr((a>>48)&255))
	os.write(fd,chr((a>>40)&255))
	os.write(fd,chr((a>>32)&255))
	os.write(fd,chr((a>>24)&255))
	os.write(fd,chr((a>>16)&255))
	os.write(fd,chr((a>>8)&255))
	os.write(fd,chr(a&255))

def writeLong(fd, a):
	os.write(fd,chr(LONG_TYPE))
	writeLong2(fd,a)

def writeDouble2(fd, a):
	os.write(fd,struct.pack(">d",a))

def writeDouble(fd, a):
	os.write(fd,chr(DOUBLE_TYPE))
	writeDouble2(fd,a)

def writeString2(fd, a):
	writeInt2(fd,len(a))
	os.write(fd,a)

def writeString(fd, a):
	os.write(fd,chr(STRING_TYPE))
	writeString2(fd,a)
	
def writeIntArray(fd, a):
	os.write(fd,chr(VEC_INT_TYPE))
	writeInt2(fd,len(a))
	for i in a :
		writeInt2(fd,i)

def writeLongArray(fd, a):
	os.write(fd,chr(VEC_LONG_TYPE))
	writeInt2(fd,len(a))
	for i in a :
		writeLong2(fd,i)

def writeDoubleArray(fd, a):
	os.write(fd,chr(VEC_DOUBLE_TYPE))
	writeInt2(fd,len(a))
	for i in a :
		writeDouble2(fd,i)

def writeStringArray(fd, a):
	os.write(fd,chr(VEC_STRING_TYPE))
	writeInt2(fd,len(a))
	for i in a :
		writeString2(fd,i)
	
def writeArg(fd, a):
	if(isinstance(a,list)):
		if(len(r) == 0):
			os.write(fd,VEC_INT_TYPE)
			writeInt2(fd,0)
		elif(isinstance(a[0],int)):
			writeIntArray(fd, a)
		elif(isinstance(a[0],long)):
			writeLongArray(fd, a)
		elif(isinstance(a[0],double)):
			writeDoubleArray(fd, a)
		elif(isinstance(a[0],str)):
			writeStringArray(fd, a)
	elif(isinstance(a,int)):
		writeInt(fd, a)
	elif(isinstance(a,long)):
		writeLong(fd, a)
	elif(isinstance(a,double)):
		writeDouble(fd, a)
	elif(isinstance(a,str)):
		writeString(fd, a)

def getInt2(fd):
	ret = struct.unpack(">i",os.read(fd,4))
	return ret[0]

def getInt(fd):
	os.read(fd,1)
	return getInt2(fd)

def getLong2(fd):
	return struct.unpack(">q",os.read(fd,8))[0]

def getLong(fd):
	os.read(fd,1)
	return getLong2(fd)

def getDouble2(fd):
	return struct.unpack(">d",os.read(fd,8))[0]

def getDouble(fd):
	os.read(fd,1)
	return getDouble2(fd)

def getString2(fd):
	L = getInt2(fd)
	res = ''
	while L > 0:
		s = os.read(fd,L)
		res = res + s
		L = L - len(s)

	return res

def getString(fd):
	os.read(fd,1)
	return getString2(fd)

def getIntArray(fd):
	os.read(fd,1)
	len = getInt2(fd)
	ret = []
	i = 0
	while( i < len ):
		ret.append(getInt2(fd))
		i = i + 1
	return ret
		
def getLongArray(fd):
	os.read(fd,1)
	len = getInt2(fd)
	ret = []
	i = 0
	while( i < len ):
		ret.append(getLong2(fd))
		i = i + 1
	return ret
		
		
def getDoubleArray(fd):
	os.read(fd,1)
	len = getInt2(fd)
	ret = []
	i = 0
	while( i < len ):
		ret.append(getDouble2(fd))
		i = i + 1
	return ret
		
		
def getStringArray(fd):
	os.read(fd,1)
	len = getInt2(fd)
	ret = []
	i = 0
	while( i < len ):
		ret.append(getString2(fd))
		i = i + 1
	return ret
		
		

getchBuffer = ''
getchPos = -1
getchBufferSize = 0
totalTm = 0

def getch(fd):
	global getchPos
	global getchBuffer
	
	if(getchPos >= len(getchBuffer)):
		getchPos = -1
		
	if(getchPos == -1):
		getchBuffer = os.read(fd,1000000)
		getchPos = 0
		
	ret = getchBuffer[getchPos]
	getchPos = getchPos + 1
	
	return ret

def gettime():
	r = time.time()
	r = r * 1000
	
	return long(r)
		
#wrapper starts here

def gettimefloat():
	r = time.time()
	r = r * 1000
	
	return r

def main():
	#global totalTm
	
	initialized = False
	
	fd_out = 12
	fd_in = 11
	
	mod = ""
	sol = ""

	timercnt = 0
	
	mypid = os.getpid()
	while 1:
		
		command = ord(os.read(fd_in,1))
		#print command
		
		if(command == TERMINATE or command == -1):
			sys.exit(0)
		elif(command == TIMEOUT):
			getInt2(fd_in)
		elif(command == METHOD_START):
			method = getInt2(fd_in)
			if(method == -1):
				pass
			<METHODS>
			elif(method == <METHOD_NUMBER>):
				#tm2 = gettime()
				
				<ARGS>
				<ARG_NAME> = <ARG_METHOD_NAME>(fd_in)
				</ARGS>
								
				#print totalTm
				
				os.kill(mypid, SIG_PROF)
				
				tm = gettime()
				timercnt = timercnt + 1
				
				if(not initialized):
					mod = __import__('<CLASS_NAME>')
					sol = mod.<CLASS_NAME>()
					initialized = True
	
				ret = sol.<METHOD_NAME>(<PARAMS>)
								
				tm = gettime() - tm - <EXPOSED_WRAPPER_CLASS>.watch.getTime()
				timercnt = timercnt + 1
						
				if(tm > 0 and timercnt >= 50):
					tm = tm - 1
					timercnt = timercnt - 50
		
				os.kill(mypid, SIG_PROF)
				
				writeTime(fd_out, tm)
				writeArg(fd_out, 1)
				<RET_METHOD_NAME>(fd_out, ret)
				flush(fd_out)
				
				sys.__stdout__.flush()
				sys.__stderr__.flush()
				
				
			</METHODS>


if __name__ == "__main__":
    main()
