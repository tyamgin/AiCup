#ifndef CODEBALL_LOGGER_H
#define CODEBALL_LOGGER_H

#include <vector>
#include <ctime>
#include <iostream>
#include <string>
#include <chrono>
#include <sstream>

#ifdef DEBUG
#define M_LOGS 1
#endif

struct Logger {
    enum Action {
        ALL,

        ACTIONS_COUNT
    };

    std::vector<std::chrono::system_clock::time_point> _timers;
    long long _cumulativeDuration[ACTIONS_COUNT];


    int tick;

    static Logger* instance() {
        static Logger *_instance = nullptr;
        if (_instance == nullptr)
            _instance = new Logger();
        return _instance;
    }

    void timerStart() {
        _timers.push_back(std::chrono::system_clock::now());
    }

    long long timerGet() {
        auto microseconds = std::chrono::duration_cast<std::chrono::microseconds>(std::chrono::system_clock::now() - _timers.back());
        return microseconds.count();
    }

    long long timerEnd() {
        auto res = timerGet();
        _timers.pop_back();
        return res;
    }

    void timerEndLog(const std::string& caption, int limit) {
        auto time = timerEnd() / 1000;
        if (time > limit)
            log(std::to_string(tick) + ">" + std::string(_timers.size() * 2, '-') + " " + caption + ":" + std::to_string(time));
    }

    void cumulativeTimerStart(Action action) {
        timerStart();
    }

    void cumulativeTimerEnd(Action action) {
        _cumulativeDuration[action] += timerEnd();
    }

    std::string getSummary() {
        std::stringstream out;
        out << "[Summary]" << std::endl;
        out << "] ALL                         " << _cumulativeDuration[ALL]                         / 1000 << "ms" << std::endl;
        return out.str();
    }

    template <typename T>
    void log(T msg) {
        std::cout << msg << std::endl;
    }

    template <typename T>
    void error(T msg) {
        std::cerr << msg << std::endl;
    }
};

#if M_LOGS
#define TIMER_START() Logger::instance()->timerStart()
#define TIMER_ENG_LOG(caption) Logger::instance()->timerEndLog((caption), 30)
#define LOG(msg) Logger::instance()->log(msg)
#define LOG_ERROR(msg) Logger::instance()->error(msg)
#define OP_START(action) Logger::instance()->cumulativeTimerStart(Logger:: ## action)
#define OP_END(action) Logger::instance()->cumulativeTimerEnd(Logger:: ## action)
#else
#define TIMER_START()
#define TIMER_ENG_LOG(caption)
#define LOG(msg)
#define LOG_ERROR(msg)
#define OP_START(action)
#define OP_END(action)
#endif

#endif //CODEBALL_LOGGER_H
