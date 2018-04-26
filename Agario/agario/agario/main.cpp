#define M_SOCKET_IO false
#define M_VISUAL true

#include "MainForm.h"

#include <iostream>
#include <string>
#include <vector>

#include "Config.hpp"
#include "Model\World.hpp"
using namespace std;

ref struct Visualizer
{
	static agario::MainForm ^form;
};

char buffer[1 << 20];

#if M_SOCKET_IO
#include <winsock2.h>
#pragma comment(lib, "ws2_32.lib")
SOCKET server;
#endif

nlohmann::json readJson()
{
#if M_SOCKET_IO
	int len = recv(server, buffer, sizeof(buffer), 0);
	buffer[len] = 0;
#else
	fgets(buffer, sizeof(buffer), stdin);
#endif

	try 
	{
		return nlohmann::json::parse(buffer);
	}
	catch (std::exception &e)
	{
		throw e;
	}
}

void writeJson(nlohmann::json json)
{
	string output = json.dump();
#if M_SOCKET_IO
	send(server, output.c_str(), output.size(), 0);
#else
	puts(output.c_str());
	fflush(stdout);
#endif
}

struct Strategy 
{
	void run() 
	{
		//auto thread1 = gcnew System::Threading::Thread(gcnew System::Threading::ThreadStart(doVisualizer));
		//thread1->Start();

		auto config_json = readJson();
		Config::parse(config_json);

		while (true) 
		{
			auto world_json = readJson();
			World world(world_json);

			auto command = onTick(world);

			writeJson(command.toJson());
		}

		//thread1->Join();
	}

	Move onTick(const World &world)
	{
		//if (!world.me.fragments.empty())
		//{
		//	if (!world.foods.empty())
		//	{
		//		auto food = world.foods[0];
		//		return{ { "X", food.x },{ "Y", food.y },{ "Debug", "FOOD" } };
		//	}
		//	return{ { "X", 0 },{ "Y", 0 },{ "Debug", "No food" } };
		//}
		return Move{ 100, 100 };
	}
};

void doStrategy()
{
	Strategy strategy;
	strategy.run();
}

void doVisualizer()
{
	System::Windows::Forms::Application::EnableVisualStyles();
	System::Windows::Forms::Application::SetCompatibleTextRenderingDefault(false);
	agario::MainForm form;

	Visualizer::form = %form;
	System::Windows::Forms::Application::Run(%form);
}

[System::STAThread]
void main() 
{
#if M_SOCKET_IO
	WSADATA WSAData;
	SOCKADDR_IN addr;
	
	WSAStartup(MAKEWORD(2, 0), &WSAData);
	server = socket(AF_INET, SOCK_STREAM, 0);
	
	addr.sin_addr.s_addr = inet_addr("127.0.0.1");
	addr.sin_family = AF_INET;
	addr.sin_port = htons(5555);
	
	connect(server, (SOCKADDR *)&addr, sizeof(addr));
	cout << "Connected to server!" << endl;
#endif
	
	doStrategy();
	
#if M_SOCKET_IO
	closesocket(server);
	WSACleanup();
	cout << "Socket closed." << endl << endl;
#endif
}