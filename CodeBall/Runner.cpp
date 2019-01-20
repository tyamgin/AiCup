#include <memory>
#include <iostream>
#include <cstdlib>
#include "Runner.h"
#include "MyStrategy.h"
#include "Const.h"

using namespace model;
using namespace std;

int main(int argc, char* argv[]) {
#ifdef DEBUG
    cerr << "Starting local runner" << endl;

    string runStr = "/Users/tyamgin/Projects/AiCup/CodeBall/local_runner/codeball2018";
    string prevBin = "m43";
#if M_NO_RANDOM
    runStr += " --disable-random";
#endif
    system((runStr + " --nitro true --duration 40000 --seed 25558399 --p2-name " + prevBin + " --p2 tcp-31002 &").c_str());
    cerr << "local runner started" << endl;

    usleep(2000 * 1000);
    system(("/Users/tyamgin/Projects/AiCup/CodeBall/release/" + prevBin + " 127.0.0.1 31002 0000000000000000 &").c_str());
#endif

    if (argc == 4) {
        Runner runner(argv[1], argv[2], argv[3]);
        runner.run();
    } else {
        Runner runner("127.0.0.1", "31001", "0000000000000000");
        runner.run();
    }

    return 0;
}

Runner::Runner(const char* host, const char* port, const char* token)
    : remoteProcessClient(host, atoi(port)), token(token) {
}

void Runner::run() {
    unique_ptr<Strategy> strategy(new MyStrategy);
    unique_ptr<Game> game;
    unordered_map<int, Action> actions;
    remoteProcessClient.write_token(token);
    unique_ptr<Rules> rules = remoteProcessClient.read_rules();
    while ((game = remoteProcessClient.read_game()) != nullptr) {
        actions.clear();
        for (const Robot& robot : game->robots) {
            if (robot.is_teammate) {
                strategy->act(robot, *rules, *game, actions[robot.id]);
            }
        }
        remoteProcessClient.write(actions, strategy->custom_rendering());
    }
}
