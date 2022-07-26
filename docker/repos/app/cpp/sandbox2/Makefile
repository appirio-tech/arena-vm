## -*- Makefile -*-
##
## User: rfairfax
## Time: Jul 6, 2006 4:41:31 PM
## Makefile created by Sun Studio.
##
## This file is generated automatically.
##


#### Compiler and tool definitions shared by all build targets #####
CC = gcc
CCC = g++
CXX = g++
## OPTIONS
## DEBUG : Full Debug
## TIMED : Time system calls and report long calls
## PTRACE_PATCH: When the sandbox will be used in a patched version of the kernel (No tracing syscall exit)
BASICOPTS = -O2
CFLAGS = $(BASICOPTS)
CCFLAGS = $(BASICOPTS)
CXXFLAGS = $(BASICOPTS)
CCADMIN = 
CCFLAGS_sandbox2 = -fPIC
LDLIBS_all = -lpthread
LDLIBS_sandbox2 = -ldl


# Define the target directories.
TARGETDIR_sandbox2=GNU-i386-Linux


all: $(TARGETDIR_sandbox2)/sandbox2 \
	$(TARGETDIR_sandbox2)/SRMInitializer.so \
	$(TARGETDIR_sandbox2)/SharedInitializer.so \
	$(TARGETDIR_sandbox2)/SharedSecurityChecker.so \
	$(TARGETDIR_sandbox2)/SRMSecurityChecker.so \
	$(TARGETDIR_sandbox2)/IntelSecurityChecker.so \
	$(TARGETDIR_sandbox2)/PythonSecurityChecker.so \
	$(TARGETDIR_sandbox2)/RLanguageSecurityChecker.so \
	$(TARGETDIR_sandbox2)/LongInitializer.so \
	$(TARGETDIR_sandbox2)/MTLongInitializer.so

## Target: sandbox2
OBJS_sandbox2 =  \
	$(TARGETDIR_sandbox2)/main.o \
	$(TARGETDIR_sandbox2)/configuration.o \
	$(TARGETDIR_sandbox2)/initializer.o \
	$(TARGETDIR_sandbox2)/util.o \
	$(TARGETDIR_sandbox2)/SecurityChecker.o \
	$(TARGETDIR_sandbox2)/mutex.o \
	$(TARGETDIR_sandbox2)/cond.o \
	$(TARGETDIR_sandbox2)/logger.o

OBJS_SRMInitializer.so =  \
	$(TARGETDIR_sandbox2)/initializer.o \
	$(TARGETDIR_sandbox2)/configuration.o \
	$(TARGETDIR_sandbox2)/SRMInitializer.o \
	$(TARGETDIR_sandbox2)/mutex.o \
	$(TARGETDIR_sandbox2)/logger.o

OBJS_LongInitializer.so =  \
	$(TARGETDIR_sandbox2)/initializer.o \
	$(TARGETDIR_sandbox2)/configuration.o \
	$(TARGETDIR_sandbox2)/LongInitializer.o \
	$(TARGETDIR_sandbox2)/mutex.o \
	$(TARGETDIR_sandbox2)/logger.o

OBJS_MTLongInitializer.so = \
	$(TARGETDIR_sandbox2)/initializer.o \
	$(TARGETDIR_sandbox2)/configuration.o \
	$(TARGETDIR_sandbox2)/MTLongInitializer.o \
	$(TARGETDIR_sandbox2)/mutex.o \
	$(TARGETDIR_sandbox2)/logger.o

OBJS_SharedInitializer.so =  \
	$(TARGETDIR_sandbox2)/initializer.o \
	$(TARGETDIR_sandbox2)/configuration.o \
	$(TARGETDIR_sandbox2)/SharedInitializer.o \
	$(TARGETDIR_sandbox2)/mutex.o \
	$(TARGETDIR_sandbox2)/logger.o

OBJS_SharedSecurityChecker.so =  \
	$(TARGETDIR_sandbox2)/SecurityChecker.o \
	$(TARGETDIR_sandbox2)/configuration.o \
	$(TARGETDIR_sandbox2)/SharedSecurityChecker.o \
	$(TARGETDIR_sandbox2)/util.o \
	$(TARGETDIR_sandbox2)/mutex.o \
	$(TARGETDIR_sandbox2)/logger.o

