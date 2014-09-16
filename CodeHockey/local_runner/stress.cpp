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

const string localRunnerPath = "D:\\Projects\\AiCup\\CodeHockey\\local_runner"; // локал раннер, стратегии old.exe, new.exe должны находиться в этой папке. И этот экзешник тоже.

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

int main(int argc, char** argv)
{
	int games = argc > 1 ? ParseInt(argv[1]) : 1;
	int newWins = 0, oldWins = 0, ties = 0;
	for (int port = 31001 + 2, game = 0; game < games; port += 2, game++)
	{
		Config conf;
		conf.Add("render-to-screen", "false");
		conf.Add("render-to-screen-sync", "false");
		conf.Add("team-size", "2");
		conf.Add("player-count", "2");
		conf.Add("p1-type", "Local");
		conf.Add("p2-type", "Local");
		conf.Add("p1-name", "New");
		conf.Add("p2-name", "Old");
		conf.Add("seed", "");

		string resultFilename = "result0.txt";
		conf.Add("results-file", resultFilename);
		conf.Add("base-adapter-port", ToString(port));
		ofstream propFile("local-runner-stress.properties");
		propFile << conf.ToString();
		propFile.close();

		string javaStart = string() + "start /B java -cp \".;*;%~dp0/*\" -jar \"" + localRunnerPath + "\\local-runner.jar\" \"" + localRunnerPath + "\\local-runner-stress.properties\"";
		system(javaStart.c_str());
		system("timeout 1");
		system(((string)"start /B new 127.0.0.1 " + ToString(port) + " 0000000000000000").c_str());
		system(((string)"old 127.0.0.1 " + ToString(port + 1) + " 0000000000000000").c_str());

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
		printf("new: %d %d :old (%d)\n", newWins, oldWins, ties);
		result.close();
		system(("type " + resultFilename).c_str());
	}
	puts("----- Total results -----");
	printf("new: %d %d :old\n", newWins, oldWins);
}