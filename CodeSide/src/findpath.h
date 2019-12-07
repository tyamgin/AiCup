#ifndef CODESIDE_FINDPATH_H
#define CODESIDE_FINDPATH_H

#include <vector>
#include <set>
#include <map>
#include <queue>
#include <memory.h>

struct TState {
    int x;
    int y;
    int timeLeft;

    bool operator <(const TState& state) const {
        if (x != state.x) {
            return x < state.x;
        }
        if (y != state.y) {
            return y < state.y;
        }
        return timeLeft < state.timeLeft;
    }

    bool samePos(const TState& state) const {
        return x == state.x && y == state.y;
    }

//    bool operator !=(const TState& state) const {
//        return x != state.x || y != state.y || timeLeft != state.timeLeft;
//    }
//
//    bool operator ==(const TState& state) const {
//        return x == state.x && y == state.y && timeLeft == state.timeLeft;
//    }

    TPoint getPoint() const {
        return TPoint(x * (1 / 6.0), y * (1 / 6.0));
    }
};

#define SZ (6 * 43)
#define MAXZ 35
#define INF 0x3f3f3f3f
#define D_LEFT 0
#define D_CENTER 1
#define D_RIGHT 2

bool isStand[SZ][SZ];
bool isValid[SZ][SZ];
bool isBound[SZ][SZ];
bool isBlockedMove[3][3][SZ][SZ];

using TDfsGoesResult = std::vector<TState>;
std::map<TState, TDfsGoesResult> dfsGoesCache;
static bool _drawMode;
std::set<TState> dfsVisitedStates;
TDfsGoesResult dfsResultBorderPoints;
bool dfsStartMode;
TState dfsTraceTarget;
std::vector<TAction> dfsTraceActResult;
std::vector<TState> dfsTraceStateResult;

class TPathFinder {
    const TSandbox* env;
    TUnit startUnit;
    TState startState;
    std::vector<std::vector<double>> dist;
    std::vector<std::vector<TState>> prev;

    static std::vector<TState> _getCellGoesDfs(const TState& state) {
        std::vector<TState> res;
        for (int xDirection = -1; xDirection <= 1; xDirection++) {
            if (isValid[state.x + xDirection][state.y + 1]) {
                if (isBlockedMove[xDirection + D_CENTER][D_CENTER + 1][state.x][state.y]) {
                    continue;
                }
                res.push_back({state.x + xDirection, state.y + 1, state.timeLeft - 1});
            }
        }
        return res;
    }

    static void _dfs(TState state) {
        if (_drawMode) {
            TDrawUtil().debugPoint(state.getPoint());
        }
        dfsVisitedStates.insert(state);
        if (dfsStartMode || isBound[state.x][state.y]) {
            dfsResultBorderPoints.emplace_back(state);
        }
        if (state.timeLeft == 0) {
            return;
        }
        for (const auto& to : _getCellGoesDfs(state)) {
            if (!dfsVisitedStates.count(to)) {
                _dfs(to);
            }
        }
    }

    static bool _dfsTrace(TState state) {
        dfsVisitedStates.insert(state);
        if (dfsTraceTarget.samePos(state)) {
            return true;
        }
        if (state.timeLeft == 0) {
            return false;
        }
        for (const auto& to : _getCellGoesDfs(state)) {
            if (!dfsVisitedStates.count(to) && _dfsTrace(to)) {
                TAction action;
                action.velocity = (to.x - state.x) * UNIT_MAX_HORIZONTAL_SPEED;
                action.jump = true;
                dfsTraceActResult.push_back(action);
                dfsTraceStateResult.push_back(to);
                return true;
            }
        }
        return false;
    }

