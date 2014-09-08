using System;
using System.IO;
using System.Net.Sockets;
using System.Text;
using Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeHockey2014.DevKit.CSharpCgdk {
    public sealed class RemoteProcessClient {
        private const int BufferSizeBytes = 1 << 20;

        private readonly TcpClient client;
        private readonly BinaryReader reader;
        private readonly BinaryWriter writer;

        public RemoteProcessClient(string host, int port) {
            client = new TcpClient(host, port) {
                SendBufferSize = BufferSizeBytes, ReceiveBufferSize = BufferSizeBytes, NoDelay = true
            };

            reader = new BinaryReader(client.GetStream());
            writer = new BinaryWriter(client.GetStream());
        }

        public void WriteTokenMessage(string token) {
            WriteEnum((sbyte?) MessageType.AuthenticationToken);
            WriteString(token);
            writer.Flush();
        }

        public int ReadTeamSizeMessage() {
            EnsureMessageType((MessageType) ReadEnum(), MessageType.TeamSize);
            return ReadInt();
        }

        public void WriteProtocolVersionMessage() {
            WriteEnum((sbyte?) MessageType.ProtocolVersion);
            WriteInt(1);
            writer.Flush();
        }

        public Game ReadGameContextMessage() {
            EnsureMessageType((MessageType) ReadEnum(), MessageType.GameContext);
            return ReadGame();
        }

        public PlayerContext ReadPlayerContextMessage() {
            MessageType messageType = (MessageType) ReadEnum();
            if (messageType == MessageType.GameOver) {
                return null;
            }

            EnsureMessageType(messageType, MessageType.PlayerContext);
            return ReadPlayerContext();
        }

        public void WriteMovesMessage(Move[] moves) {
            WriteEnum((sbyte?) MessageType.Moves);
            WriteMoves(moves);
            writer.Flush();
        }

        public void Close() {
            client.Close();
        }

        private Game ReadGame() {
            if (!ReadBoolean()) {
                return null;
            }

            return new Game(
                    ReadLong(), ReadInt(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(),
                    ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadInt(), ReadInt(), ReadInt(), ReadInt(),
                    ReadInt(), ReadInt(), ReadDouble(), ReadDouble(), ReadDouble(), ReadInt(), ReadDouble(),
                    ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadInt(), ReadDouble(),
                    ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(),
                    ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(),
                    ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(),
                    ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadInt(), ReadInt(), ReadInt(), ReadInt(),
                    ReadInt(), ReadInt(), ReadInt(), ReadInt(), ReadInt(), ReadInt(), ReadInt(), ReadInt(), ReadInt(),
                    ReadInt(), ReadDouble(), ReadDouble()
            );
        }

        private void WriteGame(Game game) {
            if (game == null) {
                WriteBoolean(false);
            } else {
                WriteBoolean(true);

                WriteLong(game.RandomSeed);
                WriteInt(game.TickCount);
                WriteDouble(game.WorldWidth);
                WriteDouble(game.WorldHeight);
                WriteDouble(game.GoalNetTop);
                WriteDouble(game.GoalNetWidth);
                WriteDouble(game.GoalNetHeight);
                WriteDouble(game.RinkTop);
                WriteDouble(game.RinkLeft);
                WriteDouble(game.RinkBottom);
                WriteDouble(game.RinkRight);
                WriteInt(game.AfterGoalStateTickCount);
                WriteInt(game.OvertimeTickCount);
                WriteInt(game.DefaultActionCooldownTicks);
                WriteInt(game.SwingActionCooldownTicks);
                WriteInt(game.CancelStrikeActionCooldownTicks);
                WriteInt(game.ActionCooldownTicksAfterLosingPuck);
                WriteDouble(game.StickLength);
                WriteDouble(game.StickSector);
                WriteDouble(game.PassSector);
                WriteInt(game.HockeyistAttributeBaseValue);
                WriteDouble(game.MinActionChance);
                WriteDouble(game.MaxActionChance);
                WriteDouble(game.StrikeAngleDeviation);
                WriteDouble(game.PassAngleDeviation);
                WriteDouble(game.PickUpPuckBaseChance);
                WriteDouble(game.TakePuckAwayBaseChance);
                WriteInt(game.MaxEffectiveSwingTicks);
                WriteDouble(game.StrikePowerBaseFactor);
                WriteDouble(game.StrikePowerGrowthFactor);
                WriteDouble(game.StrikePuckBaseChance);
                WriteDouble(game.KnockdownChanceFactor);
                WriteDouble(game.KnockdownTicksFactor);
                WriteDouble(game.MaxSpeedToAllowSubstitute);
                WriteDouble(game.SubstitutionAreaHeight);
                WriteDouble(game.PassPowerFactor);
                WriteDouble(game.HockeyistMaxStamina);
                WriteDouble(game.ActiveHockeyistStaminaGrowthPerTick);
                WriteDouble(game.RestingHockeyistStaminaGrowthPerTick);
                WriteDouble(game.ZeroStaminaHockeyistEffectivenessFactor);
                WriteDouble(game.SpeedUpStaminaCostFactor);
                WriteDouble(game.TurnStaminaCostFactor);
                WriteDouble(game.TakePuckStaminaCost);
                WriteDouble(game.SwingStaminaCost);
                WriteDouble(game.StrikeStaminaBaseCost);
                WriteDouble(game.StrikeStaminaCostGrowthFactor);
                WriteDouble(game.CancelStrikeStaminaCost);
                WriteDouble(game.PassStaminaCost);
                WriteDouble(game.GoalieMaxSpeed);
                WriteDouble(game.HockeyistMaxSpeed);
                WriteDouble(game.StruckHockeyistInitialSpeedFactor);
                WriteDouble(game.HockeyistSpeedUpFactor);
                WriteDouble(game.HockeyistSpeedDownFactor);
                WriteDouble(game.HockeyistTurnAngleFactor);
                WriteInt(game.VersatileHockeyistStrength);
                WriteInt(game.VersatileHockeyistEndurance);
                WriteInt(game.VersatileHockeyistDexterity);
                WriteInt(game.VersatileHockeyistAgility);
                WriteInt(game.ForwardHockeyistStrength);
                WriteInt(game.ForwardHockeyistEndurance);
                WriteInt(game.ForwardHockeyistDexterity);
                WriteInt(game.ForwardHockeyistAgility);
                WriteInt(game.DefencemanHockeyistStrength);
                WriteInt(game.DefencemanHockeyistEndurance);
                WriteInt(game.DefencemanHockeyistDexterity);
                WriteInt(game.DefencemanHockeyistAgility);
                WriteInt(game.MinRandomHockeyistParameter);
                WriteInt(game.MaxRandomHockeyistParameter);
                WriteDouble(game.StruckPuckInitialSpeedFactor);
                WriteDouble(game.PuckBindingRange);
            }
        }

        private Game[] ReadGames() {
            int gameCount = ReadInt();
            if (gameCount < 0) {
                return null;
            }

            Game[] games = new Game[gameCount];

            for (int gameIndex = 0; gameIndex < gameCount; ++gameIndex) {
                games[gameIndex] = ReadGame();
            }

            return games;
        }

        private void WriteGames(Game[] games) {
            if (games == null) {
                WriteInt(-1);
            } else {
                int gameCount = games.Length;
                WriteInt(gameCount);

                for (int gameIndex = 0; gameIndex < gameCount; ++gameIndex) {
                    WriteGame(games[gameIndex]);
                }
            }
        }

        private Hockeyist ReadHockeyist() {
            if (!ReadBoolean()) {
                return null;
            }

            return new Hockeyist(
                    ReadLong(), ReadLong(), ReadInt(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(),
                    ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadBoolean(), (HockeyistType) ReadEnum(),
                    ReadInt(), ReadInt(), ReadInt(), ReadInt(), ReadDouble(), (HockeyistState) ReadEnum(), ReadInt(),
                    ReadInt(), ReadInt(), ReadInt(), (ActionType?) ReadEnum(), ReadBoolean() ? (int?) ReadInt() : null
            );
        }

        private void WriteHockeyist(Hockeyist hockeyist) {
            if (hockeyist == null) {
                WriteBoolean(false);
            } else {
                WriteBoolean(true);

                WriteLong(hockeyist.Id);
                WriteLong(hockeyist.PlayerId);
                WriteInt(hockeyist.TeammateIndex);
                WriteDouble(hockeyist.Mass);
                WriteDouble(hockeyist.Radius);
                WriteDouble(hockeyist.X);
                WriteDouble(hockeyist.Y);
                WriteDouble(hockeyist.SpeedX);
                WriteDouble(hockeyist.SpeedY);
                WriteDouble(hockeyist.Angle);
                WriteDouble(hockeyist.AngularSpeed);
                WriteBoolean(hockeyist.IsTeammate);
                WriteEnum((sbyte?) hockeyist.Type);
                WriteInt(hockeyist.Strength);
                WriteInt(hockeyist.Endurance);
                WriteInt(hockeyist.Dexterity);
                WriteInt(hockeyist.Agility);
                WriteDouble(hockeyist.Stamina);
                WriteEnum((sbyte?) hockeyist.State);
                WriteInt(hockeyist.OriginalPositionIndex);
                WriteInt(hockeyist.RemainingKnockdownTicks);
                WriteInt(hockeyist.RemainingCooldownTicks);
                WriteInt(hockeyist.SwingTicks);
                WriteEnum((sbyte?) hockeyist.LastAction);
                if (hockeyist.LastActionTick == null) {
                    WriteBoolean(false);
                } else {
                    WriteBoolean(true);
                    WriteInt((int) hockeyist.LastActionTick);
                }
            }
        }

        private Hockeyist[] ReadHockeyists() {
            int hockeyistCount = ReadInt();
            if (hockeyistCount < 0) {
                return null;
            }

            Hockeyist[] hockeyists = new Hockeyist[hockeyistCount];

            for (int hockeyistIndex = 0; hockeyistIndex < hockeyistCount; ++hockeyistIndex) {
                hockeyists[hockeyistIndex] = ReadHockeyist();
            }

            return hockeyists;
        }

        private void WriteHockeyists(Hockeyist[] hockeyists) {
            if (hockeyists == null) {
                WriteInt(-1);
            } else {
                int hockeyistCount = hockeyists.Length;
                WriteInt(hockeyistCount);

                for (int hockeyistIndex = 0; hockeyistIndex < hockeyistCount; ++hockeyistIndex) {
                    WriteHockeyist(hockeyists[hockeyistIndex]);
                }
            }
        }

        private Move ReadMove() {
            if (!ReadBoolean()) {
                return null;
            }

            Move move = new Move();

            move.SpeedUp = ReadDouble();
            move.Turn = ReadDouble();
            move.Action = (ActionType) ReadEnum();
            if (move.Action == ActionType.Pass) {
                move.PassPower = ReadDouble();
                move.PassAngle = ReadDouble();
            } else if (move.Action == ActionType.Substitute) {
                move.TeammateIndex = ReadInt();
            }

            return move;
        }

        private void WriteMove(Move move) {
            if (move == null) {
                WriteBoolean(false);
            } else {
                WriteBoolean(true);

                WriteDouble(move.SpeedUp);
                WriteDouble(move.Turn);
                WriteEnum((sbyte?) move.Action);
                if (move.Action == ActionType.Pass) {
                    WriteDouble(move.PassPower);
                    WriteDouble(move.PassAngle);
                } else if (move.Action == ActionType.Substitute) {
                    WriteInt(move.TeammateIndex);
                }
            }
        }

        private Move[] ReadMoves() {
            int moveCount = ReadInt();
            if (moveCount < 0) {
                return null;
            }

            Move[] moves = new Move[moveCount];

            for (int moveIndex = 0; moveIndex < moveCount; ++moveIndex) {
                moves[moveIndex] = ReadMove();
            }

            return moves;
        }

        private void WriteMoves(Move[] moves) {
            if (moves == null) {
                WriteInt(-1);
            } else {
                int moveCount = moves.Length;
                WriteInt(moveCount);

                for (int moveIndex = 0; moveIndex < moveCount; ++moveIndex) {
                    WriteMove(moves[moveIndex]);
                }
            }
        }

        private Player ReadPlayer() {
            if (!ReadBoolean()) {
                return null;
            }

            return new Player(
                    ReadLong(), ReadBoolean(), ReadString(), ReadInt(), ReadBoolean(), ReadDouble(), ReadDouble(),
                    ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadBoolean(), ReadBoolean()
            );
        }

        private void WritePlayer(Player player) {
            if (player == null) {
                WriteBoolean(false);
            } else {
                WriteBoolean(true);

                WriteLong(player.Id);
                WriteBoolean(player.IsMe);
                WriteString(player.Name);
                WriteInt(player.GoalCount);
                WriteBoolean(player.IsStrategyCrashed);
                WriteDouble(player.NetTop);
                WriteDouble(player.NetLeft);
                WriteDouble(player.NetBottom);
                WriteDouble(player.NetRight);
                WriteDouble(player.NetFront);
                WriteDouble(player.NetBack);
                WriteBoolean(player.IsJustScoredGoal);
                WriteBoolean(player.IsJustMissedGoal);
            }
        }

        private Player[] ReadPlayers() {
            int playerCount = ReadInt();
            if (playerCount < 0) {
                return null;
            }

            Player[] players = new Player[playerCount];

            for (int playerIndex = 0; playerIndex < playerCount; ++playerIndex) {
                players[playerIndex] = ReadPlayer();
            }

            return players;
        }

        private void WritePlayers(Player[] players) {
            if (players == null) {
                WriteInt(-1);
            } else {
                int playerCount = players.Length;
                WriteInt(playerCount);

                for (int playerIndex = 0; playerIndex < playerCount; ++playerIndex) {
                    WritePlayer(players[playerIndex]);
                }
            }
        }

        private PlayerContext ReadPlayerContext() {
            if (!ReadBoolean()) {
                return null;
            }

            return new PlayerContext(ReadHockeyists(), ReadWorld());
        }

        private void WritePlayerContext(PlayerContext playerContext) {
            if (playerContext == null) {
                WriteBoolean(false);
            } else {
                WriteBoolean(true);

                WriteHockeyists(playerContext.Hockeyists);
                WriteWorld(playerContext.World);
            }
        }

        private PlayerContext[] ReadPlayerContexts() {
            int playerContextCount = ReadInt();
            if (playerContextCount < 0) {
                return null;
            }

            PlayerContext[] playerContexts = new PlayerContext[playerContextCount];

            for (int playerContextIndex = 0; playerContextIndex < playerContextCount; ++playerContextIndex) {
                playerContexts[playerContextIndex] = ReadPlayerContext();
            }

            return playerContexts;
        }

        private void WritePlayerContexts(PlayerContext[] playerContexts) {
            if (playerContexts == null) {
                WriteInt(-1);
            } else {
                int playerContextCount = playerContexts.Length;
                WriteInt(playerContextCount);

                for (int playerContextIndex = 0; playerContextIndex < playerContextCount; ++playerContextIndex) {
                    WritePlayerContext(playerContexts[playerContextIndex]);
                }
            }
        }

        private Puck ReadPuck() {
            if (!ReadBoolean()) {
                return null;
            }

            return new Puck(
                    ReadLong(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(),
                    ReadLong(), ReadLong()
            );
        }

        private void WritePuck(Puck puck) {
            if (puck == null) {
                WriteBoolean(false);
            } else {
                WriteBoolean(true);

                WriteLong(puck.Id);
                WriteDouble(puck.Mass);
                WriteDouble(puck.Radius);
                WriteDouble(puck.X);
                WriteDouble(puck.Y);
                WriteDouble(puck.SpeedX);
                WriteDouble(puck.SpeedY);
                WriteLong(puck.OwnerHockeyistId);
                WriteLong(puck.OwnerPlayerId);
            }
        }

        private Puck[] ReadPucks() {
            int puckCount = ReadInt();
            if (puckCount < 0) {
                return null;
            }

            Puck[] pucks = new Puck[puckCount];

            for (int puckIndex = 0; puckIndex < puckCount; ++puckIndex) {
                pucks[puckIndex] = ReadPuck();
            }

            return pucks;
        }

        private void WritePucks(Puck[] pucks) {
            if (pucks == null) {
                WriteInt(-1);
            } else {
                int puckCount = pucks.Length;
                WriteInt(puckCount);

                for (int puckIndex = 0; puckIndex < puckCount; ++puckIndex) {
                    WritePuck(pucks[puckIndex]);
                }
            }
        }

        private World ReadWorld() {
            if (!ReadBoolean()) {
                return null;
            }

            return new World(
                    ReadInt(), ReadInt(), ReadDouble(), ReadDouble(), ReadPlayers(), ReadHockeyists(), ReadPuck()
            );
        }

        private void WriteWorld(World world) {
            if (world == null) {
                WriteBoolean(false);
            } else {
                WriteBoolean(true);

                WriteInt(world.Tick);
                WriteInt(world.TickCount);
                WriteDouble(world.Width);
                WriteDouble(world.Height);
                WritePlayers(world.Players);
                WriteHockeyists(world.Hockeyists);
                WritePuck(world.Puck);
            }
        }

        private World[] ReadWorlds() {
            int worldCount = ReadInt();
            if (worldCount < 0) {
                return null;
            }

            World[] worlds = new World[worldCount];

            for (int worldIndex = 0; worldIndex < worldCount; ++worldIndex) {
                worlds[worldIndex] = ReadWorld();
            }

            return worlds;
        }

        private void WriteWorlds(World[] worlds) {
            if (worlds == null) {
                WriteInt(-1);
            } else {
                int worldCount = worlds.Length;
                WriteInt(worldCount);

                for (int worldIndex = 0; worldIndex < worldCount; ++worldIndex) {
                    WriteWorld(worlds[worldIndex]);
                }
            }
        }

        private static void EnsureMessageType(MessageType actualType, MessageType expectedType) {
            if (actualType != expectedType) {
                throw new ArgumentException(string.Format("Received wrong message [actual={0}, expected={1}].",
                    actualType, expectedType));
            }
        }

        private sbyte? ReadEnum() {
            sbyte value = reader.ReadSByte();
            return value < 0 ? null : (sbyte?) value;
        }

        private void WriteEnum(sbyte? value) {
            writer.Write(value ?? -1);
        }

        private string ReadString() {
            int length = ReadInt();
            if (length == -1) {
                return null;
            }

            return Encoding.UTF8.GetString(ReadBytes(length));
        }

        private void WriteString(string value) {
            if (value == null) {
                WriteInt(-1);
                return;
            }

            byte[] bytes = Encoding.UTF8.GetBytes(value);

            WriteInt(bytes.Length);
            WriteBytes(bytes);
        }

        private bool ReadBoolean() {
            return reader.ReadSByte() != 0;
        }

        private void WriteBoolean(bool value) {
            writer.Write((sbyte) (value ? 1 : 0));
        }

        private int ReadInt() {
            return reader.ReadInt32();
        }

        private void WriteInt(int value) {
            writer.Write(value);
        }

        private long ReadLong() {
            return reader.ReadInt64();
        }

        private void WriteLong(long value) {
            writer.Write(value);
        }

        private double ReadDouble() {
            return reader.ReadDouble();
        }

        private void WriteDouble(double value) {
            writer.Write(value);
        }

        private byte[] ReadBytes(int byteCount) {
            return reader.ReadBytes(byteCount);
        }

        private void WriteBytes(byte[] bytes) {
            writer.Write(bytes);
        }

        private enum MessageType {
            Unknown,
            GameOver,
            AuthenticationToken,
            TeamSize,
            ProtocolVersion,
            GameContext,
            PlayerContext,
            Moves
        }
    }
}