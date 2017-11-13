using System;
using System.Collections.Generic;
using System.Diagnostics.CodeAnalysis;
using System.IO;
using System.Net.Sockets;
using System.Text;

using Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWars2017.DevKit.CSharpCgdk {
    [SuppressMessage("ReSharper", "SuggestBaseTypeForParameter")]
    public sealed class RemoteProcessClient {
        private const int BufferSizeBytes = 1 << 20;

        private static readonly byte[] EmptyByteArray = new byte[0];

        private readonly TcpClient client;
        private readonly BinaryReader reader;
        private readonly BinaryWriter writer;

        private Player[] previousPlayers;
        private Facility[] previousFacilities;
        private TerrainType[][] terrainByCellXY;
        private WeatherType[][] weatherByCellXY;

        private readonly IDictionary<long, Player> previousPlayerById = new Dictionary<long, Player>();
        private readonly IDictionary<long, Facility> previousFacilityById = new Dictionary<long, Facility>();

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

        public void WriteProtocolVersionMessage() {
            WriteEnum((sbyte?) MessageType.ProtocolVersion);
            WriteInt(3);
            writer.Flush();
        }

        public void ReadTeamSizeMessage() {
            EnsureMessageType((MessageType) ReadEnum(), MessageType.TeamSize);
            ReadInt();
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

        public void WriteMoveMessage(Move move) {
            WriteEnum((sbyte?) MessageType.Move);
            WriteMove(move);
            writer.Flush();
        }

        public void Close() {
            client.Close();
        }

        private Facility ReadFacility() {
            sbyte flag = ReadSByte();

            if (flag == 0) {
                return null;
            }

            if (flag == 127) {
                return previousFacilityById[ReadLong()];
            }

            Facility facility = new Facility(
                    ReadLong(), (FacilityType) ReadEnum(), ReadLong(), ReadDouble(), ReadDouble(), ReadDouble(),
                    (VehicleType?) ReadEnum(), ReadInt()
            );
            previousFacilityById[facility.Id] = facility;
            return facility;
        }

        private void WriteFacility(Facility facility) {
            if (facility == null) {
                WriteBoolean(false);
                return;
            }

            WriteBoolean(true);

            WriteLong(facility.Id);
            WriteEnum((sbyte?) facility.Type);
            WriteLong(facility.OwnerPlayerId);
            WriteDouble(facility.Left);
            WriteDouble(facility.Top);
            WriteDouble(facility.CapturePoints);
            WriteEnum((sbyte?) facility.VehicleType);
            WriteInt(facility.ProductionProgress);
        }

        private Facility[] ReadFacilities() {
            int facilityCount = ReadInt();
            if (facilityCount < 0) {
                return previousFacilities;
            }

            Facility[] facilities = new Facility[facilityCount];

            for (int facilityIndex = 0; facilityIndex < facilityCount; ++facilityIndex) {
                facilities[facilityIndex] = ReadFacility();
            }

            return previousFacilities = facilities;
        }

        private void WriteFacilities(Facility[] facilities) {
            if (facilities == null) {
                WriteInt(-1);
                return;
            }

            int facilityCount = facilities.Length;
            WriteInt(facilityCount);

            for (int facilityIndex = 0; facilityIndex < facilityCount; ++facilityIndex) {
                WriteFacility(facilities[facilityIndex]);
            }
        }

        private Game ReadGame() {
            if (!ReadBoolean()) {
                return null;
            }

            return new Game(
                    ReadLong(), ReadInt(), ReadDouble(), ReadDouble(), ReadBoolean(), ReadInt(), ReadInt(), ReadInt(),
                    ReadInt(), ReadInt(), ReadInt(), ReadInt(), ReadInt(), ReadInt(), ReadDouble(), ReadDouble(),
                    ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(),
                    ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(),
                    ReadDouble(), ReadDouble(), ReadDouble(), ReadInt(), ReadDouble(), ReadDouble(), ReadDouble(),
                    ReadDouble(), ReadInt(), ReadInt(), ReadInt(), ReadInt(), ReadInt(), ReadInt(), ReadInt(),
                    ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadInt(), ReadInt(), ReadInt(), ReadInt(),
                    ReadInt(), ReadInt(), ReadInt(), ReadDouble(), ReadDouble(), ReadInt(), ReadInt(), ReadInt(),
                    ReadDouble(), ReadDouble(), ReadInt(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(),
                    ReadInt(), ReadInt(), ReadInt(), ReadInt(), ReadInt(), ReadInt(), ReadInt(), ReadDouble(),
                    ReadDouble(), ReadDouble(), ReadDouble(), ReadInt(), ReadInt(), ReadInt(), ReadInt(), ReadInt(),
                    ReadInt(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadInt(), ReadInt(),
                    ReadDouble(), ReadDouble(), ReadInt()
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
            WriteDouble(game.WorldWidth);
            WriteDouble(game.WorldHeight);
            WriteBoolean(game.IsFogOfWarEnabled);
            WriteInt(game.VictoryScore);
            WriteInt(game.FacilityCaptureScore);
            WriteInt(game.VehicleEliminationScore);
            WriteInt(game.ActionDetectionInterval);
            WriteInt(game.BaseActionCount);
            WriteInt(game.AdditionalActionCountPerControlCenter);
            WriteInt(game.MaxUnitGroup);
            WriteInt(game.TerrainWeatherMapColumnCount);
            WriteInt(game.TerrainWeatherMapRowCount);
            WriteDouble(game.PlainTerrainVisionFactor);
            WriteDouble(game.PlainTerrainStealthFactor);
            WriteDouble(game.PlainTerrainSpeedFactor);
            WriteDouble(game.SwampTerrainVisionFactor);
            WriteDouble(game.SwampTerrainStealthFactor);
            WriteDouble(game.SwampTerrainSpeedFactor);
            WriteDouble(game.ForestTerrainVisionFactor);
            WriteDouble(game.ForestTerrainStealthFactor);
            WriteDouble(game.ForestTerrainSpeedFactor);
            WriteDouble(game.ClearWeatherVisionFactor);
            WriteDouble(game.ClearWeatherStealthFactor);
            WriteDouble(game.ClearWeatherSpeedFactor);
            WriteDouble(game.CloudWeatherVisionFactor);
            WriteDouble(game.CloudWeatherStealthFactor);
            WriteDouble(game.CloudWeatherSpeedFactor);
            WriteDouble(game.RainWeatherVisionFactor);
            WriteDouble(game.RainWeatherStealthFactor);
            WriteDouble(game.RainWeatherSpeedFactor);
            WriteDouble(game.VehicleRadius);
            WriteInt(game.TankDurability);
            WriteDouble(game.TankSpeed);
            WriteDouble(game.TankVisionRange);
            WriteDouble(game.TankGroundAttackRange);
            WriteDouble(game.TankAerialAttackRange);
            WriteInt(game.TankGroundDamage);
            WriteInt(game.TankAerialDamage);
            WriteInt(game.TankGroundDefence);
            WriteInt(game.TankAerialDefence);
            WriteInt(game.TankAttackCooldownTicks);
            WriteInt(game.TankProductionCost);
            WriteInt(game.IfvDurability);
            WriteDouble(game.IfvSpeed);
            WriteDouble(game.IfvVisionRange);
            WriteDouble(game.IfvGroundAttackRange);
            WriteDouble(game.IfvAerialAttackRange);
            WriteInt(game.IfvGroundDamage);
            WriteInt(game.IfvAerialDamage);
            WriteInt(game.IfvGroundDefence);
            WriteInt(game.IfvAerialDefence);
            WriteInt(game.IfvAttackCooldownTicks);
            WriteInt(game.IfvProductionCost);
            WriteInt(game.ArrvDurability);
            WriteDouble(game.ArrvSpeed);
            WriteDouble(game.ArrvVisionRange);
            WriteInt(game.ArrvGroundDefence);
            WriteInt(game.ArrvAerialDefence);
            WriteInt(game.ArrvProductionCost);
            WriteDouble(game.ArrvRepairRange);
            WriteDouble(game.ArrvRepairSpeed);
            WriteInt(game.HelicopterDurability);
            WriteDouble(game.HelicopterSpeed);
            WriteDouble(game.HelicopterVisionRange);
            WriteDouble(game.HelicopterGroundAttackRange);
            WriteDouble(game.HelicopterAerialAttackRange);
            WriteInt(game.HelicopterGroundDamage);
            WriteInt(game.HelicopterAerialDamage);
            WriteInt(game.HelicopterGroundDefence);
            WriteInt(game.HelicopterAerialDefence);
            WriteInt(game.HelicopterAttackCooldownTicks);
            WriteInt(game.HelicopterProductionCost);
            WriteInt(game.FighterDurability);
            WriteDouble(game.FighterSpeed);
            WriteDouble(game.FighterVisionRange);
            WriteDouble(game.FighterGroundAttackRange);
            WriteDouble(game.FighterAerialAttackRange);
            WriteInt(game.FighterGroundDamage);
            WriteInt(game.FighterAerialDamage);
            WriteInt(game.FighterGroundDefence);
            WriteInt(game.FighterAerialDefence);
            WriteInt(game.FighterAttackCooldownTicks);
            WriteInt(game.FighterProductionCost);
            WriteDouble(game.MaxFacilityCapturePoints);
            WriteDouble(game.FacilityCapturePointsPerVehiclePerTick);
            WriteDouble(game.FacilityWidth);
            WriteDouble(game.FacilityHeight);
            WriteInt(game.BaseTacticalNuclearStrikeCooldown);
            WriteInt(game.TacticalNuclearStrikeCooldownDecreasePerControlCenter);
            WriteDouble(game.MaxTacticalNuclearStrikeDamage);
            WriteDouble(game.TacticalNuclearStrikeRadius);
            WriteInt(game.TacticalNuclearStrikeDelay);
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

        private void WriteMove(Move move) {
            if (move == null) {
                WriteBoolean(false);
                return;
            }

            WriteBoolean(true);

            WriteEnum((sbyte?) move.Action);
            WriteInt(move.Group);
            WriteDouble(move.Left);
            WriteDouble(move.Top);
            WriteDouble(move.Right);
            WriteDouble(move.Bottom);
            WriteDouble(move.X);
            WriteDouble(move.Y);
            WriteDouble(move.Angle);
            WriteDouble(move.Factor);
            WriteDouble(move.MaxSpeed);
            WriteDouble(move.MaxAngularSpeed);
            WriteEnum((sbyte?) move.VehicleType);
            WriteLong(move.FacilityId);
            WriteLong(move.VehicleId);
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

        private Player ReadPlayer() {
            sbyte flag = ReadSByte();

            if (flag == 0) {
                return null;
            }

            if (flag == 127) {
                return previousPlayerById[ReadLong()];
            }

            Player player = new Player(
                    ReadLong(), ReadBoolean(), ReadBoolean(), ReadInt(), ReadInt(), ReadInt(), ReadLong(), ReadInt(),
                    ReadDouble(), ReadDouble()
            );
            previousPlayerById[player.Id] = player;
            return player;
        }

        private void WritePlayer(Player player) {
            if (player == null) {
                WriteBoolean(false);
                return;
            }

            WriteBoolean(true);

            WriteLong(player.Id);
            WriteBoolean(player.IsMe);
            WriteBoolean(player.IsStrategyCrashed);
            WriteInt(player.Score);
            WriteInt(player.RemainingActionCooldownTicks);
            WriteInt(player.RemainingNuclearStrikeCooldownTicks);
            WriteLong(player.NextNuclearStrikeVehicleId);
            WriteInt(player.NextNuclearStrikeTickIndex);
            WriteDouble(player.NextNuclearStrikeX);
            WriteDouble(player.NextNuclearStrikeY);
        }

        private Player[] ReadPlayers() {
            int playerCount = ReadInt();
            if (playerCount < 0) {
                return previousPlayers;
            }

            Player[] players = new Player[playerCount];

            for (int playerIndex = 0; playerIndex < playerCount; ++playerIndex) {
                players[playerIndex] = ReadPlayer();
            }

            return previousPlayers = players;
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

            return new PlayerContext(ReadPlayer(), ReadWorld());
        }

        private void WritePlayerContext(PlayerContext playerContext) {
            if (playerContext == null) {
                WriteBoolean(false);
                return;
            }

            WriteBoolean(true);

            WritePlayer(playerContext.Player);
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

        private Vehicle ReadVehicle() {
            if (!ReadBoolean()) {
                return null;
            }

            return new Vehicle(
                    ReadLong(), ReadDouble(), ReadDouble(), ReadDouble(), ReadLong(), ReadInt(), ReadInt(),
                    ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(),
                    ReadInt(), ReadInt(), ReadInt(), ReadInt(), ReadInt(), ReadInt(), (VehicleType) ReadEnum(),
                    ReadBoolean(), ReadBoolean(), ReadInts()
            );
        }

        private void WriteVehicle(Vehicle vehicle) {
            if (vehicle == null) {
                WriteBoolean(false);
                return;
            }

            WriteBoolean(true);

            WriteLong(vehicle.Id);
            WriteDouble(vehicle.X);
            WriteDouble(vehicle.Y);
            WriteDouble(vehicle.Radius);
            WriteLong(vehicle.PlayerId);
            WriteInt(vehicle.Durability);
            WriteInt(vehicle.MaxDurability);
            WriteDouble(vehicle.MaxSpeed);
            WriteDouble(vehicle.VisionRange);
            WriteDouble(vehicle.SquaredVisionRange);
            WriteDouble(vehicle.GroundAttackRange);
            WriteDouble(vehicle.SquaredGroundAttackRange);
            WriteDouble(vehicle.AerialAttackRange);
            WriteDouble(vehicle.SquaredAerialAttackRange);
            WriteInt(vehicle.GroundDamage);
            WriteInt(vehicle.AerialDamage);
            WriteInt(vehicle.GroundDefence);
            WriteInt(vehicle.AerialDefence);
            WriteInt(vehicle.AttackCooldownTicks);
            WriteInt(vehicle.RemainingAttackCooldownTicks);
            WriteEnum((sbyte?) vehicle.Type);
            WriteBoolean(vehicle.IsAerial);
            WriteBoolean(vehicle.IsSelected);
            WriteInts(vehicle.Groups);
        }

        private Vehicle[] ReadVehicles() {
            int vehicleCount = ReadInt();
            if (vehicleCount < 0) {
                return null;
            }

            Vehicle[] vehicles = new Vehicle[vehicleCount];

            for (int vehicleIndex = 0; vehicleIndex < vehicleCount; ++vehicleIndex) {
                vehicles[vehicleIndex] = ReadVehicle();
            }

            return vehicles;
        }

        private void WriteVehicles(Vehicle[] vehicles) {
            if (vehicles == null) {
                WriteInt(-1);
                return;
            }

            int vehicleCount = vehicles.Length;
            WriteInt(vehicleCount);

            for (int vehicleIndex = 0; vehicleIndex < vehicleCount; ++vehicleIndex) {
                WriteVehicle(vehicles[vehicleIndex]);
            }
        }

        private VehicleUpdate ReadVehicleUpdate() {
            if (!ReadBoolean()) {
                return null;
            }

            return new VehicleUpdate(
                    ReadLong(), ReadDouble(), ReadDouble(), ReadInt(), ReadInt(), ReadBoolean(), ReadInts()
            );
        }

        private void WriteVehicleUpdate(VehicleUpdate vehicleUpdate) {
            if (vehicleUpdate == null) {
                WriteBoolean(false);
                return;
            }

            WriteBoolean(true);

            WriteLong(vehicleUpdate.Id);
            WriteDouble(vehicleUpdate.X);
            WriteDouble(vehicleUpdate.Y);
            WriteInt(vehicleUpdate.Durability);
            WriteInt(vehicleUpdate.RemainingAttackCooldownTicks);
            WriteBoolean(vehicleUpdate.IsSelected);
            WriteInts(vehicleUpdate.Groups);
        }

        private VehicleUpdate[] ReadVehicleUpdates() {
            int vehicleUpdateCount = ReadInt();
            if (vehicleUpdateCount < 0) {
                return null;
            }

            VehicleUpdate[] vehicleUpdates = new VehicleUpdate[vehicleUpdateCount];

            for (int vehicleUpdateIndex = 0; vehicleUpdateIndex < vehicleUpdateCount; ++vehicleUpdateIndex) {
                vehicleUpdates[vehicleUpdateIndex] = ReadVehicleUpdate();
            }

            return vehicleUpdates;
        }

        private void WriteVehicleUpdates(VehicleUpdate[] vehicleUpdates) {
            if (vehicleUpdates == null) {
                WriteInt(-1);
                return;
            }

            int vehicleUpdateCount = vehicleUpdates.Length;
            WriteInt(vehicleUpdateCount);

            for (int vehicleUpdateIndex = 0; vehicleUpdateIndex < vehicleUpdateCount; ++vehicleUpdateIndex) {
                WriteVehicleUpdate(vehicleUpdates[vehicleUpdateIndex]);
            }
        }

        private World ReadWorld() {
            if (!ReadBoolean()) {
                return null;
            }

            return new World(
                    ReadInt(), ReadInt(), ReadDouble(), ReadDouble(), ReadPlayers(), ReadVehicles(), ReadVehicleUpdates(),
                    terrainByCellXY ?? (terrainByCellXY = ReadEnums2D<TerrainType>()),
                    weatherByCellXY ?? (weatherByCellXY = ReadEnums2D<WeatherType>()),
                    ReadFacilities()
            );
        }

        private void WriteWorld(World world) {
            if (world == null) {
                WriteBoolean(false);
                return;
            }

            WriteBoolean(true);

            WriteInt(world.TickIndex);
            WriteInt(world.TickCount);
            WriteDouble(world.Width);
            WriteDouble(world.Height);
            WritePlayers(world.Players);
            WriteVehicles(world.NewVehicles);
            WriteVehicleUpdates(world.VehicleUpdates);
            WriteEnums2D(world.TerrainByCellXY);
            WriteEnums2D(world.WeatherByCellXY);
            WriteFacilities(world.Facilities);
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
                throw new ArgumentException($"Received wrong message [actual={actualType}, expected={expectedType}].");
            }
        }

        private byte[] ReadByteArray(bool nullable) {
            int count = ReadInt();

            if (nullable) {
                if (count < 0) {
                    return null;
                }
            } else {
                if (count <= 0) {
                    return EmptyByteArray;
                }
            }

            return ReadBytes(count);
        }

        private void WriteByteArray(byte[] array) {
            if (array == null) {
                WriteInt(-1);
            } else {
                WriteInt(array.Length);
                WriteBytes(array);
            }
        }

        private sbyte? ReadEnum() {
            sbyte value = reader.ReadSByte();
            return value < 0 ? null : (sbyte?) value;
        }

        private E[] ReadEnums<E>() where E : struct, IComparable, IFormattable, IConvertible {
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

        private E[][] ReadEnums2D<E>() where E : struct, IComparable, IFormattable, IConvertible {
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

        private E?[] ReadNullableEnums<E>() where E : struct, IComparable, IFormattable, IConvertible {
            int count = ReadInt();
            if (count < 0) {
                return null;
            }

            E?[] enums = new E?[count];

            for (int i = 0; i < count; ++i) {
                sbyte? value = ReadEnum();
                enums[i] = value.HasValue ? (E) Enum.ToObject(typeof(E), value) : (E?) null;
            }

            return enums;
        }

        private E?[][] ReadNullableEnums2D<E>() where E : struct, IComparable, IFormattable, IConvertible {
            int count = ReadInt();
            if (count < 0) {
                return null;
            }

            E?[][] enums = new E?[count][];

            for (int i = 0; i < count; ++i) {
                enums[i] = ReadNullableEnums<E>();
            }

            return enums;
        }

        private void WriteEnum(sbyte? value) {
            writer.Write(value ?? -1);
        }

        private void WriteEnums<E>(E[] enums) where E : struct, IComparable, IFormattable, IConvertible {
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

        private void WriteEnums2D<E>(E[][] enums) where E : struct, IComparable, IFormattable, IConvertible {
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

        private void WriteEnums<E>(E?[] enums) where E : struct, IComparable, IFormattable, IConvertible {
            if (enums == null) {
                WriteInt(-1);
                return;
            }

            int count = enums.Length;
            WriteInt(count);

            for (int i = 0; i < count; ++i) {
                WriteEnum(enums[i]?.ToSByte(null));
            }
        }

        private void WriteEnums2D<E>(E?[][] enums) where E : struct, IComparable, IFormattable, IConvertible {
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

        private sbyte ReadSByte() {
            return reader.ReadSByte();
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
                return;
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
            Move
        }
    }
}