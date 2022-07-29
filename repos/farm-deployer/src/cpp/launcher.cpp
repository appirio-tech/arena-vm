
#define WIN32_LEAN_AND_MEAN		// Exclude rarely-used stuff from Windows headers
#define _WIN32_WINNT 0x501
#include <windows.h>
#include <stdio.h>
#include <tchar.h>
#include <malloc.h>

// Maximun number of process spected in a job
#define MAX_PROC 20;

void printError( char* msg );
void printUsage();
int waitForJob(HANDLE hJob);
int waitProcesses(ULONG_PTR* processes, int procCnt);

int _tmain(int argc, _TCHAR* argv[])
{
	if (argc < 3)  {
		printUsage();
		return -1;
	}

	TCHAR jobName[24000];
	_tcscpy(jobName, _T("Global\\"));
	_tcscat(jobName, argv[2]);

	TCHAR cmdLine[24000];
	_tcscpy(cmdLine, argv[2]);
	for (int i = 3; i < argc; i++) {
		_tcscat(cmdLine, _T(" "));
		_tcscat(cmdLine, argv[i]);
	}

	STARTUPINFO si;
    PROCESS_INFORMATION pi;

    ZeroMemory( &si, sizeof(si) );
    si.cb = sizeof(si);
    ZeroMemory( &pi, sizeof(pi) );

	_tprintf(_T("Creating job %s\n"), jobName);
	HANDLE hJob = CreateJobObject(NULL, jobName);
	_tprintf(_T("Creating suspended process : %s\n"), cmdLine);


	if (!CreateProcess(NULL, cmdLine, NULL, NULL, TRUE, CREATE_SUSPENDED, NULL, NULL, &si, &pi)) {
		printError("CreateProcess");
		CloseHandle(hJob);
        return -2;
	}
	printf("Process PID=%d created. Assigning to job.\n", pi.dwProcessId);

	if (!AssignProcessToJobObject(hJob, pi.hProcess)) {
		printError("AssignProcessToJobObject");
		CloseHandle(pi.hThread);
		CloseHandle(pi.hProcess);
		CloseHandle(hJob);
        return -3;
	}

	printf("Resuming process PID=%d\n", pi.dwProcessId);

	ResumeThread(pi.hThread);
	CloseHandle(pi.hThread);
	CloseHandle(pi.hProcess);

	printf("Waiting for job finish.\n");
	int result = waitForJob(hJob);
	printf("Job finished. Exiting.\n");
	CloseHandle(hJob);
	return 0;
}

/*
 * Wait for all processes in job
 */
int waitForJob(HANDLE hJob) {
	int tries = 0;
	int size = sizeof(JOBOBJECT_BASIC_PROCESS_ID_LIST)+sizeof(ULONG_PTR)*MAX_PROC;
	JOBOBJECT_BASIC_PROCESS_ID_LIST  *procList = (JOBOBJECT_BASIC_PROCESS_ID_LIST*)_malloca(size);

	while (tries < 3) {
		Sleep(100);

		procList->NumberOfAssignedProcesses = MAX_PROC;
		if (!QueryInformationJobObject(hJob, JobObjectBasicProcessIdList, procList, size, NULL)) {
			printError("QueryInformationJobObject");
			return -1;
		}
		int processes = waitProcesses(procList->ProcessIdList, procList->NumberOfProcessIdsInList);
		if (processes == 0) {
			tries ++;
			continue;
		} if (processes > 0) {
			tries = 0;
		} else {
			return processes;
		}
	}
	return 0;
}

/*
 * Waits for all processes id in the array.
 * Returns the number of processes waited or a negative value indicating an error code
 */
int waitProcesses(ULONG_PTR* processes, int procCnt) {
	HANDLE* handlesToWait = (HANDLE *)_malloca(sizeof(HANDLE)*procCnt);
	int handlesCount = 0;

	for (int i = 0; i < procCnt; i++) {
		HANDLE hProcess = OpenProcess( PROCESS_ALL_ACCESS, FALSE, processes[i] );
		if( hProcess != NULL )
			handlesToWait[handlesCount++] = hProcess;
	}

	if (handlesCount == 0) {
		return 0;
	}

	int result = handlesCount;
	if (WaitForMultipleObjects(handlesCount, handlesToWait, TRUE, INFINITE) == -1) {
		printError("WaitForMultipleObjects");
		result = -5;
	}

	for(int i=0; i < handlesCount; i++) {
		CloseHandle( handlesToWait[i] );
	}
	return result;
}

void printError( char* msg ) {
  DWORD eNum;
  TCHAR sysMsg[256];
  TCHAR* p;

  eNum = GetLastError( );
  FormatMessage( FORMAT_MESSAGE_FROM_SYSTEM | FORMAT_MESSAGE_IGNORE_INSERTS,
         NULL, eNum,
         MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), // Default language
         sysMsg, 256, NULL );

  // Trim the end of the line and terminate it with a null
  p = sysMsg;
  while( ( *p > 31 ) || ( *p == 9 ) )
    ++p;
  do { *p-- = 0; } while( ( p >= sysMsg ) &&
                          ( ( *p == '.' ) || ( *p < 33 ) ) );

  // Display the message
  printf( "\n WARNING: %s failed with error %d (%s)", msg, eNum, sysMsg );
}

void printUsage() {
	printf(" LAUNCHER launchs an application, associates it with a job and waits for job completion.\n");
	printf(" Usage: launcher jobname command line]\n");
	printf(" example: launcher mytesjob java -cp common.jar com.test.Test\n");
}