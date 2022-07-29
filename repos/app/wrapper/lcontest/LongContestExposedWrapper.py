import os
import sys
import time
import <WRAPPER_CLASS>

class Stopwatch:
	def __init__(self):
		self.time = 0
		self.startTime = 0
			
	def reset(self):
		self.time = 0
		self.startTime = 0
	
	def start(self):
		self.startTime = <WRAPPER_CLASS>.gettime()
	
	def stop(self):
		end = <WRAPPER_CLASS>.gettime()
		self.time = self.time + (end - self.startTime)
		self.startTime = 0
		
	def getTime(self):
		return self.time

exposed_fd = 13
SIGXFSZ = 25

watch = Stopwatch()

<EXPOSED_METHODS>
def <METHOD_NAME>(<PARAMS>):
	watch.start()
	
	os.kill(os.getpid(), SIGXFSZ)

	<WRAPPER_CLASS>.startMethod(exposed_fd, <METHOD_NUMBER>)
	
	<WRITE_ARGS>
	<WRAPPER_CLASS>.<ARG_METHOD>(exposed_fd, <ARG_NAME>)
	</WRITE_ARGS>
	<WRAPPER_CLASS>.flush(exposed_fd)
	
	val = <WRAPPER_CLASS>.<RETURN_METHOD_NAME>(exposed_fd)
	
	os.kill(os.getpid(), SIGXFSZ)
	watch.stop()
	
	return val
</EXPOSED_METHODS>
