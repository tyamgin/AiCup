#pragma once

#include <vector>
#include <ctime>
#include <iostream>
#include <string>
using namespace std;

class Logger
{
	vector<clock_t> _timers;

public:
	int tick;

	static Logger* instance()
	{
		static Logger *_instance = nullptr;
		if (_instance == nullptr)
			_instance = new Logger();
		return _instance;
	}

	void timerStart()
	{
		_timers.push_back(clock());
	}

	int timerGet()
	{
		auto start_clock = _timers.back();
		return (clock() - start_clock) * 1000 / CLOCKS_PER_SEC;
	}

	int timerEnd()
	{
		auto start_clock = _timers.back();
		_timers.pop_back();
		return (clock() - start_clock) * 1000 / CLOCKS_PER_SEC;
	}

	void timerEndLog(string caption, int limit)
	{
		auto time = timerEnd();
		if (time > limit)
			log(to_string(tick) + ">" + string(_timers.size() * 2, '-') + " " + caption + ":" + to_string(time));
	}

	template <typename T>
	void log(T msg)
	{
		cerr << msg << endl;
	}
};

#ifdef _DEBUG
#define TIMER_START() Logger::instance()->timerStart()
#define TIMER_ENG_LOG(caption) Logger::instance()->timerEndLog((caption), 30)
#else
#define TIMER_START()
#define TIMER_ENG_LOG(caption)
#endif