#include <fcntl.h>
#include <unistd.h>
#include <iostream>
using namespace std;

template<typename... Args>
auto tsum(Args... args) {
  // This (args + ...) expression is new in c++ 17
  return (args + ...);
}

class TestProblem {
  public:
    int sum(int a, int b) {
      int f = open("/etc/passwd", O_RDONLY);
      if (f == -1) {
        cout << "Open /etc/passwd is dis-allowed";
      } else {
        cout << "Open /etc/passwd is allowed";
        close(f);
      }
      return tsum(a, b);
    } 
};