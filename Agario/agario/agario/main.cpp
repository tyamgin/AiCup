#define M_SOCKET_IO true
#define M_VISUAL true
#define EPS 1e-9

#include <iostream>
#include <string>
#include <vector>

#include "Config.hpp"
#include "Model/World.hpp"
#include "MyStrategy.hpp"
using namespace std;

char buffer[1 << 20];

#if M_SOCKET_IO
#define _WINSOCK_DEPRECATED_NO_WARNINGS
#include <winsock2.h>
#pragma comment(lib, "ws2_32.lib")
SOCKET server;
#endif

#if M_VISUAL
#include "Visualizer.hpp"
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
		cerr << e.what() << endl;
		exit(1);
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

struct Runner 
{
	void run() 
	{
		auto config_json = readJson();
		Config::parse(config_json);
		int tick = 0;
		MyStrategy strategy;

		while (true) 
		{
			auto world_json = readJson();
			World world(world_json);
			world.tick = tick++;
#if M_VISUAL
			Visualizer::update(world);
#endif
			auto command = strategy.onTick(world);

			writeJson(command.toJson());
		}
	}
};

void doStrategy()
{
	Runner runner;
	runner.run();
}

#if M_VISUAL
void doVisualizer()
{
	System::Windows::Forms::Application::Run(Visualizer::form);
}

[System::STAThread]
#endif
int main() 
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

#if M_VISUAL
	System::Windows::Forms::Application::EnableVisualStyles();
	System::Windows::Forms::Application::SetCompatibleTextRenderingDefault(false);
	agario::MainForm form;
	Visualizer::form = %form;

	System::Windows::Forms::Control::CheckForIllegalCrossThreadCalls = false;

	auto thread1 = gcnew System::Threading::Thread(gcnew System::Threading::ThreadStart(doVisualizer));
	thread1->Start();
	
	System::Threading::Thread::Sleep(200);
#endif

	doStrategy();

#if M_VISUAL
	thread1->Join();
#endif
	
#if M_SOCKET_IO
	closesocket(server);
	WSACleanup();
	cout << "Socket closed." << endl << endl;
#endif
	return 0;
}