    static TDfsGoesResult _getJumpGoes(const TState& state, bool startMode) {
        dfsStartMode = startMode;

        if (!startMode) {
            auto it = dfsGoesCache.find(state);
            if (it != dfsGoesCache.end()) {
                return it->second;
            }
        }

        dfsVisitedStates.clear();
        dfsResultBorderPoints.clear();
        _dfs(state);
        if (!startMode) {
            dfsGoesCache[state] = dfsResultBorderPoints;
        }
        return dfsResultBorderPoints;
    }

public:
    static void initMap() {
//        memset(isValid, 0, sizeof(isValid));
//        memset(isStand, 0, sizeof(isStand));
        TUnit unit;
        const double d = 1 / 6.0;
        for (int i = 1; i < TLevel::width - 1; i++) {
            for (int di = 0; di < 6; di++) {
                unit.x1 = i + di * d - UNIT_HALF_WIDTH;
                unit.x2 = unit.x1 + UNIT_SIZE_X;
                for (int j = 1; j < TLevel::height - 1; j++) {
                    for (int dj = 0; dj < 6; dj++) {
                        unit.y1 = j + dj * d;
                        unit.y2 = unit.y1 + UNIT_SIZE_Y;

                        int ii = i*6 + di;
                        int jj = j*6 + dj;
                        isValid[ii][jj] = unit.approxIdValid();
                        isStand[ii][jj] = isValid[ii][jj] && unit.approxIsStand();
                        if (di == 0 && unit.isOnLadder()) {
                            isStand[ii][jj] = false;
                        }
                        isBound[ii][jj] = (di == 3 || di == 2 || di == 4);

                        if (di == 3 && !unit.approxIsStandGround() && unit.approxIsStandGround(1 / 6.0)) {
                            isBlockedMove[D_RIGHT][D_CENTER][ii][jj] = true;
                        }
                        if (di == 3 && !unit.approxIsStandGround() && unit.approxIsStandGround(-1 / 6.0)) {
                            isBlockedMove[D_LEFT][D_CENTER][ii][jj] = true;
                        }
                        if (!unit.approxIdValid(d/2, d/2)) {
                            isBlockedMove[D_RIGHT][D_RIGHT][ii][jj] = true;
                        }
                        if (!unit.approxIdValid(d/2, -d/2)) {
                            isBlockedMove[D_RIGHT][D_LEFT][ii][jj] = true;
                        }
                        if (!unit.approxIdValid(-d/2, d/2)) {
                            isBlockedMove[D_LEFT][D_RIGHT][ii][jj] = true;
                        }
                        if (!unit.approxIdValid(-d/2, -d/2)) {
                            isBlockedMove[D_LEFT][D_LEFT][ii][jj] = true;
                        }
                    }
                }
            }
        }
    }

    explicit TPathFinder(const TSandbox* env, const TUnit& start) {
        this->env = env;
        this->startUnit = start;
        this->startState = _getUnitState(this->startUnit);
        _dijkstra();
    }

    bool findPath(const TPoint& target, std::vector<TPoint>& res, std::vector<TAction>& resAct) {
        TState targetState = _getPointState(target);
        TState selectedTargetState;
        std::pair<double, int> minDist(INF, INF);
        for (int dx = -7; dx <= 7; dx++) {
            for (int dy = -7; dy <= 7; dy++) {
                if (targetState.x + dx > 0 && targetState.x + dx < SZ && targetState.y + dy > 0 && targetState.y + dy < SZ) {
                    std::pair<double, int> cand(dist[targetState.x + dx][targetState.y + dy], std::abs(dx) + std::abs(dy));
                    if (cand < minDist) {
                        minDist = cand;
                        selectedTargetState.x = targetState.x + dx;
                        selectedTargetState.y = targetState.y + dy;
                    }
                }
            }
        }
        if (minDist.first >= INF) {
            return false;
        }

        for (auto s = selectedTargetState; !s.samePos(startState); s = prev[s.x][s.y]) {
            if (prev[s.x][s.y].x == 0) {
                std::cerr << "Error state\n";
                break;
            }
            std::vector<TAction> acts;
            std::vector<TState> stts;
            _getCellGoesTrace(prev[s.x][s.y], s, s.samePos(startState), acts, stts);
            resAct.insert(resAct.end(), acts.rbegin(), acts.rend());
            //std::reverse(stts.begin(), stts.end());
            for (auto& ss : stts) {
                res.push_back(ss.getPoint());
            }
            resAct.insert(resAct.end(), acts.begin(), acts.end());
        }

        auto startStatePoint = startState.getPoint();
        TAction firstAction;
        auto dx = startStatePoint.x - startUnit.position().x;
        firstAction.velocity = std::min(UNIT_MAX_HORIZONTAL_SPEED, dx * TICKS_PER_SECOND);
        bool fall = isStand[startState.x][startState.y] && !startUnit.approxIsStand();
        if (std::abs(dx) > 1e-10 || fall) {
            resAct.push_back(firstAction);
        }

        res.push_back(startState.getPoint());
        std::reverse(res.begin(), res.end());
        std::reverse(resAct.begin(), resAct.end());
        return true;
    }

