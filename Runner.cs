using Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeTroopers2013.DevKit.CSharpCgdk
{
    public sealed class Runner
    {
        private readonly RemoteProcessClient remoteProcessClient;
        private readonly string token;

        public static void Main(string[] args)
        {
            if (args.Length == 3)
            {
                new Runner(args).run();
            }
            else
            {
                new Runner(new[] {"localhost", "31001", "0000000000000000"}).run();
            }
        }

        private Runner(string[] args)
        {
            remoteProcessClient = new RemoteProcessClient(args[0], int.Parse(args[1]));
            token = args[2];
        }

        public void run()
        {
            try
            {
                remoteProcessClient.WriteToken(token);
                int teamSize = remoteProcessClient.ReadTeamSize();
                remoteProcessClient.WriteProtocolVersion();
                Game game = remoteProcessClient.readGameContext();

                IStrategy[] strategies = new IStrategy[teamSize];

                for (int strategyIndex = 0; strategyIndex < teamSize; ++strategyIndex)
                {
                    strategies[strategyIndex] = new MyStrategy();
                }

                PlayerContext playerContext;

                while ((playerContext = remoteProcessClient.ReadPlayerContext()) != null)
                {
                    Trooper playerTrooper = playerContext.Trooper;

                    Move move = new Move();
                    strategies[playerTrooper.TeammateIndex].Move(playerTrooper, playerContext.World, game, move);
                    remoteProcessClient.WriteMove(move);
                }
            }
            finally
            {
                remoteProcessClient.Close();
            }
        }
    }
}