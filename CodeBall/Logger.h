#ifndef CODEBALL_LOGGER_H
#define CODEBALL_LOGGER_H

#include <vector>
#include <ctime>
#include <iostream>
#include <string>
#include <chrono>
#include <sstream>

#define M_NO_RANDOM 0

#if M_NO_RANDOM
#ifndef LOCAL
#error "M_NO_RANDOM is only for local"
#endif
#endif

#ifdef DEBUG
#define M_LOG_DANS 1
#endif

#ifdef LOCAL
#define M_TIME_LOGS 1
#endif

#ifdef DEBUG
#define M_LOGS 1
#endif

enum LoggerAction {
    LA_ALL,
    LA_DO_TICK,

    LA_ACTIONS_COUNT
};

struct Logger {
    std::vector<std::chrono::system_clock::time_point> _timers;
    long long _cumulativeDuration[LA_ACTIONS_COUNT];
    int dans[10000];
    int tick;

    Logger() {
        memset(_cumulativeDuration, 0, sizeof(_cumulativeDuration));
        memset(dans, 0, sizeof(dans));
        tick = 0;
    }

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
            log(std::to_string(tick) + "> " + std::string(_timers.size() * 2, '-') + " " + caption + ": " + std::to_string(time) + "ms");
    }

    void cumulativeTimerStart(LoggerAction action) {
        timerStart();
    }

    void cumulativeTimerEnd(LoggerAction action) {
        _cumulativeDuration[action] += timerEnd();
    }

    std::string getSummary() {
        std::stringstream out;
        out << "[Summary]" << std::endl;
        out << "] ALL                         " << _cumulativeDuration[LA_ALL]                         / 1000 << "ms" << std::endl;
        out << "] DO_TICK                     " << _cumulativeDuration[LA_DO_TICK]                     / 1000 << "ms" << std::endl;
        for (int i = 0; i < sizeof(dans) / sizeof(dans[0]); i++) {
            if (dans[i] > 0) {
                out << "Dan " << i << ": " << dans[i] << std::endl;
            }
        }
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
#define LOG(msg) Logger::instance()->log(msg)
#define LOG_ERROR(msg) Logger::instance()->error(msg)
#else
#define LOG(msg)
#define LOG_ERROR(msg)
#endif

#if M_TIME_LOGS
#define TIMER_START() Logger::instance()->timerStart()
#define TIMER_ENG_LOG(caption) Logger::instance()->timerEndLog((caption), 30)
#define OP_START(action) Logger::instance()->cumulativeTimerStart(LA_ ## action)
#define OP_END(action) Logger::instance()->cumulativeTimerEnd(LA_ ## action)
#else
#define TIMER_START()
#define TIMER_ENG_LOG(caption)
#define OP_START(action)
#define OP_END(action)
#endif

#endif //CODEBALL_LOGGER_H
