#################################################################
#
#  Copyright Jeff Kotula
#
#
#  This Makefile is real simple and straightforward.  Modify as needed
#  for your platform and preferences.  All you need to do is "make all".
#  There are other targets included that I use during development work,
#  but they probably won't be of any use to you.  I apologize for the
#  the inelegant way multiple platforms are supported.  If any of you
#  make-gurus out there can e-mail me a better Makefile, I'll certainly
#  adopt it when time allows...
#
#  If your compiler doesn't support exceptions, make sure NOEXCEPTIONS
#  is defined.  If you are compiling on a Windows system, make sure
#  WINDOWS is defined.
#

#
#  defaults
O = o
OFLAG = -o  
OBJFLAG = -c
EXE = 

#################################################################
#
#			COMPILATION FLAGS
#
#################################################################
#
# HP Specific Flags---
#   Uncomment the following lines to build on HP
#
#CCFLAGS = +eh -g
#CC = CC


#
# Linux Specific Flags---
# Linux 1.3.1 i586 (cc version 2.7.0)
#	Uncomment the followin lines to build on Linux
#
CCFLAGS = -DNOEXCEPTIONS
CC = g++


#
# SCO Specific Flags--
#
#CCFLAGS = +.cpp -DNOEXCEPTIONS -g
#CC=CC


#
# SGI Delta compiler Specific Flags---
#
#CCFLAGS = -exceptions -g
#CC = NCC


#
# SGI Specific Flags---
#   Uncomment the following lines to build on SGI or another machine that
#   doesn't support exceptions
#
#CCFLAGS = -DNOEXCEPTIONS -g
#CC = CC


#
# Sun Specific Flags --
# Added by Terry Triplett, 12/14/95
#   Uncomment the following lines to build on Sparc 2 using g++
#
#CCFLAGS = -fhandle-exceptions -g
#CC = g++
#OFLAG = -o ./
#OBJFLAG = -c


#
# Windows NT/Visual C++ Specific Flags --
#   Uncomment the following lines to build on Windows NT using
#   the Visual C++ compiler
#
#CCFLAGS = /DWINDOWS /GX
#CC = cl
#OFLAG = /Fe
#OBJFLAG = /c
#O = obj


#
# OS2 -- Watcom C/C++ 10.5a and  Warp 4 was used.  Other versions of OS/2
# should also work, but haven't been tested.
#
#CCFLAGS = -DOS2 -w2 -xs -cc++
#CC = wcl386
#OFLAG =
#OBJFLAG = -c
#O = obj


#
# Microsoft Developer Studio Specific Flags --
#   Uncomment the following lines to build using the Microsoft
#   Developer Studio.  Need to make sure the path to the system
#   include files is correct.  Also need to set the LIB environment
#   variable to c:\msdev\lib
#
#CCFLAGS = /nologo /DWINDOWS /GX /IC:\MSDEV\INCLUDE
#CCFLAGS = /Zi /Od /nologo /DWINDOWS /GX /IC:\MSDEV\INCLUDE
#CC = cl
#OFLAG = /Zi /Od /ML /Fe
#OBJFLAG = /c
#O = obj
#EXE = .exe


#################################################################
# 
#  			TARGETS
#
#################################################################

all:	cobweb$(EXE) keyweb$(EXE) anchor$(EXE) lineage$(EXE) cocoon$(EXE) \
	recomb$(EXE) relativize$(EXE)

clean:
#	rm -f *.$(O) cobweb keyweb anchor lineage cocoon recomb relativize
	rm -f *.$(O) *.exe *.pdb *.ilk *~

util.$(O):	util.cpp util.h sys.h
	$(CC) $(CCFLAGS) $(OBJFLAG) util.cpp

sys.$(O):	sys.cpp sys.h util.h
	$(CC) $(CCFLAGS) $(OBJFLAG) sys.cpp

handlers.$(O):	handlers.cpp cobweb.h util.h sys.h
	$(CC) $(CCFLAGS) $(OBJFLAG) handlers.cpp

custom.$(O):	custom.cpp  util.h
	$(CC) $(CCFLAGS) $(OBJFLAG) custom.cpp

cobweb$(EXE): cobweb.cpp cobweb.h util.$(O) custom.$(O) handlers.$(O) sys.$(O)
	$(CC) $(CCFLAGS) $(OFLAG)cobweb cobweb.cpp util.$(O) custom.$(O) \
			handlers.$(O) sys.$(O)

keyweb$(EXE): keyweb.cpp util.$(O) custom.$(O) sys.h
	$(CC) $(CCFLAGS) $(OFLAG)keyweb keyweb.cpp util.$(O) custom.$(O) \
			sys.$(O)

lineage$(EXE): lineage.cpp util.$(O) custom.$(O)
	$(CC) $(CCFLAGS) $(OFLAG)lineage lineage.cpp util.$(O) custom.$(O) \
			sys.$(O)

cocoon$(EXE): cocoon.cpp cocoon.h util.$(O) sys.$(O) custom.$(O)
	$(CC) $(CCFLAGS) $(OFLAG)cocoon cocoon.cpp util.$(O) sys.$(O) \
			custom.$(O)

anchor$(EXE): anchor.cpp util.$(O) custom.$(O) sys.$(O)
	$(CC) $(CCFLAGS) $(OFLAG)anchor anchor.cpp util.$(O) \
			custom.$(O) sys.$(O)

recomb$(EXE): recomb.cpp util.$(O) custom.$(O)
	$(CC) $(CCFLAGS) $(OFLAG)recomb recomb.cpp util.$(O) \
			custom.$(O) sys.$(O)

relativize$(EXE): relativize.cpp util.$(O) custom.$(O)
	$(CC) $(CCFLAGS) $(OFLAG)relativize relativize.cpp util.$(O) \
			custom.$(O) sys.$(O)


#################################################################
# 
#	TARGETS for use during DEVELOPMENT
#
#################################################################

update: ginstall betadist docsamples

dist:
	rm -f cocoon.tar.gz
	tar cvf cocoon.tar *.cpp *.h *.pl *.gif *.htm Makefile README
	gzip -c < cocoon.tar > cocoon.tar.gz
	rm cocoon.tar
	cp cocoon.tar.gz doc
	rm -f cocoon.zip
	/pkzip/pkzip25 -add cocoon.zip *.cpp *.h Makefile README
	cp cocoon.zip doc

sswork: all
	cp *.exe        c:/sswork/bin
	cp srchform.htm c:/sswork/bin
	cp srchkey.pl   c:/sswork/bin
	cp coclogo.gif  c:/sswork/bin

docsamples: all
	rm -r doc/vanilla
	rm -r doc/flavored
	cocoon -x vanilla.set
	cocoon flavored.set
	cp vanilla.set doc/vanilla
	cp flavored.set doc/flavored

backup:
	cp Makefile bup
	cp README bup
	cp *.cpp bup
	cp *.h bup
	cp vanilla.set bup
	cp flavored.set bup
	cp srchform.htm bup
	cp srchkey.pl bup
	cp coclogo.gif bup

