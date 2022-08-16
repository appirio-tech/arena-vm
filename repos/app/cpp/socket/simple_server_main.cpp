#include "ServerSocket.h"
#include "SocketException.h"
#include <string>
#include <iostream>
#include <fstream>

int main ( int argc, int argv[] )
{
  std::cout << "running....\n";

  try
    {
      // Create the socket
      ServerSocket server ( 30000 );

      while ( true )
    	{
          ofstream file ("output.txt");
    	  ServerSocket new_sock;
    	  server.accept ( new_sock );
    
    	  try
    	    {
        		  std::string data;
        		  new_sock >> data;
        		  file << data << "\n";
    	    }
    	  catch ( SocketException& ) {}
          file.close();
    	}
    }
  catch ( SocketException& e )
    {
      std::cout << "Exception was caught:" << e.description() << "\nExiting.\n";
    }

  return 0;
}
