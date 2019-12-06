#ifndef CODESIDE_FINDPATH_H
#define CODESIDE_FINDPATH_H

#include <vector>
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

int dist[SZ][SZ][MAXZ];
TState prev[SZ][SZ][MAXZ];
TAction prevAct[SZ][SZ][MAXZ];
bool isStand[SZ][SZ];
bool isValid[SZ][SZ];

class TPathFinder {
    const TSandbox* env;
    TUnit start;
    TState startState;
    //std::vector<std::vector<double>> dist;
    //std::vector<std::vector<TCell>> prev;

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
                    }
                }
            }
        }
    }

    explicit TPathFinder(const TSandbox* env, const TUnit& start) {
        this->env = env;
        this->start = start;
        _bfs();
    }

    bool findPath(const TPoint& target, std::vector<TPoint>& res, std::vector<TAction>& resAct) {
        TState targetState = _getPointState(target);
        TState selectedTargetState;
        int minDist = INF;
        for (int z = 0; z < MAXZ; z++) {
            if (dist[targetState.x][targetState.y][z] < minDist) {
                minDist = dist[targetState.x][targetState.y][z];
                selectedTargetState = targetState;
                selectedTargetState.timeLeft = z;
            }
        }
        if (minDist >= INF) {
            return false;
        }
        while (selectedTargetState != startState) {
            res.push_back(selectedTargetState.getPoint());
            resAct.push_back(prevAct[selectedTargetState.x][selectedTargetState.y][selectedTargetState.timeLeft]);
            selectedTargetState = prev[selectedTargetState.x][selectedTargetState.y][selectedTargetState.timeLeft];
        }
        res.push_back(startState.getPoint());
        std::reverse(res.begin(), res.end());
        std::reverse(resAct.begin(), resAct.end());
        return true;
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

    std::vector<std::pair<TState, TAction>> _getCellGoes(const TState& state) {
        std::vector<std::pair<TState, TAction>> res;
        for (int xDirection = -1; xDirection <= 1; xDirection++) {
            auto stx = state;
            stx.x += xDirection;
            TAction actionx;
            actionx.velocity = xDirection * UNIT_MAX_HORIZONTAL_SPEED;

            if (isStand[stx.x][stx.y]) {
                if (!isStand[state.x][state.y])
                    actionx.jump = true;
                res.emplace_back(stx, actionx);
            }
            if (state.timeLeft > 0) {
                stx.y = state.y + 1;
                stx.timeLeft = state.timeLeft - 1;
                actionx.jump = true;
                actionx.jumpDown = false;
                if (isValid[stx.x][stx.y]) {
                    res.emplace_back(stx, actionx);
                }
            }
            if (isStand[state.x][state.y]) {
                stx.y = state.y + 1;
                stx.timeLeft = 32;
                actionx.jump = true;
                actionx.jumpDown = false;
                if (isValid[stx.x][stx.y]) {
                    res.emplace_back(stx, actionx);
                }
            }

            stx.y = state.y - 1;
            stx.timeLeft = 0;
            actionx.jump = false;
            actionx.jumpDown = true;
            if (isValid[stx.x][stx.y]) {
                res.emplace_back(stx, actionx);
            }
        }
        return res;
    }

    void _bfs() {
        //dist = std::vector<std::vector<double>>(TLevel::width, std::vector<double>(TLevel::height, 100000.0));
        //prev = std::vector<std::vector<TCell>>(TLevel::width, std::vector<TCell>(TLevel::height, {-1, -1}));
        memset(dist, 63, sizeof(dist));
        this->startState = _getUnitState(this->start);

        dist[this->startState.x][this->startState.y][this->startState.timeLeft] = 0;
        std::queue<TState> q;
        q.push(this->startState);
        while (!q.empty()) {
            auto v = q.front();
            q.pop();

            for (const auto& tt : _getCellGoes(v)) {
                auto& to = tt.first;
                auto& act = tt.second;
                if (dist[v.x][v.y][v.timeLeft] + 1 < dist[to.x][to.y][to.timeLeft]) {
                    dist[to.x][to.y][to.timeLeft] = dist[v.x][v.y][v.timeLeft] + 1;
                    prev[to.x][to.y][to.timeLeft] = v;
                    prevAct[to.x][to.y][to.timeLeft] = act;
                    q.push(to);
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
