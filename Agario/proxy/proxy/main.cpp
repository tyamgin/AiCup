#include <iostream>
#include <winsock2.h>
#pragma comment(lib, "ws2_32.lib")
#include <string>
#include <Windows.h>
using namespace std;

char buffer[1 << 20];

int main()
{
	WSADATA WSAData;
	SOCKET server, client;
	SOCKADDR_IN serverAddr, clientAddr;

	WSAStartup(MAKEWORD(2, 0), &WSAData);
	server = socket(AF_INET, SOCK_STREAM, 0);

	serverAddr.sin_addr.s_addr = INADDR_ANY;
	serverAddr.sin_family = AF_INET;
	serverAddr.sin_port = htons(5555);

	bind(server, (SOCKADDR *)&serverAddr, sizeof(serverAddr));
	listen(server, 0);

	// Listening for incoming connections...

	int clientAddrSize = sizeof(clientAddr);
	if ((client = accept(server, (SOCKADDR *)&clientAddr, &clientAddrSize)) != INVALID_SOCKET)
	{
		string input;
		getline(cin, input);
		send(client, input.c_str(), input.size(), 0);
		Sleep(1000);
		while (true)
		{
			getline(cin, input);
			send(client, input.c_str(), input.size(), 0);

			int len = recv(client, buffer, sizeof(buffer), 0);
			buffer[len] = 0;
			cout << buffer << endl;
		}
	}
}