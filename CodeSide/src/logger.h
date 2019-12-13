#ifndef CODESIDE_LOGGER_H
#define CODESIDE_LOGGER_H

#include <chrono>
#include <map>
#include <sstream>
#include <iostream>

#define M_TIME_LOGS 1

#ifdef DEBUG
#define M_LOGS 1
#endif

enum ELoggerAction {
    LA_ALL,
    LA_DO_TICK,

    LA_ACTIONS_COUNT
};

struct Logger {
    std::vector<std::chrono::system_clock::time_point> _timers;
    int64_t _cumulativeDuration[LA_ACTIONS_COUNT];
    int tick;

    Logger() {
        memset(_cumulativeDuration, 0, sizeof(_cumulativeDuration));
        tick = 0;
    }

    static Logger* instance() {
        static Logger *_instance = nullptr;
        if (_instance == nullptr) {
            _instance = new Logger();
        }
        return _instance;
    }

    void timerStart() {
        _timers.push_back(std::chrono::system_clock::now());
    }

    int64_t timerGet() {
        auto microseconds = std::chrono::duration_cast<std::chrono::microseconds>(std::chrono::system_clock::now() - _timers.back());
        return microseconds.count();
    }

    int64_t timerEnd() {
        auto res = timerGet();
        _timers.pop_back();
        return res;
    }

    void timerEndLog(const std::string& caption, int limit) {
        auto time = timerEnd() / 500;
        if (time > limit) {
            log(std::to_string(tick) + "> " + std::string(_timers.size() * 2, '-') + " " + caption + ": " + std::to_string(time) + "ms");
        }
    }

    void cumulativeTimerStart(ELoggerAction action) {
        timerStart();
    }

    void cumulativeTimerEnd(ELoggerAction action) {
        _cumulativeDuration[action] += timerEnd();
    }

    std::string getSummary() {
        std::stringstream out;
        out << "[Summary]" << std::endl;
        out << "] ALL                         " << _cumulativeDuration[LA_ALL]                         / 1000 << "ms" << std::endl;
        out << "] DO_TICK                     " << _cumulativeDuration[LA_DO_TICK]                     / 1000 << "ms" << std::endl;
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
#define TIMER_ENG_LOG(caption) Logger::instance()->timerEndLog((caption), 200)
#define OP_START(action) Logger::instance()->cumulativeTimerStart(LA_ ## action)
#define OP_END(action) Logger::instance()->cumulativeTimerEnd(LA_ ## action)
#else
#define TIMER_START()
#define TIMER_ENG_LOG(caption)
#define OP_START(action)
#define OP_END(action)
#endif

#endif //CODESIDE_LOGGER_H