OBJS_SRMSecurityChecker.so =  \
	$(TARGETDIR_sandbox2)/SecurityChecker.o \
	$(TARGETDIR_sandbox2)/configuration.o \
	$(TARGETDIR_sandbox2)/SRMSecurityChecker.o \
	$(TARGETDIR_sandbox2)/util.o \
	$(TARGETDIR_sandbox2)/mutex.o \
	$(TARGETDIR_sandbox2)/logger.o

OBJS_IntelSecurityChecker.so =  \
	$(TARGETDIR_sandbox2)/SecurityChecker.o \
	$(TARGETDIR_sandbox2)/configuration.o \
	$(TARGETDIR_sandbox2)/IntelSecurityChecker.o \
	$(TARGETDIR_sandbox2)/util.o \
	$(TARGETDIR_sandbox2)/mutex.o \
	$(TARGETDIR_sandbox2)/logger.o

OBJS_PythonSecurityChecker.so =  \
	$(TARGETDIR_sandbox2)/SecurityChecker.o \
	$(TARGETDIR_sandbox2)/configuration.o \
	$(TARGETDIR_sandbox2)/PythonSecurityChecker.o \
	$(TARGETDIR_sandbox2)/util.o \
	$(TARGETDIR_sandbox2)/mutex.o \
	$(TARGETDIR_sandbox2)/logger.o

OBJS_RLanguageSecurityChecker.so =  \
	$(TARGETDIR_sandbox2)/SecurityChecker.o \
	$(TARGETDIR_sandbox2)/configuration.o \
	$(TARGETDIR_sandbox2)/RLanguageSecurityChecker.o \
	$(TARGETDIR_sandbox2)/util.o \
	$(TARGETDIR_sandbox2)/mutex.o \
	$(TARGETDIR_sandbox2)/logger.o
# Link or archive
$(TARGETDIR_sandbox2)/sandbox2: $(TARGETDIR_sandbox2) $(OBJS_sandbox2)
	$(LINK.cc) $(CCFLAGS_sandbox2) $(CPPFLAGS_sandbox2) -o $@ $(OBJS_sandbox2) ${LDLIBS_all} $(LDLIBS_sandbox2)

$(TARGETDIR_sandbox2)/SRMInitializer.so: $(TARGETDIR_sandbox2) $(OBJS_SRMInitializer.so)
	$(LINK.cc) $(CCFLAGS_sandbox2) $(CPPFLAGS_SRMInitializer.so) -shared -o $@ $(OBJS_SRMInitializer.so) ${LDLIBS_all} $(LDLIBS_SRMInitializer.so)

$(TARGETDIR_sandbox2)/SharedInitializer.so: $(TARGETDIR_sandbox2) $(OBJS_SharedInitializer.so)
	$(LINK.cc) $(CCFLAGS_sandbox2) $(CPPFLAGS_SharedInitializer.so) -shared -o $@ $(OBJS_SharedInitializer.so) ${LDLIBS_all} $(LDLIBS_SharedInitializer.so)

$(TARGETDIR_sandbox2)/LongInitializer.so: $(TARGETDIR_sandbox2) $(OBJS_LongInitializer.so)
	$(LINK.cc) $(CCFLAGS_sandbox2) $(CPPFLAGS_LongInitializer.so) -shared -o $@ $(OBJS_LongInitializer.so) ${LDLIBS_all} $(LDLIBS_LongInitializer.so)

$(TARGETDIR_sandbox2)/MTLongInitializer.so: $(TARGETDIR_sandbox2) $(OBJS_MTLongInitializer.so)
	$(LINK.cc) $(CCFLAGS_sandbox2) $(CPPFLAGS_MTLongInitializer.so) -shared -o $@ $(OBJS_MTLongInitializer.so) ${LDLIBS_all} $(LDLIBS_MTLongInitializer.so)

$(TARGETDIR_sandbox2)/SharedSecurityChecker.so: $(TARGETDIR_sandbox2) $(OBJS_SharedSecurityChecker.so)
	$(LINK.cc) $(CCFLAGS_sandbox2) $(CPPFLAGS_SharedSecurityChecker.so) -shared -o $@ $(OBJS_SharedSecurityChecker.so) ${LDLIBS_all} $(LDLIBS_SharedSecurityChecker.so)

$(TARGETDIR_sandbox2)/SRMSecurityChecker.so: $(TARGETDIR_sandbox2) $(OBJS_SRMSecurityChecker.so)
	$(LINK.cc) $(CCFLAGS_sandbox2) $(CPPFLAGS_SRMSecurityChecker.so) -shared -o $@ $(OBJS_SRMSecurityChecker.so) ${LDLIBS_all} $(LDLIBS_SRMSecurityChecker.so)

