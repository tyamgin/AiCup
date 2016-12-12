using System;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Threading;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk
{
    public sealed class Runner {
        private readonly RemoteProcessClient remoteProcessClient;
        private readonly string token;

        public static void Main(string[] args) {
#if DEBUG
            Process.Start("G:\\Projects\\AiCup\\CodeWizards\\local_runner\\local-runner-sync.bat");
            Thread.Sleep(5000);

            for (var i = 0; i < 4; i++)
            {
                var otherStrategy = new Process
                {
                    StartInfo =
                    {
                        FileName = @"G:\Projects\AiCup\CodeWizards\bin\Release\csharp-cgdk.exe",
                        Arguments = "127.0.0.1 " + (31002 + i) + " 0000000000000000",
                        CreateNoWindow = true
                    }
                };
                otherStrategy.Start();
                Thread.Sleep(100);
            }

            //for (var i = 4; i < 9; i++)
            //{
            //    var otherStrategy = new Process
            //    {
            //        StartInfo =
            //        {
            //            FileName = @"G:\Projects\AiCup\CodeWizards\bin\Release\mid.exe",
            //            Arguments = "127.0.0.1 " + (31002 + i) + " 0000000000000000",
            //            CreateNoWindow = true
            //        }
            //    };
            //    otherStrategy.Start();
            //    Thread.Sleep(100);
            //}
#endif      
            new Runner(args.Length == 3 ? args : new[] {"127.0.0.1", "31001", "0000000000000000"}).Run();
        }

        private Runner(string[] args) {
            remoteProcessClient = new RemoteProcessClient(args[0], int.Parse(args[1]));
            token = args[2];
        }

        public void Run() {
            try
            {
                remoteProcessClient.WriteTokenMessage(token);
                remoteProcessClient.WriteProtocolVersionMessage();
                int teamSize = remoteProcessClient.ReadTeamSizeMessage();
                Game game = remoteProcessClient.ReadGameContextMessage();

                IStrategy[] strategies = new IStrategy[teamSize];

                for (int strategyIndex = 0; strategyIndex < teamSize; ++strategyIndex)
                {
                    strategies[strategyIndex] = new MyStrategy();
                }

                PlayerContext playerContext;

                while ((playerContext = remoteProcessClient.ReadPlayerContextMessage()) != null)
                {
                    Wizard[] playerWizards = playerContext.Wizards;
                    if (playerWizards == null || playerWizards.Length != teamSize)
                    {
                        break;
                    }

                    Move[] moves = new Move[teamSize];

                    for (int wizardIndex = 0; wizardIndex < teamSize; ++wizardIndex)
                    {
                        Wizard playerWizard = playerWizards[wizardIndex];

                        Move move = new Move();
                        moves[wizardIndex] = move;
                        strategies[wizardIndex].Move(playerWizard, playerContext.World, game, move);
                    }

                    remoteProcessClient.WriteMovesMessage(moves);
                }
            }
            //catch (Exception e)
            //{
            //    File.WriteAllText("G:\\log.txt", e.Message + "\n" + e.StackTrace);
            //}
            finally
            {
                remoteProcessClient.Close();
            }
        }
    }
}