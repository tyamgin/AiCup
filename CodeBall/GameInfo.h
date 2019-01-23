#ifndef CODEBALL_GAMEINFO_H
#define CODEBALL_GAMEINFO_H

struct GameInfo {
    static int maxTickCount;
    static bool isNitro;
    static bool isFinal;
    static int teamSize;
    static bool isOpponentCrashed;
    static bool isTeammateById[7];
    static bool usedNitro[7];

    struct GameScore {
        int my = 0, opp = 0;
    };

    static GameScore score;
};

#endif //CODEBALL_GAMEINFO_H