$(TARGETDIR_sandbox2)/IntelSecurityChecker.so: $(TARGETDIR_sandbox2) $(OBJS_IntelSecurityChecker.so)
	$(LINK.cc) $(CCFLAGS_sandbox2) $(CPPFLAGS_IntelSecurityChecker.so) -shared -o $@ $(OBJS_IntelSecurityChecker.so) ${LDLIBS_all} $(LDLIBS_IntelSecurityChecker.so)

$(TARGETDIR_sandbox2)/PythonSecurityChecker.so: $(TARGETDIR_sandbox2) $(OBJS_PythonSecurityChecker.so)
	$(LINK.cc) $(CCFLAGS_sandbox2) $(CPPFLAGS_PythonSecurityChecker.so) -shared -o $@ $(OBJS_PythonSecurityChecker.so) ${LDLIBS_all} $(LDLIBS_PythonSecurityChecker.so)

$(TARGETDIR_sandbox2)/RLanguageSecurityChecker.so: $(TARGETDIR_sandbox2) $(OBJS_RLanguageSecurityChecker.so)
	$(LINK.cc) $(CCFLAGS_sandbox2) $(CPPFLAGS_RLanguageSecurityChecker.so) -shared -o $@ $(OBJS_RLanguageSecurityChecker.so) ${LDLIBS_all} $(LDLIBS_RLanguageSecurityChecker.so)


# Compile source files into .o files
$(TARGETDIR_sandbox2)/main.o: $(TARGETDIR_sandbox2) main.cc
	$(COMPILE.cc) $(CCFLAGS_sandbox2) $(CPPFLAGS_sandbox2) -o $@ main.cc

$(TARGETDIR_sandbox2)/configuration.o: $(TARGETDIR_sandbox2) config/configuration.cc
	$(COMPILE.cc) $(CCFLAGS_sandbox2) $(CPPFLAGS_sandbox2) -o $@ config/configuration.cc

$(TARGETDIR_sandbox2)/mutex.o: $(TARGETDIR_sandbox2) mutex.cc
	$(COMPILE.cc) $(CCFLAGS_sandbox2) $(CPPFLAGS_sandbox2) -o $@ mutex.cc

$(TARGETDIR_sandbox2)/cond.o: $(TARGETDIR_sandbox2) cond.cc
	$(COMPILE.cc) $(CCFLAGS_sandbox2) $(CPPFLAGS_sandbox2) -o $@ cond.cc

$(TARGETDIR_sandbox2)/logger.o: $(TARGETDIR_sandbox2) logger.cc
	$(COMPILE.cc) $(CCFLAGS_sandbox2) $(CPPFLAGS_sandbox2) -o $@ logger.cc

$(TARGETDIR_sandbox2)/initializer.o: $(TARGETDIR_sandbox2) initializer/initializer.cc
	$(COMPILE.cc) $(CCFLAGS_sandbox2) $(CPPFLAGS_sandbox2) -o $@ initializer/initializer.cc

$(TARGETDIR_sandbox2)/SRMInitializer.o: $(TARGETDIR_sandbox2) initializer/SRMInitializer.cc
	$(COMPILE.cc) $(CCFLAGS_sandbox2) $(CPPFLAGS_sandbox2) -o $@ initializer/SRMInitializer.cc

$(TARGETDIR_sandbox2)/SharedInitializer.o: $(TARGETDIR_sandbox2) initializer/SharedInitializer.cc
	$(COMPILE.cc) $(CCFLAGS_sandbox2) $(CPPFLAGS_sandbox2) -o $@ initializer/SharedInitializer.cc

$(TARGETDIR_sandbox2)/LongInitializer.o: $(TARGETDIR_sandbox2) initializer/LongInitializer.cc
	$(COMPILE.cc) $(CCFLAGS_sandbox2) $(CPPFLAGS_sandbox2) -o $@ initializer/LongInitializer.cc

$(TARGETDIR_sandbox2)/MTLongInitializer.o: $(TARGETDIR_sandbox2) initializer/MTLongInitializer.cc
	$(COMPILE.cc) $(CCFLAGS_sandbox2) $(CPPFLAGS_sandbox2) -o $@ initializer/MTLongInitializer.cc

