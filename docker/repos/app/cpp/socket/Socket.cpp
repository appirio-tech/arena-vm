// Implementation of the Socket class.


#include "Socket.h"
#include "string.h"
#include <string.h>
#include <errno.h>
#include <fcntl.h>
#include <stdio.h>


Socket::Socket() :
  m_sock ( -1 )
{

  memset ( &m_addr,
	   0,
	   sizeof ( m_addr ) );

}

Socket::~Socket()
{
  if ( is_valid() )
    ::close ( m_sock );
}

bool Socket::create()
{
  m_sock = socket ( AF_INET,
		    SOCK_STREAM,
		    0 );

  if ( ! is_valid() )
    return false;


  // TIME_WAIT - argh
  int on = 1;
  if ( setsockopt ( m_sock, SOL_SOCKET, SO_REUSEADDR, ( const char* ) &on, sizeof ( on ) ) == -1 )
    return false;


  return true;

}



bool Socket::bind ( const int port )
{

  if ( ! is_valid() )
    {
      return false;
    }



  m_addr.sin_family = AF_INET;
  m_addr.sin_addr.s_addr = INADDR_ANY;
  m_addr.sin_port = htons ( port );

  int bind_return = ::bind ( m_sock,
			     ( struct sockaddr * ) &m_addr,
			     sizeof ( m_addr ) );


  if ( bind_return == -1 )
    {
      return false;
    }

  return true;
}


bool Socket::listen() const
{
  if ( ! is_valid() )
    {
      return false;
    }

  int listen_return = ::listen ( m_sock, MAXCONNECTIONS );


  if ( listen_return == -1 )
    {
      return false;
    }

  return true;
}


bool Socket::accept ( Socket& new_socket ) const
{
  int addr_length = sizeof ( m_addr );
  new_socket.m_sock = ::accept ( m_sock, ( sockaddr * ) &m_addr, ( socklen_t * ) &addr_length );

  if ( new_socket.m_sock <= 0 )
    return false;
  else
    return true;
}

int min(int a, int b) {
    if (a<b) return a;
    return b;
}

bool Socket::send ( const std::string s ) const
{
  int status;
  int offset = 0;
  int count = 0;
  
  do {    
    int sendSize = min(MAXRECV, s.size() - count);
    std::cout << "waiting to send ... ";
    fflush(stdout);
    status = ::send ( m_sock, s.c_str() + offset, sendSize, /*MSG_NOSIGNAL*/ 0 );
    if ( status == -1 )
      {
        return false;
      }
    std::cout << "sent " << status << " bytes\n";
    count += sendSize;
    offset += sendSize;
  } while(status > 0);
  std::cout << "done: sent " << count << " bytes total\n";
  ::send ( m_sock, s.c_str() + count, 1, 0);  // \0 character
  return true;
}


int Socket::recv ( std::string& s ) const
{
  char buf [ MAXRECV + 1 ];

  s = "";

  memset ( buf, 0, MAXRECV + 1 );

  int status;
  int count = 0;
  
  do {
      std::cout << "waiting to receive ... ";
      fflush(stdout);
      status = ::recv ( m_sock, buf, MAXRECV, 0 );
      if ( status == -1 )
        {
          std::cout << "status == -1   errno == " << errno << "  in Socket::recv\n";
          return 0;
        }
      else
        {
          if (buf[0] == '\0') break;
          std::cout << "received " << status << " bytes\n";
          s += buf;
          count += status;
        }
      
   } while (status > 0);
   std::cout << "done: received " << count << " bytes total\n";
   return count;
}



bool Socket::connect ( const std::string host, const int port )
{
  if ( ! is_valid() ) return false;

  m_addr.sin_family = AF_INET;
  m_addr.sin_port = htons ( port );

  int status = inet_pton ( AF_INET, host.c_str(), &m_addr.sin_addr );

  if ( errno == EAFNOSUPPORT ) return false;

  status = ::connect ( m_sock, ( sockaddr * ) &m_addr, sizeof ( m_addr ) );

  if ( status == 0 )
    return true;
  else
    return false;
}

void Socket::set_non_blocking ( const bool b )
{

  int opts;

  opts = fcntl ( m_sock,
		 F_GETFL );

  if ( opts < 0 )
    {
      return;
    }

  if ( b )
    opts = ( opts | O_NONBLOCK );
  else
    opts = ( opts & ~O_NONBLOCK );

  fcntl ( m_sock,
	  F_SETFL,opts );

}
