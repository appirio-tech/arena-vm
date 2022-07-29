#include "ClientSocket.h"
#include "SocketException.h"
#include <iostream>
#include <string>
#include <fstream>
using namespace std;

int main ( int argc, int argv[] )
{
  try
    {

      ClientSocket client_socket ( "localhost", 30000 );

      string line;
      ifstream infile("input.txt");

      try
	{
      while (!infile.eof()) {
        getline(infile, line);
        client_socket << line;
      }
	}
      catch ( SocketException& ) {}


    }
  catch ( SocketException& e )
    {
      std::cout << "Exception was caught:" << e.description() << "\n";
    }

  return 0;
}
