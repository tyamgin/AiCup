#ifdef _DEBUG
#define M_SOCKET_IO false
#define M_VISUAL true
#define M_FROM_LOG "C:\\Users\\tyamgin\\Downloads\\132074_dump.log"
#else
#define M_SOCKET_IO false
#define M_VISUAL false
#endif

#if defined(M_FROM_LOG) && M_SOCKET_IO
#error "Define M_FROM_LOG and M_SOCKET_IO separately"
#endif

#define _CRT_SECURE_NO_WARNINGS

#include <iostream>
#include <string>
#include <vector>

#include "Utility/Logger.h"
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
#include "Visualizer/Visualizer.hpp"
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
#ifdef M_FROM_LOG
		auto hFile = freopen(M_FROM_LOG, "r", stdin);
		if (!hFile)
		{
			cerr << "Cannot open file\"" M_FROM_LOG "\"" << endl;
			exit(1);
		}
#endif

		auto config_json = readJson();
		Config::parse(config_json);
		int tick = 0;
		MyStrategy strategy;

#ifdef M_FROM_LOG
		fgets(buffer, sizeof(buffer), stdin); // skip empty line
#endif

		

		while (true) 
		{
#ifdef M_FROM_LOG
			fgets(buffer, sizeof(buffer), stdin); // skip tick's line
#endif
			auto world_json = readJson();
			Move debug_real_move;
#ifdef M_FROM_LOG
			fgets(buffer, sizeof(buffer), stdin);
			bool has_move = strlen(buffer) > 1;
			if (has_move)
			{
				auto debug_real_move_json = nlohmann::json::parse(buffer);
				debug_real_move = Move(debug_real_move_json);
				fgets(buffer, sizeof(buffer), stdin); // skip empty line
			}
#endif
			if (tick == 0)
				Logger::instance()->timerStart();

			World world(world_json);
			world.tick = ++tick;
			Logger::instance()->tick = world.tick;

			auto command = strategy.onTick(world, debug_real_move);
			if (tick % 100 == 0)
				command.debug += "Time: " + to_string(Logger::instance()->timerGet()) + "ms\n";
#if M_VISUAL
			Visualizer::updateMove(command);
#endif

#ifdef M_FROM_LOG
			if (has_move && debug_real_move != command)
			{
				cerr << "Commands defferernt at " << world.tick << endl;
			}
#else
			writeJson(command.toJson());
#endif

#if M_VISUAL
			System::Threading::Thread::Sleep(1);
#endif
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