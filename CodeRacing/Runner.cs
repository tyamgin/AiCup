using Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk {
    public sealed class Runner {
        private readonly RemoteProcessClient remoteProcessClient;
        private readonly string token;

        public static void Main(string[] args) {
            if (args.Length == 3) {
                new Runner(args).run();
            } else {
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
                    Car[] playerCars = playerContext.Cars;
                    if (playerCars == null || playerCars.Length != teamSize) {
                        break;
                    }

                    Move[] moves = new Move[teamSize];

                    for (int carIndex = 0; carIndex < teamSize; ++carIndex) {
                        Car playerCar = playerCars[carIndex];

                        Move move = new Move();
                        moves[carIndex] = move;
                        strategies[playerCar.TeammateIndex].Move(playerCar, playerContext.World, game, move);
                    }

                    remoteProcessClient.WriteMovesMessage(moves);
                }
            } finally {
                remoteProcessClient.Close();
            }
        }
    }
}