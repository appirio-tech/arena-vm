all: sandbox

sandbox: sandbox.C
	g++ -o sandbox sandbox.C

clean: sandbox
	rm sandbox

doc: util.h print.h safe.h syscalls.h documentation.h
	cocoon -p /usr/local/bin/ config
