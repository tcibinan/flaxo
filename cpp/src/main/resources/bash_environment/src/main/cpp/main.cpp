#include <iostream>
#include <map>
#include <functional>
#include <cstring>
#include "plus.h"
#include "minus.h"

using namespace std;

int main(int argc, char **argv) {
    if (argc != 2) {
        cout << "You should specify a name of a single function to run\n";
        return -1;
    }
    char *functionName = argv[1];

    std::map <std::string, std::function<int()>> funcMap =
            {
                    {"plus", plus_task},
                    {"minus",  minus_task}
            };

    funcMap[functionName]();

    return 0;
}