$(TARGETDIR_sandbox2)/util.o: $(TARGETDIR_sandbox2) util.cc
	$(COMPILE.cc) $(CCFLAGS_sandbox2) $(CPPFLAGS_sandbox2) -o $@ util.cc

$(TARGETDIR_sandbox2)/SecurityChecker.o: $(TARGETDIR_sandbox2) security/SecurityChecker.cc
	$(COMPILE.cc) $(CCFLAGS_sandbox2) $(CPPFLAGS_sandbox2) -o $@ security/SecurityChecker.cc

$(TARGETDIR_sandbox2)/SharedSecurityChecker.o: $(TARGETDIR_sandbox2) security/SharedSecurityChecker.cc
	$(COMPILE.cc) $(CCFLAGS_sandbox2) $(CPPFLAGS_sandbox2) -o $@ security/SharedSecurityChecker.cc

$(TARGETDIR_sandbox2)/SRMSecurityChecker.o: $(TARGETDIR_sandbox2) security/SRMSecurityChecker.cc
	$(COMPILE.cc) $(CCFLAGS_sandbox2) $(CPPFLAGS_sandbox2) -o $@ security/SRMSecurityChecker.cc

$(TARGETDIR_sandbox2)/IntelSecurityChecker.o: $(TARGETDIR_sandbox2) security/IntelSecurityChecker.cc
	$(COMPILE.cc) $(CCFLAGS_sandbox2) $(CPPFLAGS_sandbox2) -o $@ security/IntelSecurityChecker.cc

$(TARGETDIR_sandbox2)/PythonSecurityChecker.o: $(TARGETDIR_sandbox2) security/PythonSecurityChecker.cc
	$(COMPILE.cc) $(CCFLAGS_sandbox2) $(CPPFLAGS_sandbox2) -o $@ security/PythonSecurityChecker.cc

$(TARGETDIR_sandbox2)/RLanguageSecurityChecker.o: $(TARGETDIR_sandbox2) security/RLanguageSecurityChecker.cc
	$(COMPILE.cc) $(CCFLAGS_sandbox2) $(CPPFLAGS_sandbox2) -o $@ security/RLanguageSecurityChecker.cc
#### Clean target deletes all generated files ####
clean:
	rm -f \
		$(TARGETDIR_sandbox2)/sandbox2 \
		$(TARGETDIR_sandbox2)/main.o \
		$(TARGETDIR_sandbox2)/configuration.o \
		$(TARGETDIR_sandbox2)/initializer.o \
		$(TARGETDIR_sandbox2)/SRMInitializer.o \
		$(TARGETDIR_sandbox2)/SRMInitializer.so \
		$(TARGETDIR_sandbox2)/SharedInitializer.o \
		$(TARGETDIR_sandbox2)/SharedInitializer.so \
		$(TARGETDIR_sandbox2)/LongInitializer.o \
		$(TARGETDIR_sandbox2)/LongInitializer.so \
		$(TARGETDIR_sandbox2)/MTLongInitializer.o \
		$(TARGETDIR_sandbox2)/MTLongInitializer.so \
		$(TARGETDIR_sandbox2)/util.o \
		$(TARGETDIR_sandbox2)/mutex.o \
		$(TARGETDIR_sandbox2)/logger.o \
		$(TARGETDIR_sandbox2)/SecurityChecker.o \
		$(TARGETDIR_sandbox2)/SharedSecurityChecker.o \
		$(TARGETDIR_sandbox2)/SharedSecurityChecker.so \
		$(TARGETDIR_sandbox2)/SRMSecurityChecker.o \
		$(TARGETDIR_sandbox2)/SRMSecurityChecker.so \
		$(TARGETDIR_sandbox2)/IntelSecurityChecker.o \
		$(TARGETDIR_sandbox2)/IntelSecurityChecker.so \
		$(TARGETDIR_sandbox2)/PythonSecurityChecker.o \
		$(TARGETDIR_sandbox2)/PythonSecurityChecker.so \
		$(TARGETDIR_sandbox2)/RLanguageSecurityChecker.o \
		$(TARGETDIR_sandbox2)/RLanguageSecurityChecker.so
	$(CCADMIN)
	rm -f -r $(TARGETDIR_sandbox2)


# Create the target directory (if needed)
$(TARGETDIR_sandbox2):
	mkdir -p $(TARGETDIR_sandbox2)


# Enable dependency checking
.KEEP_STATE:
.KEEP_STATE_FILE:.make.state.GNU-i386-Linux

