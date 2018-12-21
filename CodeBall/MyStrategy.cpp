#include "MyStrategy.h"
#include "Sandbox.h"

#include <iostream>
using namespace std;

MyStrategy::MyStrategy() { }


class Strat {
public:
    int lastTick = -1;
    Sandbox env;
    Sandbox prevEnv, prevEnv2;

    void checkEvalState() {
        if (prevEnv.hasRandomCollision) {
            cerr << "Random collision. Skip check." << endl;
            return;
        }

        if (env.ball.notEquals(prevEnv.ball, 1e-6)) {
            cerr << "ball position calculated wrong" << endl;
        }
        if (env.ball.velocity.notEquals(prevEnv.ball.velocity, 1e-6)) {
            cerr << "ball velocity calculated wrong" << endl;
        }

        for (auto& me : env.teammates()) {
            auto prev = prevEnv.robot(me->id);
            if (prev->notEquals(*me, 1e-6)) {
                cerr << "position calculated wrong" << endl;
            }
            if (prev->velocity.notEquals(me->velocity, 1e-6)) {
                cerr << "velocity calculated wrong" << endl;
            }
            if (prev->touch != me->touch) {
                cerr << "touch calculated wrong" << endl;
            }
            if (me->touch && prev->touch_normal.notEquals(me->touch_normal, 1e-6)) {
                cerr << "touch_normal calculated wrong" << endl;
            }
            if (prev->radius != me->radius) {
                cerr << "radius calculated wrong" << endl;
            }
            if (prev->nitro_amount != me->nitro_amount) {
                cerr << "nitro_amount calculated wrong" << endl;
            }
        }
    }

    void act(ARobot me, const model::Rules &rules, const model::Game &game, AAction &action) {
        std::vector<int> s = {1,2,3,4,3,5,3};


        ABall ball(game.ball);
        env = Sandbox(game, rules);
        if (env.tick == lastTick) {
            for (auto tm : env.teammates(me.id))
                tm->action = prevEnv.robot(tm->id)->action;
        } else if (env.tick > 0) {
            prevEnv2 = prevEnv;
            prevEnv.doTick();
            checkEvalState();
        }

//        Sandbox tst = env;
//        tst.my.clear();
//        tst.opp.clear();
//        tst.ball = env.ball;
//        tst.ball.x = -22.926647;
//        tst.ball.y = 17.994172;
//        tst.ball.z = 28.956842;
//        tst.ball.velocity = Point(3.139231, 0.547068, -26.599181);
//        tst.doTick();


//        tst.ball.x = -27.995519339371629286;
//        tst.ball.y = 2.9054418436248079516;
//        tst.ball.z = 6.1702947673222912073;
//        tst.ball.velocity = Point(0.57403220611490801684, 6.0725454135943017775, -12.125100730674212457);
//        tst.doTick();
return;

        // Наша стратегия умеет играть только на земле
        // Поэтому, если мы не касаемся земли, будет использовать нитро
        // чтобы как можно быстрее попасть обратно на землю
        if (!me.touch) {
            action.targetVelocity = Point(0.0, -MAX_ENTITY_SPEED, 0.0);
            action.jumpSpeed = 0.0;
            action.useNitro = true;
            return;
        }

        const double JUMP_TIME = 0.2;
        const double MAX_JUMP_HEIGHT = 3.0;

        // Если при прыжке произойдет столкновение с мячом, и мы находимся
        // с той же стороны от мяча, что и наши ворота, прыгнем, тем самым
        // ударив по мячу сильнее в сторону противника
        auto jump = me.getDistanceTo(ball) < BALL_RADIUS + ROBOT_MAX_RADIUS && me.z < ball.z;

        // Так как роботов несколько, определим нашу роль - защитник, или нападающий
        // Нападающим будем в том случае, если есть дружественный робот,
        // находящийся ближе к нашим воротам
        auto is_attacker = game.robots.size() == 2;
        for (const auto &_robot : game.robots) {
            ARobot robot(_robot);
            if (robot.is_teammate && robot.id != me.id) {
                if (robot.z < me.z) {
                    is_attacker = true;
                }
            }
        }

        if (is_attacker) {
            Point selTargetVel;
            double minSpeedDelta = 1e100;

            // Стратегия нападающего:
            // Просимулирем примерное положение мяча в следующие 10 секунд, с точностью 0.1 секунда
            for (auto i = 1; i <= 100; i++) {
                auto t = i * 0.1;
                auto ball_pos = ball + ball.velocity * t;
                // Если мяч не вылетит за пределы арены
                // (произойдет столкновение со стеной, которое мы не рассматриваем),
                // и при этом мяч будет находится ближе к вражеским воротам, чем робот,
                if (ball_pos.z > me.z
                    //&& abs(ball_pos.x) < (rules.arena.width / 2.0)
                    //&& abs(ball_pos.z) < (rules.arena.depth / 2.0)

                        ) {
                    // Посчитаем, с какой скоростью робот должен бежать,
                    // Чтобы прийти туда же, где будет мяч, в то же самое время
                    auto delta_pos = ball_pos - me;
                    delta_pos.y = 0;
                    auto need_speed = delta_pos.length() / t;
                    // Если эта скорость лежит в допустимом отрезке
                    if (need_speed < ROBOT_MAX_GROUND_SPEED || i == 100) {
                        // То это и будет наше текущее дейтвие
                        auto target_velocity = delta_pos.normalized() * min(ROBOT_MAX_GROUND_SPEED, need_speed);

                        action.targetVelocity = target_velocity;
                        action.jumpSpeed = jump ? ROBOT_MAX_JUMP_SPEED : 0.0;
                        action.useNitro = false;
                        return;
                    }

                }
            }
        }

        // Стратегия защитника (или атакующего, не нашедшего хорошего момента для удара):
        // Будем стоять посередине наших ворот
        auto target_pos = Point(0.0, 0.0, -(rules.arena.depth / 2.0) + rules.arena.bottom_radius);
        // Причем, если мяч движется в сторону наших ворот
        if (ball.velocity.z < -EPS) {
            // Найдем время и место, в котором мяч пересечет линию ворот
            auto t = (target_pos.z - ball.z) / ball.velocity.z;
            auto x = ball.x + ball.x * t;
            // Если это место - внутри ворот
            if (abs(x) < (rules.arena.goal_width / 2.0)) {
                // То пойдем защищать его
                target_pos.x = x;
            }
        }

        // Установка нужных полей для желаемого действия
        auto target_velocity = Point(
                target_pos.x - me.x,
                0.0,
                target_pos.z - me.z
        ) * ROBOT_MAX_GROUND_SPEED;

        action.targetVelocity = target_velocity;
        action.jumpSpeed = jump ? ROBOT_MAX_JUMP_SPEED : 0.0;
        action.useNitro = false;
    }
};

Strat strat;

void MyStrategy::act(const model::Robot& me, const model::Rules& rules, const model::Game& game, model::Action& action) {
    cerr << "(" << me.id << ") Tick " << game.current_tick << endl;

    AAction a;
    strat.act(ARobot(me), rules, game, a);
    action.use_nitro = a.useNitro;
    action.jump_speed = a.jumpSpeed;
    action.target_velocity_x = a.targetVelocity.x;
    action.target_velocity_y = a.targetVelocity.y;
    action.target_velocity_z = a.targetVelocity.z;

    strat.env.robot(me.id)->action = a;
    strat.prevEnv = strat.env;
    strat.lastTick = game.current_tick;
}
