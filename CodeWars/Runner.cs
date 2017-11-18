using System.Collections.Generic;
using System.Diagnostics;
using System.Threading;
using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk {
    public sealed class Runner {
        private readonly RemoteProcessClient remoteProcessClient;
        private readonly string token;

        public static void Main(string[] args) {
#if DEBUG
            Process.Start("G:\\Projects\\AiCup\\CodeWars\\local_runner\\local-runner-sync.bat");
            Thread.Sleep(2000);

            var otherStrategy = new Process
            {
                StartInfo =
                {
                    FileName = @"G:\Projects\AiCup\CodeWars\bin\1.exe",
                    Arguments = "127.0.0.1 " + (31002) + " 0000000000000000",
                    CreateNoWindow = true
                }
            };
            otherStrategy.Start();
            Thread.Sleep(100);
#endif

            new Runner(args.Length == 3 ? args : new[] {"127.0.0.1", "31001", "0000000000000000"}).Run();
        }

        private Runner(IReadOnlyList<string> args)
        {
            remoteProcessClient = new RemoteProcessClient(args[0], int.Parse(args[1]));
            token = args[2];
        }

        public void Run() {
            try {
                remoteProcessClient.WriteTokenMessage(token);
                remoteProcessClient.WriteProtocolVersionMessage();
                remoteProcessClient.ReadTeamSizeMessage();
                Game game = remoteProcessClient.ReadGameContextMessage();

                IStrategy strategy = new MyStrategy();

                PlayerContext playerContext;

                while ((playerContext = remoteProcessClient.ReadPlayerContextMessage()) != null) {
                    Player player = playerContext.Player;
                    if (player == null) {
                        break;
                    }

                    Move move = new Move();
                    strategy.Move(player, playerContext.World, game, move);

                    remoteProcessClient.WriteMoveMessage(move);
                }
            } finally {
                remoteProcessClient.Close();
            }
        }
    }
}