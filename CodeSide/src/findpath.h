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

    bool operator !=(const TState& state) const {
        return x != state.x || y != state.y || timeLeft != state.timeLeft;
    }

    TPoint getPoint() const {
        return TPoint(x * (1 / 6.0), y * (1 / 6.0));
    }
};

#define SZ (6 * 43)
#define MAXZ 35
#define INF 0x3f3f3f3f

int dist[SZ][SZ];
TState prev[SZ][SZ];
TAction prevAct[SZ][SZ];
bool isStand[SZ][SZ];
bool isValid[SZ][SZ];
bool isBound[SZ][SZ];

using TDfsGoesResult = std::vector<TState>;
std::map<TState, TDfsGoesResult> dfsGoesCache;
static bool _drawMode;
std::set<TState> dfsVisitedStates;
TDfsGoesResult dfsResultBorderPoints;

class TPathFinder {
    const TSandbox* env;
    TUnit startUnit;
    TState startState;
    //std::vector<std::vector<double>> dist;
    //std::vector<std::vector<TCell>> prev;

    static std::vector<TState> _getCellGoesDfs(const TState& state) {
        std::vector<TState> res;
        for (int xDirection = -1; xDirection <= 1; xDirection++) {
            if (isValid[state.x + xDirection][state.y + 1]) {
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
        if (isBound[state.x][state.y]) {
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

    static TDfsGoesResult _getJumpGoes(const TState& state) {
//        if (state.x == 24*6 + 4 && state.y == 14*6) {
//            _drawMode = true;
//        } else {
//            _drawMode = false;
//        }
        auto it = dfsGoesCache.find(state);
        if (it != dfsGoesCache.end()) {
            return it->second;
        }

        dfsVisitedStates.clear();
        dfsResultBorderPoints.clear();
        TState start = state;
        start.timeLeft = 33;
        _dfs(start);
        dfsGoesCache[state] = dfsResultBorderPoints;
        return dfsResultBorderPoints;
    }

public:
    static void initMap() {
        memset(isValid, 0, sizeof(isValid));
        memset(isStand, 0, sizeof(isStand));
        TUnit unit;
        for (int i = 1; i < TLevel::width - 1; i++) {
            for (int di = 0; di < 6; di++) {
                unit.x1 = i + di * (1 / 6.0) - UNIT_HALF_WIDTH;
                unit.x2 = unit.x1 + UNIT_SIZE_X;
                for (int j = 1; j < TLevel::height - 1; j++) {
                    for (int dj = 0; dj < 6; dj++) {
                        unit.y1 = j + dj * (1 / 6.0);
                        unit.y2 = unit.y1 + UNIT_SIZE_Y;

                        int ii = i*6 + di;
                        int jj = j*6 + dj;
                        isValid[ii][jj] = unit.approxIdValid();
                        isStand[ii][jj] = isValid[ii][jj] && unit.approxIsStand();
                        isBound[ii][jj] = (di == 3 || di == 2 || di == 4);
//                        auto r = getTile(i, j - 1);
//                        auto l = getTile(i - 1, j - 1);
//                        if (di == 0 && dj == 0 && tileMatch(r, ETile::WALL) && !tileMatch(l, ETile::WALL)) {
//                            isStand[ii][jj] = isValid[ii][jj] = false;
//                        }
//                        if (di == 0 && dj == 0 && tileMatch(l, ETile::WALL) && !tileMatch(r, ETile::WALL)) {
//                            isStand[ii][jj] = isValid[ii][jj] = false;
//                        }
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
        std::pair<int, int> minDist(INF, INF);
        for (int dx = -7; dx <= 7; dx++) {
            for (int dy = -7; dy <= 7; dy++) {
                if (targetState.x + dx > 0 && targetState.x + dx < SZ && targetState.y + dy > 0 && targetState.y + dy < SZ) {
                    std::pair<int, int> cand(dist[targetState.x + dx][targetState.y + dy], std::abs(dx) + std::abs(dy));
                    if (cand < minDist) {
                        minDist = cand;
                        selectedTargetState.x = targetState.x + dx;
                        selectedTargetState.y = targetState.y + dy;
                        selectedTargetState.timeLeft = 0;
                    }
                }
            }
        }
        if (minDist.first >= INF) {
            return false;
        }

        while (selectedTargetState != startState) {
            res.push_back(selectedTargetState.getPoint());
            resAct.push_back(prevAct[selectedTargetState.x][selectedTargetState.y]);
            selectedTargetState = prev[selectedTargetState.x][selectedTargetState.y];
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
        for (int i = 0; i < SZ; i += M_DRAW_REACHABILITY_X) {
            for (int j = 0; j < SZ; j += M_DRAW_REACHABILITY_Y) {
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

    std::vector<TState> _getCellGoes(const TState& state) {
        std::vector<TState> res;
        for (int xDirection = -1; xDirection <= 1; xDirection++) {
            auto stx = state;
            stx.x += xDirection;

            // идти по платформе
            if (isStand[stx.x][stx.y]) {
                res.emplace_back(stx);
            }

            // лететь вниз
            stx.y = state.y - 1;
            if (isValid[stx.x][stx.y]) {
                res.emplace_back(stx);
            }
        }
        // прыгать
        if (isStand[state.x][state.y] && isBound[state.x][state.y]) {
            auto jumpGoes = _getJumpGoes(state);
            res.insert(res.end(), jumpGoes.begin(), jumpGoes.end());
        }

        return res;
    }

    void _dijkstra() {
        memset(dist, 63, sizeof(dist));
        std::priority_queue<std::pair<double, TState>> q;
        q.push(std::make_pair(0.0, startState));
        dist[startState.x][startState.y] = 0;
        while (!q.empty()) {
            auto v = q.top().second;
            auto curDist = -q.top().first;
            q.pop();
            if (curDist > dist[v.x][v.y]) {
                continue;
            }

            for (const auto& to : _getCellGoes(v)) {
                auto dst = std::abs(to.x - v.x) + std::abs(to.y - v.y);
                if (dist[v.x][v.y] + dst < dist[to.x][to.y]) {
                    dist[to.x][to.y] = dist[v.x][v.y] + dst;
                    prev[to.x][to.y] = v;
                    q.push(std::make_pair(-dist[to.x][to.y], to));
                }
            }
        }
    }
};





class TPathFinder2 {
    const TSandbox* env;
    TCell start;
    std::vector<std::vector<double>> dist;
    std::vector<std::vector<TCell>> prev;

public:
    explicit TPathFinder2(const TSandbox* env, const TCell& start) {
        this->env = env;
        this->start = start;
        _dijkstra();
    }

    bool findPathTo(const TCell& cell, std::vector<TCell>& res) {
        auto cur = prev[cell.x][cell.y];
        if (cur.x == -1) {
            return false;
        }
        while (cur != start) {
            res.push_back(cur);
            cur = prev[cur.x][cur.y];
        }
        res.push_back(start);
        std::reverse(res.begin(), res.end());
        return true;
    }

private:
    struct TEdge {
        TCell cell;
        double dist = 0.0;
    };

    std::vector<TEdge> _getCellGoes(const TCell& cell) {
        std::vector<TEdge> res;
        for (int h = 0; h < 6; h++) { // TODO
            if (getTile(cell.x, cell.y + h) == ETile::WALL || getTile(cell.x, cell.y + h + 1) == ETile::WALL) {
                break;
            }
            if (isStayableTile(cell.x, cell.y + h + 1)) {
                res.push_back({{cell.x, cell.y + h + 1}, h + 0.0});
            }

            for (int dx = -1; dx <= 1; dx++) {
                if (isStayableTile(cell.x + dx, cell.y + h)) {
                    res.push_back({{cell.x + dx, cell.y + h}, h + 1.0});
                }
            }
        }
        return res;
    }

    void _dijkstra() {
        dist = std::vector<std::vector<double>>(TLevel::width, std::vector<double>(TLevel::height, 100000.0));
        prev = std::vector<std::vector<TCell>>(TLevel::width, std::vector<TCell>(TLevel::height, {-1, -1}));

        dist[start.x][start.y] = 0;

        std::priority_queue<std::pair<double, TCell>> q;
        q.push(std::make_pair(0.0, start));
        while (!q.empty()) {
            auto v = q.top().second;
            auto curDist = -q.top().first;
            q.pop();
            if (curDist > dist[v.x][v.y]) {
                continue;
            }

            for (const auto& to : _getCellGoes(v)) {
                if (dist[v.x][v.y] + to.dist < dist[to.cell.x][to.cell.y]) {
                    dist[to.cell.x][to.cell.y] = dist[v.x][v.y] + to.dist;
                    prev[to.cell.x][to.cell.y] = v;
                    q.push(std::make_pair(-dist[to.cell.x][to.cell.y], to.cell));
                }
            }
        }
    }
};

#endif //CODESIDE_FINDPATH_H
