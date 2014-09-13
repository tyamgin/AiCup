using System;
using System.Diagnostics;
using System.Threading;
using Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk {
    public sealed class Runner {
        private readonly RemoteProcessClient remoteProcessClient;
        private readonly string token;

        public static void Main(string[] args) {

            Process.Start("D:\\Projects\\AiCup\\CodeHockey\\local_runner\\local-runner.bat");
            Thread.Sleep(2000);
            var oldStrategy = new Process
            {
                StartInfo =
                {
                    FileName = "D:\\Projects\\AiCup\\CodeHockey\\local_runner\\old.exe",
                    Arguments = "127.0.0.1 31002 0000000000000000",
                    CreateNoWindow = true
                }
            };
            oldStrategy.Start();

            if (args.Length == 3) {
                new Runner(args).run();
            } else {
                Console.WriteLine("no");
                new Runner(new[] { "127.0.0.1", "31001", "0000000000000000" }).run();
            }
        }

        private Runner(string[] args) {
            remoteProcessClient = new RemoteProcessClient(args[0], int.Parse(args[1]));
            token = args[2];
        }

        public void run() {
            try {
                remoteProcessClient.WriteTokenMessage(token);
                int teamSize = remoteProcessClient.ReadTeamSizeMessage();
                remoteProcessClient.WriteProtocolVersionMessage();
                Game game = remoteProcessClient.ReadGameContextMessage();

                IStrategy[] strategies = new IStrategy[teamSize];

                for (int strategyIndex = 0; strategyIndex < teamSize; ++strategyIndex) {
                    strategies[strategyIndex] = new MyStrategy();
                }

                PlayerContext playerContext;

                while ((playerContext = remoteProcessClient.ReadPlayerContextMessage()) != null) {
                    Hockeyist[] playerHockeyists = playerContext.Hockeyists;
                    if (playerHockeyists == null || playerHockeyists.Length != teamSize) {
                        break;
                    }

                    Move[] moves = new Move[teamSize];

                    for (int hockeyistIndex = 0; hockeyistIndex < teamSize; ++hockeyistIndex) {
                        Hockeyist playerHockeyist = playerHockeyists[hockeyistIndex];

                        Move move = new Move();
                        moves[hockeyistIndex] = move;
                        strategies[playerHockeyist.TeammateIndex].Move(
                                playerHockeyist, playerContext.World, game, move
                        );
                    }

                    remoteProcessClient.WriteMovesMessage(moves);
                }
            } finally {
                remoteProcessClient.Close();
            }
        }
    }
}