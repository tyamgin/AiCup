using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk {
    public sealed class Runner {
        private readonly RemoteProcessClient remoteProcessClient;
        private readonly string token;

        public static void Main(string[] args) {
            new Runner(args.Length == 3 ? args : new[] { "127.0.0.1", "31001", "0000000000000000" }).Run();
        }

        private Runner(string[] args) {
            remoteProcessClient = new RemoteProcessClient(args[0], int.Parse(args[1]));
            token = args[2];
        }

        public void Run() {
            try {
                remoteProcessClient.WriteTokenMessage(token);
                remoteProcessClient.WriteProtocolVersionMessage();
                int teamSize = remoteProcessClient.ReadTeamSizeMessage();
                Game game = remoteProcessClient.ReadGameContextMessage();

                IStrategy[] strategies = new IStrategy[teamSize];

                for (int strategyIndex = 0; strategyIndex < teamSize; ++strategyIndex) {
                    strategies[strategyIndex] = new MyStrategy();
                }

                PlayerContext playerContext;

                while ((playerContext = remoteProcessClient.ReadPlayerContextMessage()) != null) {
                    Wizard[] playerWizards = playerContext.Wizards;
                    if (playerWizards == null || playerWizards.Length != teamSize) {
                        break;
                    }

                    Move[] moves = new Move[teamSize];

                    for (int wizardIndex = 0; wizardIndex < teamSize; ++wizardIndex) {
                        Wizard playerWizard = playerWizards[wizardIndex];

                        Move move = new Move();
                        moves[wizardIndex] = move;
                        strategies[wizardIndex].Move(playerWizard, playerContext.World, game, move);
                    }

                    remoteProcessClient.WriteMovesMessage(moves);
                }
            } finally {
                remoteProcessClient.Close();
            }
        }
    }
}