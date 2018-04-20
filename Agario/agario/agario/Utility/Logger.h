#pragma once

#include <vector>
#include <ctime>
#include <iostream>
#include <string>
#include <chrono>
#include <sstream>
using namespace std;

struct Logger
{
	enum Action
	{
		ALL,
		DANGER_STRATEGY,
		SANDBOX,
		SANDBOX_OPPONENT_DUMMY_MOVE,

		ACTIONS_COUNT
	};

	vector<chrono::system_clock::time_point> _timers;
	long long _cumulativeDuration[ACTIONS_COUNT];
		

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
		_timers.push_back(chrono::system_clock::now());
	}

	long long timerGet()
	{
		auto microseconds = chrono::duration_cast<chrono::microseconds>(chrono::system_clock::now() - _timers.back());
		return microseconds.count();
	}

	long long timerEnd()
	{
		auto res = timerGet();
		_timers.pop_back();
		return res;
	}

	void timerEndLog(string caption, int limit)
	{
		auto time = timerEnd() / 1000;
		if (time > limit)
			log(to_string(tick) + ">" + string(_timers.size() * 2, '-') + " " + caption + ":" + to_string(time));
	}

	void cumulativeTimerStart(Action action)
	{
		timerStart();
	}

	void cumulativeTimerEnd(Action action)
	{
		_cumulativeDuration[action] += timerEnd();
	}

	string getSummary()
	{
		stringstream out;
		out << "[Summary]" << endl;
		out << "] ALL                         " << _cumulativeDuration[ALL]                         / 1000 << "ms" << endl;
		out << "] DANGER_STRATEGY             " << _cumulativeDuration[DANGER_STRATEGY]             / 1000 << "ms" << endl;
		out << "] SANDBOX                     " << _cumulativeDuration[SANDBOX]                     / 1000 << "ms" << endl;
		out << "] SANDBOX_OPPONENT_DUMMY_MOVE " << _cumulativeDuration[SANDBOX_OPPONENT_DUMMY_MOVE] / 1000 << "ms" << endl;
		
		return out.str();
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
#define LOG(msg) Logger::instance()->log(msg)
#define OP_START(action) Logger::instance()->cumulativeTimerStart(Logger:: ## action)
#define OP_END(action) Logger::instance()->cumulativeTimerEnd(Logger:: ## action)
#else
#define TIMER_START()
#define TIMER_ENG_LOG(caption)
#define LOG(msg)
#define OP_START(action)
#define OP_END(action)
#endif