#ifndef CODEBALL_LOGGER_H
#define CODEBALL_LOGGER_H

#include <vector>
#include <ctime>
#include <iostream>
#include <string>
#include <chrono>
#include <sstream>

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
    LA_TAKE_NITRO,
    LA_MOVE_TO_BALL,
    LA_K,
    LA_KW,

    LA_ACTIONS_COUNT
};

struct Logger {
    std::vector<std::chrono::system_clock::time_point> _timers;
    long long _cumulativeDuration[LA_ACTIONS_COUNT];
    int dans[10000];
    std::unordered_map<double, int> corrXYZStat[3];
    std::map<Point, int> corrXYZOnlyJumpStat;
    int sandboxTicksCount = 0;
    int sandbox3TicksCount = 0;
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
        out << "] TAKE_NITRO                  " << _cumulativeDuration[LA_TAKE_NITRO]                  / 1000 << "ms" << std::endl;
        out << "] MOVE_TO_BALL                " << _cumulativeDuration[LA_MOVE_TO_BALL]                / 1000 << "ms" << std::endl;
        out << "] K                           " << _cumulativeDuration[LA_K]                           / 1000 << "ms" << std::endl;
        out << "] KW                          " << _cumulativeDuration[LA_KW]                          / 1000 << "ms" << std::endl;
        for (int i = 0; i < int(sizeof(dans) / sizeof(dans[0])); i++) {
            if (dans[i] > 0) {
                out << "Dan " << i << ": " << dans[i] << std::endl;
            }
        }
        for (int i = 0; i < 3; i++) {
            for (auto& mp : corrXYZStat[i]) {
                out << "Corr" << "XYZ"[i] << " " << mp.first << ":" << mp.second << std::endl;
            }
        }
        for (auto& mp : corrXYZOnlyJumpStat) {
            out << "Corr jump only " << mp.first.x << "," << mp.first.y << "," << mp.first.z << ":" << mp.second << std::endl;
        }
        out << "Sandbox ticks " << sandboxTicksCount << std::endl;
        out << "Sandbox 3ticks " << sandbox3TicksCount << std::endl;
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

#endif //CODEBALL_LOGGER_H
