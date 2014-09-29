#pragma comment(linker, "/STACK:500000000") 
#include <functional>
#include <algorithm> 
#include <iostream> 
#include <string.h> 
#include <stdlib.h> 
#include <numeric>
#include <sstream> 
#include <fstream>
#include <ctype.h> 
#include <stdio.h> 
#include <bitset>
#include <vector> 
#include <string> 
#include <math.h> 
#include <time.h> 
#include <queue> 
#include <stack> 
#include <list>
#include <map> 
#include <set> 
#define Int long long 
#define INF 0x3F3F3F3F 
#define eps 1e-9
using namespace std;
typedef pair<int, int> pii;
#include <windows.h>

struct Config
{
	vector<pair<string, string> > conf;

	void Add(string key, string value)
	{
		conf.push_back({ key, value });
	}

	string ToString()
	{
		string res = "";
		for (auto row : conf)
			res += row.first + "=" + row.second + "\n";
		return res;
	}
};

int ParseInt(char *str)
{
	int res;
	sscanf(str, "%d", &res);
	return res;
}

char buf[20];
string ToString(int a)
{
	sprintf(buf, "%d", a);
	return buf;
}

void system(string cmd)
{
	system(cmd.c_str());
}

string getExePath() 
{
    char buffer[MAX_PATH];
    GetModuleFileName( NULL, buffer, MAX_PATH );
    string::size_type pos = string( buffer ).find_last_of( "\\/" );
    return string( buffer ).substr( 0, pos);
}

int main(int argc, char** argv)
{
	string localRunnerPath = getExePath(); // локал раннер, стратегии old.exe, new.exe должны находиться в этой папке.

	string first = argv[1];
	string second = argv[2];
	int games = ParseInt(argv[3]);
	string ticksCount = argc > 4 ? argv[4] : "";
	
	srand(time(0));
	
	int startPort = 31001 + 2 + rand() / 10;
	string resultFilename = "result" + ToString(startPort) + ".txt";
	string propFilename = "local-runner-stress" + ToString(startPort) + ".properties";
	
	for(auto swapSides : vector<string> { "false", "true" })
	{
		int newWins = 0, oldWins = 0, ties = 0;
		for (int port = startPort, game = 0; game < games; port += 2, game++)
		{
			Config conf;
			conf.Add("render-to-screen", "false");
			conf.Add("render-to-screen-sync", "false");
			conf.Add("team-size", "3");
			conf.Add("player-count", "2");
			conf.Add("p1-type", "Local");
			conf.Add("p2-type", "Local");
			conf.Add("p1-name", first.c_str());
			conf.Add("p2-name", second.c_str());
			conf.Add("seed", "");
			conf.Add("tick-count", ticksCount);
			conf.Add("swap-sides", swapSides);
			conf.Add("render-to-screen-scale", "1.0");
			conf.Add("results-file", resultFilename);
			conf.Add("base-adapter-port", ToString(port));
			
			ofstream propFile(propFilename.c_str());
			propFile << conf.ToString();
			propFile.close();

			string javaStart = "start /B java -cp \".;*;%~dp0/*\" -jar \"" + localRunnerPath + "\\local-runner.jar\" \"" + localRunnerPath + "\\" + propFilename + "\"";
			system(javaStart);
			Sleep(1000);
			system("start /B " + first + " 127.0.0.1 " + ToString(port) + " 0000000000000000");
			system(second + " 127.0.0.1 " + ToString(port + 1) + " 0000000000000000");

			ifstream result(resultFilename);
			string t;
			int place1, place2;
			result >> t >> t >> t;
			result >> place1 >> t >> t;
			result >> place2 >> t >> t;
			if (place1 == 1 && place2 == 2)
				newWins++;
			else if (place2 == 1 && place1 == 2)
				oldWins++;
			else
				ties++;
			cout << first << ": " << newWins << "  " << oldWins << " :" << second << "  (" << ties << ")" << endl;
			result.close();
			Sleep(500);
			system("type " + resultFilename);
			system("del " + propFilename);
			system("del " + resultFilename);
		}
		puts("----- Total results -----");
		cout << first << ": " << newWins << "  " << oldWins << " :" << second << "  (" << ties << ")" << endl;
	}
	system("pause");
}