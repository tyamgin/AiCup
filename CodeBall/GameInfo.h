#ifndef CODEBALL_GAMEINFO_H
#define CODEBALL_GAMEINFO_H

struct GameInfo {
    static int maxTickCount;
    static bool isFinal;

    struct GameScore {
        int my = 0, opp = 0;
    };

    static GameScore score;
};

#endif //CODEBALL_GAMEINFO_H
