#ifndef CODESIDE_FINDPATH_H
#define CODESIDE_FINDPATH_H

#include <vector>
#include <set>
#include <map>
#include <queue>
#include <memory.h>
#include <algorithm>

struct TState {
    int x;
    int y;
    int timeLeft = 0;
    bool pad = false;

    bool operator <(const TState& state) const {
        if (x != state.x) {
            return x < state.x;
        }
        if (y != state.y) {
            return y < state.y;
        }
        if (timeLeft != state.timeLeft) {
            return timeLeft < state.timeLeft;
        }
        return pad < state.pad;
    }

    bool samePos(const TState& state) const {
        return x == state.x && y == state.y;
    }

    TPoint getPoint() const {
        return TPoint(x * (1 / 6.0), y * (1 / 6.0));
    }
};

std::vector<TPoint> statesToPoints(const std::vector<TState>& states) {
    std::vector<TPoint> res;
    for (const auto& s : states) {
        res.push_back(s.getPoint());
    }
    return res;
}

#define STEPS_PER_CELL 6

const int JUMP_TICKS_COUNT = 33;
const int JUMP_PAD_TICKS_COUNT = 31; // 31.5 actually

const int MAX_MAP_SIZE = 45;
const double STEP_LENGTH = 1.0 / STEPS_PER_CELL;

const int MICROMAP_SIZE = STEPS_PER_CELL * MAX_MAP_SIZE;

#define INF 0x3f3f3f3f
#define D_LEFT 0
#define D_CENTER 1
#define D_RIGHT 2
#define DIRECTION_ORDER {0, -1, 1}

bool isStand[MICROMAP_SIZE][MICROMAP_SIZE];
bool isValid[MICROMAP_SIZE][MICROMAP_SIZE];
bool isBound[MICROMAP_SIZE][MICROMAP_SIZE];
bool isOnLadder[MICROMAP_SIZE][MICROMAP_SIZE];
bool isBlockedMove[3][3][MICROMAP_SIZE][MICROMAP_SIZE];
bool isTouchPad[MICROMAP_SIZE][MICROMAP_SIZE];

using TDfsGoesResult = std::vector<std::pair<TState, double>>;

TDfsGoesResult* dfsGoesCache[MICROMAP_SIZE][MICROMAP_SIZE];
bool _drawMode;
std::set<TState> dfsVisitedStates;
TDfsGoesResult dfsResultBorderPoints;
bool dfsStartMode;
TState dfsTraceTarget;
std::vector<TAction> dfsTraceActResult;
std::vector<TState> dfsTraceStateResult;

class TPathFinder {
public:
    std::vector<std::vector<double>> dist, penalty;
private:
    const TSandbox* env;
    TUnit startUnit;
    TState startState;
    std::vector<std::vector<TState>> prev;

    static TDfsGoesResult _getCellGoesDfs(const TState& state) {
        TDfsGoesResult res;
        const auto dy = 1 + state.pad;
        for (int xDirection : DIRECTION_ORDER) {
            if (isValid[state.x + xDirection][state.y + dy]) {
                if (isBlockedMove[xDirection + D_CENTER][D_CENTER + 1][state.x][state.y]) {
                    continue;
                }
                TState next = {state.x + xDirection, state.y + dy, state.timeLeft - 1, state.pad};
                if (isTouchPad[next.x][next.y]) {
                    next.timeLeft = JUMP_PAD_TICKS_COUNT;
                    next.pad = true;
                }
                res.emplace_back(next, dy);
            }
        }
        return res;
    }

    static void _dfs(TState state, double dist) {
        dfsVisitedStates.insert(state);
        if (dfsStartMode || isBound[state.x][state.y] || state.pad) {
            if (!state.pad || state.timeLeft == 0 || isOnLadder[state.x][state.y] || !isValid[state.x][state.y + 2]) {
                dfsResultBorderPoints.emplace_back(state, dist);
                if (_drawMode) {
                    TDrawUtil().debugPoint(state.getPoint());
                }
            }
        }
        if (state.timeLeft == 0 || (state.pad && isOnLadder[state.x][state.y])) {
            return;
        }
        for (const auto& [to, w] : _getCellGoesDfs(state)) {
            if (!dfsVisitedStates.count(to)) {
                _dfs(to, dist + w);
            }
        }
    }