    std::vector<TPoint> getReachableForDraw() {
        std::vector<TPoint> res;
#if M_DRAW_REACHABILITY_X > 0 && M_DRAW_REACHABILITY_Y > 0
        for (int i = 0; i < (int) dist.size(); i += M_DRAW_REACHABILITY_X) {
            for (int j = 0; j < (int) dist[0].size(); j += M_DRAW_REACHABILITY_Y) {
                if (dist[i][j] < INF) {
                    res.push_back(TState{i, j, 0}.getPoint());
                }
            }
        }
#endif
        return res;
    }


private:
    TState _getPointState(const TPoint& point) {
        TState res;
        res.x = int(point.x / (1.0 / 6) + 1e-10);
        res.y = int(point.y / (1.0 / 6) + 1e-10);
        return res;
    }

    TState _getUnitState(const TUnit& unit) {
        TState res = _getPointState(TPoint(unit.x1 + UNIT_HALF_WIDTH, unit.y1));
        res.timeLeft = int(unit.jumpMaxTime * 60);
        return res;
    }

    std::vector<std::pair<TState, double>> _getCellGoes(const TState& state) {
        std::vector<std::pair<TState, double>> res;
        for (int xDirection = -1; xDirection <= 1; xDirection++) {
            auto stx = state;
            stx.x += xDirection;

            // идти по платформе
            if (isStand[stx.x][stx.y] && !isBlockedMove[xDirection + D_CENTER][D_CENTER][state.x][state.y]) {
                res.emplace_back(stx, 1.0);
            }

            // лететь вниз
            stx.y = state.y - 1;
            if (isValid[stx.x][stx.y] && !isBlockedMove[xDirection + D_CENTER][D_CENTER - 1][state.x][state.y]) {
                res.emplace_back(stx, 1.0);
            }
        }
        // прыгать
        if (isStand[state.x][state.y] && isBound[state.x][state.y]) {
            auto jumpGoes = _getJumpGoes({state.x, state.y, 33}, false);
            for (auto& j : jumpGoes) {
                res.emplace_back(TState{j.x, j.y, 0}, j.y - state.y + 0.0);
            }
        }

        return res;
    }

    void _getCellGoesTrace(const TState& state, const TState& end, bool beg, std::vector<TAction>& resAct, std::vector<TState>& resState) {
        for (int xDirection = -1; xDirection <= 1; xDirection++) {
            auto stx = state;
            stx.x += xDirection;
            TAction act;
            act.velocity = xDirection * UNIT_MAX_HORIZONTAL_SPEED;

            // идти по платформе
            if (stx.samePos(end)) {
                resAct.emplace_back(act);
                resState.emplace_back(stx);
                return;
            }

            // лететь вниз
            stx.y = state.y - 1;
            act.jumpDown = true;
            if (stx.samePos(end)) {
                resAct.emplace_back(act);
                resState.emplace_back(stx);
                return;
            }
        }
        // прыгать

        dfsVisitedStates.clear();
        dfsTraceActResult.clear();
        dfsTraceStateResult.clear();
        dfsTraceTarget = end;
        if (!_dfsTrace({state.x, state.y, beg ? state.timeLeft : 33})) {
            std::cerr << "Error trace dfs\n";
            return;
        }
        resAct = dfsTraceActResult;
        resState = dfsTraceStateResult;
    }

    void _dijkstra() {
        dist = std::vector<std::vector<double>>(TLevel::width*6, std::vector<double>(TLevel::height*6, INF + 1.0));
        prev = std::vector<std::vector<TState>>(TLevel::width*6, std::vector<TState>(TLevel::height*6, {-1, -1}));

        std::priority_queue<std::pair<double, TState>> q;
        for (const auto& s : _getJumpGoes(startState, true)) {
            double dst = std::abs(s.y - startState.y);
            q.push(std::make_pair(dst, s));
            dist[s.x][s.y] = dst;
            prev[s.x][s.y] = startState;
        }
        while (!q.empty()) {
            auto v = q.top().second;
            auto curDist = -q.top().first;
            q.pop();
            if (curDist > dist[v.x][v.y]) {
                continue;
            }

            for (const auto& t : _getCellGoes(v)) {
                auto& to = t.first;
                auto dst = t.second;
                if (dist[v.x][v.y] + dst < dist[to.x][to.y]) {
                    dist[to.x][to.y] = dist[v.x][v.y] + dst;
                    prev[to.x][to.y] = v;
                    q.push(std::make_pair(-dist[to.x][to.y], to));
                }
            }
        }
    }
};

#endif //CODESIDE_FINDPATH_H
