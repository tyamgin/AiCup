using System;
using System.IO;
using System.Net.Sockets;
using System.Text;
using Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeRacing2015.DevKit.CSharpCgdk {
    public sealed class RemoteProcessClient {
        private const int BufferSizeBytes = 1 << 20;

        private readonly TcpClient client;
        private readonly BinaryReader reader;
        private readonly BinaryWriter writer;

        private string mapName;
        private TileType[][] tilesXY;
        private int[][] waypoints;
        private Direction? startingDirection;

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
            WriteInt(2);
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

        private Bonus ReadBonus() {
            if (!ReadBoolean()) {
                return null;
            }

            return new Bonus(
                    ReadLong(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(),
                    ReadDouble(), ReadDouble(), ReadDouble(), (BonusType) ReadEnum()
            );
        }

        private void WriteBonus(Bonus bonus) {
            if (bonus == null) {
                WriteBoolean(false);
                return;
            }

            WriteBoolean(true);

            WriteLong(bonus.Id);
            WriteDouble(bonus.Mass);
            WriteDouble(bonus.X);
            WriteDouble(bonus.Y);
            WriteDouble(bonus.SpeedX);
            WriteDouble(bonus.SpeedY);
            WriteDouble(bonus.Angle);
            WriteDouble(bonus.AngularSpeed);
            WriteDouble(bonus.Width);
            WriteDouble(bonus.Height);
            WriteEnum((sbyte?) bonus.Type);
        }

        private Bonus[] ReadBonuses() {
            int bonusCount = ReadInt();
            if (bonusCount < 0) {
                return null;
            }

            Bonus[] bonuses = new Bonus[bonusCount];

            for (int bonusIndex = 0; bonusIndex < bonusCount; ++bonusIndex) {
                bonuses[bonusIndex] = ReadBonus();
            }

            return bonuses;
        }

        private void WriteBonuses(Bonus[] bonuses) {
            if (bonuses == null) {
                WriteInt(-1);
                return;
            }

            int bonusCount = bonuses.Length;
            WriteInt(bonusCount);

            for (int bonusIndex = 0; bonusIndex < bonusCount; ++bonusIndex) {
                WriteBonus(bonuses[bonusIndex]);
            }
        }

        private Car ReadCar() {
            if (!ReadBoolean()) {
                return null;
            }

            return new Car(
                    ReadLong(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(),
                    ReadDouble(), ReadDouble(), ReadDouble(), ReadLong(), ReadInt(), ReadBoolean(),
                    (CarType) ReadEnum(), ReadInt(), ReadInt(), ReadInt(), ReadInt(), ReadInt(), ReadInt(), ReadInt(),
                    ReadInt(), ReadDouble(), ReadDouble(), ReadDouble(), ReadInt(), ReadInt(), ReadInt(), ReadBoolean()
            );
        }

        private void WriteCar(Car car) {
            if (car == null) {
                WriteBoolean(false);
                return;
            }

            WriteBoolean(true);

            WriteLong(car.Id);
            WriteDouble(car.Mass);
            WriteDouble(car.X);
            WriteDouble(car.Y);
            WriteDouble(car.SpeedX);
            WriteDouble(car.SpeedY);
            WriteDouble(car.Angle);
            WriteDouble(car.AngularSpeed);
            WriteDouble(car.Width);
            WriteDouble(car.Height);
            WriteLong(car.PlayerId);
            WriteInt(car.TeammateIndex);
            WriteBoolean(car.IsTeammate);
            WriteEnum((sbyte?) car.Type);
            WriteInt(car.ProjectileCount);
            WriteInt(car.NitroChargeCount);
            WriteInt(car.OilCanisterCount);
            WriteInt(car.RemainingProjectileCooldownTicks);
            WriteInt(car.RemainingNitroCooldownTicks);
            WriteInt(car.RemainingOilCooldownTicks);
            WriteInt(car.RemainingNitroTicks);
            WriteInt(car.RemainingOiledTicks);
            WriteDouble(car.Durability);
            WriteDouble(car.EnginePower);
            WriteDouble(car.WheelTurn);
            WriteInt(car.NextWaypointIndex);
            WriteInt(car.NextWaypointX);
            WriteInt(car.NextWaypointY);
            WriteBoolean(car.IsFinishedTrack);
        }

        private Car[] ReadCars() {
            int carCount = ReadInt();
            if (carCount < 0) {
                return null;
            }

            Car[] cars = new Car[carCount];

            for (int carIndex = 0; carIndex < carCount; ++carIndex) {
                cars[carIndex] = ReadCar();
            }

            return cars;
        }

        private void WriteCars(Car[] cars) {
            if (cars == null) {
                WriteInt(-1);
                return;
            }

            int carCount = cars.Length;
            WriteInt(carCount);

            for (int carIndex = 0; carIndex < carCount; ++carIndex) {
                WriteCar(cars[carIndex]);
            }
        }

        private Game ReadGame() {
            if (!ReadBoolean()) {
                return null;
            }

            return new Game(
                    ReadLong(), ReadInt(), ReadInt(), ReadInt(), ReadDouble(), ReadDouble(), ReadInt(), ReadInt(),
                    ReadInt(), ReadDouble(), ReadInts(), ReadInt(), ReadDouble(), ReadDouble(), ReadInt(), ReadDouble(),
                    ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(),
                    ReadDouble(), ReadDouble(), ReadInt(), ReadInt(), ReadInt(), ReadDouble(), ReadInt(), ReadInt(),
                    ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(),
                    ReadDouble(), ReadInt(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(),
                    ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(),
                    ReadInt(), ReadInt()
            );
        }

        private void WriteGame(Game game) {
            if (game == null) {
                WriteBoolean(false);
                return;
            }

            WriteBoolean(true);

            WriteLong(game.RandomSeed);
            WriteInt(game.TickCount);
            WriteInt(game.WorldWidth);
            WriteInt(game.WorldHeight);
            WriteDouble(game.TrackTileSize);
            WriteDouble(game.TrackTileMargin);
            WriteInt(game.LapCount);
            WriteInt(game.LapTickCount);
            WriteInt(game.InitialFreezeDurationTicks);
            WriteDouble(game.BurningTimeDurationFactor);
            WriteInts(game.FinishTrackScores);
            WriteInt(game.FinishLapScore);
            WriteDouble(game.LapWaypointsSummaryScoreFactor);
            WriteDouble(game.CarDamageScoreFactor);
            WriteInt(game.CarEliminationScore);
            WriteDouble(game.CarWidth);
            WriteDouble(game.CarHeight);
            WriteDouble(game.CarEnginePowerChangePerTick);
            WriteDouble(game.CarWheelTurnChangePerTick);
            WriteDouble(game.CarAngularSpeedFactor);
            WriteDouble(game.CarMovementAirFrictionFactor);
            WriteDouble(game.CarRotationAirFrictionFactor);
            WriteDouble(game.CarLengthwiseMovementFrictionFactor);
            WriteDouble(game.CarCrosswiseMovementFrictionFactor);
            WriteDouble(game.CarRotationFrictionFactor);
            WriteInt(game.ThrowProjectileCooldownTicks);
            WriteInt(game.UseNitroCooldownTicks);
            WriteInt(game.SpillOilCooldownTicks);
            WriteDouble(game.NitroEnginePowerFactor);
            WriteInt(game.NitroDurationTicks);
            WriteInt(game.CarReactivationTimeTicks);
            WriteDouble(game.BuggyMass);
            WriteDouble(game.BuggyEngineForwardPower);
            WriteDouble(game.BuggyEngineRearPower);
            WriteDouble(game.JeepMass);
            WriteDouble(game.JeepEngineForwardPower);
            WriteDouble(game.JeepEngineRearPower);
            WriteDouble(game.BonusSize);
            WriteDouble(game.BonusMass);
            WriteInt(game.PureScoreAmount);
            WriteDouble(game.WasherRadius);
            WriteDouble(game.WasherMass);
            WriteDouble(game.WasherInitialSpeed);
            WriteDouble(game.WasherDamage);
            WriteDouble(game.SideWasherAngle);
            WriteDouble(game.TireRadius);
            WriteDouble(game.TireMass);
            WriteDouble(game.TireInitialSpeed);
            WriteDouble(game.TireDamageFactor);
            WriteDouble(game.TireDisappearSpeedFactor);
            WriteDouble(game.OilSlickInitialRange);
            WriteDouble(game.OilSlickRadius);
            WriteInt(game.OilSlickLifetime);
            WriteInt(game.MaxOiledStateDurationTicks);
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
                return;
            }

            int gameCount = games.Length;
            WriteInt(gameCount);

            for (int gameIndex = 0; gameIndex < gameCount; ++gameIndex) {
                WriteGame(games[gameIndex]);
            }
        }

        private Move ReadMove() {
            if (!ReadBoolean()) {
                return null;
            }

            Move move = new Move();

            move.EnginePower = ReadDouble();
            move.IsBrake = ReadBoolean();
            move.WheelTurn = ReadDouble();
            move.IsThrowProjectile = ReadBoolean();
            move.IsUseNitro = ReadBoolean();
            move.IsSpillOil = ReadBoolean();

            return move;
        }

        private void WriteMove(Move move) {
            if (move == null) {
                WriteBoolean(false);
                return;
            }

            WriteBoolean(true);

            WriteDouble(move.EnginePower);
            WriteBoolean(move.IsBrake);
            WriteDouble(move.WheelTurn);
            WriteBoolean(move.IsThrowProjectile);
            WriteBoolean(move.IsUseNitro);
            WriteBoolean(move.IsSpillOil);
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
                return;
            }

            int moveCount = moves.Length;
            WriteInt(moveCount);

            for (int moveIndex = 0; moveIndex < moveCount; ++moveIndex) {
                WriteMove(moves[moveIndex]);
            }
        }

        private OilSlick ReadOilSlick() {
            if (!ReadBoolean()) {
                return null;
            }

            return new OilSlick(
                    ReadLong(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(),
                    ReadDouble(), ReadDouble(), ReadInt()
            );
        }

        private void WriteOilSlick(OilSlick oilSlick) {
            if (oilSlick == null) {
                WriteBoolean(false);
                return;
            }

            WriteBoolean(true);

            WriteLong(oilSlick.Id);
            WriteDouble(oilSlick.Mass);
            WriteDouble(oilSlick.X);
            WriteDouble(oilSlick.Y);
            WriteDouble(oilSlick.SpeedX);
            WriteDouble(oilSlick.SpeedY);
            WriteDouble(oilSlick.Angle);
            WriteDouble(oilSlick.AngularSpeed);
            WriteDouble(oilSlick.Radius);
            WriteInt(oilSlick.RemainingLifetime);
        }

        private OilSlick[] ReadOilSlicks() {
            int oilSlickCount = ReadInt();
            if (oilSlickCount < 0) {
                return null;
            }

            OilSlick[] oilSlicks = new OilSlick[oilSlickCount];

            for (int oilSlickIndex = 0; oilSlickIndex < oilSlickCount; ++oilSlickIndex) {
                oilSlicks[oilSlickIndex] = ReadOilSlick();
            }

            return oilSlicks;
        }

        private void WriteOilSlicks(OilSlick[] oilSlicks) {
            if (oilSlicks == null) {
                WriteInt(-1);
                return;
            }

            int oilSlickCount = oilSlicks.Length;
            WriteInt(oilSlickCount);

            for (int oilSlickIndex = 0; oilSlickIndex < oilSlickCount; ++oilSlickIndex) {
                WriteOilSlick(oilSlicks[oilSlickIndex]);
            }
        }

        private Player ReadPlayer() {
            if (!ReadBoolean()) {
                return null;
            }

            return new Player(ReadLong(), ReadBoolean(), ReadString(), ReadBoolean(), ReadInt());
        }

        private void WritePlayer(Player player) {
            if (player == null) {
                WriteBoolean(false);
                return;
            }

            WriteBoolean(true);

            WriteLong(player.Id);
            WriteBoolean(player.IsMe);
            WriteString(player.Name);
            WriteBoolean(player.IsStrategyCrashed);
            WriteInt(player.Score);
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
                return;
            }

            int playerCount = players.Length;
            WriteInt(playerCount);

            for (int playerIndex = 0; playerIndex < playerCount; ++playerIndex) {
                WritePlayer(players[playerIndex]);
            }
        }

        private PlayerContext ReadPlayerContext() {
            if (!ReadBoolean()) {
                return null;
            }

            return new PlayerContext(ReadCars(), ReadWorld());
        }

        private void WritePlayerContext(PlayerContext playerContext) {
            if (playerContext == null) {
                WriteBoolean(false);
                return;
            }

            WriteBoolean(true);

            WriteCars(playerContext.Cars);
            WriteWorld(playerContext.World);
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
                return;
            }

            int playerContextCount = playerContexts.Length;
            WriteInt(playerContextCount);

            for (int playerContextIndex = 0; playerContextIndex < playerContextCount; ++playerContextIndex) {
                WritePlayerContext(playerContexts[playerContextIndex]);
            }
        }

        private Projectile ReadProjectile() {
            if (!ReadBoolean()) {
                return null;
            }

            return new Projectile(
                    ReadLong(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(),
                    ReadDouble(), ReadDouble(), ReadLong(), ReadLong(), (ProjectileType) ReadEnum()
            );
        }

        private void WriteProjectile(Projectile projectile) {
            if (projectile == null) {
                WriteBoolean(false);
                return;
            }

            WriteBoolean(true);

            WriteLong(projectile.Id);
            WriteDouble(projectile.Mass);
            WriteDouble(projectile.X);
            WriteDouble(projectile.Y);
            WriteDouble(projectile.SpeedX);
            WriteDouble(projectile.SpeedY);
            WriteDouble(projectile.Angle);
            WriteDouble(projectile.AngularSpeed);
            WriteDouble(projectile.Radius);
            WriteLong(projectile.CarId);
            WriteLong(projectile.PlayerId);
            WriteEnum((sbyte?) projectile.Type);
        }

        private Projectile[] ReadProjectiles() {
            int projectileCount = ReadInt();
            if (projectileCount < 0) {
                return null;
            }

            Projectile[] projectiles = new Projectile[projectileCount];

            for (int projectileIndex = 0; projectileIndex < projectileCount; ++projectileIndex) {
                projectiles[projectileIndex] = ReadProjectile();
            }

            return projectiles;
        }

        private void WriteProjectiles(Projectile[] projectiles) {
            if (projectiles == null) {
                WriteInt(-1);
                return;
            }

            int projectileCount = projectiles.Length;
            WriteInt(projectileCount);

            for (int projectileIndex = 0; projectileIndex < projectileCount; ++projectileIndex) {
                WriteProjectile(projectiles[projectileIndex]);
            }
        }

        private TileType[][] ReadTilesXY() {
            TileType[][] newTilesXY = ReadEnums2D<TileType>();

            if (newTilesXY != null && newTilesXY.Length > 0) {
                tilesXY = newTilesXY;
            }

            return tilesXY;
        }

        private World ReadWorld() {
            if (!ReadBoolean()) {
                return null;
            }

            return new World(
                    ReadInt(), ReadInt(), ReadInt(), ReadInt(), ReadInt(), ReadPlayers(), ReadCars(), ReadProjectiles(),
                    ReadBonuses(), ReadOilSlicks(),
                    mapName == null ? mapName = ReadString() : mapName,
                    ReadTilesXY(),
                    waypoints == null ? waypoints = ReadInts2D() : waypoints,
                    (Direction) (startingDirection.HasValue ? startingDirection : startingDirection = (Direction?) ReadEnum())
            );
        }

        private void WriteWorld(World world) {
            if (world == null) {
                WriteBoolean(false);
                return;
            }

            WriteBoolean(true);

            WriteInt(world.Tick);
            WriteInt(world.TickCount);
            WriteInt(world.LastTickIndex);
            WriteInt(world.Width);
            WriteInt(world.Height);
            WritePlayers(world.Players);
            WriteCars(world.Cars);
            WriteProjectiles(world.Projectiles);
            WriteBonuses(world.Bonuses);
            WriteOilSlicks(world.OilSlicks);
            WriteString(world.MapName);
            WriteEnums2D<TileType>(world.TilesXY);
            WriteInts2D(world.Waypoints);
            WriteEnum((sbyte?) world.StartingDirection);
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
                return;
            }

            int worldCount = worlds.Length;
            WriteInt(worldCount);

            for (int worldIndex = 0; worldIndex < worldCount; ++worldIndex) {
                WriteWorld(worlds[worldIndex]);
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

        private E[] ReadEnums<E>() where E : IComparable, IFormattable, IConvertible {
            int count = ReadInt();
            if (count < 0) {
                return null;
            }

            E[] enums = new E[count];

            for (int i = 0; i < count; ++i) {
                enums[i] = (E) Enum.ToObject(typeof(E), ReadEnum());
            }

            return enums;
        }

        private E[][] ReadEnums2D<E>() where E : IComparable, IFormattable, IConvertible {
            int count = ReadInt();
            if (count < 0) {
                return null;
            }

            E[][] enums = new E[count][];

            for (int i = 0; i < count; ++i) {
                enums[i] = ReadEnums<E>();
            }

            return enums;
        }

        private void WriteEnum(sbyte? value) {
            writer.Write(value ?? -1);
        }

        private void WriteEnums<E>(E[] enums) where E : IComparable, IFormattable, IConvertible {
            if (enums == null) {
                WriteInt(-1);
                return;
            }

            int count = enums.Length;
            WriteInt(count);

            for (int i = 0; i < count; ++i) {
                WriteEnum(enums[i].ToSByte(null));
            }
        }

        private void WriteEnums2D<E>(E[][] enums) where E : IComparable, IFormattable, IConvertible {
            if (enums == null) {
                WriteInt(-1);
                return;
            }

            int count = enums.Length;
            WriteInt(count);

            for (int i = 0; i < count; ++i) {
                WriteEnums(enums[i]);
            }
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

        private int[] ReadInts() {
            int count = ReadInt();
            if (count < 0) {
                return null;
            }

            int[] ints = new int[count];

            for (int i = 0; i < count; ++i) {
                ints[i] = ReadInt();
            }

            return ints;
        }

        private int[][] ReadInts2D() {
            int count = ReadInt();
            if (count < 0) {
                return null;
            }

            int[][] ints = new int[count][];

            for (int i = 0; i < count; ++i) {
                ints[i] = ReadInts();
            }

            return ints;
        }

        private void WriteInt(int value) {
            writer.Write(value);
        }

        private void WriteInts(int[] ints) {
            if (ints == null) {
                WriteInt(-1);
                return;
            }

            int count = ints.Length;
            WriteInt(count);

            for (int i = 0; i < count; ++i) {
                WriteInt(ints[i]);
            }
        }

        private void WriteInts2D(int[][] ints) {
            if (ints == null) {
                WriteInt(-1);
            }

            int count = ints.Length;
            WriteInt(count);

            for (int i = 0; i < count; ++i) {
                WriteInts(ints[i]);
            }
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