    static bool _dfsTrace(TState state) {
        dfsVisitedStates.insert(state);
        if (dfsTraceTarget.samePos(state)) {
            if (!state.pad || state.timeLeft == 0 || isOnLadder[state.x][state.y] || !isValid[state.x][state.y + 2]) {
                return true;
            }
        }
        if (state.timeLeft == 0) {
            return false;
        }
        for (const auto& [to, w] : _getCellGoesDfs(state)) {
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

    static TDfsGoesResult* _getJumpGoes(const TState& state, TDfsGoesResult* startModeResult) {
        dfsStartMode = startModeResult != nullptr;

        if (!dfsStartMode && dfsGoesCache[state.x][state.y] != nullptr) {
            return dfsGoesCache[state.x][state.y];
        }

        dfsVisitedStates.clear();
        dfsResultBorderPoints.clear();
        _dfs(state, 0);
        if (!dfsStartMode) {
            dfsGoesCache[state.x][state.y] = new TDfsGoesResult();
            dfsGoesCache[state.x][state.y]->swap(dfsResultBorderPoints);
            return dfsGoesCache[state.x][state.y];
        }
        startModeResult->swap(dfsResultBorderPoints);
        return startModeResult;
    }

public:
    static void initMap() {
        TUnit unit;
        for (int i = 1; i < TLevel::width - 1; i++) {
            for (int di = 0; di < STEPS_PER_CELL; di++) {
                unit.x1 = i + di * STEP_LENGTH - UNIT_HALF_WIDTH;
                unit.x2 = unit.x1 + UNIT_WIDTH;
                for (int j = 1; j < TLevel::height - 1; j++) {
                    for (int dj = 0; dj < STEPS_PER_CELL; dj++) {
                        unit.y1 = j + dj * STEP_LENGTH;
                        unit.y2 = unit.y1 + UNIT_HEIGHT;

                        int ii = i*STEPS_PER_CELL + di;
                        int jj = j*STEPS_PER_CELL + dj;
                        isValid[ii][jj] = unit.approxIdValid();
                        isStand[ii][jj] = isValid[ii][jj] && unit.approxIsStand();
                        if (di == 0 && unit.isOnLadder()) {
                            isStand[ii][jj] = false;
                        }
                        isBound[ii][jj] = (di == 3 || di == 2 || di == 4);
                        isOnLadder[ii][jj] = isStand[ii][jj] && unit.isOnLadder();
                        isTouchPad[ii][jj] = unit.isTouchJumpPad();

                        if (di == 3 && !unit.approxIsStandGround() && unit.approxIsStandGround(STEP_LENGTH)) {
                            isBlockedMove[D_RIGHT][D_CENTER][ii][jj] = true;
                        }
                        if (di == 3 && !unit.approxIsStandGround() && unit.approxIsStandGround(-STEP_LENGTH)) {
                            isBlockedMove[D_LEFT][D_CENTER][ii][jj] = true;
                        }
                        const auto halfStep = STEP_LENGTH / 2;
                        if (!unit.approxIdValid(halfStep, halfStep) || unit.isTouchJumpPad(halfStep, halfStep)) {
                            isBlockedMove[D_RIGHT][D_RIGHT][ii][jj] = true;
                        }
                        if (!unit.approxIdValid(halfStep, -halfStep) || unit.isTouchJumpPad(halfStep, -halfStep)) {
                            isBlockedMove[D_RIGHT][D_LEFT][ii][jj] = true;
                        }
                        if (!unit.approxIdValid(-halfStep, halfStep) || unit.isTouchJumpPad(-halfStep, halfStep)) {
                            isBlockedMove[D_LEFT][D_RIGHT][ii][jj] = true;
                        }
                        if (!unit.approxIdValid(-halfStep, -halfStep) || unit.isTouchJumpPad(-halfStep, -halfStep)) {
                            isBlockedMove[D_LEFT][D_LEFT][ii][jj] = true;
                        }
                    }
                }
            }
        }
    }

    TPathFinder() {
    }

    explicit TPathFinder(const TSandbox* env, const TUnit& start) {
        this->env = env;
        this->startUnit = start;
        this->startState = _getUnitState(this->startUnit);
    }

    bool findPath(const TPoint& target, std::vector<TState>& resultStates, std::vector<TAction>& resultActions) {
        resultStates.clear();
        resultActions.clear();
        _run();
        OP_START(FINDPATH);
        auto ret = _findPath(target, resultStates, resultActions);
        OP_END(FINDPATH);
        return ret;
    }

    bool _findPath(const TPoint& target, std::vector<TState>& resultStates, std::vector<TAction>& resultActions) {
        resultStates.clear();
        resultActions.clear();
        _run();

        TState targetState = _getPointState(target);
        TState selectedTargetState;
        std::pair<int, double> minDist(INF, INF);
        for (int dx = -7; dx <= 7; dx++) {
            for (int dy = -7; dy <= 7; dy++) {
                if (targetState.x + dx > 0 && targetState.x + dx < dist.size() && targetState.y + dy > 0 && targetState.y + dy < dist[0].size()) {
                    auto d = dist[targetState.x + dx][targetState.y + dy];
                    if (d < INF/2) {
                        std::pair<int, double> cand(std::abs(dx) + std::abs(dy), d);
                        if (cand < minDist) {
                            minDist = cand;
                            selectedTargetState.x = targetState.x + dx;
                            selectedTargetState.y = targetState.y + dy;
                        }
                    }
                }
            }
        }
        if (minDist.second >= INF) {
            return false;
        }

        bool qwe = false;
        auto standFix = isStand[startState.x][startState.y] && !startUnit.isStand();
        for (auto s = selectedTargetState; !s.samePos(startState); s = prev[s.x][s.y]) {
            if (prev[s.x][s.y].x == -1) {
                std::cerr << "Error state\n";
                break;
            }
            std::vector<TAction> acts;
            std::vector<TState> stts;
            auto beg = prev[s.x][s.y].samePos(startState);
            if (!_getCellGoesTrace(prev[s.x][s.y], s, beg, standFix, startUnit.isPadFly(), acts, stts)) {
                qwe = true;
                //break;
            }
            resultStates.insert(resultStates.end(), stts.begin(), stts.end());
            resultActions.insert(resultActions.end(), acts.begin(), acts.end());
        }

        auto startStatePoint = startState.getPoint();
        bool fall = isStand[startState.x][startState.y] && !startUnit.approxIsStand();
        if (resultActions.size()) {
            fall |= isStand[startState.x][startState.y] && !startUnit.canJump && resultActions.back().jump;
            fall |= standFix && resultActions.back().jump;
        }

        auto dx = startStatePoint.x - startUnit.position().x;

        if (std::abs(dx) > 1e-10 || fall) {
            TAction firstAction;
            firstAction.velocity = std::min(UNIT_MAX_HORIZONTAL_SPEED, dx * TICKS_PER_SECOND);
            firstAction.jump = (startUnit.y1 - (int)startUnit.y1 > 0.5);
            if (std::abs(dx) > 1e-10) {
                // проверяем, что нужно округлить в другую сторону, если заблокировали
                auto testEnv = *env;
                auto newUnit = testEnv.getUnit(startUnit.id);
                newUnit->action = firstAction;
                testEnv.doTick();
                if (std::abs(_getUnitState(*newUnit).getPoint().x - newUnit->center().x) > 1e-10) {
                    auto tmp = startState;
                    tmp.x += dx < 0 ? 1 : -1;
                    dx = tmp.getPoint().x - startUnit.position().x;
                    firstAction.velocity = std::min(UNIT_MAX_HORIZONTAL_SPEED, dx * TICKS_PER_SECOND);
                }
            }
            resultActions.push_back(firstAction);
        }

        resultStates.push_back(startState);
        std::reverse(resultStates.begin(), resultStates.end());
        std::reverse(resultActions.begin(), resultActions.end());
        return true;
    }

    template<typename TVisitor>
    void traverseReachable(TUnit unit, TVisitor visitor) {
        _run();
        for (int i = 0; i < (int) dist.size(); i++) {
            for (int j = 0; j < (int) dist[0].size(); j++) {
                if (dist[i][j] < INF) {
                    TState state{i, j, 0};
                    unit.x1 = i * STEP_LENGTH - UNIT_HALF_WIDTH;
                    unit.x2 = unit.x1 + UNIT_WIDTH;
                    unit.y1 = j * STEP_LENGTH;
                    unit.y2 = unit.y1 + UNIT_HEIGHT;
                    visitor(dist[i][j], unit, state);
                }
            }
        }
    }

private:
    void _run() {
        if (dist.empty()) {
            OP_START(DIJKSTRA);
            _dijkstra();
            OP_END(DIJKSTRA);
        }
    }

    TState _getPointState(const TPoint& point) {
        TState res;
        res.x = int((point.x + STEP_LENGTH/2) / STEP_LENGTH); // round to nearest
        res.y = int((point.y + 1 / 60.0) / STEP_LENGTH + 1e-8);
        return res;
    }

    TState _getUnitState(const TUnit& unit) {
        TState res = _getPointState(TPoint(unit.x1 + UNIT_HALF_WIDTH, unit.y1));
        res.timeLeft = int(unit.jumpMaxTime * TICKS_PER_SECOND + 1e-10);
        res.pad = unit.isPadFly();
        return res;
    }

    bool _getCellGoesTrace(const TState& state, const TState& end, bool beg, bool standFix, bool pad, std::vector<TAction>& resAct, std::vector<TState>& resState) {
        if (!isTouchPad[state.x][state.y]) {
            for (int xDirection : DIRECTION_ORDER) {
                auto stx = state;
                stx.x += xDirection;
                TAction act;
                act.velocity = xDirection * UNIT_MAX_HORIZONTAL_SPEED;

                // идти по платформе
                if (stx.samePos(end)) {
                    resAct.emplace_back(act);
                    resState.emplace_back(stx);
                    return true;
                }

                // лететь вниз
                stx.y = state.y - 1;
                act.jumpDown = isStand[state.x][state.y];
                if (stx.samePos(end)) {
                    resAct.emplace_back(act);
                    resState.emplace_back(stx);
                    return true;
                }

                // лезть по лестнице вверх
                stx.y = state.y + 1;
                act.jumpDown = false;
                act.jump = true;
                if (stx.samePos(end)) {
                    resAct.emplace_back(act);
                    resState.emplace_back(stx);
                    return true;
                }
            }
        }
        // прыгать

        dfsVisitedStates.clear();
        dfsTraceActResult.clear();
        dfsTraceStateResult.clear();
        dfsTraceTarget = end;
        auto pd = isTouchPad[state.x][state.y] || (pad && beg);
        if (!_dfsTrace({state.x, state.y, beg && !standFix ? state.timeLeft : (pd ? JUMP_PAD_TICKS_COUNT : JUMP_TICKS_COUNT), pd})) {
            std::cerr << "Error trace dfs\n";
            return false;
        }
        resAct = dfsTraceActResult;
        resState = dfsTraceStateResult;
        return true;
    }

    void _dijkstra() {
        dist = std::vector<std::vector<double>>((size_t)TLevel::width * STEPS_PER_CELL, std::vector<double>((size_t)TLevel::height * STEPS_PER_CELL, INF + 1.0));
        penalty = std::vector<std::vector<double>>(TLevel::width * STEPS_PER_CELL, std::vector<double>(TLevel::height * STEPS_PER_CELL, 0));
        prev = std::vector<std::vector<TState>>(TLevel::width * STEPS_PER_CELL, std::vector<TState>(TLevel::height * STEPS_PER_CELL, {-1, -1}));

        for (auto& opp : env->units) {
            if (opp.playerId == TLevel::myId) {
                continue;
            }
            auto oppCenter = opp.center();
            auto oppCenterState = _getPointState(oppCenter);
            for (int di = -40; di <= 40; di++) {
                for (int dj = -40; dj <= 40; dj++) {
                    auto st = TState{oppCenterState.x + di, oppCenterState.y + dj, 0};
                    auto pt = st.getPoint();
                    if (st.x >= 0 && st.x < dist.size() && st.y >= 0 && st.y < dist[0].size()) {
                        const double mx = 10.0;
                        penalty[st.x][st.y] = std::max(0.0, mx - pt.getDistanceTo(oppCenter));
#if M_DRAW_PENALTY
                        TDrawUtil().debug->draw(CustomData::Rect(Vec2Float{float(pt.x), float(pt.y)},
                                                                 Vec2Float{0.05, 0.05},
                                                                 ColorFloat(1, 0, 0, penalty[st.x][st.y] / mx)));
#endif
                    }
                }
            }
        }

        std::priority_queue<std::pair<double, TState>> q;
        TDfsGoesResult startGoes;
        for (const auto& [s, dst] : *_getJumpGoes(startState, &startGoes)) {
            q.push(std::make_pair(-dst, s));
            dist[s.x][s.y] = dst;
            prev[s.x][s.y] = startState;
        }
        while (!q.empty()) {
            auto state = q.top().second;
            auto curDist = -q.top().first;
            q.pop();
            if (curDist > dist[state.x][state.y]) {
                continue;
            }

#define DJ_PUSH(to_x, to_y, to_dist) do {                                           \
                auto dst = to_dist + penalty[to_x][to_y];                           \
                if (dist[state.x][state.y] + dst < dist[to_x][to_y]) {              \
                    dist[to_x][to_y] = dist[state.x][state.y] + dst;                \
                    prev[to_x][to_y] = state;                                       \
                    q.push(std::make_pair(-dist[to_x][to_y], TState{to_x, to_y}));  \
                }                                                                   \
            } while(0)

            if (!isTouchPad[state.x][state.y]) {
                for (int xDirection : DIRECTION_ORDER) {
                    auto stx = state.x + xDirection;
                    auto sty = state.y;

                    // идти по платформе
                    if (isStand[stx][sty] && !isBlockedMove[xDirection + D_CENTER][D_CENTER][state.x][state.y]) {
                        DJ_PUSH(stx, sty, 0.9999);
                    }

                    // лететь вниз
                    sty = state.y - 1;
                    if (isValid[stx][sty] && !isBlockedMove[xDirection + D_CENTER][D_CENTER - 1][state.x][state.y]) {
                        DJ_PUSH(stx, sty, 0.9999);
                    }

                    // лезть по лестнице вверх
                    sty = state.y + 1;
                    if (isOnLadder[state.x][state.y] && isOnLadder[stx][sty]) {
                        DJ_PUSH(stx, sty, 0.9998);
                    }
                }
            }
            // прыгать
            if (isStand[state.x][state.y] && isBound[state.x][state.y]) {
                auto pad = isTouchPad[state.x][state.y];
                auto jumpGoes = _getJumpGoes({state.x, state.y, pad ? JUMP_PAD_TICKS_COUNT : JUMP_TICKS_COUNT, pad}, nullptr);
                for (auto& [to, to_d] : *jumpGoes) {
                    DJ_PUSH(to.x, to.y, to_d);
                }
            }
        }
    }
};

#endif //CODESIDE_FINDPATH_H
