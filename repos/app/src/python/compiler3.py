# Copyright (C) - 2022 TopCoder Inc., All Rights Reserved.

"""
Compiler for Python3 solution.

@author liuliquan
@version 1.0
"""

import sys
import py_compile

def main(argv):
	try:
		f = readfile(argv[0])
		compile(f, argv[1], "exec")
		py_compile.compile(argv[0], argv[0] + "c", argv[1] + ".py", True)
	except Exception as e:
		print("%s on line %d col %d\n%s" % (e.msg, e.lineno, e.offset, e.text))
		sys.exit(1)
	sys.exit(0)

def readfile(file):
	ret = ""
	fileobj = open(file, 'r')
	while 1:
		lineStr = fileobj.readline()
		if not(lineStr):
			break;
		ret += lineStr + "\n"
	
	return ret

main(sys.argv[1:])
